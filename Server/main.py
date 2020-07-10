# Импортируем нужные библиотеки
import logging
import os
import random
from threading import Thread

from apscheduler.schedulers.background import BackgroundScheduler
from flask import Flask, redirect, render_template, request, jsonify, g, url_for, flash
from flask_login import LoginManager, logout_user, login_required
from flask_mail import Mail, Message
from werkzeug.security import generate_password_hash

from auth import basic_auth, token_auth
from conf.config import generate_config
from conf.routes import generate_routes
from data import db_session
from data.users import User, correct_password
from forms.reset_password_form import ResetPasswordForm
from resources.corona_scrapper import scrap_countries

# Инициализируем Flask

app = Flask(__name__)
# Добавляем к приложению config файлы
generate_config(app)

# Запись логов
logger = logging.getLogger("NeoBrain")
logger.setLevel(logging.DEBUG)
logging.basicConfig(
    filename='neo.log',
    format='%(asctime)s %(levelname)s %(name)s %(message)s'
)

# Добавляем к приложению API
generate_routes(app)

# Инициализурем объект LoginManager для добавления функциональности авторизации пользователей
login_manager = LoginManager()
login_manager.init_app(app)

# Создаём объект почты, для рассылки
mail = Mail(app)

# Добавляем постоянную задачу
scheduler = BackgroundScheduler()
logger.debug("Background task added")
job = scheduler.add_job(scrap_countries, 'interval', minutes=30)
scheduler.start()


# Главный метод для работы с сервером
def main():
    logger.debug("START")
    # Инициализируем базу данных
    db_session.global_init("db/neobrain.db")
    # Если не найден PORT среди файлов, поставь порт 5000
    port = int(os.environ.get("PORT", 5000))
    # Запускаем приложение
    app.run(host='0.0.0.0', port=port)


# Загружаем пользователя
@login_manager.user_loader
def load_user(user_id):
    logger.debug("Отправлен запрос в базу данных на сохранение пользователя")
    session = db_session.create_session()
    return session.query(User).get(user_id)


# Выход пользователя
@app.route('/logout')
@login_required
def logout():
    logger.debug("Отправлен запрос в базу данных на выход пользователя")
    logout_user()
    return redirect("/")


@app.route('/api/auth', methods=['POST'])
@basic_auth.login_required
# Путь получает в заголовках запроса логин и пароль пользователя (декоратор @basic.auth.login_required)
# и, если данные верны, возвращает токен. Чтобы защитить маршруты API с помощью токенов, необходимо
# добавить декоратор @token_auth.login_required
def get_token():
    token = g.current_user.get_token()
    g.db_session.commit()
    return jsonify({'token': token,
                    'expires': str(g.current_user.token_expiration)})


@app.route('/api/logout', methods=['POST'])
@token_auth.login_required
# Отзыв токена
def revoke_token():
    g.current_user.revoke_token()
    g.db_session.commit()
    g.current_user = None
    g.db_session = None
    return jsonify({'success': True})


# Регистрация
@app.route('/register', methods=['GET', 'POST'])
def register():
    return render_template('reg.html')


# Авторизация
@app.route('/login', methods=['GET', 'POST'])
def login():
    pass


@app.route('/api/reset_password/<token>', methods=['GET', 'POST'])
def reset_password(token):
    user = User.verify_reset_password_token(token)
    if not user:
        return redirect(url_for('index'))
    session = db_session.create_session()
    form = ResetPasswordForm()
    if form.validate_on_submit():
        if correct_password(form.password.data)[0] == "OK":
            user.set_password(form.password.data)
            session.commit()
            return render_template('successful.html')
        return render_template('reset_password.html', form=form)
    return render_template('reset_password.html', form=form)


def send_email(subject, recipients, html_body):
    msg = Message(subject, recipients=recipients)
    msg.html = html_body
    Thread(target=send_async_email, args=(app, msg)).start()
    logger.info("Mail was send to user")


@app.route('/api/send_email', methods=['POST'])
def send_code_authorization_email():
    if not request.json:
        return jsonify({'status': 400,
                        'text': 'Empty request'})
    numbers = [random.randrange(10), random.randrange(10), random.randrange(10),
               random.randrange(10), random.randrange(10), random.randrange(10)]
    code = ' '.join(str(digit) for digit in numbers)
    send_email("Подтверждение электронной почты",
               [request.json['email']],
               html_body=render_template('email/get_code_authorization.html', code=code)
               )
    logger.info("Send code to user email")
    return jsonify({'status': 200,
                    'text': ''.join(str(digit) for digit in numbers)})


@app.route('/api/send_password_reset_email', methods=['POST'])
def send_password_reset_email():
    if not request.json:
        return jsonify({'status': 400,
                        'text': 'Empty request'})
    session = db_session.create_session()
    user = session.query(User).filter(User.email == request.json['email']).first()
    if user:
        token = user.get_reset_password_token()
        send_email('Сброс вашего пароля',
                   recipients=[user.email],
                   html_body=render_template('email/reset_password.html',
                                             user=user, token=token))
        return jsonify({'status': 200,
                        'text': token})
    return jsonify({'status': 404,
                    'text': 'Email not found'})


def send_async_email(app, msg):
    with app.app_context():
        mail.send(msg)


# Landing-page
@app.route("/")
def index():
    return render_template('index.html')


if __name__ == '__main__':
    main()

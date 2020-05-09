# Импортируем нужные библиотеки
import datetime
import logging
import os
import random

from flask import Flask, redirect, render_template, request, jsonify
from flask_login import LoginManager, logout_user, login_required
from flask_mail import Mail, Message

from conf.routes import generate_routes
from data import db_session
from data.users import User

# Инициализируем Flask
app = Flask(__name__)
# Добавляем к приложению config файлы
app.config['SECRET_KEY'] = 'NeoBrainKey'
app.config['PERMANENT_SESSION_LIFETIME'] = datetime.timedelta(days=365)
app.config['MAIL_SERVER'] = 'smtp.googlemail.com'
app.config['MAIL_PORT'] = 587
app.config['MAIL_USE_TLS'] = True
app.config['MAIL_USERNAME'] = 'neo.brain.team@gmail.com'
app.config['MAIL_DEFAULT_SENDER'] = 'neo.brain.team@gmail.com'
app.config['MAIL_PASSWORD'] = '9BP-Ha9-5Tn-6rm'

# # Запись логов
# logger = logging.getLogger("NeoBrain")
# logger.setLevel(logging.DEBUG)
# logging.basicConfig(
#     filename='neo.log',
#     format='%(asctime)s %(levelname)s %(name)s %(message)s'
# )

# Добавляем к приложению API
generate_routes(app)

# Инициализурем объект LoginManager для добавления функциональности авторизации пользователей
login_manager = LoginManager()
login_manager.init_app(app)

#
mail = Mail(app)


# Главный метод для работы с сервером
def main():
    # logger.debug("START")
    # Инициализируем базу данных
    db_session.global_init("db/neobrain.db")
    # Если не найден PORT среди файлов, поставь порт 5000
    port = int(os.environ.get("PORT", 5000))
    # Запускаем приложение
    app.run(host='0.0.0.0', port=port)


# Загружаем пользователя
@login_manager.user_loader
def load_user(user_id):
    # logger.debug("Отправлен запрос в базу данных на сохранение пользователя")
    session = db_session.create_session()
    return session.query(User).get(user_id)


# Выход пользователя
@app.route('/logout')
@login_required
def logout():
    # logger.debug("Отправлен запрос в базу данных на выход пользователя")
    logout_user()
    return redirect("/")


# Регистрация
@app.route('/register', methods=['GET', 'POST'])
def register():
    pass


# Авторизация
@app.route('/login', methods=['GET', 'POST'])
def login():
    pass


@app.route('/api/send_email', methods=['POST'])
def send_mail():
    if not request.json:
        return jsonify({'status': 400,
                        'text': 'Empty request'})
    numbers = [random.randrange(10), random.randrange(10), random.randrange(10),
               random.randrange(10), random.randrange(10), random.randrange(10)]
    msg = Message("Подтверждение электронной почты", recipients=[request.json['email']])
    msg.html = f"<h1>{' '.join(str(digit) for digit in numbers)}</h1>\n<p>Код подтверждения вашей почты в приложении NeoBrain</p>"
    mail.send(msg)
    return jsonify({'status': 200,
                    'text': ''.join(str(digit) for digit in numbers)})


# Landing-page
@app.route("/")
def index():
    return render_template('index.html')


if __name__ == '__main__':
    main()

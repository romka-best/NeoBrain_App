# Импортируем нужные библиотеки
import datetime
import logging
import os

from flask import Flask, request, redirect
from flask_login import LoginManager, logout_user, login_required

from conf.routes import generate_routes
from data import db_session
from data.users import User

# Инициализируем Flask
app = Flask(__name__)
# Добавляем к приложению config файлы
app.config['SECRET_KEY'] = 'NeoBrainKey'
app.config['PERMANENT_SESSION_LIFETIME'] = datetime.timedelta(days=365)

# Запись логов
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
    if request.method == 'POST':
        pass


# Landing-page
@app.route("/")
def index():
    return "NeoHello!"


if __name__ == '__main__':
    main()

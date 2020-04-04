# Импортируем нужные библиотеки
import datetime

import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from werkzeug.security import generate_password_hash, check_password_hash
from flask_login import UserMixin
from sqlalchemy_serializer import SerializerMixin
import re


# Для проверки пароля
def correct_password(password):
    errors = []  # Массив ошибок
    if len(password) < 8:
        errors.append("Less than 8 characters")
    if not (re.search(r'[a-z]', password) and re.search(r'[A-Z]', password)):
        errors.append("Uppercase only or lowercase only")
    if not re.search(r'[0-9]', password):
        errors.append("Missing digits")
    # Если массив не пустой, значит отправляем ошибки
    if errors:
        return errors
    # В ином случае проверяем насколько хороший пароль:
    # 8 - Нормальный, 10 - Хороший, 12 - отличный
    balls = 8
    if re.search('[{}@#$%^&+=*()?!.,~_]', password):
        balls += 2
    if len(password) >= 12:
        balls += 2
    return "OK", balls


class User(SqlAlchemyBase, UserMixin, SerializerMixin):
    __tablename__ = 'users'

    # id пользователя
    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    # Имя пользователя
    name = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    # Фамилия пользователя
    surname = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    # Никнейм пользователя
    nickname = sqlalchemy.Column(sqlalchemy.String, nullable=False, unique=True)
    # Номер телефона пользователя в формате 7XXXXXXXXXX
    number = sqlalchemy.Column(sqlalchemy.String, unique=True, index=True, nullable=False)
    # Хэшированный пароль
    hashed_password = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    # Емайл пользователя в формате email@name.com
    email = sqlalchemy.Column(sqlalchemy.String, nullable=True, unique=True)
    # Закрыт ли аккаунт
    is_closed = sqlalchemy.Column(sqlalchemy.Boolean, default=False)
    # Статус пользователя
    about = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    # Дата рождения пользователя в формате YYYY-MM-DD
    birthday = sqlalchemy.Column(sqlalchemy.DateTime, nullable=True)
    # Возраст пользователя
    age = sqlalchemy.Column(sqlalchemy.Integer, nullable=True)
    # id юзеров кто в чёрном списке в формате id1, id2, id3...
    in_black_list = sqlalchemy.Column(sqlalchemy.String, default=False)
    # Могут ли смотреть его аудио
    can_see_audio = sqlalchemy.Column(sqlalchemy.Boolean, default=True)
    # Могут ли смотреть его группы
    can_see_groups = sqlalchemy.Column(sqlalchemy.Boolean, default=True)
    # Могут ли смотреть его видео
    can_see_videos = sqlalchemy.Column(sqlalchemy.Boolean, default=True)
    # Могут ли ему писать сообщения
    can_write_message = sqlalchemy.Column(sqlalchemy.Boolean, default=True)
    # Город пользователя
    city = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    # Республика(Область) пользователя
    republic = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    # Страна пользователя
    country = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    # Образование пользователя
    education = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    # Количество подписанных на пользователя
    followers_count = sqlalchemy.Column(sqlalchemy.Integer, default=0)
    # Количество подписок на других пользователей
    subscriptions_count = sqlalchemy.Column(sqlalchemy.Integer, default=0)
    # Статус пользователя 0 - Offline, 1 - Online
    status = sqlalchemy.Column(sqlalchemy.Integer, default=1)
    # Вошёл ли пользователь
    authenticated = sqlalchemy.Column(sqlalchemy.Boolean, nullable=True)
    # Последний вход пользователя в формате YYYY-MM-DD HH:MM:SS
    last_seen = sqlalchemy.Column(sqlalchemy.DateTime, default=datetime.datetime.now)

    # Дата создания пользователя
    created_date = sqlalchemy.Column(sqlalchemy.DateTime,
                                     default=datetime.datetime.now)
    # Дата изменения пользователя
    modified_date = sqlalchemy.Column(sqlalchemy.DateTime,
                                      default=datetime.datetime.now)

    # Связь с Chat
    chats = orm.relation("Chat", back_populates='user')
    # Связь с Message
    messages_from = orm.relation("Message", back_populates='author')
    # Связь с Photo
    photo = orm.relation("Photo")
    photo_id = sqlalchemy.Column(sqlalchemy.Integer,
                                 sqlalchemy.ForeignKey("photos.id"), default=2)

    # Для установки пароля
    def set_password(self, password):
        self.hashed_password = generate_password_hash(password)

    # Для проверки пароля
    def check_password(self, password):
        return check_password_hash(self.hashed_password, password)

    # Зашёл ли пользователь
    def is_authenticated(self):
        return self.authenticated

    # Активный ли пользователь
    def is_active(self):
        return bool(self.status)

    # Анонимус ли пользователь (Всегда False, потому что он зарегестрирован)
    def is_anonymous(self):
        return False

    # Возвращаем id пользователя
    def get_id(self):
        return self.id

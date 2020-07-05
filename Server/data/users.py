# Импортируем нужные библиотеки
import datetime
import re

import sqlalchemy
from flask_login import UserMixin
from sqlalchemy import orm
from sqlalchemy_serializer import SerializerMixin
from werkzeug.security import generate_password_hash, check_password_hash

from .db_session import SqlAlchemyBase


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
        return "WRONG", errors
    # В ином случае проверяем насколько хороший пароль:
    # 8 - Нормальный, 10 - Хороший, 12 - отличный
    balls = 8
    if re.search('[{}@#$%^&+=*()?!.,~_]', password):
        balls += 2
    if len(password) >= 12:
        balls += 2
    return "OK", balls


def get_current_time() -> datetime:
    delta = datetime.timedelta(hours=3, minutes=0)
    return datetime.datetime.now(datetime.timezone.utc) + delta


class User(SqlAlchemyBase, UserMixin, SerializerMixin):
    __tablename__ = 'users'

    # Формат даты
    datetime_format = '%Y-%m-%d %H:%M:%S'
    # id пользователя
    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    # Имя пользователя
    name = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    # Фамилия пользователя
    surname = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    # Никнейм пользователя
    nickname = sqlalchemy.Column(sqlalchemy.String, nullable=False, unique=True)
    # Номер телефона пользователя в формате 7XXXXXXXXXX
    number = sqlalchemy.Column(sqlalchemy.String, unique=True, index=True, nullable=True)
    # Хэшированный пароль
    hashed_password = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    # Емайл пользователя в формате email@name.com
    email = sqlalchemy.Column(sqlalchemy.String, nullable=True, unique=True)
    # Закрыт ли аккаунт
    is_closed = sqlalchemy.Column(sqlalchemy.Boolean, default=False)
    # Статус пользователя
    about = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    # Дата рождения пользователя в формате YYYY-MM-DD или MM-DD
    birthday = sqlalchemy.Column(sqlalchemy.DateTime, nullable=True)
    # Возраст пользователя
    age = sqlalchemy.Column(sqlalchemy.Integer, nullable=True)
    # id юзеров кто в чёрном списке в формате id1, id2, id3...
    in_black_list = sqlalchemy.Column(sqlalchemy.String)
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
    # Пол пользователя: 0 - Женский, 1 - Мужской
    gender = sqlalchemy.Column(sqlalchemy.Integer, nullable=True)
    # Количество подписанных на пользователя
    followers_count = sqlalchemy.Column(sqlalchemy.Integer, default=0)
    # Количество подписок на других пользователей
    subscriptions_count = sqlalchemy.Column(sqlalchemy.Integer, default=0)
    # Статус пользователя 0 - Offline, 1 - Online
    status = sqlalchemy.Column(sqlalchemy.Integer, default=1)

    # Последний вход пользователя в формате YYYY-MM-DD HH:MM:SS
    last_seen = sqlalchemy.Column(sqlalchemy.DateTime, default=get_current_time)

    # Дата создания пользователя
    created_date = sqlalchemy.Column(sqlalchemy.DateTime,
                                     default=get_current_time)
    # Дата изменения пользователя
    modified_date = sqlalchemy.Column(sqlalchemy.DateTime,
                                      default=get_current_time)

    # Счётчики
    count_incoming_messages = sqlalchemy.Column(sqlalchemy.Integer,
                                                default=0)
    count_outgoing_messages = sqlalchemy.Column(sqlalchemy.Integer,
                                                default=0)
    count_music = sqlalchemy.Column(sqlalchemy.Integer,
                                    default=0)
    count_apps = sqlalchemy.Column(sqlalchemy.Integer,
                                   default=0)
    count_chats = sqlalchemy.Column(sqlalchemy.Integer,
                                    default=0)
    count_groups = sqlalchemy.Column(sqlalchemy.Integer,
                                     default=0)
    count_posts = sqlalchemy.Column(sqlalchemy.Integer,
                                    default=0)
    count_upload_photos = sqlalchemy.Column(sqlalchemy.Integer,
                                            default=0)

    # Связь с Achievement
    achievements = orm.relation("AchievementAssociation")

    # Связь с Post
    posts = orm.relation("PostAssociation")

    # Связь с App
    apps = orm.relation("AppAssociation")

    # Связь с Group
    groups = orm.relation("Group",
                          secondary="group_association",
                          backref="users")

    # Связь с Music
    music = orm.relation("Music",
                         secondary="music_association",
                         backref="users")

    # Связь с Chat
    chats = orm.relation("Chat",
                         secondary="chat_association",
                         backref="users")

    # Связь с People
    people = orm.relation("People",
                          secondary="people_association",
                          backref="users")

    # Связь с Message
    messages_from = orm.relation("Message", back_populates='author')
    # Связь с Photo
    photos = orm.relation("PhotoAssociation")

    # Для установки пароля
    def set_password(self, password):
        self.hashed_password = generate_password_hash(password)

    # Для проверки пароля
    def check_password(self, password):
        return check_password_hash(self.hashed_password, password)

    # Возвращаем id пользователя
    def get_id(self):
        return self.id

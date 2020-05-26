# Импортируем нужные библиотеки
import datetime

import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from sqlalchemy_serializer import SerializerMixin


def get_current_time() -> datetime:
    delta = datetime.timedelta(hours=3, minutes=0)
    return datetime.datetime.now(datetime.timezone.utc) + delta


class Post(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'posts'

    # Формат даты
    datetime_format = '%Y-%m-%d %H:%M:%S'
    # id поста
    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    # Имя(Заголовок) поста
    title = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    # Текст поста
    text = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    # Дата создания поста в формате YYYY-MM-DD HH:MM:SS
    created_date = sqlalchemy.Column(sqlalchemy.DateTime,
                                     default=get_current_time)
    # Дата изменения поста в формате YYYY-MM-DD HH:MM:SS
    modified_date = sqlalchemy.Column(sqlalchemy.DateTime,
                                      default=get_current_time)

    # Оценки поста
    like_emoji_count = sqlalchemy.Column(sqlalchemy.Integer, default=-1)
    laughter_emoji_count = sqlalchemy.Column(sqlalchemy.Integer, default=-1)
    heart_emoji_count = sqlalchemy.Column(sqlalchemy.Integer, default=-1)
    disappointed_emoji_count = sqlalchemy.Column(sqlalchemy.Integer, default=-1)
    smile_emoji_count = sqlalchemy.Column(sqlalchemy.Integer, default=-1)
    angry_emoji_count = sqlalchemy.Column(sqlalchemy.Integer, default=-1)
    smile_with_heart_eyes_count = sqlalchemy.Column(sqlalchemy.Integer, default=-1)
    screaming_emoji_count = sqlalchemy.Column(sqlalchemy.Integer, default=-1)

    # Связь с фото и его foreign key
    photo = orm.relation("Photo")
    photo_id = sqlalchemy.Column(sqlalchemy.Integer,
                                 sqlalchemy.ForeignKey("photos.id"), default=2)

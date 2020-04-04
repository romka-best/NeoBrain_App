# Импортируем нужные библиотеки
import datetime

import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from sqlalchemy_serializer import SerializerMixin


class Post(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'posts'

    # Формат даты
    datetime_format = '%Y-%m-%d %H:%M:%S'
    # id поста
    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    # Имя(Заголовок) поста
    title = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    # текст поста
    text = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    # Дата создания поста в формате YYYY-MM-DD HH:MM:SS
    created_date = sqlalchemy.Column(sqlalchemy.DateTime,
                                     default=datetime.datetime.now)

    # Связь с пользователем и его foreign key
    user = orm.relation("User")
    user_id = sqlalchemy.Column(sqlalchemy.Integer,
                                sqlalchemy.ForeignKey("users.id"))
    # Связь с фото и его foreign key
    photo = orm.relation("Photo")
    photo_id = sqlalchemy.Column(sqlalchemy.Integer,
                                 sqlalchemy.ForeignKey("photos.id"), default=2)

# Импортируем нужные библиотеки
import datetime

import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from sqlalchemy_serializer import SerializerMixin


class Post(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'posts'

    # id поста
    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    # текст поста
    text = sqlalchemy.Column(sqlalchemy.String, nullable=True)

    # Связь с пользователем и его foreign key
    user = orm.relation("User")
    user_id = sqlalchemy.Column(sqlalchemy.Integer,
                                sqlalchemy.ForeignKey("users.id"))

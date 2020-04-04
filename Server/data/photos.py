# Импортируем нужные библиотеки
import datetime

import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from sqlalchemy_serializer import SerializerMixin


class Photo(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'photos'

    # id фото
    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    # описание фото
    about = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    # данные фото типа BLOB
    data = sqlalchemy.Column(sqlalchemy.BLOB, nullable=False)

    # Отношения User и Chat
    users = orm.relation("User", back_populates='photo')
    chats = orm.relation("Chat", back_populates='photo')

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

    # Отношения фогографии с другими таблицами
    users = orm.relation("User", back_populates='photo')
    chats = orm.relation("Chat", back_populates='photo')
    posts = orm.relation("Post", back_populates='photo')
    apps = orm.relation("App", back_populates='photo')
    music = orm.relation("Music", back_populates='photo')
    achievements = orm.relation("Achievement", back_populates='photo')

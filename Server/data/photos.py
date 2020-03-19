import datetime

import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from sqlalchemy_serializer import SerializerMixin


class Photo(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'photos'

    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    about = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    data = sqlalchemy.Column(sqlalchemy.BLOB, nullable=False)

    users = orm.relation("User", back_populates='photo')
    chats = orm.relation("Chat", back_populates='photo')

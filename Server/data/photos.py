import datetime

import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from sqlalchemy_serializer import SerializerMixin


class Photo(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'photos'

    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    name = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    type_of_chat = sqlalchemy.Column(sqlalchemy.Integer, nullable=False)
    status = sqlalchemy.Column(sqlalchemy.Integer, nullable=False)
    last_time_message = sqlalchemy.Column(sqlalchemy.DateTime, nullable=False)
    last_message = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    count_new_messages = sqlalchemy.Column(sqlalchemy.Integer, nullable=True)
    count_messages = sqlalchemy.Column(sqlalchemy.Integer, default=0)

    users = orm.relation("Users", back_populates='photo')
    chats = orm.relation("Chats", back_populates='photo')

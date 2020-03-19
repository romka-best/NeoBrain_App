import datetime

import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from sqlalchemy_serializer import SerializerMixin


class Chat(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'chats'

    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    name = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    type_of_chat = sqlalchemy.Column(sqlalchemy.Integer, nullable=False)
    status = sqlalchemy.Column(sqlalchemy.Integer, nullable=False)
    last_time_message = sqlalchemy.Column(sqlalchemy.DateTime, nullable=True)
    last_message = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    count_new_messages = sqlalchemy.Column(sqlalchemy.Integer, nullable=True)
    count_messages = sqlalchemy.Column(sqlalchemy.Integer, default=0)

    created_date = sqlalchemy.Column(sqlalchemy.DateTime,
                                     default=datetime.datetime.now)

    user = orm.relation("User")
    user_id = sqlalchemy.Column(sqlalchemy.Integer,
                                sqlalchemy.ForeignKey("users.id"))

    photo = orm.relation("Photo")
    photo_id = sqlalchemy.Column(sqlalchemy.Integer,
                                 sqlalchemy.ForeignKey("photos.id"))

    messages = orm.relation("Message", back_populates='chat')

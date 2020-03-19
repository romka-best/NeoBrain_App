import datetime

import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from sqlalchemy_serializer import SerializerMixin


class Message(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'messages'

    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    text = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    status = sqlalchemy.Column(sqlalchemy.Integer, nullable=False)
    with_attachments = sqlalchemy.Column(sqlalchemy.Boolean, nullable=False)

    created_date = sqlalchemy.Column(sqlalchemy.DateTime,
                                     default=datetime.datetime.now)
    modified_date = sqlalchemy.Column(sqlalchemy.DateTime,
                                      default=datetime.datetime.now)

    author = orm.relation("User")
    author_id = sqlalchemy.Column(sqlalchemy.Integer,
                                  sqlalchemy.ForeignKey("users.id"))

    to = sqlalchemy.Column(sqlalchemy.String,
                           nullable=False)

    chat = orm.relation("Chat")
    chat_id = sqlalchemy.Column(sqlalchemy.Integer,
                                sqlalchemy.ForeignKey("chats.id"))

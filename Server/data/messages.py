# Импортируем нужные библиотеки
import datetime

import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from sqlalchemy_serializer import SerializerMixin


def get_current_time() -> datetime:
    delta = datetime.timedelta(hours=3, minutes=0)
    return datetime.datetime.now(datetime.timezone.utc) + delta


class Message(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'messages'

    # Формат даты
    datetime_format = '%Y-%m-%d %H:%M:%S'
    # id сообщения
    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    # текст сообщения
    text = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    # статус сообщения: 0 - Неудачная отправка, 1 - Отправлено, 2 - Доставлено, 3 - Прочитано
    status = sqlalchemy.Column(sqlalchemy.Integer, nullable=False, default=0)

    # Дата создания сообщения
    created_date = sqlalchemy.Column(sqlalchemy.DateTime,
                                     default=get_current_time)
    # Дата изменения сообщения
    modified_date = sqlalchemy.Column(sqlalchemy.DateTime,
                                      default=get_current_time)

    # Автор сообщения
    author = orm.relation("User")
    author_id = sqlalchemy.Column(sqlalchemy.Integer,
                                  sqlalchemy.ForeignKey("users.id"), nullable=False)

    # Связь с чатом
    chat = orm.relation("Chat")
    chat_id = sqlalchemy.Column(sqlalchemy.Integer,
                                sqlalchemy.ForeignKey("chats.id"))

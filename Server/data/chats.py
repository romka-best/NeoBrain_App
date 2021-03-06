# Импортируем нужные библиотеки
import datetime

import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from sqlalchemy_serializer import SerializerMixin

# Добавляем ассоциативную таблицу с user и chat
association_table = sqlalchemy.Table('chat_association', SqlAlchemyBase.metadata,
                                     # Внешний ключ пользователя
                                     sqlalchemy.Column('user', sqlalchemy.Integer,
                                                       sqlalchemy.ForeignKey('users.id')),
                                     # Внешний ключ чата
                                     sqlalchemy.Column('chat', sqlalchemy.Integer,
                                                       sqlalchemy.ForeignKey('chats.id'))
                                     )


def get_current_time() -> datetime:
    delta = datetime.timedelta(hours=3, minutes=0)
    return datetime.datetime.now(datetime.timezone.utc) + delta


class Chat(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'chats'

    # Формат даты
    datetime_format = '%Y-%m-%d %H:%M:%S'
    # id Чата
    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    # Имя(Заголовок) чата
    name = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    # Тип чата: 0 - UserWithUser, 1 - UserWithUsers, 2 - Chanel, 3 - Бот
    type_of_chat = sqlalchemy.Column(sqlalchemy.Integer, nullable=False)
    # 0 - Offline, >=1 Количество user-ов онлайн в чате
    status = sqlalchemy.Column(sqlalchemy.Integer, nullable=True)
    # Время последнего отправленного сообщения в формате YYYY-MM-DD HH:MM:SS
    last_time_message = sqlalchemy.Column(sqlalchemy.DateTime, nullable=False, default=get_current_time)
    # Последнее сообщение в чате
    last_message = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    # Количество новых сообщений
    count_new_messages = sqlalchemy.Column(sqlalchemy.Integer, default=0)
    # Количество сообщений
    count_messages = sqlalchemy.Column(sqlalchemy.Integer, default=0)
    # Дата создания чата в формате YYYY-MM-DD HH:MM:SS
    created_date = sqlalchemy.Column(sqlalchemy.DateTime,
                                     default=get_current_time)

    # Связь с фото и его foreign key
    photo = orm.relation("Photo")
    photo_id = sqlalchemy.Column(sqlalchemy.Integer,
                                 sqlalchemy.ForeignKey("photos.id"), default=2)

    # Связь с сообщениями
    messages = orm.relation("Message", back_populates='chat')

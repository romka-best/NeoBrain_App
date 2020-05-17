# Импортируем нужные библиотеки
import datetime

import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from sqlalchemy_serializer import SerializerMixin

# добавляем ассоциативную таблицу с user и group
association_table = sqlalchemy.Table('group_association', SqlAlchemyBase.metadata,
                                     # Внешний ключ юзера
                                     sqlalchemy.Column('user', sqlalchemy.Integer,
                                                       sqlalchemy.ForeignKey('users.id')),
                                     # Внешний ключ группы
                                     sqlalchemy.Column('group', sqlalchemy.Integer,
                                                       sqlalchemy.ForeignKey('groups.id')),
                                     # false - не автор, true - автор
                                     sqlalchemy.Column('is_author', sqlalchemy.Boolean, default=False)
                                     )


def get_current_time() -> datetime:
    delta = datetime.timedelta(hours=3, minutes=0)
    return datetime.datetime.now(datetime.timezone.utc) + delta


class Group(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'groups'

    # Формат даты
    datetime_format = '%Y-%m-%d %H:%M:%S'
    # id группы
    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    # Название группы
    title = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    # описание группы
    description = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    # Дата создания поста в формате YYYY-MM-DD HH:MM:SS
    created_date = sqlalchemy.Column(sqlalchemy.DateTime,
                                     default=get_current_time)

    # Связь с пользователем и его foreign key
    user = orm.relation("User")
    user_id = sqlalchemy.Column(sqlalchemy.Integer,
                                sqlalchemy.ForeignKey("users.id"))
    # Связь с фото и его foreign key
    photo = orm.relation("Photo")
    photo_id = sqlalchemy.Column(sqlalchemy.Integer,
                                 sqlalchemy.ForeignKey("photos.id"), default=2)

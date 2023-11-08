# Импортируем нужные библиотеки
import datetime

import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from sqlalchemy_serializer import SerializerMixin

association_table = sqlalchemy.Table('music_association', SqlAlchemyBase.metadata,
                                     sqlalchemy.Column('user', sqlalchemy.Integer,
                                                       sqlalchemy.ForeignKey('users.id')),
                                     sqlalchemy.Column('music', sqlalchemy.Integer,
                                                       sqlalchemy.ForeignKey('music.id'))
                                     )


def get_current_time() -> datetime:
    delta = datetime.timedelta(hours=3, minutes=0)
    return datetime.datetime.now(datetime.timezone.utc) + delta


class Music(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'music'

    # Формат даты
    datetime_format = '%Y-%m-%d %H:%M'

    # id музыки
    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    # Название музыки
    title = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    # Исполнитель музыки
    author = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    # Длительность музыки в секундах
    duration = sqlalchemy.Column(sqlalchemy.Integer, nullable=False)
    # данные музыки типа BLOB
    data = sqlalchemy.Column(sqlalchemy.BLOB, nullable=False)

    # Дата создания музыки в формате YYYY-MM-DD HH:MM
    created_date = sqlalchemy.Column(sqlalchemy.DateTime,
                                     default=get_current_time)

    # Связь с фото и его foreign key
    photo = orm.relation("Photo")
    photo_id = sqlalchemy.Column(sqlalchemy.Integer,
                                 sqlalchemy.ForeignKey("photos.id"), default=10)

    def get_music(self):
        return encodebytes(music.data).decode()

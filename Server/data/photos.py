# Импортируем нужные библиотеки
import datetime

import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from sqlalchemy_serializer import SerializerMixin


def get_current_time() -> datetime:
    delta = datetime.timedelta(hours=3, minutes=0)
    return datetime.datetime.now(datetime.timezone.utc) + delta


class Photo(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'photos'

    # Формат даты
    datetime_format = '%Y-%m-%d %H:%M:%S'
    # id фото
    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    # описание фото
    about = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    # данные фото типа BLOB
    data = sqlalchemy.Column(sqlalchemy.BLOB, nullable=False)

    # Связь с Photo
    association = orm.relation("PhotoAssociation")

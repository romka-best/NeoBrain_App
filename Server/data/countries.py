import sqlalchemy
from sqlalchemy_serializer import SerializerMixin

from .db_session import SqlAlchemyBase


class Country(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'countries'

    # id страны
    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    # Название страны
    country_name = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    # Количество смертей
    deaths_count = sqlalchemy.Column(sqlalchemy.Integer, nullable=False)
    # Случаев заражения
    cases = sqlalchemy.Column(sqlalchemy.Integer, nullable=False)
    # Заразившихся недавно
    cases_today = sqlalchemy.Column(sqlalchemy.Integer, nullable=False)
    # Ссылка на флаг
    flag = sqlalchemy.Column(sqlalchemy.String, nullable=True)

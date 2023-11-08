# Импортируем нужные библиотеки
import sqlalchemy
from sqlalchemy import orm
from sqlalchemy_serializer import SerializerMixin

from .db_session import SqlAlchemyBase


class App(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'apps'

    # id приложения
    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    # Название приложения
    title = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    # Краткое описание приложения
    secondary_text = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    # Описание приложения
    description = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    # Ссылки на приложение
    link_android = sqlalchemy.Column(sqlalchemy.String)
    link_ios = sqlalchemy.Column(sqlalchemy.String)

    association = orm.relation("AppAssociation")

    # Связь с фото и его foreign key
    photo = orm.relation("Photo")
    photo_id = sqlalchemy.Column(sqlalchemy.Integer,
                                 sqlalchemy.ForeignKey("photos.id"), nullable=False)

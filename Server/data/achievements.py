# Импортируем нужные библиотеки
import sqlalchemy
from sqlalchemy import orm
from sqlalchemy_serializer import SerializerMixin

from .db_session import SqlAlchemyBase


class Achievement(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'achievements'

    # id достижения
    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    # Название достижения
    title = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    # Описание достижения
    description = sqlalchemy.Column(sqlalchemy.String, nullable=False)

    association = orm.relation("AchievementAssociation")

    # Связь с фото и его foreign key
    photo = orm.relation("Photo")
    photo_id = sqlalchemy.Column(sqlalchemy.Integer,
                                 sqlalchemy.ForeignKey("photos.id"), nullable=False)

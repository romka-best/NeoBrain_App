# Импортируем нужные библиотеки
import sqlalchemy
from sqlalchemy import orm

from .db_session import SqlAlchemyBase


class AppAssociation(SqlAlchemyBase):
    __tablename__ = 'app_association'
    child1 = orm.relation("User")
    child2 = orm.relation("App")

    user_id = sqlalchemy.Column(sqlalchemy.Integer,
                                sqlalchemy.ForeignKey("users.id"), primary_key=True)
    app_id = sqlalchemy.Column(sqlalchemy.Integer,
                               sqlalchemy.ForeignKey("apps.id"), primary_key=True)

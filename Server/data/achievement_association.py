# Импортируем нужные библиотеки
import sqlalchemy
from sqlalchemy import orm

from .db_session import SqlAlchemyBase


class AchievementAssociation(SqlAlchemyBase):
    __tablename__ = 'achievement_association'
    child1 = orm.relation("User")
    child2 = orm.relation("Achievement")

    user_id = sqlalchemy.Column(sqlalchemy.Integer,
                                sqlalchemy.ForeignKey("users.id"), primary_key=True)
    achievement_id = sqlalchemy.Column(sqlalchemy.Integer,
                                       sqlalchemy.ForeignKey("achievements.id"), primary_key=True)
    is_get = sqlalchemy.Column(sqlalchemy.Boolean, default=False)

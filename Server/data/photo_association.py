# Импортируем нужные библиотеки
import sqlalchemy
from sqlalchemy import orm

from .db_session import SqlAlchemyBase


class PhotoAssociation(SqlAlchemyBase):
    __tablename__ = 'photo_association'
    child1 = orm.relation("User")
    child2 = orm.relation("Photo")

    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    user_id = sqlalchemy.Column(sqlalchemy.Integer,
                                sqlalchemy.ForeignKey("users.id"))
    photo_id = sqlalchemy.Column(sqlalchemy.Integer,
                                 sqlalchemy.ForeignKey("photos.id"))
    is_avatar = sqlalchemy.Column(sqlalchemy.Boolean, default=False)

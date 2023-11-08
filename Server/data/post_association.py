# Импортируем нужные библиотеки
import sqlalchemy
from sqlalchemy import orm

from .db_session import SqlAlchemyBase


class PostAssociation(SqlAlchemyBase):
    __tablename__ = 'post_association'
    child1 = orm.relation("User")
    child2 = orm.relation("Post")

    user_id = sqlalchemy.Column(sqlalchemy.Integer,
                                sqlalchemy.ForeignKey("users.id"), primary_key=True)
    post_id = sqlalchemy.Column(sqlalchemy.Integer,
                                sqlalchemy.ForeignKey("posts.id"), primary_key=True)

    like_emoji = sqlalchemy.Column(sqlalchemy.Boolean, default=False)
    laughter_emoji = sqlalchemy.Column(sqlalchemy.Boolean, default=False)
    heart_emoji = sqlalchemy.Column(sqlalchemy.Boolean, default=False)
    disappointed_emoji = sqlalchemy.Column(sqlalchemy.Boolean, default=False)
    smile_emoji = sqlalchemy.Column(sqlalchemy.Boolean, default=False)
    angry_emoji = sqlalchemy.Column(sqlalchemy.Boolean, default=False)
    smile_with_heart_eyes = sqlalchemy.Column(sqlalchemy.Boolean, default=False)
    screaming_emoji = sqlalchemy.Column(sqlalchemy.Boolean, default=False)

    is_author = sqlalchemy.Column(sqlalchemy.Boolean, default=True)

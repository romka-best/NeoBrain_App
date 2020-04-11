# Импортируем нужные библиотеки
import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from sqlalchemy_serializer import SerializerMixin

association_table = sqlalchemy.Table('people_association', SqlAlchemyBase.metadata,
                                     sqlalchemy.Column('user', sqlalchemy.Integer,
                                                       sqlalchemy.ForeignKey('users.id')),
                                     sqlalchemy.Column('people', sqlalchemy.Integer,
                                                       sqlalchemy.ForeignKey('people.id'))
                                     )


class People(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'people'

    # id человека
    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    # user_id
    user = orm.relation("User")
    user_id = sqlalchemy.Column(sqlalchemy.Integer,
                                sqlalchemy.ForeignKey("users.id"))

# Импортируем нужные библиотеки
import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from sqlalchemy_serializer import SerializerMixin

# добавляем ассоциативную таблицу с user и people
association_table = sqlalchemy.Table('people_association', SqlAlchemyBase.metadata,
                                     sqlalchemy.Column('user', sqlalchemy.Integer,
                                                       sqlalchemy.ForeignKey('users.id')),
                                     sqlalchemy.Column('people', sqlalchemy.Integer,
                                                       sqlalchemy.ForeignKey('people.id'))
                                     )


# People - люди, на кого ты подписан
class People(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'people'

    # id человека
    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    # Отношение с User и его внешний ключ
    user = orm.relation("User")
    user_id = sqlalchemy.Column(sqlalchemy.Integer,
                                sqlalchemy.ForeignKey("users.id"))

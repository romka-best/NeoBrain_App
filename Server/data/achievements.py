# Импортируем нужные библиотеки
import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from sqlalchemy_serializer import SerializerMixin

# добавляем ассоциативную таблицу с user и achievement
association_table = sqlalchemy.Table('achievement_association', SqlAlchemyBase.metadata,
                                     # Внешний ключ юзера
                                     sqlalchemy.Column('user', sqlalchemy.Integer,
                                                       sqlalchemy.ForeignKey('users.id')),
                                     # Внешний ключ достижения
                                     sqlalchemy.Column('achievement', sqlalchemy.Integer,
                                                       sqlalchemy.ForeignKey('achievements.id')),
                                     # false - не получено, true - получено
                                     sqlalchemy.Column('is_get', sqlalchemy.Boolean, default=False)
                                     )


class Achievement(SqlAlchemyBase, SerializerMixin):
    __tablename__ = 'achievements'

    # id достижения
    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    # Название достижения
    title = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    # Описание достижения
    description = sqlalchemy.Column(sqlalchemy.String, nullable=False)

    # Связь с фото и его foreign key
    photo = orm.relation("Photo")
    photo_id = sqlalchemy.Column(sqlalchemy.Integer,
                                 sqlalchemy.ForeignKey("photos.id"), nullable=False)

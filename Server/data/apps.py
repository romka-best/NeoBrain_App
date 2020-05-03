# Импортируем нужные библиотеки
import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from sqlalchemy_serializer import SerializerMixin

# Добавляем ассоциативную таблицу с user и app
association_table = sqlalchemy.Table('app_association', SqlAlchemyBase.metadata,
                                     # Внешний ключ пользователя
                                     sqlalchemy.Column('user', sqlalchemy.Integer,
                                                       sqlalchemy.ForeignKey('users.id')),
                                     # Внешний ключ приложения
                                     sqlalchemy.Column('app', sqlalchemy.Integer,
                                                       sqlalchemy.ForeignKey('apps.id'))
                                     )


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

    # Связь с фото и его foreign key
    photo = orm.relation("Photo")
    photo_id = sqlalchemy.Column(sqlalchemy.Integer,
                                 sqlalchemy.ForeignKey("photos.id"), nullable=False)

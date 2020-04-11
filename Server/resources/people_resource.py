# Импортируем нужные библиотеки
from base64 import decodebytes

from flask import jsonify
from flask_restful import reqparse, abort, Resource

from data import db_session
from data.people import People
from data.photos import Photo
from data.users import User
from .users_resource import abort_if_user_not_found


# Основной ресурс для работы с Photo
class PeopleResource(Resource):
    def get(self, user_nickname):
        # Проверяем, есть ли пользователь
        abort_if_user_not_found(user_nickname)
        # Создаём сессию в БД и получаем чаты
        session = db_session.create_session()
        user = session.query(User).filter(User.nickname == user_nickname).first()
        people = user.people
        return jsonify({'people': [profile.to_dict(
            only=('id', 'user_id')) for profile in people]})

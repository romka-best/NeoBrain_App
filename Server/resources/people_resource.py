# Импортируем нужные библиотеки
from flask import jsonify
from flask_restful import Resource, reqparse

from data import db_session
from data.people import People
from data.users import User
from .users_resource import abort_if_user_not_found


# Основной ресурс для работы с People
class PeopleResource(Resource):
    def __init__(self):
        # Инициализируем parser, так как доступ к данным,
        # переданным в теле POST-запроса, осуществляется с помощью парсера аргументов
        self.parser = reqparse.RequestParser()
        # Id пользователя, на которого подписывается человек
        self.parser.add_argument('user_subscribe_id', required=True, type=int)

    def get(self, user_id):
        # Проверяем, есть ли пользователь
        abort_if_user_not_found(user_id)
        # Создаём сессию в БД и получаем чаты
        session = db_session.create_session()
        user = session.query(User).get(user_id)
        people = user.people
        return jsonify({'people': [profile.to_dict(
            only=('id', 'user_id')) for profile in people]})

    # Отписываемся. user_id - кто отписывается
    def delete(self, user_id):
        pass


# Ресурс для добавления в Люди(Подписчики)
class PeopleCreateResource(Resource):
    def __init__(self):
        # Инициализируем parser, так как доступ к данным,
        # переданным в теле POST-запроса, осуществляется с помощью парсера аргументов
        self.parser = reqparse.RequestParser()
        # Id пользователя, который добавляет человека в Люди (Подписывается на него)
        self.parser.add_argument('user_author_id', required=True, type=int)
        # Id пользователя, на которого подписывается человек
        self.parser.add_argument('user_subscribe_id', required=True, type=int)

    # Подписываемся на другого пользователя
    def post(self):
        # Получаем аргументы
        args = self.parser.parse_args()
        # Защита от дурака
        if args['user_author_id'] == args['user_subscribe']:
            return jsonify({'status': 400,
                            'text': "Bad request"})
        abort_if_user_not_found(args['user_author_id'])
        abort_if_user_not_found(args['user_subscribe_id'])
        # Создаём сессию в БД
        session = db_session.create_session()
        user = session.query(User).get(args['user_author_id'])
        # Подписываемся
        people = People(
            user_id=args['user_subscribe_id']
        )
        # Добавляем в БД подписку
        session.add(people)
        user.people.append(people)
        session.commit()
        return jsonify({'status': 201,
                        'text': f'{people.id} created'})

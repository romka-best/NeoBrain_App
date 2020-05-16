# Импортируем нужные библиотеки
from flask import jsonify
from flask_restful import Resource, reqparse

from data import db_session
from data.apps import App
from data.users import User
from .users_resource import abort_if_user_not_found


# Основной ресурс для работы с Achievement
class AppResource(Resource):
    def get(self, user_id):
        # Проверяем, есть ли пользователь
        abort_if_user_not_found(user_id)
        # Создаём сессию в БД и получаем людей(друзей)
        session = db_session.create_session()
        user = session.query(User).get(user_id)
        apps = user.apps
        return jsonify({'apps': [app.to_dict(
            only=('id', 'user_id')) for app in apps]})


class AppDeleteResource(Resource):
    # Удаляем приложение. user_id - кто удаляет, app_id - какое приложение удаляем
    def delete(self, user_id, app_id):
        # Проверяем, есть ли пользователи
        abort_if_user_not_found(user_id)
        # Создаём сессию в БД и получаем людей(друзей)
        session = db_session.create_session()
        user = session.query(User).get(user_id)
        list_apps_id = []
        while True:
            try:
                app = session.query(App).filter(App.user_id == user_id,
                                                App.id.notin_(list_apps_id)).first()
                list_apps_id.append(app.id)
                # Удаляем (Отписываемся)
                user.apps.remove(app)
                break
            except ValueError:
                continue
        session.commit()
        return jsonify({'status': 200,
                        'text': 'deleted'})


# Ресурс для добавления в Люди(Подписчики)
class AppCreateResource(Resource):
    def __init__(self):
        # Инициализируем parser, так как доступ к данным,
        # переданным в теле POST-запроса, осуществляется с помощью парсера аргументов
        self.parser = reqparse.RequestParser()
        # Id пользователя, который добавляет человека в Люди (Подписывается на него)
        self.parser.add_argument('user_id', required=True, type=int)
        # Id пользователя, на которого подписывается человек
        self.parser.add_argument('app_id', required=True, type=int)

    # Подписываемся на другого пользователя
    def post(self):
        # Получаем аргументы
        args = self.parser.parse_args()
        abort_if_user_not_found(args['user_id'])
        # Создаём сессию в БД
        session = db_session.create_session()
        user = session.query(User).get(args['user_id'])
        # Добавляем приложение
        app = session.query(App).get(args['app_id'])
        # Добавляем в БД подписку
        user.apps.append(app)
        session.commit()
        return jsonify({'status': 201,
                        'text': f'successful'})

    def get(self):
        session = db_session.create_session()
        apps = session.query(App).all()
        return jsonify({'apps': [app.to_dict(
            only=('id', 'title', 'secondary_text', 'description',
                  'link_android', 'link_ios', 'photo_id'))
            for app in apps]})

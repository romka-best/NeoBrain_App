# Импортируем нужные библиотеки
from flask import jsonify
from flask_login import login_required
from flask_restful import Resource, reqparse
from sqlalchemy.exc import IntegrityError

from data import db_session
from data.app_association import AppAssociation
from data.apps import App
from .users_resource import abort_if_user_not_found


# Основной ресурс для работы с App
class AppResource(Resource):

    decorators = [login_required]

    def get(self, user_id):
        # Проверяем, есть ли пользователь
        abort_if_user_not_found(user_id)
        # Создаём сессию в БД и получаем приложения
        session = db_session.create_session()
        app_association = session.query(AppAssociation).filter(AppAssociation.user_id == user_id).all()
        apps = {'apps': []}
        for app in app_association:
            cur_app = session.query(App).get(app.app_id)
            apps['apps'].append({"id": cur_app.id,
                                 "title": cur_app.title,
                                 "secondary_text": cur_app.secondary_text,
                                 "description": cur_app.description,
                                 "link_android": cur_app.link_android,
                                 "link_ios": cur_app.link_ios,
                                 "photo_id": cur_app.photo_id})
        return jsonify(apps)


class AppDeleteResource(Resource):

    decorators = [login_required]

    # Удаляем приложение. user_id - кто удаляет, app_id - какое приложение удаляем
    def delete(self, user_id, app_id):
        # Проверяем, есть ли пользователи
        abort_if_user_not_found(user_id)
        # Создаём сессию в БД и получаем людей(друзей)
        session = db_session.create_session()
        app = session.query(AppAssociation).filter(AppAssociation.user_id == user_id,
                                                   AppAssociation.app_id == app_id).first()
        session.delete(app)
        session.commit()
        return jsonify({'status': 200,
                        'text': 'deleted'})


# Ресурс для добавления в Люди(Подписчики)
class AppCreateResource(Resource):

    decorators = [login_required]

    def __init__(self):
        # Инициализируем parser, так как доступ к данным,
        # переданным в теле POST-запроса, осуществляется с помощью парсера аргументов
        self.parser = reqparse.RequestParser()
        # Id пользователя, который добавляет человека в Люди (Подписывается на него)
        self.parser.add_argument('user_id', required=True, type=int)
        # Id пользователя, на которого подписывается человек
        self.parser.add_argument('app_id', required=True, type=int)

    # Подписываемся на приложение
    def post(self):
        # Получаем аргументы
        args = self.parser.parse_args()
        abort_if_user_not_found(args['user_id'])
        # Создаём сессию в БД
        session = db_session.create_session()
        # Добавляем приложение
        try:
            app_association = AppAssociation()
            app_association.user_id = args['user_id']
            app_association.app_id = args['app_id']
        except IntegrityError:
            return jsonify({'status': 400,
                            'text': f'already exists'})
        session.add(app_association)
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


class AppSearchResource(Resource):

    decorators = [login_required]

    def get(self, app_name):
        if app_name.find("?") != -1:
            app_name = app_name[:app_name.find("?")].lower().strip()
        if not app_name:
            return jsonify({'status': 400,
                            'text': "Empty request"})
        # Создаём сессию в БД и находим пользователей
        session = db_session.create_session()
        apps = session.query(App).filter(App.title.like(f"%{app_name}%")).all()
        if not apps:
            apps = session.query(App).filter(App.secondary_text.like(f"%{app_name}%")).all()
        return jsonify({'apps': [app.to_dict(
            only=('id', 'title', 'secondary_text', 'description',
                  'link_android', 'link_ios', 'photo_id'))
            for app in apps]})

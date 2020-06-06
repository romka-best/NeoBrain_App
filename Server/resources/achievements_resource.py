# Импортируем нужные библиотеки
from flask import jsonify
from flask_restful import Resource, reqparse

from data import db_session
from data.achievement_association import AchievementAssociation
from data.achievements import Achievement
from data.users import User
from .users_resource import abort_if_user_not_found


# Основной ресурс для работы с Achievement
class AchievementUserResource(Resource):
    def __init__(self):
        # Инициализируем parser, так как доступ к данным,
        # переданным в теле POST-запроса, осуществляется с помощью парсера аргументов
        self.parser = reqparse.RequestParser()

        self.parser.add_argument('id', required=True, type=int)
        self.parser.add_argument('is_got', required=True, type=bool)

    def get(self, user_id):
        # Проверяем, есть ли пользователь
        abort_if_user_not_found(user_id)
        # Создаём сессию в БД и получаем достижения пользователя
        session = db_session.create_session()
        association = session.query(AchievementAssociation).filter(AchievementAssociation.user_id == user_id).all()
        print(association)
        if not association:
            for achievement in session.query(Achievement).all():
                cur_association = AchievementAssociation(
                    user_id=user_id,
                    achievement_id=achievement.id
                )
                session.add(cur_association)
                session.commit()
            association = session.query(AchievementAssociation).filter(AchievementAssociation.user_id == user_id).all()
        achievements = {'achievements': []}
        for achievement in association:
            cur_achievement = session.query(Achievement).get(achievement.achievement_id)
            achievements['achievements'].append({"id": cur_achievement.id,
                                                 "title": cur_achievement.title,
                                                 "description": cur_achievement.description,
                                                 "is_got": achievement.is_get,
                                                 "photo_id": cur_achievement.photo_id})
        return jsonify(achievements)

    def put(self, user_id):
        # Получаем аргументы
        args = self.parser.parse_args()
        # Создаём сессию в БД и получаем достижение
        session = db_session.create_session()
        # В зависимости от аргументов, изменяем достижение
        if args.get('id', None):
            achievement = session.query(AchievementAssociation).filter(
                AchievementAssociation.achievement_id == args['id'],
                AchievementAssociation.user_id == user_id).first()
            achievement.is_get = args['is_got']
        session.commit()
        return jsonify({'status': 200,
                        'text': 'edited'})


class AchievementResource(Resource):
    def get(self, achievement_id):
        # Создаём сессию в БД и получаем достижение
        session = db_session.create_session()
        achievement = session.query(Achievement).get(achievement_id)
        return jsonify({'achievement': achievement})


class AchievementCreateResource(Resource):
    def __init__(self):
        # Инициализируем parser, так как доступ к данным,
        # переданным в теле POST-запроса, осуществляется с помощью парсера аргументов
        self.parser = reqparse.RequestParser()
        # Id пользователя, который добавляет человека в Люди (Подписывается на него)
        self.parser.add_argument('user_id', required=True, type=int)
        # Id пользователя, на которого подписывается человек
        self.parser.add_argument('achievement_id', required=True, type=int)

    # Получаем достижение
    def post(self):
        # Получаем аргументы
        args = self.parser.parse_args()
        abort_if_user_not_found(args['user_id'])
        # Создаём сессию в БД
        session = db_session.create_session()
        user = session.query(User).get(args['user_id'])
        achievement = session.query(Achievement).get(args['achievement_id'])
        # Добавляем в БД
        achievement_association = AchievementAssociation()
        achievement_association.user_id = user.id
        achievement_association.achievement_id = achievement.id
        session.add(achievement_association)
        session.commit()
        return jsonify({'status': 201,
                        'text': f'successful'})

    def get(self):
        session = db_session.create_session()
        achievements = session.query(Achievement).all()
        return jsonify({'achievements': [achievement.to_dict(
            only=('id', 'title', 'description', 'photo_id'))
            for achievement in achievements]})

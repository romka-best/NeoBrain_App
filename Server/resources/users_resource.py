# Импортируем библиотеки
import datetime
from base64 import decodebytes

from flask import jsonify
from flask_restful import reqparse, abort, Resource

from data import db_session
from data.photos import Photo
from data.users import User


# Если user не найден, то приходит ответа сервера
def abort_if_user_not_found(user_id):
    session = db_session.create_session()
    user = session.query(User).get(user_id)
    if not user:
        abort(404, message=f"User {user_id} not found")


# Основной ресурс для работы с User
class UserResource(Resource):
    def __init__(self):
        # Инициализируем parser, так как доступ к данным,
        # переданным в теле POST-запроса, осуществляется с помощью парсера аргументов
        self.parser = reqparse.RequestParser()
        # Имя пользователя
        self.parser.add_argument('name', required=False, type=str)
        # Фамилия пользователя
        self.parser.add_argument('surname', required=False, type=str)
        # Никнейм пользователя
        self.parser.add_argument('nickname', required=False, type=str)
        # Номер телефона пользователя в формате 7XXXXXXXXXX
        self.parser.add_argument('number', required=False, type=str)
        # Емайл пользователя в формате email@name.com
        self.parser.add_argument('email', required=False, type=str)
        # Дата создания пользователя
        self.parser.add_argument('created_date', required=False)
        # Дата модфикации пользователя
        self.parser.add_argument('modified_date', required=False)
        # Хэшированный пароль
        self.parser.add_argument('hashed_password', required=False, type=str)
        # Закрыт ли аккаунт
        self.parser.add_argument('is_closed', required=False, type=bool)
        # Статус пользователя
        self.parser.add_argument('about', required=False, type=str)
        # Дата рождения пользователя в формате YYYY-MM-DD
        self.parser.add_argument('birthday', required=False)
        # Возраст пользователя
        self.parser.add_argument('age', required=False, type=int)
        # Могут ли смотреть его аудио
        self.parser.add_argument('can_see_audio', required=False, type=bool)
        # Могут ли смотреть его группы
        self.parser.add_argument('can_see_groups', required=False, type=bool)
        # Смогут ли смотреть его видео
        self.parser.add_argument('can_see_videos', required=False, type=bool)
        # Могут ли ему писать сообщения
        self.parser.add_argument('can_write_message', required=False, type=bool)
        # Город пользователя
        self.parser.add_argument('city', required=False, type=str)
        # Республика(Область) пользователя
        self.parser.add_argument('republic', required=False, type=str)
        # Страна пользователя
        self.parser.add_argument('country', required=False, type=str)
        # Образование пользователя
        self.parser.add_argument('education', required=False, type=str)
        # Количество подписанных на пользователя
        self.parser.add_argument('followers_count', required=False, type=int)
        # Количество подписок на других пользователей
        self.parser.add_argument('subscriptions_count', required=False, type=int)
        # Статус пользователя 0 - Offline, 1 - Online
        self.parser.add_argument('status', required=False, type=int)
        # Последний вход пользователя в формате YYYY-MM-DD HH:MM:SS
        self.parser.add_argument('last_seen', required=False, type=str)
        # Фото пользователя
        self.parser.add_argument('photo', required=False, type=str)
        # id Фото пользователя
        self.parser.add_argument('photo_id', required=False, type=int)

    # @login_required
    # Получаем пользователя по егу nickname
    def get(self, user_id):
        if str(user_id).find("?") != -1:
            user_id = int(user_id[:str(user_id).find("?")].strip())
        # Проверяем, есть ли пользователь
        abort_if_user_not_found(user_id)
        # Создаём сессию в БД и получаем пользователя
        session = db_session.create_session()
        user = session.query(User).get(user_id)
        return jsonify({'user': user.to_dict(
            only=('id', 'name', 'surname', 'nickname', 'number',
                  'created_date', 'modified_date', 'is_closed', 'email', 'about',
                  'birthday', 'age', 'gender', 'can_see_audio', 'can_see_groups', 'can_see_videos',
                  'can_write_message', 'city', 'republic', 'country', 'education', 'status',
                  'last_seen', 'followers_count', 'subscriptions_count', 'photo_id'))})

    # @login_required
    # Изменяем пользователя по его nickname
    def put(self, user_id):
        if str(user_id).find("?") != -1:
            user_id = int(user_id[:str(user_id).find("?")].strip())
        # Проверяем, есть ли пользователь
        abort_if_user_not_found(user_id)
        # Получаем аргументы
        args = self.parser.parse_args()
        # Если нет аргументов, передаём статус 400
        if not args:
            return jsonify({'status': 400,
                            'text': "Empty request"})
        # Создаём сессию в БД и получаем пользователя
        session = db_session.create_session()
        user = session.query(User).get(user_id)
        # В зависимости от аргументов, меняем пользователя
        if args['name']:
            user.name = args['name'].title()
        if args['surname']:
            user.surname = args['surname'].title()
        if args['nickname']:
            user.nickname = args['nickname']
        if args['number']:
            user.number = args['number'].lower()
        if args['email']:
            user.email = args['email'].lower()
        if args['hashed_password']:
            user.set_password(args['hashed_password'])
        if args['is_closed']:
            user.is_closed = args['is_closed']
        if args['about']:
            user.about = args['about']
        if args['birthday']:
            user.birthday = args['birthday']
        if args['age']:
            user.age = args['age']
        if args['can_see_audio']:
            user.can_see_audio = args['can_see_audio']
        if args['can_see_groups']:
            user.can_see_groups = args['can_see_groups']
        if args['can_see_videos']:
            user.can_see_videos = args['can_see_videos']
        if args['can_write_message']:
            user.can_write_message = args['can_write_message']
        if args['city']:
            user.city = args['city']
        if args['republic']:
            user.republic = args['republic']
        if args['country']:
            user.country = args['country']
        if args['education']:
            user.education = args['education']
        if args['followers_count']:
            user.followers_count = args['followers_count']
        if args['subscriptions_count']:
            user.subscriptions_count = args['subscriptions_count']
        if args['status']:
            user.status = args['status']
        if args['last_seen']:
            user.last_seen = args['last_seen']
        if args['photo']:
            if user.photo_id != 2:
                photo = session.query(Photo).filter(Photo.id == user.photo_id).first()
                photo.data = decodebytes(args['photo'].encode())
            else:
                # Создаём фото
                photo = Photo(
                    data=decodebytes(args['photo'].encode())
                )
                session.add(photo)
                session.commit()
                user.photo_id = photo.id
        if args['photo_id']:
            user.photo_id = args['photo_id']
        # Обновляем дату модификации пользователя
        user.modified_date = datetime.datetime.now()
        session.commit()
        return jsonify({'status': 200,
                        'text': 'edited'})

    # Удаляем пользователя по егу nickname
    def delete(self, user_id):
        if str(user_id).find("?") != -1:
            user_id = int(user_id[:str(user_id).find("?")].strip())
        # Ищем пользователя
        abort_if_user_not_found(user_id)
        # Создаём сессию и получаем user
        session = db_session.create_session()
        user = session.query(User).get(user_id)
        # Удаляем пользователя из БД
        session.delete(user)
        session.commit()
        return jsonify({'status': 200,
                        'text': 'deleted'})


class UserSearchResource(Resource):
    # Получаем пользователя по егу nickname
    def get(self, user_name_surname):
        if user_name_surname.find("?") != -1:
            user_name_surname = user_name_surname[:user_name_surname.find("?")].strip()
        user_name_surname_list = user_name_surname.split("&")
        if len(user_name_surname_list) == 2:
            param1, param2 = map(str.title, user_name_surname_list)
        elif len(user_name_surname_list) == 1:
            param1, param2 = user_name_surname_list[0].title(), user_name_surname_list[0].title()
        else:
            return jsonify({'status': 400,
                            'text': "Empty request"})
        # Создаём сессию в БД и находим пользователя
        session = db_session.create_session()
        users = session.query(User).filter((User.name.like(f"%{param1}%")) | (User.name.like(f"%{param2}%")) |
                                           (User.surname.like(f"%{param1}%")) | (
                                               User.surname.like(f"%{param2}%"))).all()
        return jsonify({'users': [user.to_dict(
            only=('id', 'name', 'surname', 'nickname', 'number',
                  'created_date', 'modified_date', 'is_closed', 'email', 'about',
                  'birthday', 'age', 'gender', 'can_see_audio', 'can_see_groups', 'can_see_videos',
                  'can_write_message', 'city', 'republic', 'country', 'education', 'status',
                  'last_seen', 'followers_count', 'subscriptions_count', 'photo_id')) for user in users]})


# Ресурс входа пользователя
class UserLoginResource(Resource):
    def __init__(self):
        # Инициализируем parser, так как доступ к данным,
        # переданным в теле POST-запроса, осуществляется с помощью парсера аргументов
        self.parser = reqparse.RequestParser()
        # Никнейм пользователя
        self.parser.add_argument('nickname', required=False)
        # Номер телефона пользователя в формате 7XXXXXXXXXX
        self.parser.add_argument('number', required=False)
        # E-mail пользователя в формате name@domen.com
        self.parser.add_argument('email', required=False)
        # Пароль пользователя
        self.parser.add_argument('hashed_password', required=True)

    # Вход пользователя
    def post(self):
        # Получаем аргументы
        args = self.parser.parse_args()
        if not args:
            return jsonify({'status': 400,
                            'text': "Empty request"})
        elif not any(key in args for key in ['number', 'nickname', 'email']):
            return jsonify({'status': 400,
                            'text': "Bad request"})
        # Создаём сессиб и берём пользователя
        session = db_session.create_session()
        if args['number']:
            user = session.query(User).filter(User.number == args['number'].lower()).first()
        elif args['nickname']:
            user = session.query(User).filter(User.nickname == args['nickname']).first()
        elif args['email']:
            user = session.query(User).filter(User.email == args['email'].lower()).first()
        else:
            return jsonify({'status': 400,
                            'text': 'Bad request'})
        # Проверяем корректен ли пароль пользователя
        if user and user.check_password(args['hashed_password']):
            return jsonify({'status': 200,
                            'text': f'Login {user.id} allowed'})
        elif user and not user.check_password(args['hashed_password']):
            return jsonify({'status': 449,
                            'text': 'Password is not correct'})
        return jsonify({'status': 404,
                        'text': 'User is not defined'})


# Ресурс для создания пользователя, а также для получения всех пользователей
class UsersListResource(Resource):
    def __init__(self):
        # Инициализируем parser, так как доступ к данным,
        # переданным в теле POST-запроса, осуществляется с помощью парсера аргументов
        self.parser = reqparse.RequestParser()
        # Имя пользователя
        self.parser.add_argument('name', required=True, type=str)
        # Фамилия пользователя
        self.parser.add_argument('surname', required=True, type=str)
        # Никнейм пользователя
        self.parser.add_argument('nickname', required=True, type=str)
        # Номер телефона пользователя в формате 7XXXXXXXXXX
        self.parser.add_argument('number', required=False, type=str)
        # Емайл пользователя в формате email@name.com
        self.parser.add_argument('email', required=False, type=str)
        # Дата создания пользователя
        self.parser.add_argument('created_date', required=False)
        # Дата модфикации пользователя
        self.parser.add_argument('modified_date', required=False)
        # Хэшированный пароль
        self.parser.add_argument('hashed_password', required=True, type=str)
        # Закрыт ли аккаунт
        self.parser.add_argument('is_closed', required=False, type=bool)
        # Статус пользователя
        self.parser.add_argument('about', required=False, type=str)
        # Дата рождения пользователя в формате YYYY-MM-DD
        self.parser.add_argument('birthday', required=False)
        # Возраст пользователя
        self.parser.add_argument('age', required=False, type=int)
        # Могут ли смотреть его аудио
        self.parser.add_argument('can_see_audio', required=False, type=bool)
        # Могут ли смотреть его группы
        self.parser.add_argument('can_see_groups', required=False, type=bool)
        # Смогут ли смотреть его видео
        self.parser.add_argument('can_see_videos', required=False, type=bool)
        # Могут ли ему писать сообщения
        self.parser.add_argument('can_write_message', required=False, type=bool)
        # Город пользователя
        self.parser.add_argument('city', required=False, type=str)
        # Республика(Область) пользователя
        self.parser.add_argument('republic', required=False, type=str)
        # Страна пользователя
        self.parser.add_argument('country', required=False, type=str)
        # Образование пользователя
        self.parser.add_argument('education', required=False, type=str)
        # Количество подписанных на пользователя
        self.parser.add_argument('followers_count', required=False, type=int)
        # Количество подписок на других пользователей
        self.parser.add_argument('subscriptions_count', required=False, type=int)
        # Статус пользователя 0 - Offline, 1 - Online
        self.parser.add_argument('status', required=False, type=int)
        # Последний вход пользователя в формате YYYY-MM-DD HH:MM:SS
        self.parser.add_argument('last_seen', required=False, type=str)

    # @login_required
    # Получаем всех пользователей
    def get(self):
        session = db_session.create_session()
        users = session.query(User).all()
        return jsonify({'users': [user.to_dict(
            only=('id', 'name', 'surname', 'nickname', 'number',
                  'created_date', 'modified_date', 'is_closed', 'email', 'about',
                  'birthday', 'age', 'gender', 'can_see_audio', 'can_see_groups', 'can_see_videos',
                  'can_write_message', 'city', 'republic', 'country', 'education', 'status',
                  'last_seen', 'followers_count', 'subscriptions_count', 'photo_id')) for user in users]})

    # Создаём пользователя
    def post(self):
        # Получаем аргументы и создаём сессию в БД
        args = self.parser.parse_args()
        session = db_session.create_session()
        if not args:
            return jsonify({'status': 400,
                            'text': "Empty request"})
        # Обязательно либо номер, либо почта
        if not args['number'] and not args['email']:
            return jsonify({'status': 400,
                            'text': "Bad request"})
        # Проверяем занят ли никнейм
        elif session.query(User).filter(
                User.nickname == args['nickname']).first():
            return jsonify({'status': 449,
                            'text': 'Nickname already exists'})
        # Создаём пользователя
        user = User(
            name=args['name'].title(),
            surname=args['surname'].title(),
            nickname=args['nickname'],
            photo_id=2
        )
        user.set_password(args['hashed_password'])
        if args['number']:
            # Проверяем занят ли номер телефона
            if session.query(User).filter(User.number == args['number'].lower()).first():
                return jsonify({'status': 449,
                                'text': 'Phone number already exists'})
            user.number = args['number'].lower()
        if args['email']:
            # Проверяем занята ли почта
            if session.query(User).filter(User.email == args['email'].lower()).first():
                return jsonify({'status': 449,
                                'text': 'Email already exists'})
            user.email = args['email'].lower()
        # Добавляем пользователя
        session.add(user)
        session.commit()
        return jsonify({'status': 201,
                        'text': 'created'})

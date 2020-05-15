# Импортируем библиотеки
import datetime
from base64 import decodebytes

from flask import jsonify
from flask_restful import reqparse, abort, Resource

from data import db_session
from data.chats import Chat
from data.photos import Photo
from data.posts import Post
from data.users import User, correct_password


# Если user не найден, то приходит ответа сервера
def abort_if_user_not_found(user_id):
    session = db_session.create_session()
    user = session.query(User).get(user_id)
    if not user:
        abort(404, message=f"User {user_id} not found")


def get_current_time() -> datetime:
    delta = datetime.timedelta(hours=3, minutes=0)
    return datetime.datetime.now(datetime.timezone.utc) + delta


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
    # Получаем пользователя по егу id
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

    # Изменяем пользователя по его id
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
        if args.get('name', None):
            user.name = args['name'].title()
        if args.get('surname', None):
            user.surname = args['surname'].title()
        if args.get('nickname', None):
            user.nickname = args['nickname']
        if args.get('number', None):
            user.number = args['number'].lower()
        if args.get('email', None):
            user.email = args['email'].lower()
        if args.get('hashed_password', None):
            user.set_password(args['hashed_password'])
        if args.get('is_closed', None) is not None:
            user.is_closed = args['is_closed']
        if args.get('about', None):
            user.about = args['about']
        if args.get('birthday', None):
            user.birthday = args['birthday']
        if args.get('age', None):
            user.age = args['age']
        if args.get('can_see_audio', None) is not None:
            user.can_see_audio = args['can_see_audio']
        if args.get('can_see_groups', None) is not None:
            user.can_see_groups = args['can_see_groups']
        if args.get('can_see_videos', None) is not None:
            user.can_see_videos = args['can_see_videos']
        if args.get('can_write_message', None) is not None:
            user.can_write_message = args['can_write_message']
        if args.get('city', None):
            user.city = args['city']
        if args.get('republic', None):
            user.republic = args['republic']
        if args.get('country', None):
            user.country = args['country']
        if args.get('education', None):
            user.education = args['education']
        if args.get('followers_count', None) is not None:
            user.followers_count = args['followers_count']
        if args.get('subscriptions_count', None) is not None:
            user.subscriptions_count = args['subscriptions_count']
        if args.get('status', None) is not None:
            user.status = args['status']
            user.last_seen = get_current_time()
        if args.get('last_seen', None):
            user.last_seen = args['last_seen']
        if args.get('photo', None):
            if user.photo_id != 2:
                # Изменяем фото
                photo = session.query(Photo).filter(Photo.id == user.photo_id).first()
                photo.data = decodebytes(args['photo'].encode())
            else:
                # Создаём фото
                photo = Photo(
                    data=decodebytes(args['photo'].encode())
                )
                cur_photo_id = user.photo_id
                session.add(photo)
                session.commit()
                posts = session.query(Post).filter(Post.photo_id == cur_photo_id).all()
                if posts:
                    for post in posts:
                        post.photo_id = photo.id
                chats = session.query(Chat).filter(Chat.photo_id == cur_photo_id).all()
                if chats:
                    for chat in chats:
                        chat.photo_id = photo.id
                user.photo_id = photo.id
        if args.get('photo_id', None):
            user.photo_id = args['photo_id']
        # Обновляем дату модификации пользователя
        user.modified_date = get_current_time()
        session.commit()
        return jsonify({'status': 200,
                        'text': 'edited',
                        'message': str(user.photo_id)})

    # Удаляем пользователя по егу id
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


class UsersSearchResource(Resource):
    # Получаем пользователей по имени и фамилии
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
        # Создаём сессию в БД и находим пользователей
        session = db_session.create_session()
        users = session.query(User).filter((User.name.like(f"%{param1}%")) |
                                           (User.name.like(f"%{param2}%")) |
                                           (User.surname.like(f"%{param1}%")) |
                                           (User.surname.like(f"%{param2}%"))).all()
        return jsonify({'users': [user.to_dict(
            only=('id', 'name', 'surname', 'nickname', 'number',
                  'created_date', 'modified_date', 'is_closed', 'email', 'about',
                  'birthday', 'age', 'gender', 'can_see_audio', 'can_see_groups', 'can_see_videos',
                  'can_write_message', 'city', 'republic', 'country', 'education', 'status',
                  'last_seen', 'followers_count', 'subscriptions_count', 'photo_id'))
            for user in users]})


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
        # Создаём сессию и берём пользователя
        session = db_session.create_session()
        if args.get('number', None):
            user = session.query(User).filter(User.number == args['number'].lower()).first()
        elif args.get('nickname', None):
            user = session.query(User).filter(User.nickname == args['nickname']).first()
        elif args.get('email', None):
            user = session.query(User).filter(User.email == args['email'].lower()).first()
        else:
            return jsonify({'status': 400,
                            'text': 'Bad request'})
        # Проверяем корректен ли пароль пользователя
        if user and user.check_password(args.get('hashed_password', None)):
            return jsonify({'status': 200,
                            'text': f'Login {user.id} allowed'})
        elif user and not user.check_password(args.get('hashed_password', None)):
            return jsonify({'status': 449,
                            'text': 'Password is not correct'})
        return jsonify({'status': 404,
                        'text': 'Password or login is not correct'})


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

    # Получаем всех пользователей
    def get(self):
        session = db_session.create_session()
        users = session.query(User).all()
        return jsonify({'users': [user.to_dict(
            only=('id', 'name', 'surname', 'nickname', 'number',
                  'created_date', 'modified_date', 'is_closed', 'email', 'about',
                  'birthday', 'age', 'gender', 'can_see_audio', 'can_see_groups',
                  'can_see_videos', 'can_write_message', 'city', 'republic',
                  'country', 'education', 'status', 'last_seen',
                  'followers_count', 'subscriptions_count', 'photo_id'))
            for user in users]})

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
        # Проверяем корректность пароля
        if correct_password(args['hashed_password'])[0] == "WRONG":
            return jsonify({'status': 449,
                            'text': 'Password is not correct'})
        # Создаём пользователя
        # TODO Проверка email
        user = User(
            name=args['name'].title(),
            surname=args['surname'].title(),
            nickname=args['nickname'],
            photo_id=2
        )
        user.set_password(args['hashed_password'])
        if args.get('number', None):
            # Проверяем занят ли номер телефона
            if session.query(User).filter(User.number == args['number'].lower()).first():
                return jsonify({'status': 449,
                                'text': 'Phone number already exists'})
            user.number = args['number'].lower()
        if args.get('email', None):
            # Проверяем занята ли почта
            if session.query(User).filter(User.email == args['email'].lower()).first():
                return jsonify({'status': 449,
                                'text': 'Email already exists'})
            user.email = args['email'].lower()
        # Добавляем пользователя
        session.add(user)
        session.commit()
        return jsonify({'status': 201,
                        'text': f'User {user.id} created'})

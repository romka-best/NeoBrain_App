# Импортируем нужные библиотеки
import logging
from base64 import decodebytes
import datetime

from flask import jsonify
from flask_restful import reqparse, abort, Resource

from auth import token_auth
from data import db_session
from data.chats import Chat
from data.photos import Photo
from data.users import User
from .users_resource import abort_if_user_not_found


# Если чат не найден, то приходит ответа сервера
def abort_if_chat_not_found(chat_id):
    session = db_session.create_session()
    chat = session.query(Chat).get(chat_id)
    if not chat:
        logging.getLogger("NeoBrain").warning(f"Chat {chat_id} not found")
        abort(404, message=f"Chat {chat_id} not found")


def get_current_time() -> datetime:
    delta = datetime.timedelta(hours=3, minutes=0)
    return datetime.datetime.now(datetime.timezone.utc) + delta


#  Основной ресурс чата
class ChatResource(Resource):
    def __init__(self):
        # Инициализируем parser, так как доступ к данным,
        # переданным в теле POST-запроса, осуществляется с помощью парсера аргументов
        self.parser = reqparse.RequestParser()
        # Имя чата
        self.parser.add_argument('name', required=False, type=str)
        # Тип чата: 0 - UserWithUser, 1 - UserWithUsers, 2 - Chanel, 3 - Бот
        self.parser.add_argument('type_of_chat', required=False, type=int)
        # 0 - Offline, >=1 Количество user-ов онлайн в чате
        self.parser.add_argument('status', required=False, type=int)
        # Время последнего отправленного сообщения в формате YYYY-MM-DD HH:MM:SS
        self.parser.add_argument('last_time_message', required=False)
        # Последнее сообщение в чате
        self.parser.add_argument('last_message', required=False, type=str)
        # Количество новых сообщений
        self.parser.add_argument('count_new_messages', required=False, type=int)
        # Количество сообщений
        self.parser.add_argument('count_messages', required=False, type=int)
        # Дата создания чата
        self.parser.add_argument('created_date', required=False)
        # Чат user-a
        self.parser.add_argument('user_id', required=False, type=int)
        # Фото чата
        self.parser.add_argument('photo_id', required=False, type=int)

    # Получаем чат по его id
    @token_auth.login_required
    def get(self, chat_id):
        # Создаём сессию и получаем чат
        session = db_session.create_session()
        chat = session.query(Chat).get(chat_id)
        logging.getLogger("NeoBrain").debug(f"Chat {chat_id} returned")
        return jsonify({'chat': chat.to_dict(
            only=('name', 'type_of_chat', 'status', 'last_time_message',
                  'last_message', 'count_new_messages', 'count_messages',
                  'created_date', 'photo_id'))})

    # Изменяем чат по его id
    @token_auth.login_required
    def put(self, chat_id):
        # Получаем аргументы
        args = self.parser.parse_args()
        # Если нет аргументов, отправляем ошибку 400 с текстом 'Empty request'
        if not args:
            logging.getLogger("NeoBrain").debug("Empty PUT chat request")
            return jsonify({'status': 400,
                            'text': "Empty request"})
        # Создаём сессию в БД и получаем чат
        session = db_session.create_session()
        chat = session.query(Chat).get(chat_id)
        # В зависимости от аргументов, изменяем чат
        if args.get('name', None):
            chat.name = args['name']
        if args.get('type_of_chat', None) is not None:
            chat.type_of_chat = args['type_of_chat']
        if args.get('status', None) is not None:
            chat.status = args['status']
        if args.get('last_time_message', None):
            chat.last_time_message = args['last_time_message']
        if args.get('last_message'):
            chat.last_message = args['last_message']
            chat.last_time_message = get_current_time()
        if args.get('count_new_messages', None) is not None:
            chat.count_new_messages = args['count_new_messages']
        if args.get('count_messages', None) is not None:
            chat.count_messages = args['count_messages']
        if args.get('photo', None) is not None:
            if chat.photo_id != 2:
                photo = session.query(Photo).filter(Photo.id == chat.photo_id).first()
                photo.data = decodebytes(args['photo'].encode())
            else:
                # Создаём фото
                photo = Photo(
                    data=decodebytes(args['photo'].encode())
                )
                session.add(photo)
                session.commit()
                chat.photo_id = photo.id
        if args.get('photo_id', None):
            chat.photo_id = args['photo_id']
        chat.modified_date = get_current_time()
        session.commit()
        logging.getLogger("NeoBrain").debug(f"Chat {chat_id} edited!")
        return jsonify({'status': 200,
                        'text': 'edited'})

    # Удаляем чат по его id
    @token_auth.login_required
    def delete(self, chat_id):
        # Создаём сессию в БД и получаем чат, а затем его удаляем
        session = db_session.create_session()
        chat = session.query(Chat).get(chat_id)
        session.delete(chat)
        session.commit()
        logging.getLogger("NeoBrain").info(f"Chat {chat_id} deleted")
        return jsonify({'status': 200,
                        'text': 'deleted'})


# Ресурс для получения чатов
class ChatsListResource(Resource):
    # Получаем чаты user-a по его id
    @token_auth.login_required
    def get(self, user_id):
        # Проверяем, есть ли пользователь
        abort_if_user_not_found(user_id)
        # Создаём сессию в БД и получаем чаты
        session = db_session.create_session()
        user = session.query(User).get(user_id)
        chats = user.chats
        logging.getLogger("NeoBrain").debug(f"Chats for user {user_id} returned")
        return jsonify({'chats': [chat.to_dict(
            only=('id', 'name', 'type_of_chat', 'status', 'last_time_message',
                  'last_message', 'count_new_messages', 'count_messages',
                  'created_date', 'photo_id')) for chat in chats]})


class ChatUsersResource(Resource):
    # Получаем пользователей с которым переписывался user_id
    @token_auth.login_required
    def get(self, user_id):
        # Проверяем, есть ли пользователь
        abort_if_user_not_found(user_id)
        # Создаём сессию в БД и получаем пользователей
        session = db_session.create_session()
        user = session.query(User).get(user_id)
        chats = user.chats
        users = []
        for chat in chats:
            # получаем чат
            chat = session.query(Chat).get(chat.id)
            cur_users = chat.users
            for cur_user in cur_users:
                if cur_user.id != user.id:
                    users.append(cur_user)
        logging.getLogger("NeoBrain").debug(f"Interlocutors of user {user_id} returned")
        return jsonify({'users': [user.to_dict(
            only=('id', 'name', 'surname', 'nickname', 'number',
                  'created_date', 'modified_date', 'is_closed', 'email', 'about',
                  'birthday', 'age', 'gender', 'can_see_audio', 'can_see_groups',
                  'can_see_videos', 'can_write_message', 'city', 'republic',
                  'country', 'education', 'status', 'last_seen',
                  'followers_count', 'subscriptions_count', 'photo_id'))
            for user in users]})


class ChatTwoUsersResource(Resource):
    # Возвращаем chat_id двух пользователей
    @token_auth.login_required
    def get(self, user_id1, user_id2):
        # Проверяем, есть ли пользователм
        abort_if_user_not_found(user_id1)
        abort_if_user_not_found(user_id2)
        # Создаём сессию в БД и получаем пользователей
        session = db_session.create_session()
        user1 = session.query(User).get(user_id1)
        chats = user1.chats
        for chat in chats:
            cur_chat = session.query(Chat).get(chat.id)
            cur_users = cur_chat.users
            for cur_user in cur_users:
                if cur_user.id == user_id2:
                    logging.getLogger("NeoBrain")\
                        .debug(f"Chat for users {user_id1} and {user_id2} returned")
                    return jsonify({'chat': cur_chat.to_dict(
                        only=('id', 'name', 'type_of_chat', 'status', 'last_time_message',
                              'last_message', 'count_new_messages', 'count_messages',
                              'created_date', 'photo_id'))})
        else:
            logging.getLogger("NeoBrain")\
                .debug(f"Chat for users {user_id1} and {user_id2} not found")
            return jsonify({'status': 404,
                            'text': 'Not found'})


class ChatFindUsersResource(Resource):
    # Получаем юзеров по id чата
    @token_auth.login_required
    def get(self, chat_id):
        # Создаём сессию и получаем чат
        session = db_session.create_session()
        chat = session.query(Chat).get(chat_id)
        users = chat.users
        logging.getLogger("NeoBrain").debug(f"Users of chat {chat_id} returned")
        return jsonify({'users': [user.id for user in users]})


class ChatCreateResource(Resource):
    def __init__(self):
        # Инициализируем parser, так как доступ к данным,
        # переданным в теле POST-запроса, осуществляется с помощью парсера аргументов
        self.parser = reqparse.RequestParser()
        # Id user-а
        self.parser.add_argument('user_author_id', required=True, type=int)
        # Id 2-ого user-a
        self.parser.add_argument('user_other_id', required=True, type=int)
        # Имя чата
        self.parser.add_argument('name', required=False, type=str)
        # Тип чата: 0 - UserWithUser, 1 - UserWithUsers, 2 - Chanel, 3 - Бот
        self.parser.add_argument('type_of_chat', required=True, type=int)
        # 0 - Offline, >=1 Количество user-ов онлайн в чате
        self.parser.add_argument('status', required=False, type=int)
        # Время последнего отправленного сообщения в формате YYYY-MM-DD HH:MM:SS
        self.parser.add_argument('last_time_message', required=False)
        # Последнее сообщение в чате
        self.parser.add_argument('last_message', required=True, type=str)
        # Количество новых сообщений
        self.parser.add_argument('count_new_messages', required=False, type=int)
        # Количество сообщений
        self.parser.add_argument('count_messages', required=False, type=int)
        # Дата создания чата
        self.parser.add_argument('created_date', required=False)
        # Чат user-a
        self.parser.add_argument('user_nickname', required=False, type=str)
        # Фото чата
        self.parser.add_argument('photo_id', required=False, type=int)

    # Создаём чат
    @token_auth.login_required
    def post(self):
        # Получаем аргументы
        args = self.parser.parse_args()
        # Создаём сессию в БД
        session = db_session.create_session()
        abort_if_user_not_found(args['user_author_id'])
        abort_if_user_not_found(args['user_other_id'])
        user = session.query(User).get(args['user_author_id'])
        user2 = session.query(User).get(args['user_other_id'])
        # Создаём чат
        chat = Chat(
            type_of_chat=args['type_of_chat'],
            last_message=args['last_message']
        )
        # В зависимости от аргументов добавляем в чат новые
        if args.get('name', None):
            chat.name = args['name']
        if args.get('photo_id', None):
            chat.photo_id = args['photo_id']
        if args.get('last_time_message', None):
            chat.last_time_message = args['last_time_message']
        if args.get('status', None) is not None:
            chat.status = args['status']
        if args.get('count_new_messages', None) is not None:
            chat.count_new_messages = args['count_new_messages']
        if args.get('count_messages', None) is not None:
            chat.count_messages = args['count_messages']
        # Добавляем в БД чат
        session.add(chat)
        user.chats.append(chat)
        user2.chats.append(chat)
        session.commit()
        logging.getLogger("NeoBrain").debug(f"Chat {chat.id} created!")
        return jsonify({'status': 201,
                        'text': f'{chat.id} created'})

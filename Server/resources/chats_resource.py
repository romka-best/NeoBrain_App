# Импортируем нужные библиотеки
from flask import jsonify
from flask_restful import reqparse, abort, Resource

from data import db_session
from data.chats import Chat
from data.photos import Photo
from data.users import User
from .users_resource import abort_if_user_not_found


# Если чат не найден, то приходит ответа сервера
def abort_if_chat_not_found(chat_id):
    session = db_session.create_session()
    chat = session.query(Chat).filter(Chat.id == chat_id).first()
    if not chat:
        abort(404, message=f"Chat {chat_id} not found")


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
    def get(self, chat_id):
        if str(chat_id).find("?") != -1:
            chat_id = int(chat_id[:chat_id.find("?")].strip())
        # Создаём сессию и получаем чат
        session = db_session.create_session()
        chat = session.query(Chat).get(chat_id)
        return jsonify({'chat': chat.to_dict(
            only=('name', 'type_of_chat', 'status', 'last_time_message',
                  'last_message', 'count_new_messages', 'count_messages',
                  'created_date', 'user_id', 'photo_id'))})

    # Изменяем чат по его id
    def put(self, chat_id):
        if str(chat_id).find("?") != -1:
            chat_id = int(chat_id[:chat_id.find("?")].strip())
        # Получаем аргументы
        args = self.parser.parse_args()
        # Если нет аргументов, отправляем ошибку 400 с текстом 'Empty request'
        if not args:
            return jsonify({'status': 400,
                            'text': "Empty request"})
        # Создаём сессию в БД и получаем чат
        session = db_session.create_session()
        chat = session.query(Chat).get(chat_id)
        # В зависимости от аргументов, изменяем чат
        if args['name']:
            chat.name = args['name']
        if args['type_of_chat']:
            chat.type_of_chat = args['type_of_chat']
        if args['status']:
            chat.status = args['status']
        if args['last_time_message']:
            chat.last_time_message = args['last_time_message']
        if args['last_message']:
            chat.last_message = args['last_message']
        if args['count_new_messages']:
            chat.count_new_messages = args['count_new_messages']
        if args['count_messages']:
            chat.count_messages = args['count_messages']
        # TODO Менять фото
        session.commit()
        return jsonify({'status': 200,
                        'text': 'edited'})

    # Удаляем чат по его id
    def delete(self, chat_id):
        if str(chat_id).find("?") != -1:
            chat_id = int(chat_id[:chat_id.find("?")].strip())
        # Создаём сессию в БД и получаем чат, а затем его удаляем
        session = db_session.create_session()
        chat = session.query(Chat).get(chat_id)
        session.delete(chat)
        session.commit()
        return jsonify({'status': 200,
                        'text': 'deleted'})


# Ресурс для получения чатов
class ChatsListResource(Resource):
    # Получаем чаты user-a по его никнейму
    def get(self, user_nickname):
        # Проверяем, есть ли пользователь
        abort_if_user_not_found(user_nickname)
        # Создаём сессию в БД и получаем чаты
        session = db_session.create_session()
        user = session.query(User).filter(User.nickname == user_nickname).first()
        chats = session.query(Chat).filter(Chat.user_id == user.id).all()
        return jsonify({'chats': [chat.to_dict(
            only=('id', 'name', 'type_of_chat', 'status', 'last_time_message',
                  'last_message', 'count_new_messages', 'count_messages',
                  'created_date', 'user_id', 'photo_id')) for chat in chats]})


class ChatCreateResource(Resource):
    def __init__(self):
        # Инициализируем parser, так как доступ к данным,
        # переданным в теле POST-запроса, осуществляется с помощью парсера аргументов
        self.parser = reqparse.RequestParser()
        # Имя чата
        self.parser.add_argument('name', required=True, type=str)
        # Тип чата: 0 - UserWithUser, 1 - UserWithUsers, 2 - Chanel, 3 - Бот
        self.parser.add_argument('type_of_chat', required=True, type=int)
        # 0 - Offline, >=1 Количество user-ов онлайн в чате
        self.parser.add_argument('status', required=True, type=int)
        # Время последнего отправленного сообщения в формате YYYY-MM-DD HH:MM
        self.parser.add_argument('last_time_message', required=True)
        # Последнее сообщение в чате
        self.parser.add_argument('last_message', required=True, type=str)
        # Количество новых сообщений
        self.parser.add_argument('count_new_messages', required=False, type=int)
        # Количество сообщений
        self.parser.add_argument('count_messages', required=False, type=int)
        # Дата создания чата
        self.parser.add_argument('created_date', required=False)
        # Чат user-a
        self.parser.add_argument('user_id', required=True, type=int)
        # Фото чата
        self.parser.add_argument('photo_id', required=False, type=int)

    # Создаём чат
    def post(self):
        # Получаем аргументы
        args = self.parser.parse_args()
        # Если нет аргументов, отправляем ошибку 400 с текстом 'Empty request'
        if not args:
            return jsonify({'status': 400,
                            'text': "Empty request"})
        # Создаём сессию в БД
        session = db_session.create_session()
        # Создаём чат
        chat = Chat(
            name=args['name'],
            type_of_chat=args['type_of_chat'],
            status=args['status'],
            last_time_message=args['last_time_message'],
            last_message=args['last_message'],
            user_id=args['user_id']
        )
        # В зависимости от аргументов добавляем в чат новые
        if args['photo_id']:
            chat.photo_id = args['photo_id']
        if args['count_new_messages']:
            chat.count_new_messages = args['count_new_messages']
        if args['count_messages']:
            chat.count_messages = args['count_messages']
        # Добавляем в БД чат
        session.add(chat)
        session.commit()
        return jsonify({'status': 201,
                        'text': f'{chat.id} created'})

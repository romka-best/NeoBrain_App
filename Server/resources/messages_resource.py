import datetime
import logging

from flask import jsonify
from flask_restful import abort, Resource, reqparse

from auth import token_auth
from data import db_session
from data.messages import Message
from resources.chats_resource import abort_if_chat_not_found
from resources.users_resource import abort_if_user_not_found


# Если сообщение не найдено, то приходит ответа сервера
def abort_if_message_not_found(message_id):
    session = db_session.create_session()
    message = session.query(Message).get(message_id)
    if not message:
        logging.getLogger("NeoBrain").warning(f"Message {message_id} not found")
        abort(404, message=f"Message {message_id} not found")


def get_current_time() -> datetime:
    delta = datetime.timedelta(hours=3, minutes=0)
    return datetime.datetime.now(datetime.timezone.utc) + delta


class MessageResource(Resource):
    def __init__(self):
        # Инициализируем parser, так как доступ к данным,
        # переданным в теле POST-запроса, осуществляется с помощью парсера аргументов
        self.parser = reqparse.RequestParser()
        # текст сообщения
        self.parser.add_argument('text', required=False, type=str)
        # статус сообщения: 0 - Неудачная отправка, 1 - Отправлено, 2 - Доставлено, 3 - Прочитано
        self.parser.add_argument('status', required=False, type=int)
        # дата редактирования сообщения в формате YYYY-mm-dd HH:MM:SS
        self.parser.add_argument('modified_date', required=False, type=str)
        # Id автора сообщения
        self.parser.add_argument('author_id', required=False, type=int)

    # Получаем сообщение по его id
    @token_auth.login_required
    def get(self, message_id):
        # Создаём сессию и получаем сообщение
        session = db_session.create_session()
        message = session.query(Message).get(message_id)
        logging.getLogger("NeoBrain").debug(f"Message {message_id} returned")
        return jsonify({'message': message.to_dict(
            only=('id', 'text', 'status', 'created_date',
                  'modified_date', 'author_id', 'chat_id'))})

    # Изменяем сообщение по его id
    @token_auth.login_required
    def put(self, message_id):
        # Получаем аргументы
        args = self.parser.parse_args()
        # Если нет аргументов, отправляем ошибку 400 с текстом 'Empty request'
        if not args:
            logging.getLogger("NeoBrain").debug("Empty PUT message request")
            return jsonify({'status': 400,
                            'text': "Empty request"})
        # Создаём сессию в БД и получаем сообщение
        session = db_session.create_session()
        message = session.query(Message).get(message_id)
        # В зависимости от аргументов, изменяем сообщение
        if args.get('text', None):
            message.text = args['text']
        if args.get('status', None) is not None:
            message.status = args['status']
        message.modified_date = get_current_time()
        session.commit()
        logging.getLogger("NeoBrain").debug(f"Message {message_id} edited")
        return jsonify({'status': 200,
                        'text': 'edited'})


#  Ресурс для получение сообщений по его чату
class MessageListResource(Resource):
    # Получаем сообщения чата по его id
    @token_auth.login_required
    def get(self, chat_id):
        # Проверяем, есть ли чат
        abort_if_chat_not_found(chat_id)
        # Создаём сессию в БД и получаем сообщения
        session = db_session.create_session()
        messages = session.query(Message).filter(Message.chat_id == chat_id).all()
        logging.getLogger("NeoBrain").debug(f"Messages from chat {chat_id} returned")
        return jsonify({'messages': [message.to_dict(
            only=('id', 'text', 'status',
                  'created_date', 'modified_date', 'author_id'))
            for message in messages]})


class MessageCreateResource(Resource):
    def __init__(self):
        # Инициализируем parser, так как доступ к данным,
        # переданным в теле POST-запроса, осуществляется с помощью парсера аргументов
        self.parser = reqparse.RequestParser()
        # текст сообщения
        self.parser.add_argument('text', required=True, type=str)
        # статус сообщения: 0 - Неудачная отправка, 1 - Отправлено, 2 - Доставлено, 3 - Прочитано
        self.parser.add_argument('status', required=False, type=int)
        # Id автора сообщения
        self.parser.add_argument('author_id', required=True, type=int)
        # Id чата
        self.parser.add_argument('chat_id', required=True, type=int)

    # Создаём сообщение
    @token_auth.login_required
    def post(self):
        # Получаем аргументы
        args = self.parser.parse_args()
        # Создаём сессию в БД
        session = db_session.create_session()
        abort_if_user_not_found(args['author_id'])
        # Создаём сообщение
        message = Message(
            text=args['text'],
            author_id=args['author_id'],
            chat_id=args['chat_id']
        )
        # Обновляем статус
        message.status = 1
        # Добавляем в БД сообщение
        session.add(message)
        session.commit()
        logging.getLogger("NeoBrain").debug(f"Message {message.id} created")
        return jsonify({'status': 201,
                        'text': f'{message.id} created'})

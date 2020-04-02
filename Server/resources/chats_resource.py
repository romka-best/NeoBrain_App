import datetime

from flask_restful import reqparse, abort, Resource
from flask_login import login_required, current_user
from flask import jsonify, request
from data import db_session
from data.chats import Chat
from data.users import User
from data.photos import Photo
from .users_resource import abort_if_user_not_found


def abort_if_chat_not_found(chat_id):
    session = db_session.create_session()
    chat = session.query(Chat).filter(Chat.id == chat_id).first()
    if not chat:
        abort(404, message=f"Chat {chat_id} not found")


class ChatResource(Resource):
    def __init__(self):
        self.parser = reqparse.RequestParser()
        self.parser.add_argument('name', required=True, type=str)
        self.parser.add_argument('type_of_chat', required=True, type=int)
        self.parser.add_argument('status', required=True, type=int)
        self.parser.add_argument('last_time_message', required=True)
        self.parser.add_argument('last_message', required=True, type=str)
        self.parser.add_argument('count_new_messages', required=False, type=int)
        self.parser.add_argument('count_messages', required=True, type=int)
        self.parser.add_argument('created_date', required=False)
        self.parser.add_argument('user_id', required=True, type=int)
        self.parser.add_argument('photo_id', required=False, type=int)

    def get(self, chat_id):
        if chat_id.find("?") != -1:
            chat_id = int(chat_id[:chat_id.find("?")].strip())
        session = db_session.create_session()
        chat = session.query(Chat).get(chat_id)
        return jsonify({'chat': chat.to_dict(
            only=('name', 'type_of_chat', 'status', 'last_time_message',
                  'last_message', 'count_new_messages', 'count_messages',
                  'created_date', 'user_id', 'photo_id'))})

    def put(self, chat_id):
        if chat_id.find("?") != -1:
            chat_id = int(chat_id[:chat_id.find("?")].strip())
        args = self.parser.parse_args()
        session = db_session.create_session()
        if not args:
            return jsonify({'status': 400,
                            'text': "Empty request"})
        elif not all(key in args for key in
                     ['name', 'type_of_chat', 'status', 'last_time_message',
                      'last_message', 'count_messages', 'user_id']):
            return jsonify({'status': 400,
                            'text': "Bad request"})
        chat = session.query(Chat).get(chat_id)
        chat.name = args['name']
        chat.type_of_chat = args['type_of_chat']
        chat.status = args['status']
        chat.last_time_message = args['last_time_message']
        chat.last_message = args['last_message']
        chat.count_messages = args['count_messages']
        chat.user_id = args['user_id']
        session.commit()
        return jsonify({'status': 200,
                        'text': 'edited'})

    def delete(self, chat_id):
        if chat_id.find("?") != -1:
            chat_id = int(chat_id[:chat_id.find("?")].strip())
        session = db_session.create_session()
        chat = session.query(Chat).get(chat_id)
        session.delete(chat)
        session.commit()
        return jsonify({'status': 200,
                        'text': 'deleted'})


class ChatsListResource(Resource):
    def get(self, user_nickname):
        abort_if_user_not_found(user_nickname)
        session = db_session.create_session()
        user = session.query(User).filter(User.nickname == user_nickname).first()
        chats = session.query(Chat).filter(Chat.user_id == user.id).all()
        return jsonify({'chats': [chat.to_dict(
            only=('id', 'name', 'type_of_chat', 'status', 'last_time_message',
                  'last_message', 'count_new_messages', 'count_messages',
                  'created_date', 'user_id', 'photo_id')) for chat in chats]})


class ChatCreateResource(Resource):
    def __init__(self):
        self.parser = reqparse.RequestParser()
        self.parser.add_argument('name', required=True, type=str)
        self.parser.add_argument('type_of_chat', required=True, type=int)
        self.parser.add_argument('status', required=True, type=int)
        self.parser.add_argument('last_time_message', required=False)
        self.parser.add_argument('last_message', required=True, type=str)
        self.parser.add_argument('count_new_messages', required=False, type=int)
        self.parser.add_argument('count_messages', required=False, type=int)
        self.parser.add_argument('created_date', required=False)
        self.parser.add_argument('user_id', required=True, type=int)
        self.parser.add_argument('photo_id', required=False, type=int)

    def post(self):
        args = self.parser.parse_args()
        session = db_session.create_session()
        if not args:
            return jsonify({'status': 400,
                            'text': "Empty request"})
        elif not all(key in args for key in
                     ['name', 'type_of_chat', 'status',
                      'last_message', 'count_messages',
                      'created_date', 'user_id']) and \
                not any(key in args for key in ['number', 'email']):
            return jsonify({'status': 400,
                            'text': "Bad request"})
        if args['photo_id']:
            photo_id = args['photo_id']
        else:
            photo_id = session.query(Photo).get(2)
        chat = Chat(
            name=args['name'],
            type_of_chat=args['type_of_chat'],
            status=args['status'],
            last_message=args['last_message'],
            count_messages=args['count_messages'],
            user_id=args['user_id'],
            photo_id=photo_id.data
        )
        if args['count_new_messages']:
            chat.count_new_messages = args['count_new_messages']
        session.add(chat)
        session.commit()
        return jsonify({'status': 201,
                        'text': 'created'})

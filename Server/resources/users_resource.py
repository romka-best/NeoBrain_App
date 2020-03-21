import array
import datetime
import sqlite3
from base64 import encodebytes, decodebytes

from flask_restful import reqparse, abort, Resource
from flask import jsonify, request
from data import db_session
from data.users import User
from data.photos import Photo


def abort_if_user_not_found(user_nickname):
    session = db_session.create_session()
    user = session.query(User).filter(User.nickname == user_nickname).first()
    if not user:
        abort(404, message=f"User {user_nickname} not found")


class UserResource(Resource):
    def __init__(self):
        self.parser = reqparse.RequestParser()
        self.parser.add_argument('name', required=False, type=str)
        self.parser.add_argument('surname', required=False, type=str)
        self.parser.add_argument('nickname', required=False, type=str)
        self.parser.add_argument('number', required=False, type=str)
        self.parser.add_argument('email', required=False, type=str)
        self.parser.add_argument('created_date', required=False)
        self.parser.add_argument('modified_date', required=False)
        self.parser.add_argument('hashed_password', required=False, type=str)
        self.parser.add_argument('is_closed', required=False, type=bool)
        self.parser.add_argument('about', required=False, type=str)
        self.parser.add_argument('birthday', required=False)
        self.parser.add_argument('age', required=False, type=int)
        self.parser.add_argument('can_see_audio', required=False, type=bool)
        self.parser.add_argument('can_see_groups', required=False, type=bool)
        self.parser.add_argument('can_see_videos', required=False, type=bool)
        self.parser.add_argument('can_write_message', required=False, type=bool)
        self.parser.add_argument('city', required=False, type=str)
        self.parser.add_argument('republic', required=False, type=str)
        self.parser.add_argument('country', required=False, type=str)
        self.parser.add_argument('education', required=False, type=str)
        self.parser.add_argument('followers_count', required=False, type=int)
        self.parser.add_argument('subscriptions_count', required=False, type=int)
        self.parser.add_argument('status', required=False, type=int)
        self.parser.add_argument('last_seen', required=False, type=str)
        self.parser.add_argument('photo_id', required=False, type=int)
        self.parser.add_argument('photo', required=False, type=str)

    def get(self, user_nickname):
        if user_nickname.find("?") != -1:
            user_nickname = user_nickname[:user_nickname.find("?")].strip()
        abort_if_user_not_found(user_nickname)
        session = db_session.create_session()
        user = session.query(User).filter(User.nickname == user_nickname).first()
        photo = encodebytes(session.query(Photo).filter(Photo.id == user.photo_id).first().data).decode()
        return jsonify({'user': user.to_dict(
            only=('name', 'surname', 'nickname', 'number',
                  'created_date', 'modified_date', 'email')), 'photo': photo})

    def put(self, user_nickname):
        if user_nickname.find("?") != -1:
            user_nickname = user_nickname[:user_nickname.find("?")].strip()
        abort_if_user_not_found(user_nickname)
        args = self.parser.parse_args()
        session = db_session.create_session()
        if not args:
            return jsonify({'status': 400,
                            'text': "Empty request"})
        user = session.query(User).filter(User.nickname == user_nickname).first()
        if args['name']:
            user.name = args['name']
        if args['surname']:
            user.surname = args['surname']
        if args['nickname']:
            user.nickname = args['nickname']
        if args['number']:
            user.number = args['number']
        if args['email']:
            user.email = args['email']
        if args['hashed_password']:
            user.set_password(args['hashed_password'])
        if args['photo']:
            photo = session.query(Photo).filter(Photo.id == user.photo_id).first()
            photo.data = array.array('B', decodebytes(args['photo'].encode()))
        user.modified_date = datetime.datetime.now()
        session.commit()
        return jsonify({'status': 200,
                        'text': 'edited'})

    def delete(self, user_nickname):
        if user_nickname.find("?") != -1:
            user_nickname = user_nickname[:user_nickname.find("?")].strip()
        abort_if_user_not_found(user_nickname)
        session = db_session.create_session()
        user = session.query(User).filter(User.nickname == user_nickname).first()
        session.delete(user)
        session.commit()
        return jsonify({'status': 200,
                        'text': 'deleted'})


class UserLoginResource(Resource):
    def __init__(self):
        self.parser = reqparse.RequestParser()
        self.parser.add_argument('nickname', required=False)
        self.parser.add_argument('number', required=False)
        self.parser.add_argument('email', required=False)
        self.parser.add_argument('hashed_password', required=True)

    def post(self):
        args = self.parser.parse_args()
        session = db_session.create_session()
        if not args:
            return jsonify({'status': 400,
                            'text': "Empty request"})
        elif not all(key in args for key in
                     ['hashed_password']) and \
                not any(key in args for key in ['number', 'nickname', 'email']):
            return jsonify({'status': 400,
                            'text': "Bad request"})
        if args['number']:
            user = session.query(User).filter(User.number == args['number']).first()
        elif args['nickname']:
            user = session.query(User).filter(User.nickname == args['nickname']).first()
        elif args['email']:
            user = session.query(User).filter(User.email == args['email']).first()
        else:
            return jsonify({'status': 400,
                            'text': 'Bad request'})
        if user and user.check_password(args['hashed_password']):
            return jsonify({'status': 200,
                            'text': f'Login {user.nickname} allowed'})
        elif user and not user.check_password(args['hashed_password']):
            return jsonify({'status': 449,
                            'text': 'Password is not correct'})
        return jsonify({'status': 404,
                        'text': 'User is not defined'})


class UsersListResource(Resource):
    def __init__(self):
        self.parser = reqparse.RequestParser()
        self.parser.add_argument('name', required=True, type=str)
        self.parser.add_argument('surname', required=True, type=str)
        self.parser.add_argument('nickname', required=True, type=str)
        self.parser.add_argument('number', required=True, type=str)
        self.parser.add_argument('email', required=False, type=str)
        self.parser.add_argument('created_date', required=False)
        self.parser.add_argument('modified_date', required=False)
        self.parser.add_argument('hashed_password', required=True, type=str)

    def get(self):
        session = db_session.create_session()
        users = session.query(User).all()
        return jsonify({'users': [item.to_dict(
            only=('name', 'surname', 'nickname', 'number',
                  'created_date', 'modified_date', 'email')) for item in users]})

    def post(self):
        args = self.parser.parse_args()
        session = db_session.create_session()
        if not args:
            return jsonify({'status': 400,
                            'text': "Empty request"})
        elif not all(key in args for key in
                     ['name', 'surname', 'nickname', 'number', 'hashed_password']) and not any(
            key in args for key in ['number', 'email']):
            return jsonify({'status': 400,
                            'text': "Bad request"})
        elif session.query(User).filter(
                User.nickname == args['nickname']).first():
            return jsonify({'status': 449,
                            'text': 'Nickname already exists'})
        elif session.query(User).filter(User.number == args['number']).first():
            return jsonify({'status': 449,
                            'text': 'Phone number already exists'})
        user = User(
            name=args['name'],
            surname=args['surname'],
            nickname=args['nickname']
        )
        user.set_password(args['hashed_password'])
        if args['number']:
            user.number = args['number']
        if args['email']:
            user.email = args['email']
        user.photo_id = 2
        session.add(user)
        session.commit()
        return jsonify({'status': 201,
                        'text': 'created'})

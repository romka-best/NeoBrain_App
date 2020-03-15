import datetime

from flask_restful import reqparse, abort, Resource
from flask import jsonify, request
from data import db_session
from data.users import User


def abort_if_user_not_found(user_nickname):
    session = db_session.create_session()
    user = session.query(User).filter(User.nickname == user_nickname).first()
    if not user:
        abort(404, message=f"Users {user_nickname} not found")


class UserResource(Resource):
    def __init__(self):
        self.parser = reqparse.RequestParser()
        self.parser.add_argument('name', required=True)
        self.parser.add_argument('surname', required=True)
        self.parser.add_argument('nickname', required=True)
        self.parser.add_argument('number', required=True)
        self.parser.add_argument('email', required=False)
        self.parser.add_argument('created_date', required=False)
        self.parser.add_argument('modified_date', required=False)
        self.parser.add_argument('hashed_password', required=True, type=str)

    def get(self, user_nickname):
        abort_if_user_not_found(user_nickname)
        session = db_session.create_session()
        user = session.query(User).filter(User.nickname == user_nickname).first()
        return jsonify({'user': user.to_dict(
            only=('name', 'surname', 'nickname', 'number',
                  'created_date', 'modified_date', 'email'))})

    def put(self, user_nickname):
        abort_if_user_not_found(user_nickname)
        args = self.parser.parse_args()
        session = db_session.create_session()
        if not args:
            return jsonify({'status': 400,
                            'text': "Empty request"})
        elif not all(key in args for key in
                     ['name', 'surname', 'nickname', 'number', 'hashed_password']):
            return jsonify({'status': 400,
                            'text': "Bad request"})
        user = session.query(User).filter(User.nickname == user_nickname).first()
        user = User(
            name=args['name'],
            surname=args['surname'],
            nickname=args['nickname'],
            number=args['number'],
            modified_date=datetime.datetime.now()
        )
        user.set_password(args['hashed_password'])
        session.commit()
        return jsonify({'status': 200,
                        'text': 'edited'})

    def delete(self, user_nickname):
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
        self.parser.add_argument('name', required=True)
        self.parser.add_argument('surname', required=True)
        self.parser.add_argument('nickname', required=True)
        self.parser.add_argument('number', required=True)
        self.parser.add_argument('email', required=False)
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
                     ['name', 'surname', 'nickname', 'number', 'hashed_password']):
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
            nickname=args['nickname'],
            number=args['number']
        )
        user.set_password(args['hashed_password'])
        session.add(user)
        session.commit()
        return jsonify({'status': 201,
                        'text': 'created'})

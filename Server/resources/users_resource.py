import datetime

from flask_restful import reqparse, abort, Resource
from flask import jsonify
from data import db_session
from data.users import User

parser = reqparse.RequestParser()
parser.add_argument('name', required=True)
parser.add_argument('surname', required=True)
parser.add_argument('nickname', required=True)
parser.add_argument('number', required=True)
parser.add_argument('email', required=False)
parser.add_argument('created_date', required=False)
parser.add_argument('modified_date', required=False)
parser.add_argument('hashed_password', required=True)


def abort_if_user_not_found(user_id):
    session = db_session.create_session()
    user = session.query(User).get(user_id)
    if not user:
        abort(404, message=f"Users {user_id} not found")


class UserResource(Resource):
    def get(self, user_id):
        abort_if_user_not_found(user_id)
        session = db_session.create_session()
        user = session.query(User).get(user_id)
        return jsonify({'user': user.to_dict(
            only=('name', 'surname', 'nickname', 'number',
                  'created_date', 'modified_date', 'email'))})

    def post(self):
        args = parser.parse_args()
        session = db_session.create_session()
        if not args:
            return jsonify({'status': 400,
                            'text': "Empty request"})
        elif not all(key in args for key in
                     ['name', 'surname', 'nickname', 'number', 'password']):
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
        user.set_password(args['password'])
        session.add(user)
        session.commit()
        return jsonify({'status': 201,
                        'text': 'created'})

    def put(self, user_id):
        abort_if_user_not_found(user_id)
        args = parser.parse_args()
        session = db_session.create_session()
        if not args:
            return jsonify({'status': 400,
                            'text': "Empty request"})
        elif not all(key in args for key in
                     ['name', 'surname', 'nickname', 'number', 'password']):
            return jsonify({'status': 400,
                            'text': "Bad request"})
        user = session.query(User).get(user_id)
        user = User(
            id=user_id,
            name=args['name'],
            surname=args['surname'],
            nickname=args['nickname'],
            number=args['number'],
            modified_date=datetime.datetime.now()
        )
        user.set_password(args['password'])
        session.commit()
        return jsonify({'status': 200,
                        'text': 'edited'})

    def delete(self, users_id):
        abort_if_user_not_found(users_id)
        session = db_session.create_session()
        user = session.query(User).get(users_id)
        session.delete(user)
        session.commit()
        return jsonify({'status': 200,
                        'text': 'deleted'})


class UserLoginResource(Resource):
    def post(self):
        args = parser.parse_args()
        session = db_session.create_session()
        if not args:
            return jsonify({'status': 400,
                            'text': "Empty request"})
        elif not all(key in args for key in
                     ['number', 'password']):
            return jsonify({'status': 400,
                            'text': "Bad request"})
        user = session.query(User).filter(User.number == args['number']).first()
        if user and user.check_password(args['password']):
            return jsonify({'status': 200,
                            'text': 'Login allowed'})
        elif user and not user.check_password(args['password']):
            return jsonify({'status': 449,
                            'text': 'Password is not correct'})
        return jsonify({'status': 404,
                        'text': 'User is not defined'})


class UsersListResource(Resource):
    def get(self):
        session = db_session.create_session()
        users = session.query(User).all()
        return jsonify({'users': [item.to_dict(
            only=('name', 'surname', 'nickname', 'number',
                  'created_date', 'modified_date', 'email')) for item in users]})

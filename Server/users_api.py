import flask
from flask import jsonify, request
from data import db_session
from data.users import User

blueprint = flask.Blueprint('users_api', __name__,
                            template_folder='templates')


@blueprint.route('/api/users', methods=["GET"])
def get_users():
    session = db_session.create_session()
    users = session.query(User).all()
    return jsonify(
        {
            'users':
                [item.to_dict(only=('name', 'surname', 'nickname', 'number',
                                    'created_date', 'modified_date', 'email'))
                 for item in users]
        }
    )


@blueprint.route('/api/users/<int:users_id>', methods=['GET'])
def get_one_user(users_id):
    session = db_session.create_session()
    user = session.query(User).get(users_id)
    if not user:
        return jsonify({'error': 'Not found'})
    return jsonify(
        {
            'users': user.to_dict(only=('name', 'surname', 'nickname', 'number',
                                        'created_date', 'modified_date', 'email'))
        }
    )


@blueprint.route('/api/users/create', methods=['POST'])
def create_user():
    session = db_session.create_session()
    if not request.json:
        return jsonify({'error': 'Empty request'})
    elif not all(key in request.json for key in
                 ['name', 'surname', 'nickname', 'number', 'password']):
        return jsonify({'error': 'Bad request'})
    elif session.query(User).filter(User.nickname == request.json['nickname']).first():
        return jsonify({'error': 'Id already exists'})
    user = User(
        name=request.json['name'],
        surname=request.json['surname'],
        nickname=request.json['nickname'],
        number=request.json['number']
    )
    user.set_password(request.json['password'])
    session.add(user)
    session.commit()
    return jsonify({'error': 'OK'})


@blueprint.route('/api/users/login', methods=['POST'])
def login_user():
    session = db_session.create_session()
    if not request.json:
        return jsonify({'error': 'Empty request'})
    elif not all(key in request.json for key in
                 ['number', 'password']):
        return jsonify({'error': 'Bad request'})
    user = session.query(User).filter(User.number == request.json['number']).first()
    if user and user.check_password(request.json['password']):
        return jsonify({'success': 'OK'})
    return jsonify({'error': 404})


@blueprint.route('/api/user_delete/<int:user_id>', methods=['DELETE'])
def delete_user(user_id):
    session = db_session.create_session()
    user = session.query(User).get(user_id)
    if not user:
        return jsonify({'error': 'Not found'})
    session.delete(user)
    session.commit()
    return jsonify({'success': 'OK'})


@blueprint.route('/api/user_edit/<int:user_id>', methods=['PUT'])
def edit_user(user_id):
    session = db_session.create_session()
    if not request.json:
        return jsonify({'error': 'Empty request'})
    elif not all(key in request.json for key in
                 ['name', 'surname', 'nickname', 'number', 'modified_date', 'password']):
        return jsonify({'error': 'Bad request'})
    user = session.query(User).get(user_id)
    if not user:
        return jsonify({'error': 'Not found'})
    user = User(
        id=user_id,
        name=request.json['name'],
        surname=request.json['surname'],
        nickname=request.json['nickname'],
        number=request.json['number'],
        modified_date=request.json['modified_date']
    )
    user.set_password(request.json['password'])
    session.commit()
    return jsonify({'success': 'OK'})

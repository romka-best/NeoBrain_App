# Импортируем нужные библиотеки
from flask import jsonify
from flask_restful import reqparse, Resource, abort

from data import db_session
from data.posts import Post

# Если пост не найден, то приходит ответа сервера
from data.users import User
from resources.users_resource import abort_if_user_not_found


def abort_if_post_not_found(post_id):
    session = db_session.create_session()
    post = session.query(Post).filter(Post.id == post_id).first()
    if not post:
        abort(404, message=f"Chat {post_id} not found")


# Основной ресурс для работы с Post
class PostResource(Resource):
    def __init__(self):
        # Инициализируем parser, так как доступ к данным,
        # переданным в теле POST-запроса, осуществляется с помощью парсера аргументов
        self.parser = reqparse.RequestParser()
        # Текст
        self.parser.add_argument('text', required=True, type=str)
        # id пользователя
        self.parser.add_argument('user_id', required=True)

    # Получаем пост по его id
    def get(self, post_id):
        if str(post_id).find("?") != -1:
            post_id = int(post_id[:post_id.find("?")].strip())
        abort_if_post_not_found(post_id)
        # Создаём сессию и получаем пост
        session = db_session.create_session()
        post = session.query(Post).get(post_id)
        return jsonify({'post': post.to_dict(
            only=('id', 'text', 'user_id'))})


# Ресурс для получения постов
class PostsListResource(Resource):
    # Получаем посты user-a по его никнейму
    def get(self, user_nickname):
        # Проверяем, есть ли пользователь
        abort_if_user_not_found(user_nickname)
        # Создаём сессию в БД и получаем чаты
        session = db_session.create_session()
        user = session.query(User).filter(User.nickname == user_nickname).first()
        posts = session.query(Post).filter(Post.user_id == user.id).all()
        return jsonify({'posts': [post.to_dict(
            only=('id', 'name', 'user_id')) for post in posts]})


class PostCreateResource(Resource):
    def __init__(self):
        # Инициализируем parser, так как доступ к данным,
        # переданным в теле POST-запроса, осуществляется с помощью парсера аргументов
        self.parser = reqparse.RequestParser()
        # Текст
        self.parser.add_argument('text', required=True)
        # id пользователя
        self.parser.add_argument('user_id', required=True)

    # Создаём пост
    def post(self):
        # Получаем аргументы
        args = self.parser.parse_args()
        # Если нет аргументов, отправляем ошибку 400 с текстом 'Empty request'
        if not args:
            return jsonify({'status': 400,
                            'text': "Empty request"})
        # Создаём сессию в БД
        session = db_session.create_session()
        # Создаём пост
        post = Post(
            text=args['text'],
            user_id=args['user_id']
        )
        # Добавляем в БД чат
        session.add(post)
        session.commit()
        return jsonify({'status': 201,
                        'text': f'{post.id} created'})

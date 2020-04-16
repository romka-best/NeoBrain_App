# Импортируем нужные библиотеки
from flask import jsonify
from flask_restful import reqparse, Resource, abort

from data import db_session
from data.posts import Post

from data.users import User
from resources.users_resource import abort_if_user_not_found


# Если пост не найден, то приходит ответа сервера
def abort_if_post_not_found(post_id):
    session = db_session.create_session()
    post = session.query(Post).get(post_id)
    if not post:
        abort(404, message=f"Post {post_id} not found")


# Основной ресурс для работы с Post
class PostResource(Resource):
    def __init__(self):
        # Инициализируем parser, так как доступ к данным,
        # переданным в теле POST-запроса, осуществляется с помощью парсера аргументов
        self.parser = reqparse.RequestParser()
        # Заголовок(Имя) поста
        self.parser.add_argument('title', required=False, type=str)
        # Текст
        self.parser.add_argument('text', required=False, type=str)
        # Дата создания поста
        self.parser.add_argument('created_date', type=str)
        # id пользователя
        self.parser.add_argument('user_id', required=False, type=int)
        # id фото
        self.parser.add_argument('photo_id', required=False, type=int)

    # Получаем пост по его id
    def get(self, post_id):
        if str(post_id).find("?") != -1:
            post_id = int(post_id[:post_id.find("?")].strip())
        abort_if_post_not_found(post_id)
        # Создаём сессию и получаем пост
        session = db_session.create_session()
        post = session.query(Post).get(post_id)
        return jsonify({'post': post.to_dict(
            only=('id', 'title', 'text', 'created_date', 'user_id', 'photo_id'))})


# Ресурс для получения постов
class PostsListResource(Resource):
    # Получаем посты user-a по его никнейму
    def get(self, user_id):
        # Проверяем, есть ли пользователь
        abort_if_user_not_found(user_id)
        # Создаём сессию в БД и получаем чаты
        session = db_session.create_session()
        posts = session.query(Post).filter(Post.user_id == user_id).all()
        return jsonify({'posts': [post.to_dict(
            only=('id', 'title', 'text', 'created_date', 'user_id', 'photo_id')) for post in posts]})


class PostCreateResource(Resource):
    def __init__(self):
        # Инициализируем parser, так как доступ к данным,
        # переданным в теле POST-запроса, осуществляется с помощью парсера аргументов
        self.parser = reqparse.RequestParser()
        # Заголовок(Имя) поста
        self.parser.add_argument('title', required=False, type=str)
        # Текст
        self.parser.add_argument('text', required=True, type=str)
        # Дата создания поста
        self.parser.add_argument('created_date', required=False, type=str)
        # id пользователя
        self.parser.add_argument('user_id', required=False, type=int)
        # Никнейм пользователя
        self.parser.add_argument("user_nickname", required=False, type=str)
        # id фото
        self.parser.add_argument('photo_id', required=False, type=int)

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
            text=args['text']
        )
        user = session.query(User).get(args['user_id'])
        post.user_id = args['user_id']
        if args.get('photo_id', None):
            post.photo_id = args['photo_id']
        else:
            post.photo_id = user.photo_id
        if args.get('title', None):
            post.title = args['title']
        else:
            post.title = f"{user.name} {user.surname}"
        # Добавляем в БД пост
        session.add(post)
        session.commit()
        return jsonify({'status': 201,
                        'text': f'{post.id} created'})

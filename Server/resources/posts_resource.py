# Импортируем нужные библиотеки
import logging

from flask import jsonify
from flask_restful import reqparse, Resource, abort

from data import db_session
from data.post_association import PostAssociation
from data.posts import Post, get_current_time
from data.users import User
from resources.users_resource import abort_if_user_not_found


# Если пост не найден, то приходит ответа сервера
def abort_if_post_not_found(post_id):
    session = db_session.create_session()
    post = session.query(Post).get(post_id)
    if not post:
        logging.getLogger("NeoBrain").warning(f"Post with id {post_id} not found")
        abort(404, message=f"Post {post_id} not found")


def get_post(cur_post, post):
    logging.getLogger("NeoBrain").debug("Post info returned")
    return {"id": cur_post.id,
            "title": cur_post.title,
            "text": cur_post.text,
            "created_date": cur_post.created_date.strftime("%Y-%m-%d %H:%M:%S"),
            "modified_date": cur_post.modified_date.strftime("%Y-%m-%d %H:%M:%S"),
            "user_id": post.user_id,
            "is_author": post.is_author,
            "photo_id": cur_post.photo_id,
            "like_emoji_count": cur_post.like_emoji_count,
            "laughter_emoji_count": cur_post.laughter_emoji_count,
            "heart_emoji_count": cur_post.heart_emoji_count,
            "disappointed_emoji_count": cur_post.disappointed_emoji_count,
            "smile_emoji_count": cur_post.smile_emoji_count,
            "angry_emoji_count": cur_post.angry_emoji_count,
            "smile_with_heart_eyes_count": cur_post.smile_with_heart_eyes_count,
            "screaming_emoji_count": cur_post.screaming_emoji_count,
            "like_emoji": post.like_emoji,
            "laughter_emoji": post.laughter_emoji,
            "heart_emoji": post.heart_emoji,
            "disappointed_emoji": post.disappointed_emoji,
            "smile_emoji": post.smile_emoji,
            "angry_emoji": post.angry_emoji,
            "smile_with_heart_eyes": post.smile_with_heart_eyes,
            "screaming_emoji": post.screaming_emoji
            }


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
        # Оценки пост
        self.parser.add_argument('like_emoji', required=False, type=bool)
        self.parser.add_argument('laughter_emoji', required=False, type=bool)
        self.parser.add_argument('heart_emoji', required=False, type=bool)
        self.parser.add_argument('disappointed_emoji', required=False, type=bool)
        self.parser.add_argument('smile_emoji', required=False, type=bool)
        self.parser.add_argument('angry_emoji', required=False, type=bool)
        self.parser.add_argument('smile_with_heart_eyes', required=False, type=bool)
        self.parser.add_argument('screaming_emoji', required=False, type=bool)
        # id пользователя
        self.parser.add_argument('user_id', required=False, type=int)
        # id фото
        self.parser.add_argument('photo_id', required=False, type=int)

    # Получаем пост по его id
    def get(self, post_id):
        abort_if_post_not_found(post_id)
        # Создаём сессию и получаем пост
        session = db_session.create_session()
        post = session.query(Post).get(post_id)
        post_association = session.query(PostAssociation).filter(PostAssociation.post_id == post_id).all()
        users = []
        for post1 in post_association:
            cur_post = session.query(Post).get(post1.post_id)
            users.append({"post": get_post(cur_post, post1)})
        logging.getLogger("NeoBrain").debug(f"Post info {post_id} returned")
        return jsonify({'post': post.to_dict(
            only=('id', 'title', 'text', 'created_date', 'photo_id',
                  'like_emoji_count', 'laughter_emoji_count', 'heart_emoji_count',
                  'disappointed_emoji_count', 'smile_emoji_count', 'angry_emoji_count',
                  'smile_with_heart_eyes_count', 'screaming_emoji_count')), 'users': users})

    def put(self, post_id):
        # Получаем аргументы
        args = self.parser.parse_args()
        # Проверяем на наличие поста и пользователя в БД
        abort_if_post_not_found(post_id)
        abort_if_user_not_found(args['user_id'])
        # Создаём сессию и получаем пост
        session = db_session.create_session()
        post = session.query(Post).get(post_id)
        post_association = session.query(PostAssociation).filter(PostAssociation.user_id == args['user_id'],
                                                                 PostAssociation.post_id == post.id).first()
        if not post_association:
            post_association = PostAssociation(
                user_id=args['user_id'],
                post_id=post.id,
                is_author=False
            )
            session.add(post_association)
            session.commit()
        if args.get('text', None):
            post.text = args['text']
            # Обновляем дату модификации поста
            post.modified_date = get_current_time()
        if args.get('like_emoji', None) is not None:
            if not args['like_emoji'] and post_association.like_emoji:
                post.like_emoji_count -= 1
            elif args['like_emoji'] and not post_association.like_emoji:
                post.like_emoji_count += 1
            post_association.like_emoji = args['like_emoji']
        if args.get('laughter_emoji', None) is not None:
            if not args['laughter_emoji'] and post_association.laughter_emoji:
                post.laughter_emoji_count -= 1
            elif args['laughter_emoji'] and not post_association.laughter_emoji:
                post.laughter_emoji_count += 1
            post_association.laughter_emoji = args['laughter_emoji']
        if args.get('heart_emoji', None) is not None:
            if not args['heart_emoji'] and post_association.heart_emoji:
                post.heart_emoji_count -= 1
            elif args['heart_emoji'] and not post_association.heart_emoji:
                post.heart_emoji_count += 1
            post_association.heart_emoji = args['heart_emoji']
        if args.get('disappointed_emoji', None) is not None:
            if not args['disappointed_emoji'] and post_association.disappointed_emoji:
                post.disappointed_emoji_count -= 1
            elif args['disappointed_emoji'] and not post_association.disappointed_emoji:
                post.disappointed_emoji_count += 1
            post_association.disappointed_emoji = args['disappointed_emoji']
        if args.get('smile_emoji', None) is not None:
            if not args['smile_emoji'] and post_association.smile_emoji:
                post.smile_emoji_count -= 1
            elif args['smile_emoji'] and not post_association.smile_emoji:
                post.smile_emoji_count += 1
            post_association.smile_emoji = args['smile_emoji']
        if args.get('angry_emoji', None) is not None:
            if not args['angry_emoji'] and post_association.angry_emoji:
                post.angry_emoji_count -= 1
            elif args['angry_emoji'] and not post_association.angry_emoji:
                post.angry_emoji_count += 1
            post_association.angry_emoji = args['angry_emoji']
        if args.get('smile_with_heart_eyes', None) is not None:
            if not args['smile_with_heart_eyes'] and post_association.smile_with_heart_eyes:
                post.smile_with_heart_eyes_count -= 1
            elif args['smile_with_heart_eyes'] and not post_association.smile_with_heart_eyes:
                post.smile_with_heart_eyes_count += 1
            post_association.smile_with_heart_eyes = args['smile_with_heart_eyes']
        if args.get('screaming_emoji', None) is not None:
            if not args['screaming_emoji'] and post_association.screaming_emoji:
                post.screaming_emoji_count -= 1
            elif args['screaming_emoji'] and not post_association.screaming_emoji:
                post.screaming_emoji_count += 1
            post_association.screaming_emoji = args['screaming_emoji']
        session.commit()
        logging.getLogger("NeoBrain").debug(f"Post {post_id} edited")
        return jsonify({'status': 200,
                        'text': 'edited'})

    def delete(self, post_id):
        abort_if_post_not_found(post_id)
        # Создаём сессию и получаем пост
        session = db_session.create_session()
        post = session.query(Post).get(post_id)
        post_association = session.query(PostAssociation).filter(PostAssociation.post_id == post_id).all()
        # Удаляем пост
        session.delete(post)
        for post in post_association:
            session.delete(post)
        session.commit()
        logging.getLogger("NeoBrain").info(f"Post {post_id} deleted")
        return jsonify({'status': 200,
                        'text': 'deleted'})


# Ресурс для получения постов
class PostsListResource(Resource):
    # Получаем посты user-a по его id
    def get(self, author_id, user_id):
        # Проверяем, есть ли пользователь
        abort_if_user_not_found(author_id)
        abort_if_user_not_found(user_id)
        # Создаём сессию в БД и получаем посты
        session = db_session.create_session()

        association = session.query(PostAssociation).filter(PostAssociation.user_id == author_id,
                                                            PostAssociation.is_author == 1).all()
        posts = {'posts': []}
        for post in association:
            cur_post = session.query(Post).get(post.post_id)
            if author_id != user_id:
                post = session.query(PostAssociation).filter(PostAssociation.post_id == cur_post.id,
                                                             PostAssociation.user_id == user_id).first()
                if not post:
                    post = PostAssociation(
                        user_id=user_id,
                        post_id=cur_post.id,
                        is_author=False
                    )
                    session.add(post)
                    session.commit()
            posts['posts'].append(get_post(cur_post, post))
        logging.getLogger("NeoBrain").debug(f"Posts of user {user_id} returned")
        return jsonify(posts)


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
        # Оценки пост
        self.parser.add_argument('like_emoji_count', required=False, type=int)
        self.parser.add_argument('laughter_emoji_count', required=False, type=int)
        self.parser.add_argument('heart_emoji_count', required=False, type=int)
        self.parser.add_argument('disappointed_emoji_count', required=False, type=int)
        self.parser.add_argument('smile_emoji_count', required=False, type=int)
        self.parser.add_argument('angry_emoji_count', required=False, type=int)
        self.parser.add_argument('smile_with_heart_eyes_count', required=False, type=int)
        self.parser.add_argument('screaming_emoji_count', required=False, type=int)
        # id пользователя
        self.parser.add_argument('user_id', required=False, type=int)
        # id фото
        self.parser.add_argument('photo_id', required=False, type=int)

    # Создаём пост
    def post(self):
        # Получаем аргументы
        args = self.parser.parse_args()
        # Если нет аргументов, отправляем ошибку 400 с текстом 'Empty request'
        if not args:
            logging.getLogger("NeoBrain").debug(f"Empty post create POST request")
            return jsonify({'status': 400,
                            'text': "Empty request"})
        # Проверяем наличие пользователя
        abort_if_user_not_found(args['user_id'])
        # Создаём сессию в БД
        session = db_session.create_session()
        user = session.query(User).get(args['user_id'])
        # Создаём пост
        post = Post(
            text=args['text']
        )
        if args.get('photo_id', None):
            post.photo_id = args['photo_id']
        else:
            post.photo_id = user.photo_id
        if args.get('title', None):
            post.title = args['title']
        else:
            post.title = f"{user.name} {user.surname}"
        if args.get('like_emoji_count', None) is not None:
            post.like_emoji_count = args['like_emoji_count']
        if args.get('laughter_emoji_count', None) is not None:
            post.laughter_emoji_count = args['laughter_emoji_count']
        if args.get('heart_emoji_count', None) is not None:
            post.heart_emoji_count = args['heart_emoji_count']
        if args.get('disappointed_emoji_count', None) is not None:
            post.disappointed_emoji_count = args['disappointed_emoji_count']
        if args.get('smile_emoji_count', None) is not None:
            post.smile_emoji_count = args['smile_emoji_count']
        if args.get('angry_emoji_count', None) is not None:
            post.angry_emoji_count = args['angry_emoji_count']
        if args.get('smile_with_heart_eyes_count', None) is not None:
            post.smile_with_heart_eyes_count = args['smile_with_heart_eyes_count']
        if args.get('screaming_emoji_count', None) is not None:
            post.screaming_emoji_count = args['screaming_emoji_count']
        session.add(post)
        session.commit()
        association = PostAssociation(
            user_id=user.id,
            post_id=post.id
        )
        session.add(association)
        session.commit()
        logging.getLogger("NeoBrain").info(f"Post {post.id} created!")
        return jsonify({'status': 201,
                        'text': f'{post.id} created'})


class PostsSearchResource(Resource):
    pass

# Импортируем нужные библиотеки
from flask import jsonify
from flask_restful import reqparse, Resource, abort

from data import db_session
from data.posts import Post

from data.users import User
from resources.users_resource import abort_if_user_not_found


class LentaResource(Resource):
    def get(self, user_id):
        # Проверяем, есть ли пользователь
        abort_if_user_not_found(user_id)
        # Создаём сессию в БД и получаем посты людей(друзей)
        session = db_session.create_session()
        user = session.query(User).get(user_id)
        posts = []
        for people in user.people:
            user_people = session.query(User).get(people.user_id)
            cur_posts = session.query(Post).filter(Post.user_id == user_people.id).all()
            posts.extend(cur_posts)
        return jsonify({'posts': [post.to_dict(
            only=('id', 'title', 'text', 'created_date', 'user_id', 'photo_id')) for post in posts]})

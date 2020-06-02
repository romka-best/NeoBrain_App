# Импортируем нужные библиотеки
from flask import jsonify
from flask_restful import Resource

from data import db_session
from data.post_association import PostAssociation
from data.posts import Post
from data.users import User
from resources.posts_resource import get_post
from resources.users_resource import abort_if_user_not_found


class LentaResource(Resource):
    def get(self, user_id):
        # Проверяем, есть ли пользователь
        abort_if_user_not_found(user_id)
        # Создаём сессию в БД и получаем посты людей(друзей)
        session = db_session.create_session()
        user = session.query(User).get(user_id)
        posts = {'posts': []}
        for people in user.people:
            user_people = session.query(User).get(people.user_id)
            association = session.query(PostAssociation).filter(PostAssociation.user_id == user_people.id,
                                                                PostAssociation.is_author == 1).all()
            for post in association:
                cur_post = session.query(Post).get(post.post_id)
                cur_association = session.query(PostAssociation).filter(PostAssociation.post_id == cur_post.id,
                                                                        PostAssociation.user_id == user_id).first()
                if not cur_association:
                    cur_association = PostAssociation(
                        user_id=user_id,
                        post_id=cur_post.id,
                        is_author=False
                    )
                    session.add(cur_association)
                    session.commit()
                posts['posts'].append(get_post(cur_post, cur_association))
        return jsonify(posts)

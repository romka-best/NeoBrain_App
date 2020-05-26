# Импортируем нужные библиотеки
from flask import jsonify
from flask_restful import reqparse, Resource, abort

from data import db_session
from data.post_association import PostAssociation
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
                posts['posts'].append({"id": cur_post.id,
                                       "title": cur_post.title,
                                       "text": cur_post.text,
                                       "created_date": cur_post.created_date.strftime("%Y-%m-%d %H:%M:%S"),
                                       "modified_date": cur_post.modified_date.strftime("%Y-%m-%d %H:%M:%S"),
                                       "user_id": cur_association.user_id,
                                       "photo_id": cur_post.photo_id,
                                       "like_emoji_count": cur_post.like_emoji_count,
                                       "laughter_emoji_count": cur_post.laughter_emoji_count,
                                       "heart_emoji_count": cur_post.heart_emoji_count,
                                       "disappointed_emoji_count": cur_post.disappointed_emoji_count,
                                       "smile_emoji_count": cur_post.smile_emoji_count,
                                       "angry_emoji_count": cur_post.angry_emoji_count,
                                       "smile_with_heart_eyes_count": cur_post.smile_with_heart_eyes_count,
                                       "screaming_emoji_count": cur_post.screaming_emoji_count,
                                       "like_emoji": cur_association.like_emoji,
                                       "laughter_emoji": cur_association.laughter_emoji,
                                       "heart_emoji": cur_association.heart_emoji,
                                       "disappointed_emoji": cur_association.disappointed_emoji,
                                       "smile_emoji": cur_association.smile_emoji,
                                       "angry_emoji": cur_association.angry_emoji,
                                       "smile_with_heart_eyes": cur_association.smile_with_heart_eyes,
                                       "screaming_emoji": cur_association.screaming_emoji
                                       })
        return jsonify(posts)

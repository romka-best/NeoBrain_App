# Импортируем нужные библиотеки
import logging
from base64 import encodebytes

from flask import jsonify
from flask_restful import reqparse, Resource, abort

from auth import token_auth
from data import db_session
from data.apps import App
from data.chats import Chat
from data.music import Music
from data.photo_association import PhotoAssociation
from data.photos import Photo
from data.posts import Post
from data.users import User


# Если photo не найдено, то приходит ответа сервера
def abort_if_photo_not_found(photo_id):
    session = db_session.create_session()
    photo = session.query(Photo).get(photo_id)
    if not photo:
        logging.getLogger("NeoBrain").warning(f"Photo {photo_id} not found")
        abort(404, message=f"Photo {photo_id} not found")


# Основной ресурс для работы с Photo
class PhotoResource(Resource):
    @token_auth.login_required
    def get(self, photo_id):
        if str(photo_id).find("?") != -1:
            photo_id = int(photo_id[:photo_id.find("?")].strip())
        # Проверяем, есть ли фото
        abort_if_photo_not_found(photo_id)
        # Создаём сессию и получаем фото закодированую в Base64
        session = db_session.create_session()
        photo = encodebytes(session.query(Photo).get(photo_id).data).decode()
        logging.getLogger("NeoBrain").debug(f"Photo {photo_id} returned")
        return jsonify({'photo': photo})

    @token_auth.login_required
    def delete(self, photo_id):
        # Проверяем, есть ли фото
        abort_if_photo_not_found(photo_id)
        # Создаём сессию и получаем фото
        session = db_session.create_session()
        photo = session.query(Photo).get(photo_id)
        # Изменяем картинки на стандартные, когда удаляем
        users = session.query(PhotoAssociation).filter(PhotoAssociation.photo_id == photo_id,
                                                       PhotoAssociation.is_avatar == True).all()
        if users:
            for user in users:
                user.photo_id = 2
        posts = session.query(Post).filter(Post.photo_id == photo_id).all()
        if posts:
            for post in posts:
                post.photo_id = 2
        chats = session.query(Chat).filter(Chat.photo_id == photo_id).all()
        if chats:
            for chat in chats:
                chat.photo_id = 2
        music = session.query(Music).filter(Music.photo_id == photo_id).all()
        if music:
            for sound in music:
                sound.photo_id = 10
        apps = session.query(App).filter(App.photo_id == photo_id).all()
        if apps:
            for app in apps:
                app.photo_id = 12
        session.commit()
        session.delete(photo)
        session.commit()
        logging.getLogger("NeoBrain").debug(f"Photo {photo_id} deleted")
        return jsonify({'status': 200,
                        'text': 'deleted'})


# Создаём фото
class PhotoCreateResource(Resource):
    def __init__(self):
        # Инициализируем parser, так как доступ к данным,
        # переданным в теле POST-запроса, осуществляется с помощью парсера аргументов
        self.parser = reqparse.RequestParser()
        # Фотография типа BLOB
        self.parser.add_argument('data', required=True)
        # Описание к фотографии
        self.parser.add_argument('about', required=False)

    # Создаём фото
    @token_auth.login_required
    def post(self):
        # Получаем аргументы и создаём сессию в БД
        args = self.parser.parse_args()
        session = db_session.create_session()
        # Создаём фото
        photo = Photo(
            data=args['data']
        )
        session.add(photo)
        session.commit()
        logging.getLogger("NeoBrain").debug(f"Photo {photo.id} created!")
        return jsonify({'status': 201,
                        'text': f'{photo.id} created'})

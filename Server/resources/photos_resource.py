# Импортируем нужные библиотеки
from base64 import encodebytes

from flask import jsonify
from flask_restful import reqparse, Resource, abort

from data import db_session
from data.photos import Photo


# Если photo не найдено, то приходит ответа сервера
def abort_if_photo_not_found(photo_id):
    session = db_session.create_session()
    photo = session.query(Photo).get(photo_id)
    if not photo:
        abort(404, message=f"Photo {photo_id} not found")


# Основной ресурс для работы с Photo
class PhotoResource(Resource):
    def get(self, photo_id):
        if str(photo_id).find("?") != -1:
            photo_id = int(photo_id[:photo_id.find("?")].strip())
        # Проверяем, есть ли фото
        abort_if_photo_not_found(photo_id)
        # Создаём сессию и получаем фото закодированую в Base64
        session = db_session.create_session()
        photo = encodebytes(session.query(Photo).get(photo_id).data).decode()
        return jsonify({'photo': photo})

    def delete(self, photo_id):
        if str(photo_id).find("?") != -1:
            photo_id = int(photo_id[:photo_id.find("?")].strip())
        # Проверяем, есть ли фото
        abort_if_photo_not_found(photo_id)
        # Создаём сессию и получаем фото
        session = db_session.create_session()
        photo = session.query(Photo).get(photo_id)
        session.delete(photo)
        session.commit()
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
        return jsonify({'status': 201,
                        'text': f'created'})

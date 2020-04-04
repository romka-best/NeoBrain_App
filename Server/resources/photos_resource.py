# Импортируем нужные библиотеки
from base64 import encodebytes

from flask import jsonify
from flask_restful import reqparse, Resource

from data import db_session
from data.photos import Photo


# Основной ресурс для работы с Photo
class PhotoResource(Resource):
    def __init__(self):
        # Инициализируем parser, так как доступ к данным,
        # переданным в теле POST-запроса, осуществляется с помощью парсера аргументов
        self.parser = reqparse.RequestParser()
        # Фотография типа BLOB
        self.parser.add_argument('data', required=True)
        # Описание к фотографии
        self.parser.add_argument('about', required=False)

    def get(self, photo_id):
        if str(photo_id).find("?") != -1:
            photo_id = int(photo_id[:photo_id.find("?")].strip())
        # Создаём сессию и получаем фото закодированую в Base64
        session = db_session.create_session()
        photo = encodebytes(session.query(Photo).get(photo_id).data).decode()
        return jsonify({'photo': photo})


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

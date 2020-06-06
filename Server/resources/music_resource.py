# Импортируем нужные библиотеки
from base64 import encodebytes

from flask import jsonify
from flask_restful import Resource, abort

from data import db_session
from data.music import Music
from data.users import User
from resources.users_resource import abort_if_user_not_found


def abort_if_music_not_found(music_id):
    session = db_session.create_session()
    music = session.query(Music).get(music_id)
    if not music:
        abort(404, message=f"Music {music_id} not found")


# Основной ресурс для работы с Music
class MusicResource(Resource):
    def get(self, music_id):
        # Проверяем, есть ли песня
        abort_if_music_not_found(music_id)
        # Создаём сессию и получаем песню закодированую в Base64
        session = db_session.create_session()
        music = session.query(Music).get(music_id)
        data = encodebytes(music.data).decode()
        return jsonify({'music': {"data": data,
                                  "title": music.title,
                                  "author": music.author,
                                  "duration": music.duration,
                                  "created_date": music.created_date,
                                  "photo_id": music.photo_id}})

    def delete(self, music_id):
        # Проверяем, есть ли песня
        abort_if_music_not_found(music_id)
        # Создаём сессию и получаем песню
        session = db_session.create_session()
        music = session.query(Music).get(music_id)
        session.delete(music)
        session.commit()
        return jsonify({'status': 200,
                        'text': 'deleted'})


class MusicListResource(Resource):
    def get(self, user_id):
        # Проверяем, есть ли user
        abort_if_user_not_found(user_id)
        # Создаём сессию и получаем песню закодированую в Base64
        session = db_session.create_session()
        user = session.query(User).get(user_id)
        music_user = user.music
        return jsonify({'music': [music.to_dict(
            only=('id', 'title', 'author', 'duration', 'created_date',
                  'photo_id'), rules='get_music') for music in music_user]})

# Импортируем нужные библиотеки
from flask import jsonify
from flask_restful import reqparse, Resource, abort

from data import db_session
from data.posts import Post

from data.users import User
from resources.users_resource import abort_if_user_not_found


class LentaResource(Resource):
    pass

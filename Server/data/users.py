import datetime

import sqlalchemy
from sqlalchemy import orm
from .db_session import SqlAlchemyBase
from werkzeug.security import generate_password_hash, check_password_hash
from flask_login import UserMixin
from sqlalchemy_serializer import SerializerMixin
import re


def correct_password(password):
    errors = []
    if len(password) < 8:
        errors.append("Less than 8 characters")
    if not (re.search(r'[a-z]', password) and re.search(r'[A-Z]', password)):
        errors.append("Uppercase only or lowercase only")
    if not re.search(r'[0-9]', password):
        errors.append("Missing digits")
    if errors:
        return errors
    balls = 8
    if re.search('[{}@#$%^&+=*()?!.,~_]', password):
        balls += 2
    if len(password) >= 12:
        balls += 2
    return "OK", balls


class User(SqlAlchemyBase, UserMixin, SerializerMixin):
    __tablename__ = 'users'

    id = sqlalchemy.Column(sqlalchemy.Integer, autoincrement=True, primary_key=True)
    name = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    surname = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    nickname = sqlalchemy.Column(sqlalchemy.String, nullable=False, unique=True)
    number = sqlalchemy.Column(sqlalchemy.String, unique=True, index=True, nullable=False)
    hashed_password = sqlalchemy.Column(sqlalchemy.String, nullable=False)
    email = sqlalchemy.Column(sqlalchemy.String, nullable=True, unique=True)
    is_closed = sqlalchemy.Column(sqlalchemy.Boolean, default=False)
    about = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    birthday = sqlalchemy.Column(sqlalchemy.DateTime, nullable=True)
    age = sqlalchemy.Column(sqlalchemy.Integer, nullable=True)
    in_black_list = sqlalchemy.Column(sqlalchemy.String, default=False)
    can_see_audio = sqlalchemy.Column(sqlalchemy.Boolean, default=True)
    can_see_groups = sqlalchemy.Column(sqlalchemy.Boolean, default=True)
    can_see_videos = sqlalchemy.Column(sqlalchemy.Boolean, default=True)
    can_write_message = sqlalchemy.Column(sqlalchemy.Boolean, default=True)
    city = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    republic = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    country = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    education = sqlalchemy.Column(sqlalchemy.String, nullable=True)
    followers_count = sqlalchemy.Column(sqlalchemy.Integer, default=0)
    subscriptions_count = sqlalchemy.Column(sqlalchemy.Integer, default=0)
    status = sqlalchemy.Column(sqlalchemy.Integer, default=1)
    authenticated = sqlalchemy.Column(sqlalchemy.Boolean, nullable=True)
    last_seen = sqlalchemy.Column(sqlalchemy.String, default=datetime.datetime.now)

    created_date = sqlalchemy.Column(sqlalchemy.DateTime,
                                     default=datetime.datetime.now)
    modified_date = sqlalchemy.Column(sqlalchemy.DateTime,
                                      default=datetime.datetime.now)

    chats = orm.relation("Chat", back_populates='user')
    messages_from = orm.relation("Message", back_populates='author')
    photo = orm.relation("Photo")
    photo_id = sqlalchemy.Column(sqlalchemy.Integer,
                                 sqlalchemy.ForeignKey("photos.id"))

    def set_password(self, password):
        self.hashed_password = generate_password_hash(password)

    def check_password(self, password):
        return check_password_hash(self.hashed_password, password)

    def is_authenticated(self):
        return self.authenticated

    def is_active(self):
        return True

    def is_anonymous(self):
        return False

    def get_id(self):
        return self.id

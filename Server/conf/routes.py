# Импортируем нужные библиотеки
import logging

from resources.lenta_resource import LentaResource
from resources.messages_resource import MessageListResource, MessageCreateResource, MessageResource, \
    MessagesSearchResource
from resources.music_resource import MusicResource, MusicListResource, MusicSearchResource
from resources.people_resource import PeopleResource, PeopleCreateResource
from resources.users_resource import UserResource, UsersListResource, UserLoginResource, UsersSearchResource
from resources.chats_resource import ChatResource, ChatsListResource, ChatCreateResource, ChatFindUsersResource, \
    ChatsSearchResource
from resources.photos_resource import PhotoResource, PhotoCreateResource
from resources.posts_resource import PostResource, PostCreateResource, PostsListResource, PostsSearchResource
from flask_restful import Api


# logger = logging.getLogger("routes")
# logger.setLevel(logging.DEBUG)


def generate_routes(app):
    # logger.debug("Add API resources")

    # Инициализируем объект Api
    api = Api(app)

    # Добавляем к api ресурсы
    # Ресурсы с User
    api.add_resource(UserResource, '/api/users/<int:user_id>')
    api.add_resource(UserLoginResource, '/api/users/login')
    api.add_resource(UsersSearchResource, '/api/users/search/<string:user_name_surname>')
    api.add_resource(UsersListResource, '/api/users')

    # Ресурсы с Chat
    api.add_resource(ChatResource, '/api/chats/<int:chat_id>')
    api.add_resource(ChatCreateResource, '/api/chats')
    api.add_resource(ChatsSearchResource, '/api/chats/search/<int:user_id>/<string:name_surname>')
    api.add_resource(ChatsListResource, '/api/chats/users/<int:user_id>')
    api.add_resource(ChatFindUsersResource, '/api/chats/search/<int:chat_id>')

    # Ресурсы с Message
    api.add_resource(MessageResource, '/api/messages/<int:message_id>')
    api.add_resource(MessagesSearchResource, '/api/messages/chats/<int:chat_id>/search/<string:text_message>')
    api.add_resource(MessageListResource, '/api/chats/<int:chat_id>/messages')
    api.add_resource(MessageCreateResource, '/api/messages')

    # Ресурсы с Photo
    api.add_resource(PhotoResource, '/api/photos/<int:photo_id>')
    api.add_resource(PhotoCreateResource, '/api/photos')

    # Ресурсы с Post
    api.add_resource(PostResource, '/api/posts/<int:post_id>')
    api.add_resource(PostCreateResource, '/api/posts')
    api.add_resource(PostsSearchResource, '/api/posts/search/<string:post_text>')
    api.add_resource(PostsListResource, '/api/posts/users/<int:user_id>')

    # Ресурсы с Lenta
    api.add_resource(LentaResource, '/api/lenta/<int:user_id>')

    # Ресурсы с People
    api.add_resource(PeopleResource, '/api/people/<int:user_id>')
    api.add_resource(PeopleCreateResource, '/api/people')

    # Ресурсы с Music
    api.add_resource(MusicResource, '/api/music/<int:music_id>')
    api.add_resource(MusicSearchResource, '/api/music/search/<string:title_music>')
    api.add_resource(MusicListResource, '/api/music/users/<int:user_id>')

    # Ресурсы с App


    # Ресурсы с Group

    # logger.debug("Added API resources")

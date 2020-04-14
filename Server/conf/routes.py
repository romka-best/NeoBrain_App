# Импортируем нужные библиотеки
import logging

from resources.messages_resource import MessageListResource, MessageCreateResource, MessageResource
from resources.people_resource import PeopleResource
from resources.users_resource import UserResource, UsersListResource, UserLoginResource, UserSearchResource
from resources.chats_resource import ChatResource, ChatsListResource, ChatCreateResource
from resources.photos_resource import PhotoResource, PhotoCreateResource
from resources.posts_resource import PostResource, PostCreateResource, PostsListResource
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
    api.add_resource(UserSearchResource, '/api/users/search/<string:user_name_surname>')
    api.add_resource(UsersListResource, '/api/users')

    # Ресурсы с Chat
    api.add_resource(ChatResource, '/api/chats/<int:chat_id>')
    api.add_resource(ChatCreateResource, '/api/chats')
    api.add_resource(ChatsListResource, '/api/chats/users/<int:user_id>')

    # Ресурсы с Message
    api.add_resource(MessageResource, '/api/messages/<int:message_id>')
    api.add_resource(MessageListResource, '/api/chats/<int:chat_id>/messages')
    api.add_resource(MessageCreateResource, '/api/messages')

    # Ресурсы с Photo
    api.add_resource(PhotoResource, '/api/photos/<int:photo_id>')
    api.add_resource(PhotoCreateResource, '/api/photos')

    # Ресурсы с Post
    api.add_resource(PostResource, '/api/posts/<int:post_id>')
    api.add_resource(PostCreateResource, '/api/posts')
    api.add_resource(PostsListResource, '/api/posts/users/<int:user_id>')

    # Ресурсы с Lenta
    # api.add_resource(LentaResource, '/api/lenta/<int:user_id>')

    # Ресурсы с People
    api.add_resource(PeopleResource, '/api/people/<int:user_id>')

    # logger.debug("Added API resources")

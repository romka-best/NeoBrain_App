# Импортируем нужные библиотеки
from resources.users_resource import UserResource, UsersListResource, UserLoginResource
from resources.chats_resource import ChatResource, ChatsListResource, ChatCreateResource
from resources.photos_resource import PhotoResource
from resources.posts_resource import PostResource, PostCreateResource, PostsListResource
from flask_restful import Api


def generate_routes(app):
    # Инициализируем объект Api
    api = Api(app)

    # Добавляем к api ресурсы
    # Ресурсы с User
    api.add_resource(UserResource, '/api/users/<string:user_nickname>')
    api.add_resource(UsersListResource, '/api/users')
    api.add_resource(UserLoginResource, '/api/users/login')

    # Ресурсы с Chat
    api.add_resource(ChatResource, '/api/chats/<int:chat_id>')
    api.add_resource(ChatCreateResource, '/api/chats')
    api.add_resource(ChatsListResource, '/api/chats/<string:user_nickname>')

    # Ресурсы с Photo
    api.add_resource(PhotoResource, '/api/photos/<int:photo_id>')

    # Ресурсы с Post
    api.add_resource(PostResource, '/api/posts/<int:post_id>')
    api.add_resource(PostCreateResource, '/api/posts')
    api.add_resource(PostsListResource, '/api/posts/<string:user_nickname>')
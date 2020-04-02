from resources.users_resource import UserResource, UsersListResource, UserLoginResource, UserLogoutResource
from resources.chats_resource import ChatResource, ChatsListResource, ChatCreateResource
from resources.photos_resource import PhotoResource
from flask_restful import Api


def generate_routes(app):
    api = Api(app)

    api.add_resource(UserResource, '/api/users/<string:user_nickname>')
    api.add_resource(UsersListResource, '/api/users')
    api.add_resource(UserLoginResource, '/api/users/login')
    api.add_resource(UserLogoutResource, '/api/users/logout')

    api.add_resource(ChatResource, '/api/chats/<int:chat_id>')
    api.add_resource(ChatCreateResource, '/api/chats')
    api.add_resource(ChatsListResource, '/api/chats/<string:user_nickname>')

    api.add_resource(PhotoResource, '/api/photos/<int:photo_id>')

# Импортируем нужные библиотеки

from flask_restful import Api

from resources.achievements_resource import AchievementResource, AchievementUserResource, AchievementCreateResource
from resources.apps_resource import AppCreateResource, AppDeleteResource, AppResource
from resources.chats_resource import ChatResource, ChatsListResource, ChatCreateResource, ChatFindUsersResource, \
    ChatUsersResource, ChatTwoUsersResource
from resources.country_resource import CountryResource, CountryListResource
from resources.lenta_resource import LentaResource
from resources.messages_resource import MessageListResource, MessageCreateResource, MessageResource
from resources.music_resource import MusicResource, MusicListResource, MusicSearchResource
from resources.people_resource import PeopleResource, PeopleCreateResource, PeopleDeleteResource
from resources.photos_resource import PhotoResource, PhotoCreateResource
from resources.posts_resource import PostResource, PostCreateResource, PostsListResource, PostsSearchResource
from resources.users_resource import UserResource, UsersListResource, UserLoginResource, UsersSearchResource


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
    api.add_resource(ChatUsersResource, '/api/chats/users_id/<int:user_id>')
    api.add_resource(ChatTwoUsersResource, '/api/chats/two_users/<int:user_id1>/<int:user_id2>')
    api.add_resource(ChatsListResource, '/api/chats/users/<int:user_id>')
    api.add_resource(ChatFindUsersResource, '/api/chats/search/<int:chat_id>')

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
    api.add_resource(PostsSearchResource, '/api/posts/search/<string:post_text>')
    api.add_resource(PostsListResource, '/api/posts/users/<int:author_id>/<int:user_id>')

    # Ресурсы с Lenta
    api.add_resource(LentaResource, '/api/lenta/<int:user_id>')

    # Ресурсы с People
    api.add_resource(PeopleResource, '/api/people/<int:user_id>')
    api.add_resource(PeopleDeleteResource, '/api/people/<int:user_id1>/<int:user_id2>')
    api.add_resource(PeopleCreateResource, '/api/people')

    # Ресурсы с Music
    api.add_resource(MusicResource, '/api/music/<int:music_id>')
    api.add_resource(MusicSearchResource, '/api/music/search/<string:title_music>')
    api.add_resource(MusicListResource, '/api/music/users/<int:user_id>')

    # Ресурсы с App
    api.add_resource(AppResource, '/api/apps/<int:user_id>')
    api.add_resource(AppDeleteResource, '/api/apps/<int:user_id>/<int:app_id>')
    api.add_resource(AppCreateResource, '/api/apps')

    # Ресурсы с Group

    # Ресурсы с Achievements
    api.add_resource(AchievementResource, '/api/achievements/<int:achievement_id>')
    api.add_resource(AchievementUserResource, '/api/achievements/user/<int:user_id>')
    api.add_resource(AchievementCreateResource, '/api/achievements')

    # Ресурсы с Corona
    api.add_resource(CountryResource, '/api/countries/<int:country_id>')
    api.add_resource(CountryListResource, '/api/countries')

    # logger.debug("Added API resources")

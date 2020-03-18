import datetime
import os

from flask import Flask, request, make_response, jsonify, redirect, render_template
from flask_login import LoginManager, login_user, logout_user, login_required, current_user

from data import db_session

from data.users import User
from resources.users_resource import UserResource, UsersListResource, UserLoginResource

from flask_restful import abort, Api

app = Flask(__name__)
app.config['SECRET_KEY'] = 'NeoBrainKey'
app.config['PERMANENT_SESSION_LIFETIME'] = datetime.timedelta(days=365)

api = Api(app)
api.add_resource(UserResource, '/api/users/<string:user_nickname>')
api.add_resource(UsersListResource, '/api/users')
api.add_resource(UserLoginResource, '/api/users/login')

login_manager = LoginManager()
login_manager.init_app(app)


def main():
    db_session.global_init("db/neobrain.db")
    port = int(os.environ.get("PORT", 5000))
    app.run(host='0.0.0.0', port=port)


@login_manager.user_loader
def load_user(user_id):
    session = db_session.create_session()
    return session.query(User).get(user_id)


@app.route('/logout')
@login_required
def logout():
    logout_user()
    return redirect("/")


@app.route('/register', methods=['GET', 'POST'])
def register():
    pass


@app.route('/login', methods=['GET', 'POST'])
def login():
    pass


@app.route("/")
def index():
    return "Test"


if __name__ == '__main__':
    main()

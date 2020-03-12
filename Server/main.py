import datetime

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
api.add_resource(UserResource, '/api/users/<int:user_id>', '/api/users')
api.add_resource(UsersListResource, '/api/users')
api.add_resource(UserLoginResource, '/api/users/login')

login_manager = LoginManager()
login_manager.init_app(app)


def main():
    db_session.global_init("db/neobrain.db")
    app.run(port=5000, host='0.0.0.0', debug=True)


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
    # json = request.get_json()
    # if not json:
    #     print("Ошибка получения")
    #     return jsonify({"status": 'error',
    #                     "description": "invalid json"})
    # name = json["name"]
    # surname = json["surname"]
    # nickname = json["nickname"]
    # password = json["password"]
    # number = json["number"]
    # "Если всё ок, создаём пользователя"
    # session = db_session.create_session()
    # user = User(
    #     name=name,
    #     surname=surname,
    #     nickname=nickname,
    #     number=number
    # )
    # user.set_password(password)
    # session.add(user)
    # session.commit()
    # return jsonify({"status": "ok"})


@app.route('/login', methods=['GET', 'POST'])
def login():
    pass
    # form = LoginForm()
    # if form.validate_on_submit():
    #     session = db_session.create_session()
    #     user = session.query(User).filter(User.email == form.email.data).first()
    #     if user and user.check_password(form.password.data):
    #         login_user(user, remember=form.remember_me.data)
    #         return redirect("/")
    #     return render_template('login.html',
    #                            message="Incorrect login or password",
    #                            form=form)
    # return render_template('login.html', title='Authorization', form=form)


if __name__ == '__main__':
    main()

from flask import Flask, render_template, redirect, request, jsonify
from flask_socketio import SocketIO
from data import db_session
from data.users import User
import requests

app = Flask(__name__)
app.config['SECRET_KEY'] = 'NeoBrainKey'


# socketio = SocketIO(app)


def main():
    # socketio.run(app)
    app.run(port=5000, host='127.0.0.1')


@app.route('/register', methods=['POST'])
def reqister():
    json = request.get_json()
    if not json:
        print("Ошибка получения")
        return jsonify({"status": 'error',
                        "description": "invalid json"})
    name = json["name"]
    surname = json["surname"]
    nickname = json["nickname"]
    password = json["password"]
    number = json["number"]
    "Если всё ок, создаём пользователя"
    session = db_session.create_session()
    user = User(
        name=name,
        surname=surname,
        nickname=nickname,
        number=number
    )
    user.set_password(password)
    session.add(user)
    session.commit()
    return jsonify({"status": "ok"})


if __name__ == '__main__':
    main()

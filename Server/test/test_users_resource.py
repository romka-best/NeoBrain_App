from requests import get, post, put, delete


def test():
    print(post("http://192.168.1.83:5000/api/users", json={
        "name": "Test_Name",
        "surname": "Test_Surname",
        "nickname": "Test_Nickname",
        "number": "1",
        "hashed_password": "123"
    }).json())

    # print(get("http://192.168.1.83:5000/api/users/1").json())

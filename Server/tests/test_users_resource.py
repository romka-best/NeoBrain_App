import pytest
from base64 import decodebytes
from requests import get, post, put, delete


def test():
    # print(post("http://172.17.0.127:5000/api/users", json={
    #     "name": "Test_Name",
    #     "surname": "Test_Surname",
    #     "nickname": "Test_Nickname",
    #     "number": "123",
    #     "hashed_password": "123"
    # }).json())

    # print(get("http://192.168.1.83:5000/api/users/Test_Nickname").json())

    r = get("http://192.168.1.84:5000/api/users/Test_Nickname", stream=True)
    if r.ok:
        r_new = r.json()
        with open("filename.jpg", 'wb') as f:
            f.write(decodebytes(r_new['photo'].encode()))

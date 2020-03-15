import pytest
from requests import get, post, put, delete


def test():
    # print(post("http://172.17.0.127:5000/api/users", json={
    #     "name": "Test_Name",
    #     "surname": "Test_Surname",
    #     "nickname": "Test_Nickname",
    #     "number": "123",
    #     "hashed_password": "123"
    # }).json())

    print(get("http://192.168.1.83:5000/api/users/Test_Nickname").json())


test()

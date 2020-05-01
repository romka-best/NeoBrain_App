import pytest
from datetime import datetime
from requests import get, post, put, delete


def test():
    print(get("http://192.168.1.115:5000/api/chats/search/1"))
    # print(post("http://192.168.1.84:5000/api/chats", json={
    #     "name": "Test_Name_Chat",
    #     "type_of_chat": 0,
    #     "status": 0,
    #     "last_message": "Hello_Test!",
    #     "user_id": 2
    # }).json())

    # print(get("http://192.168.1.84:5000/api/chats/Test_Nickname").json())

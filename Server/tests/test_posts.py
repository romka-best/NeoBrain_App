import pytest
from datetime import datetime
from requests import get, post, put, delete


def test():
    print(get("http://192.168.1.115:5000/api/posts/2").json())

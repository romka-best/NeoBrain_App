from requests import get


def test():
    print(get("http://192.168.1.115:5000/api/achievements/user/10").json())

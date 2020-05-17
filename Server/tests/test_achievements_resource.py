from requests import get


def test():
    print(get("http://192.168.1.115:5000/api/achievements/2").json())

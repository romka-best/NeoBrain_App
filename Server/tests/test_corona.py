from requests import get


def test():
    print(123)
    print(get("http://127.0.0.1:5000/api/countries/24").json())
    print(get("http://127.0.0.1:5000/api/countries").json())

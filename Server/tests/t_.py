from requests import get, post, delete, put

print(post('http://127.0.0.1:5000/api/users/create',
           json={'name': 'Roman',
                 'surname': 'Danilov',
                 'nickname': 'romka.best',
                 'number': "+79639035431",
                 'password': "123456"}).json())

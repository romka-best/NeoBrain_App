import requests
from PIL import Image


def download_pic(url, filename, chunk_size=1024):
    r = requests.get(url, stream=True)
    if r.ok:
        with open(filename, 'wb') as f:
            for chunk in r.iter_content(chunk_size):
                print(chunk)
                f.write(chunk)


filename = 'logo.png'

download_pic("https://sun9-51.userapi.com/c852120/v852120978/5e21a/SQIFQs_7r-Y.jpg", filename)

img = Image.open(filename)

import datetime


def generate_config(app):
    app.config['SECRET_KEY'] = 'NeoBrainKey'
    app.config['PERMANENT_SESSION_LIFETIME'] = datetime.timedelta(days=365)
    app.config['MAIL_SERVER'] = 'smtp.googlemail.com'
    app.config['MAIL_PORT'] = 587
    app.config['MAIL_USE_TLS'] = True
    app.config['MAIL_USERNAME'] = 'neo.brain.team@gmail.com'
    app.config['MAIL_DEFAULT_SENDER'] = 'neo.brain.team@gmail.com'
    app.config['MAIL_PASSWORD'] = '9BP-Ha9-5Tn-6rm'

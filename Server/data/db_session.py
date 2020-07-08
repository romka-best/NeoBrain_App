# Импортируем нужные библиотеки
import sqlalchemy as sa
import sqlalchemy.orm as orm
from sqlalchemy.orm import Session
import sqlalchemy.ext.declarative as dec

# Создадим две переменные: SqlAlchemyBase — некоторую абстрактную декларативную базу,
# в которую позднее будем наследовать все наши модели,
# и __factory, которую будем использовать для получения сессий подключения к нашей базе данных.
SqlAlchemyBase = dec.declarative_base()

__factory = None


# global_init принимает на вход адрес базы данных,
# затем проверяет, не создали ли мы уже фабрику подключений
# (то есть не вызываем ли мы функцию не первый раз).
# Если уже создали, то завершаем работу, так как начальную инициализацию
# надо проводить только единожды.
def global_init(db_file):
    global __factory

    if __factory:
        return

    if not db_file or not db_file.strip():
        raise Exception("Необходимо указать файл базы данных.")

    conn_str = f'sqlite:///{db_file.strip()}?check_same_thread=False'

    engine = sa.create_engine(conn_str, echo=False)
    __factory = orm.sessionmaker(bind=engine)

    from . import __all_models

    SqlAlchemyBase.metadata.create_all(engine)


def create_session() -> Session:
    global __factory
    return __factory()

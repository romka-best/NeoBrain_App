from flask import jsonify
from flask_login import login_required
from flask_restful import Resource

from data import db_session
from data.countries import Country


class CountryResource(Resource):

    decorators = [login_required]

    def get(self, country_id):
        session = db_session.create_session()
        country = session.query(Country).get(country_id)
        return jsonify({'country': country.to_dict()})


class CountryListResource(Resource):

    decorators = [login_required]

    def get(self):
        session = db_session.create_session()
        countries = session.query(Country).all()
        return jsonify({'countries': [country.to_dict() for country in countries]})

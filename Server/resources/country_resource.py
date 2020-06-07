import logging

from flask import jsonify
from flask_restful import Resource

from data import db_session
from data.countries import Country


class CountryResource(Resource):
    def get(self, country_id):
        session = db_session.create_session()
        country = session.query(Country).get(country_id)
        logging.getLogger("NeoBrain").debug(f"Info about country {country_id} returned")
        return jsonify({'country': country.to_dict()})


class CountryListResource(Resource):
    def get(self):
        session = db_session.create_session()
        countries = session.query(Country).all()
        logging.getLogger("NeoBrain").debug(f"Info about all countries returned")
        return jsonify({'countries': [country.to_dict() for country in countries]})

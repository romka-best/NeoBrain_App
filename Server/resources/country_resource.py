from flask import jsonify
from flask_restful import Resource

from data import db_session
from data.countries import Country


class CountryResource(Resource):
    def get(self, country_id):
        db_session.global_init("db/neobrain.db")
        session = db_session.create_session()
        if str(country_id).find("?") != -1:
            country_id = int(country_id[:country_id.find("?")].strip())
        country = session.query(Country).get(country_id)
        return jsonify({'country': country.to_dict()})


class CountryListResource(Resource):
    def get(self):
        db_session.global_init("db/neobrain.db")
        session = db_session.create_session()
        countries = session.query(Country).all()
        return jsonify({'countries': [country.to_dict() for country in countries]})

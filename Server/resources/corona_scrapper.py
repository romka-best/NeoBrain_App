import requests

from data import db_session
from data.countries import Country


def scrap_countries():
    corona_server = "https://corona.lmao.ninja/v2/countries?yesterday=false&sort"
    response = requests.get(corona_server)
    db_session.global_init("db/neobrain.db")
    session = db_session.create_session()
    if not response:
        pass
    json_response = response.json()
    for county in json_response:

        name = county['country']
        county_flag = county['countryInfo']['flag']
        cases = county['cases']
        today_cases = county['todayCases']
        deaths = county['deaths']

        countries = Country()

        if session.query(Country).filter(Country.country_name == name).first():

            county_exist = session.query(Country).filter(Country.country_name == name).first()
            county_exist.cases = cases
            county_exist.deaths_count = deaths
            county_exist.cases_today = today_cases

            session.commit()
        else:

            countries.country_name = name
            countries.flag = county_flag
            countries.cases = cases
            countries.deaths_count = deaths
            countries.cases_today = today_cases

            session.add(countries)
            session.commit()

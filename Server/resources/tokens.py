# import json
# from functools import wraps
#
# import jwt
# from flask import request, jsonify, Response
#
#
# def token_required(f):
#     @wraps(f)
#     def decorated(*args, **kwargs):
#         token = request.args.get('token')
#         if not token:
#             message = json.dumps({'message': "Token is missing"})
#             return Response(message, status=403, mimetype='application/json')
#         try:
#             from main import app
#             data = jwt.decode(token, app.config['SECRET_KEY'])
#         except Exception:
#             message = json.dumps({'message': "Token is invalid!"})
#             return Response(message, status=403, mimetype='application/json')
#         return f(*args, **kwargs)
#     return decorated

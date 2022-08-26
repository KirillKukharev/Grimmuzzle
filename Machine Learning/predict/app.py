from flask import Flask, request, abort, jsonify
from flask_cors import cross_origin
import os

app = Flask(__name__)
model = __import__(os.environ.get("MODELFILE", 'MLModel'))


@app.errorhandler(400)
def bad_requests(e):
    return jsonify(error=str(e)), 400


@app.route('/get-data', methods=['GET', 'POST'])
@cross_origin()
def get_data():
    data = request.json
    try:
        text = data["input"]
        length = int(data["length"])
    except KeyError:
        abort(400, "Invalid input")
    if text is None or length is None:
        abort(400, "Some input parameters are empty")

    return str(model.generation_text(text, length))


@app.route('/hello')
@cross_origin()
def string():
    return "hello, this is a test string"


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=int(os.environ.get("PORT", 8080)))

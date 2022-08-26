FROM python:3.6-slim-buster

COPY requirements.txt /app/requirements.txt

WORKDIR /app

COPY . ./

RUN python -m pip install -r requirements.txt

ENV PYTHONUNBUFFERED True
ENV WORKERS 2
ENV THREADS 1
ENV MODELFILE MLModel

CMD exec gunicorn --bind :$PORT --workers $WORKERS --threads $THREADS --timeout 0 app:app
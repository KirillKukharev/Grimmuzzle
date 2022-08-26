# Fairytale Generator

## Storage of models
All trained models are located in google cloud bucket:\
[Click here to open](https://console.cloud.google.com/storage/browser/aitextgen_models;tab=objects?forceOnBucketsSortingFiltering=false&project=fairy-tales-312113&prefix=&forceOnObjectsSortingFiltering=false)

## Where to place the model
Put chosen model  to the `./model` folder.\
It must be two files: `config.json` and `pytorch_model.bin`\
After that step you are ready to build image and start to deploy application.

## Run Fairytale Docker image locally 
### 1. Build Fairytale Generator image
in `./predict` folder enter
`docker build -t text-generator:0.1 .`

### 2. Run docker container
in `./predict` folder enter\
`docker run -e PORT=8080 text-generator:0.1`

### 3. Call docker container
```
POST http://127.0.0.1:8080/get-data
{
    "input": "Some string",
    "length": 22
}
```


## Deploy app with Google Run
Before deployment, you should verify that you have the Google Cloud SDK configured.\
[Visit this webpage to configure](https://cloud.google.com/sdk/docs/authorizing#:~:text=To%20authorize%20using%20a%20service,in%20the%20Google%20Cloud%20Console.&text=Choose%20an%20existing%20account%20or,by%20clicking%20Create%20service%20account.&text=If%20required%2C%20move%20the%20key,are%20authorizing%20Cloud%20SDK%20tools.)
### 1. Build Fairytale Generator Google Cloud image
in `./predict` folder enter
```
gcloud builds submit --tag gcr.io/PROJECT_NAME/SERVER_NAME
```
Notice: Make sure everything is fine, and the image has been completely built successfully.

### 2. Deploy Fairytale Generator image in Cloud Run
in `./predict` folder enter

```
gcloud run deploy CREATIVENAME 
    --image gcr.io/PROJECT_NAME/SERVER_NAME:latest 
    --platform managed 
    --cpu 4 
    --memory 8Gi  
    --update-env-vars "WORKERS=4" 
    --update-env-vars "THREADS=1"
    --region europe-north1  
    --concurrency 25
```
where `--cpu` - cpu allocated,\
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;`--memory` - memory allocated,  
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;`--workers` - spec variable for threads,\
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;`--threads` - allows sending multiple requests,\
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;`--region` - area, where allocated container,\
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;`--concurrency` - allow to receive many requests



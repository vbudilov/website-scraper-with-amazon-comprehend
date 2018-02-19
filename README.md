#### Amazon Comprehend demo
This project lets you analyze websites using [Amazon Comprehend](https://aws.amazon.com/comprehend). 

You can deploy this project using [SAM](https://github.com/awslabs/serverless-application-model). 
Once deployed, you're able to query the service by including a header value of 'url', which indicates the url of the website that you want to "understand/comprehend". The
Lambda function parses the website represented by the url and runs the body of this website through the [Amazon Comprehend](https://aws.amazon.com/comprehend) service.

_NOTE:_ For the most part your test usage _should_ fall under the [Free Tier](https://aws.amazon.com/free/), 
but check [this](https://aws.amazon.com/free/) page to make sure, as well as your current billing levels. 

### TO-DO
Additions you might want to implement (I might add some of this in the future)

* Add another property to define an ElasticSearch endpoint and automatically pipe the output there
* Save the data in [Amazon S3](https://aws.amazon.com/s3/) and analyze the data using [Amazon Athena](https://aws.amazon.com/athena/) and
 [Amazon Quicksight](https://aws.amazon.com/quicksight/)
* Use [Amazon Rekognition](https://aws.amazon.com/rekognition/) to add additional analysis by analyzing images found on the website
* Understand the websites that you're parsing and extract only the content that makes the most sense. I'm using [JSoup](https://jsoup.org/) 
so it should make the additional parsing easier
* Use [Amazon Translate](https://aws.amazon.com/translate/) for additional insight of international, non-English, websites
* Use a crawler instead of a scraper - more website coverage

### Build and deploy
```
# Build the code
./gradlew jar

# Package it
aws cloudformation package --template-file sam.yaml --s3-bucket YOUR_BUCKET_NAME --output-template-file /tmp/UpdatedSAMTemplate.yaml

# Deploy it (change the parameter value as needed)
aws cloudformation deploy --template-file /tmp/UpdatedSAMTemplate.yaml --stack-name comprehend-stack --parameter-overrides RegionParameter=us-east-1 --capabilities CAPABILITY_IAM

```

### Test it
```
# Get the Comprehend output
curl -XGET 'https://$API_GATEWAY_ID.execute-api.$REGION.amazonaws.com/Prod/token/valid' --header "url: url-to-parse"

```

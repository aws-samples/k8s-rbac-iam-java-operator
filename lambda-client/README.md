## Kubernetes client using AWS Lambda

A Kubernetes Java client implemented as an AWS Lambda function whose execution is triggered whenever an IAM user is added or removed from an IAM group. This is made possible using Amazon EventBridge, which is a serverless event bus service that makes it easy to deliver a stream of real-time data from the IAM service and route that data to targets such as AWS Lambda.

## Build Requirements

<ul>
  <li><a href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">Java SE Development Kit 8</a></li>
  <li><a href="https://maven.apache.org/download.cgi">Apache Maven 3.6.2</a></li>
  <li><a href="https://www.docker.com/products/container-runtime">Docker 19.03</a></li>
</ul>

## Build and Installation Instructions

To build the JAR file, type <b>mvn clean</b> followed by <b>mvn package</b> at the command line. Upload the JAR file to an S3 bucket.

Update the file createFunction.json specifying appropriate values for the following environment variables:

<ul>
  <li>Code.S3Bucket: S3 bucket where the JAR file from the above build has been uploaded.
  <li>Environment.Variables.ASSUMED_ROLE</li> IAM role that is mapped to a Kuberneted group in the EKS cluster which has permissions to manage IamUserGroup custom resources in the kube-system namespace.
  <li>Environment.Variables.ACCESS_KEY_ID</li>
  <li>Environment.Variables.SECRET_ACCESS_KEY</li> Credentials of IAM user that has permissions to assume the above IAM role
  
</ul>
  



Deploy the Lambda function with the following command:
aws lambda create-function --cli-input-json file://createFunction.json 

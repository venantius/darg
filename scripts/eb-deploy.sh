#!/bin/bash -ex

export NOW=$(date +%s)
export APPLICATION_VERSION="$NOW-${CIRCLE_SHA1:0:8}"
export AWS_REGION="us-west-2"
export EB_BUCKET="ursacorp-elastic-beanstalk-$AWS_REGION"
export EB_KEY="$CIRCLE_PROJECT_REPONAME/$APPLICATION_VERSION.zip"
export EB_DESTINATION="s3://$EB_BUCKET/$EB_KEY"
export EB_ENVIRONMENT_NAME="$CIRCLE_PROJECT_REPONAME-env"
export EB_RETRIES=10
export EB_DELAY=10

zip -q -r eb.zip Dockerrun.aws.json
aws s3 cp eb.zip $EB_DESTINATION

aws elasticbeanstalk create-application-version \
    --application-name $CIRCLE_PROJECT_REPONAME \
    --version-label $APPLICATION_VERSION \
    --source-bundle S3Bucket=$EB_BUCKET,S3Key=$EB_KEY \
    --region $AWS_REGION

for i in `seq $EB_RETRIES`; do
    EB_DESCRIBE_CMD="aws elasticbeanstalk describe-environments --region $AWS_REGION --environment-name $EB_ENVIRONMENT_NAME"
    EB_STATUS=`$EB_DESCRIBE_CMD | jq -r '.Environments[0].Status'`
    EB_VERSION_TIMESTAMP=`$EB_DESCRIBE_CMD | jq -r '.Environments[0].VersionLabel' | cut -d '-' -f 1`
    if [ $NOW -lt $EB_VERSION_TIMESTAMP ]; then
        echo "Skipping $EB_ENVIRONMENT_NAME deploy; later version already deployed..."
        break
    elif [ "$EB_STATUS" = "Ready" ]; then
        echo "Updating $EB_ENVIRONMENT_NAME environment..."

        # Update Elastic Beanstalk environment to new version
        aws elasticbeanstalk update-environment \
            --environment-name $EB_ENVIRONMENT_NAME \
            --version-label $APPLICATION_VERSION \
            --region $AWS_REGION

        break
    else
        echo "Environment status is $EB_STATUS. Sleeping for $EB_DELAY seconds..."
        sleep $EB_DELAY
    fi
done


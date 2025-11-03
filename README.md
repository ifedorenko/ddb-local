Demonstrates unexpected GSI not active when using DynamoDb-local 3.1.0 on 
Apple MacBook Pro M3 MAX laptop.

## Steps to reproduce the problem

1. Build the project

    mvn clean package

2. Download and start DynamoDb-local

    java -D"java.library.path=./DynamoDBLocal_lib" -jar DynamoDBLocal.jar -sharedDb -inMemory -port 8000 -disableTelemetry

3. Execute this program

   java -cp target/ddb-local-1.0-SNAPSHOT.jar adhoc.dynamodb_local.DynamoDbLocalNotActive
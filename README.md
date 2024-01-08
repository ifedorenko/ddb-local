Demonstrates unexpected TrimmedDataAccessException when using DynamoDb-local 2.6.1 or 3.1.0.

Steps to reproduce the problem

1. Execute `adhoc.dynamodb_local.DDBLocalTrimmedDataAccessException`

Example output:

```
create table1
create table2
get table1 shard iterator
putItem table2(!) then table1
get table1 change records
Exception in thread "main" software.amazon.awssdk.services.dynamodb.model.TrimmedDataAccessException: The operation attempted to read past the oldest stream record in a shard. (Service: DynamoDBLocal, Status Code: 400, Request ID: null)
	at software.amazon.awssdk.services.dynamodb.model.TrimmedDataAccessException$BuilderImpl.build(TrimmedDataAccessException.java:164)
	at software.amazon.awssdk.services.dynamodb.model.TrimmedDataAccessException$BuilderImpl.build(TrimmedDataAccessException.java:112)
	at software.amazon.dynamodb.services.local.embedded.DDBExceptionMappingInvocationHandler.copyAwsServiceExceptionFields(DDBExceptionMappingInvocationHandler.java:191)
	at software.amazon.dynamodb.services.local.embedded.DDBExceptionMappingInvocationHandler.handleDynamoDBLocalServiceException(DDBExceptionMappingInvocationHandler.java:136)
	at software.amazon.dynamodb.services.local.embedded.DDBExceptionMappingInvocationHandler.invoke(DDBExceptionMappingInvocationHandler.java:241)
	at jdk.proxy1/jdk.proxy1.$Proxy21.getRecords(Unknown Source)
	at adhoc.dynamodb_local.DDBLocalTrimmedDataAccessException.main(DDBLocalTrimmedDataAccessException.java:115)
```

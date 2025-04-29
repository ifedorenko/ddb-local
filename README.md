Demonstrates unexpected LocalDBAccessException when using DynamoDb-local 2.5.2 or 2.6.0 on 
Apple MacBook Pro M3 MAX laptop.

Steps to reproduce the problem

1. Execute `adhoc.dynamodb_local.DDBLocalEmbeddedSQLiteException`

Example `adhoc.dynamodb_local.DDBLocalEmbeddedSQLiteException` output:
```
0
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
27
28
29
30
31
32
33
34
35
Apr 28, 2025 11:03:38 P.M. com.almworks.sqlite4java.Internal log
WARNING: [sqlite] SQLiteDBAccess$23@23a21ac9: job exception
com.almworks.sqlite4java.SQLiteException: [1] DB[1] prepare() SELECT COUNT(indexKey_0) FROM "test.mytable36" WHERE indexKey_0 IS NOT NULL AND indexKey_1 IS NOT NULL; [no such column: indexKey_0]
	at com.almworks.sqlite4java.SQLiteConnection.throwResult(SQLiteConnection.java:1436)
	at com.almworks.sqlite4java.SQLiteConnection.prepare(SQLiteConnection.java:580)
	at com.almworks.sqlite4java.SQLiteConnection.prepare(SQLiteConnection.java:635)
	at com.almworks.sqlite4java.SQLiteConnection.prepare(SQLiteConnection.java:622)
	at com.amazonaws.services.dynamodbv2.local.shared.access.sqlite.AmazonDynamoDBOfflineSQLiteJob.getPreparedStatement(AmazonDynamoDBOfflineSQLiteJob.java:159)
	at com.amazonaws.services.dynamodbv2.local.shared.access.sqlite.SQLiteDBAccess$23.doWork(SQLiteDBAccess.java:2493)
	at com.amazonaws.services.dynamodbv2.local.shared.access.sqlite.SQLiteDBAccess$23.doWork(SQLiteDBAccess.java:2477)
	at com.amazonaws.services.dynamodbv2.local.shared.access.sqlite.AmazonDynamoDBOfflineSQLiteJob.job(AmazonDynamoDBOfflineSQLiteJob.java:117)
	at com.almworks.sqlite4java.SQLiteJob.execute(SQLiteJob.java:372)
	at com.almworks.sqlite4java.SQLiteQueue.executeJob(SQLiteQueue.java:534)
	at com.almworks.sqlite4java.SQLiteQueue.queueFunction(SQLiteQueue.java:667)
	at com.almworks.sqlite4java.SQLiteQueue.runQueue(SQLiteQueue.java:623)
	at com.almworks.sqlite4java.SQLiteQueue.access$000(SQLiteQueue.java:77)
	at com.almworks.sqlite4java.SQLiteQueue$1.run(SQLiteQueue.java:205)
	at java.base/java.lang.Thread.run(Thread.java:1583)

oops
com.amazonaws.services.dynamodbv2.local.shared.exceptions.LocalDBAccessException: [1] DB[1] prepare() SELECT COUNT(indexKey_0) FROM "test.mytable36" WHERE indexKey_0 IS NOT NULL AND indexKey_1 IS NOT NULL; [no such column: indexKey_0]
	at com.amazonaws.services.dynamodbv2.local.shared.access.sqlite.AmazonDynamoDBOfflineSQLiteJob.get(AmazonDynamoDBOfflineSQLiteJob.java:84)
	at com.amazonaws.services.dynamodbv2.local.shared.access.sqlite.SQLiteDBAccess.getGSIItemCount(SQLiteDBAccess.java:2498)
	at com.amazonaws.services.dynamodbv2.local.shared.access.api.cp.ControlPlaneFunction.fillDescriptionHelper(ControlPlaneFunction.java:525)
	at com.amazonaws.services.dynamodbv2.local.shared.access.api.cp.DescribeTableFunction.apply(DescribeTableFunction.java:27)
	at com.amazonaws.services.dynamodbv2.local.shared.access.awssdkv1.client.LocalAmazonDynamoDB.describeTable(LocalAmazonDynamoDB.java:245)
	at com.amazonaws.services.dynamodbv2.local.shared.access.awssdkv2.client.LocalDynamoDbClient.describeTable(LocalDynamoDbClient.java:316)
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:103)
	at java.base/java.lang.reflect.Method.invoke(Method.java:580)
	at com.amazonaws.services.dynamodbv2.local.embedded.DDBExceptionMappingInvocationHandler.invoke(DDBExceptionMappingInvocationHandler.java:178)
	at jdk.proxy1/jdk.proxy1.$Proxy20.describeTable(Unknown Source)
	at adhoc.dynamodb_local.DDBLocalEmbeddedSQLiteException.doit0(DDBLocalEmbeddedSQLiteException.java:237)
	at adhoc.dynamodb_local.DDBLocalEmbeddedSQLiteException.main(DDBLocalEmbeddedSQLiteException.java:21)
done
```

package adhoc.dynamodb_local;

import com.google.common.base.Preconditions;
import software.amazon.awssdk.services.dynamodb.model.*;
import software.amazon.dynamodb.services.local.embedded.DynamoDBEmbedded;

import java.util.Arrays;
import java.util.Map;

public class DDBLocalTrimmedDataAccessException {

    private static final String LOCAL_ACCESS_KEY = "dummyAccessKey";
    private static final String LOCAL_SECRET_KEY = "secretkey1234567890";
    private static final String endpoint = "http://localhost:8000";

    public static void main(String[] args) throws InterruptedException {
        var server = DynamoDBEmbedded.create(true);
        var db = server.dynamoDbClient();
        var dbstreams = server.dynamoDbStreamsClient();

//        var db = DynamoDbClient.builder()
//                .endpointOverride(URI.create(endpoint))
//                .credentialsProvider(() -> AwsBasicCredentials.create(LOCAL_ACCESS_KEY, LOCAL_SECRET_KEY))
//                .region(Region.US_EAST_1)
//                .build();
//        var dbstreams = DynamoDbStreamsClient.builder()
//                .endpointOverride(URI.create(endpoint))
//                .credentialsProvider(() -> AwsBasicCredentials.create(LOCAL_ACCESS_KEY, LOCAL_SECRET_KEY))
//                .region(Region.US_EAST_1)
//                .build();

        var tableName1 = "table1" + System.currentTimeMillis();
        var tableName2 = "table2" + System.currentTimeMillis();

        //
        System.out.println("create table1");
        var table1Description = db.createTable(
                CreateTableRequest.builder()
                        .tableName(tableName1)
                        .billingMode(BillingMode.PAY_PER_REQUEST)
                        .streamSpecification(StreamSpecification.builder()
                                .streamEnabled(true)
                                .streamViewType(StreamViewType.NEW_AND_OLD_IMAGES)
                                .build())
                        .attributeDefinitions(Arrays.asList(
                                AttributeDefinition.builder()
                                        .attributeName("0")
                                        .attributeType(ScalarAttributeType.S)
                                        .build()
                        ))
                        .keySchema(Arrays.asList(
                                KeySchemaElement.builder()
                                        .attributeName("0")
                                        .keyType(KeyType.HASH)
                                        .build()
                        ))
                        .build()
        ).tableDescription();

        //
        System.out.println("create table2");
        db.createTable(
                CreateTableRequest.builder()
                        .tableName(tableName2)
                        .billingMode(BillingMode.PAY_PER_REQUEST)
                        .streamSpecification(StreamSpecification.builder()
                                .streamEnabled(true)
                                .streamViewType(StreamViewType.NEW_AND_OLD_IMAGES)
                                .build())
                        .attributeDefinitions(Arrays.asList(
                                AttributeDefinition.builder()
                                        .attributeName("0")
                                        .attributeType(ScalarAttributeType.S)
                                        .build()
                        ))
                        .keySchema(Arrays.asList(
                                KeySchemaElement.builder()
                                        .attributeName("0")
                                        .keyType(KeyType.HASH)
                                        .build()
                        ))
                        .build()
        );

        //
        System.out.println("get table1 shard iterator");
        var streamArn = table1Description.latestStreamArn();
        var streamDescription = dbstreams.describeStream(it -> {
            it.streamArn(streamArn);
        }).streamDescription();
        Preconditions.checkArgument(streamDescription.shards().size() == 1);
        var shard = streamDescription.shards().getFirst();
        var shardIterator = dbstreams.getShardIterator(it -> {
            it.streamArn(streamArn);
            it.shardId(shard.shardId());
            it.shardIteratorType(ShardIteratorType.TRIM_HORIZON);
        }).shardIterator();


        System.out.println("putItem table2(!) then table1");
        db.putItem(it -> {
            it.tableName(tableName2);
            it.item(Map.of(
                    "0", AttributeValue.fromS("key")
            ));
        });
        db.putItem(it -> {
            it.tableName(tableName1);
            it.item(Map.of(
                    "0", AttributeValue.fromS("key")
            ));
        });

        System.out.println("get table1 change records");
        var resp = dbstreams.getRecords(GetRecordsRequest.builder()
                .shardIterator(shardIterator)
                .build());

        System.err.println("done");

        server.shutdownNow();
    }

}

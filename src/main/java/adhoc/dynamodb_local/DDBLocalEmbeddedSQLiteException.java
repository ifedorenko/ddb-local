package adhoc.dynamodb_local;

import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Arrays;
import java.util.Map;

/**
 * Demonstrates unexpected LocalDBAccessException when using DynamoDb-local on
 * Apple MacBook Pro M3 MAX.
 */
public class DDBLocalEmbeddedSQLiteException {

    public static void main(String[] args) {
        var server = DynamoDBEmbedded.create(true);
        var db = server.dynamoDbClient();

        try {
            for (int i=0; i< 500; i++) {
                final var tableName = "test.mytable" + i;

                doit0(db, tableName);
                db.deleteTable((it) ->it.tableName(tableName));
                System.out.println(i);
            }
        } catch (Exception e) {
            System.err.println("oops");
            e.printStackTrace();
        }

        System.err.println("done");

        server.shutdownNow();
    }

    static void doit0(DynamoDbClient db, String tableName) {
// 1745891767401 thread qtp281759815-61
        {
            DescribeTableRequest request = DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build();
            try {
                db.describeTable(request);
            } catch (ResourceNotFoundException e) {
                // Ignore ResourceNotFoundException
            }
        }

// 1745891767402 thread qtp281759815-61
        {
            CreateTableRequest request = CreateTableRequest.builder()
                    .tableName(tableName)
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .streamSpecification(StreamSpecification.builder()
                            .streamEnabled(true)
                            .streamViewType(StreamViewType.NEW_AND_OLD_IMAGES)
                            .build())
                    .attributeDefinitions(Arrays.asList(
                            AttributeDefinition.builder()
                                    .attributeName("0")
                                    .attributeType(ScalarAttributeType.B)
                                    .build()
                            ,
                            AttributeDefinition.builder()
                                    .attributeName("1")
                                    .attributeType(ScalarAttributeType.B)
                                    .build()

                    ))
                    .keySchema(Arrays.asList(
                            KeySchemaElement.builder()
                                    .attributeName("0")
                                    .keyType(KeyType.HASH)
                                    .build()
                            ,
                            KeySchemaElement.builder()
                                    .attributeName("1")
                                    .keyType(KeyType.RANGE)
                                    .build()

                    ))
                    .build();
            db.createTable(request);
        }

// 1745891767409 thread qtp281759815-61
        {
            DescribeTableRequest request = DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build();
            try {
                db.describeTable(request);
            } catch (ResourceNotFoundException e) {
                // Ignore ResourceNotFoundException
            }
        }

// 1745891767411 thread qtp281759815-61
        {
            DescribeTimeToLiveRequest request = DescribeTimeToLiveRequest.builder()
                    .tableName(tableName)
                    .build();
            db.describeTimeToLive(request);
        }

// 1745891767411 thread qtp281759815-61
        {
            UpdateTimeToLiveRequest request = UpdateTimeToLiveRequest.builder()
                    .tableName(tableName)
                    .timeToLiveSpecification(TimeToLiveSpecification.builder()
                            .enabled(true)
                            .attributeName("#")
                            .build())
                    .build();
            db.updateTimeToLive(request);
        }

// 1745891767481 thread main
        { // sdk v1
            PutItemRequest request = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(Map.ofEntries(
                            Map.entry("0", AttributeValue.builder()
                                    .b(SdkBytes.fromByteArray(new byte[] {0, 1, 0, 19}))
                                    .build()
                            )
                            ,
                            Map.entry("1", AttributeValue.builder()
                                    .b(SdkBytes.fromByteArray(new byte[] {49, 0, 1}))
                                    .build()
                            )
                            ,
                            Map.entry("C", AttributeValue.builder()
                                    .s("a")
                                    .build()
                            )
                            ,
                            Map.entry("-", AttributeValue.builder()
                                    .n("1")
                                    .build()
                            )

                    ))
                    .returnConsumedCapacity(ReturnConsumedCapacity.TOTAL)
                    .returnValuesOnConditionCheckFailure(ReturnValuesOnConditionCheckFailure.ALL_OLD)
                    .conditionExpression("((attribute_not_exists(#0) AND attribute_not_exists(#1)) OR attribute_exists(#2)) AND (((attribute_not_exists(#0) AND attribute_not_exists(#1)) OR attribute_exists(#2)) OR (#3 = :0))")
                    .expressionAttributeNames(Map.ofEntries(
                            Map.entry("#0", "0")
                            ,
                            Map.entry("#1", "1")
                            ,
                            Map.entry("#2", "%")
                            ,
                            Map.entry("#3", "-")

                    ))
                    .expressionAttributeValues(Map.ofEntries(
                            Map.entry(":0", AttributeValue.builder()
                                    .n("1")
                                    .build()
                            )

                    ))
                    .build();
            db.putItem(request);
        }

// 1745891767500 thread qtp281759815-39
        {
            DescribeTableRequest request = DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build();
            try {
                db.describeTable(request);
            } catch (ResourceNotFoundException e) {
                // Ignore ResourceNotFoundException
            }
        }

// 1745891767507 thread qtp281759815-39
        {
            UpdateTableRequest request = UpdateTableRequest.builder()
                    .tableName(tableName)
                    .attributeDefinitions(Arrays.asList(
                            AttributeDefinition.builder()
                                    .attributeName("0")
                                    .attributeType(ScalarAttributeType.B)
                                    .build()
                            ,
                            AttributeDefinition.builder()
                                    .attributeName("1")
                                    .attributeType(ScalarAttributeType.B)
                                    .build()
                            ,
                            AttributeDefinition.builder()
                                    .attributeName("2")
                                    .attributeType(ScalarAttributeType.B)
                                    .build()
                            ,
                            AttributeDefinition.builder()
                                    .attributeName("3")
                                    .attributeType(ScalarAttributeType.B)
                                    .build()

                    ))
                    .globalSecondaryIndexUpdates(Arrays.asList(
                            GlobalSecondaryIndexUpdate.builder()
                                    .create(CreateGlobalSecondaryIndexAction.builder()
                                            .indexName("idx_0_0")
                                            .keySchema(Arrays.asList(
                                                    KeySchemaElement.builder()
                                                            .attributeName("2")
                                                            .keyType(KeyType.HASH)
                                                            .build()
                                                    ,
                                                    KeySchemaElement.builder()
                                                            .attributeName("3")
                                                            .keyType(KeyType.RANGE)
                                                            .build()

                                            ))
                                            .projection(Projection.builder()
                                                    .projectionType(ProjectionType.ALL)
                                                    .build())
                                            .build())
                                    .build()

                    ))
                    .build();
            db.updateTable(request);
        }

// 1745891767523 thread qtp281759815-39
        for (int i =0; i< 100; i++) {
            DescribeTableRequest request = DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build();
            db.describeTable(request);
        }
    }

}

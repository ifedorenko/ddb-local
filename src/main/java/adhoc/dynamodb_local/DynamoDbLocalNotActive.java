package adhoc.dynamodb_local;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.URI;
import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;

/**
 * Demonstrates Dynamodb-local failure to add secondary index to a table with TTL enabled.
 * <p>
 * Executes the following in a loop:
 * - createTable and awaits for the table and its GSIs to become ACTIVE
 * - updateTimeToLive
 * - updateTable and awaits for the table and its GSIs to become ACTIVE
 * <p>
 * On my Apple MacBook Pro M3 MAX laptop Dynamodb-local fails to activate
 * added GSI after 6 or 7 iterations of the loop.
 */
public class DynamoDbLocalNotActive {

    public static void main(String[] args) throws InterruptedException {

        var ddb = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:8000"))
                .credentialsProvider(() -> AwsBasicCredentials.create("dummyAccessKey", "secretkey1234567890"))
                .region(Region.US_EAST_1)
                .build();

        var counter = 0;
        while (true) {
            var tableName = String.format("test-%03d-%s", counter++, UUID.randomUUID().toString());

            System.out.printf("Table %s\n", tableName);

            // create a table with one GSI
            ddb.createTable(CreateTableRequest.builder()
                    .tableName(tableName)
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .globalSecondaryIndexes(Arrays.asList(
                            GlobalSecondaryIndex.builder()
                                    .indexName("idx_0_0-")
                                    .keySchema(Arrays.asList(
                                            KeySchemaElement.builder()
                                                    .attributeName("2-")
                                                    .keyType(KeyType.HASH)
                                                    .build()
                                            ,
                                            KeySchemaElement.builder()
                                                    .attributeName("3-")
                                                    .keyType(KeyType.RANGE)
                                                    .build()

                                    ))
                                    .projection(Projection.builder()
                                            .projectionType(ProjectionType.KEYS_ONLY)
                                            .build())
                                    .build()

                    ))
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
                            ,
                            AttributeDefinition.builder()
                                    .attributeName("2-")
                                    .attributeType(ScalarAttributeType.B)
                                    .build()
                            ,
                            AttributeDefinition.builder()
                                    .attributeName("3-")
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
                    .build()
            );
            awaitActive(ddb, tableName, "createTable");

            // enable table TTL
            ddb.updateTimeToLive(
                    UpdateTimeToLiveRequest.builder()
                            .tableName(tableName)
                            .timeToLiveSpecification(TimeToLiveSpecification.builder()
                                    .enabled(true)
                                    .attributeName("#")
                                    .build())
                            .build()
            );

            // update the table to add second GSI
            ddb.updateTable(
                    UpdateTableRequest.builder()
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
                                            .attributeName("2-")
                                            .attributeType(ScalarAttributeType.B)
                                            .build()
                                    ,
                                    AttributeDefinition.builder()
                                            .attributeName("3-")
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
                            .build()
            );

            // this gets stuck after 6 or 7 tables because the added GSI never becomes ACTIVE
            awaitActive(ddb, tableName, "updateTable");
        }
    }

    private static void awaitActive(DynamoDbClient ddb, String tableName, String op) throws InterruptedException {
        var stopwatch = System.currentTimeMillis();
        while (!isTableActive(ddb, tableName)) {
            Thread.sleep(Duration.ofSeconds(1));
            var elapsed = (System.currentTimeMillis() - stopwatch) / 1000;
            if (elapsed > 2) {
                System.out.printf("Table %s is not active after %s %ds\n", tableName, op, elapsed);
            }
        }
    }

    private static boolean isTableActive(DynamoDbClient ddb, String tableName) {
        var table = ddb.describeTable(it -> it.tableName(tableName)).table();
        if (table.tableStatus() != TableStatus.ACTIVE) {
            return false;
        }
        if (table.globalSecondaryIndexes().stream().anyMatch(it -> it.indexStatus() != IndexStatus.ACTIVE)) {
            return false;
        }
        return true;
    }

}

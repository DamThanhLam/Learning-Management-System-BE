package lms.payment_service_lms.config;

import lms.payment_service_lms.entity.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@Configuration
public class DynamoDBConfig {
    private static final String ORDER_TABLE_NAME = "Order";

    @Value("${aws.accessKeyId}")
    private String awsAccessKeyId;

    @Value("${aws.secretAccessKey}")
    private String awsSecretAccessKey;

    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .region(Region.of(awsRegion)) // Đặt khu vực từ application.properties
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsAccessKeyId, awsSecretAccessKey)))
                .build();
    }

    // Cấu hình DynamoDbEnhancedClient sử dụng DynamoDbClient
    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    // Cấu hình DynamoDbTable cho các bảng

    @Bean
    public DynamoDbTable<Order> courseTable(DynamoDbEnhancedClient dynamoDbEnhancedClient,
                                            DynamoDbClient dynamoDbClient) {
        createOrderTableIfNotExists(dynamoDbClient); // Tạo bảng nếu chưa có
        return dynamoDbEnhancedClient.table(ORDER_TABLE_NAME, TableSchema.fromBean(Order.class));
    }

    private void createOrderTableIfNotExists(DynamoDbClient dynamoDbClient) {
        ListTablesResponse tables = dynamoDbClient.listTables();
        if (!tables.tableNames().contains(ORDER_TABLE_NAME)) {
            CreateTableRequest createTableRequest = CreateTableRequest.builder()
                    .tableName(ORDER_TABLE_NAME)
                    .keySchema(KeySchemaElement.builder()
                            .attributeName("orderId")
                            .keyType(KeyType.HASH)
                            .build())
                    .attributeDefinitions(AttributeDefinition.builder()
                            .attributeName("orderId")
                            .attributeType(ScalarAttributeType.S)
                            .build())
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .build();

            dynamoDbClient.createTable(createTableRequest);
        }
    }
}

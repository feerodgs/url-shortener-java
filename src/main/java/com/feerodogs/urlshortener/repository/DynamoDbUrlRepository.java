package com.feerodogs.urlshortener.repository;

import com.feerodogs.urlshortener.domain.ShortenedUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.Map;
import java.util.Optional;

@Repository
public class DynamoDbUrlRepository implements UrlRepository {

    private final DynamoDbTable<ShortenedUrl> table;
    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public DynamoDbUrlRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient, DynamoDbClient dynamoDbClient, @Value("${aws.dynamodb.table-name}") String tableName) {
        this.table = dynamoDbEnhancedClient.table(tableName, TableSchema.fromBean(ShortenedUrl.class));
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
    }

    @Override
    public void save(ShortenedUrl shortenedUrl) {
        table.putItem(shortenedUrl);
    }

    @Override
    public Optional<ShortenedUrl> findByShortCode(String shortCode) {
        ShortenedUrl shortenedUrl = table.getItem(Key.builder().partitionValue(shortCode).build());

        return Optional.ofNullable(shortenedUrl);
    }

    @Override
    public void incrementClicks(String shortCode) {
        UpdateItemRequest request = UpdateItemRequest.builder().tableName(tableName).key(Map.of("shortCode", AttributeValue.builder().s(shortCode).build())).updateExpression("ADD clicks :increment").expressionAttributeValues(Map.of(":increment", AttributeValue.builder().n("1").build())).build();

        dynamoDbClient.updateItem(request);
    }
}
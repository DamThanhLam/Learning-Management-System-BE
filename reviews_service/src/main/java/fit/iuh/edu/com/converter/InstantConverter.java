package fit.iuh.edu.com.converter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;

public class InstantConverter implements AttributeConverter<Instant> {

    @Override
    public AttributeValue transformFrom(Instant input) {
        return AttributeValue.builder().s(input.toString()).build(); // ISO format
    }

    @Override
    public Instant transformTo(AttributeValue input) {
        return Instant.parse(input.s());
    }

    @Override
    public EnhancedType<Instant> type() {
        return EnhancedType.of(Instant.class);
    }


    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}

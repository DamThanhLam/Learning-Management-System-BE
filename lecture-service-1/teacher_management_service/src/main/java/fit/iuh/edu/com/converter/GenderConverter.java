package fit.iuh.edu.com.converter;

import fit.iuh.edu.com.enums.Gender;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class GenderConverter implements AttributeConverter<Gender> {

    @Override
    public AttributeValue transformFrom(Gender gender) {
        return AttributeValue.builder().s(gender.name()).build();
    }

    @Override
    public Gender transformTo(AttributeValue attributeValue) {
        if (attributeValue == null || attributeValue.s() == null) {
            return Gender.UNKNOWN;
        }
        try {
            return Gender.valueOf(attributeValue.s().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return Gender.UNKNOWN;
        }
    }

    @Override
    public EnhancedType<Gender> type() {
        return EnhancedType.of(Gender.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}

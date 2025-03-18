package fit.iuh.edu.com.converter;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import javax.print.attribute.Attribute;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter implements AttributeConverter<LocalDate> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    @Override
    public AttributeValue transformFrom(LocalDate localDate) {
        return localDate == null ? null : AttributeValue.builder().s(localDate.format(formatter)).build();
    }

    @Override
    public LocalDate transformTo(AttributeValue attributeValue) {
        return attributeValue == null ? null : LocalDate.parse(attributeValue.s(), formatter);
    }

    @Override
    public EnhancedType<LocalDate> type() {
        return EnhancedType.of(LocalDate.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }
}

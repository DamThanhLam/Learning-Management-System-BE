package email_service_lms.messaging;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.List;

public class AllowedClassesMessageConverter extends SimpleMessageConverter {

    private final List<String> allowedClasses;

    public AllowedClassesMessageConverter(List<String> allowedClasses) {
        this.allowedClasses = allowedClasses;
    }

    @Override
    public Object fromMessage(Message message) throws MessageConversionException {
        try {
            // Convert the message body (byte array) into an InputStream
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(message.getBody());
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            // Deserialize the object
            Object obj = objectInputStream.readObject();

            // Check if the deserialized object's class is allowed
            if (allowedClasses.contains(obj.getClass().getName())) {
                return obj;
            } else {
                throw new MessageConversionException("Class not allowed for deserialization");
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new MessageConversionException("Error during message deserialization", e);
        }
    }
}

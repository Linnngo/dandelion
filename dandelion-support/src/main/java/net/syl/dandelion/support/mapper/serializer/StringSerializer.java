package net.syl.dandelion.support.mapper.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class StringSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeString(value);
    }
}

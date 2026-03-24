package dev.dhev.realworld.shared;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.Optional;

public class OptionalStringDeserializer extends StdDeserializer<Optional<String>> {

    public OptionalStringDeserializer() {
        super(Optional.class);
    }

    @Override
    public Optional<String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return Optional.ofNullable(p.getText());
    }

    @Override
    public Optional<String> getNullValue(DeserializationContext ctxt) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getAbsentValue(DeserializationContext ctxt) {
        return null;
    }
}

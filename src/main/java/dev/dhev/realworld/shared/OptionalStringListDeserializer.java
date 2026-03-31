package dev.dhev.realworld.shared;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class OptionalStringListDeserializer extends StdDeserializer<Optional<List<String>>> {

    public OptionalStringListDeserializer() {
        super(Optional.class);
    }

    @Override
    public Optional<List<String>> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        return Optional.of(p.readValueAs(new TypeReference<List<String>>() {}));
    }

    @Override
    public Optional<List<String>> getNullValue(DeserializationContext ctxt) {
        return Optional.empty();
    }

    @Override
    @SuppressWarnings("OptionalAssignedToNull")
    public Optional<List<String>> getAbsentValue(DeserializationContext ctxt) {
        return null;
    }
}

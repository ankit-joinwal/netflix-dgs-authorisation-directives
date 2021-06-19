package com.example.scalar;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import java.util.UUID;
import java.util.function.Function;

public class UUIDCoercing implements Coercing<UUID, String> {

    @Override
    public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
        UUID uuid;

        if (dataFetcherResult instanceof UUID) {
            uuid = (UUID) dataFetcherResult;
        } else if (dataFetcherResult instanceof String) {
            uuid = parseUUIDString((String) dataFetcherResult, CoercingSerializeException::new);
        } else {
            throw new CoercingSerializeException(
                    "Expected something we can convert to 'java.util.UUID' but was '"
                            + dataFetcherResult.getClass().getSimpleName()
                            + "'.");
        }

        return uuid.toString();
    }

    @Override
    public UUID parseValue(Object input) throws CoercingParseValueException {
        UUID uuid;

        if (input instanceof UUID) {
            uuid = (UUID) input;
        } else if (input instanceof String) {
            uuid = parseUUIDString((String) input, CoercingParseValueException::new);
        } else {
            throw new CoercingParseValueException(
                    "Expected something we can convert to 'java.util.UUID' but was '"
                            + input.getClass().getSimpleName()
                            + "'.");
        }

        return uuid;
    }

    @Override
    public UUID parseLiteral(Object input) throws CoercingParseLiteralException {
        if (!(input instanceof StringValue)) {
            throw new CoercingParseValueException(
                    "Expected AST type 'StringValue' but was '" + input.getClass().getSimpleName() + "'.");
        }

        return parseUUIDString(((StringValue) input).getValue(), CoercingParseLiteralException::new);
    }

    private UUID parseUUIDString(
            String uuidString, Function<String, RuntimeException> exceptionFunction) {
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            throw exceptionFunction.apply(
                    "Invalid UUID value '" + uuidString + "' due to error: " + e.getMessage());
        }
    }
}

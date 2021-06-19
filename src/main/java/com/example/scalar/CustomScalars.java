package com.example.scalar;

import graphql.schema.GraphQLScalarType;

public class CustomScalars {

    public static final GraphQLScalarType UUID =
            GraphQLScalarType.newScalar()
                    .name("UUID")
                    .description("Custom UUID scalar")
                    .coercing(new UUIDCoercing())
                    .build();


}

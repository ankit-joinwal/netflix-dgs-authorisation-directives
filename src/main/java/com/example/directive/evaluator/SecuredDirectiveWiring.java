package com.example.directive.evaluator;

import com.example.exception.AccessDeniedError;
import com.netflix.graphql.dgs.context.DgsContext;
import com.netflix.graphql.dgs.internal.DgsRequestData;
import graphql.GraphQLContext;
import graphql.execution.DataFetcherResult;
import graphql.execution.ResultPath;
import graphql.language.SourceLocation;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLFieldsContainer;
import graphql.schema.GraphQLObjectType;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * As per graphql-java, implementation of a GraphQL schema directive such as <code>@secured</code> can be provided by using an implementation of
 * {@link SchemaDirectiveWiring}. <br/>
 * This class implements the <code>@secured</code> directive as following:
 * 1. Intercepts a field resolver.
 * 2. Extracts the @secured directive from a field if present.
 * 3. Evaluates the security expression provided in <code>@secured</code> directive for field.
 * 4. If security expression evaluates to <code>true</code>, original resolver function of the fiel is called.
 * 5. Else if security expression evaluates to <code>false</code>,an {@link com.example.exception.AccessDeniedError} is thrown and resolver function of field is <b>not invoked</b>
 */
@Component
public class SecuredDirectiveWiring implements SchemaDirectiveWiring {

    public static final String SECURED_DIRECTIVE = "secured";
    public static final String REQUIRES_ATTR = "requires";

    private final SecuredDirectiveEvaluator directiveEvaluator;

    public SecuredDirectiveWiring(SecuredDirectiveEvaluator directiveEvaluator) {
        this.directiveEvaluator = directiveEvaluator;
    }

    @Override
    public GraphQLObjectType onObject(SchemaDirectiveWiringEnvironment<GraphQLObjectType> environment) {
        GraphQLObjectType field = environment.getElement();
        GraphQLFieldsContainer parentType = environment.getFieldsContainer();


        if (field.getDirective(SECURED_DIRECTIVE) == null) {
            return field;
        }

        String expressionValue = (String) field.getDirective(SECURED_DIRECTIVE).getArgument(REQUIRES_ATTR).getValue();

        for (GraphQLFieldDefinition fieldDefinition : field.getFieldDefinitions()) {
            DataFetcher originalDataFetcher = environment.getCodeRegistry().getDataFetcher(parentType, fieldDefinition);

            DataFetcher authDataFetcher = dataFetchingEnvironment -> {

                DgsRequestData requestData =  DgsContext.getRequestData(dataFetchingEnvironment);
               String userUuid = requestData.getHeaders().getFirst("USER-UUID");

                DataFetcherResult.Builder<Object> resultBuilder = DataFetcherResult.newResult();
                boolean result = directiveEvaluator.evaluateExpression(expressionValue,userUuid, fieldDefinition.getName());

                if (result) {
                    return originalDataFetcher.get(dataFetchingEnvironment);
                } else {
                    List<SourceLocation> locations = List.of(field.getDefinition().getSourceLocation());
                    ResultPath resultPath = dataFetchingEnvironment.getExecutionStepInfo().getPath();
                    return resultBuilder.error(new AccessDeniedError(locations, resultPath)).build();
                }
            };

            // now change the field definition to have the new authorising data fetcher
            environment.getCodeRegistry().dataFetcher(parentType, fieldDefinition, authDataFetcher);
        }

        return field;
    }

    @Override
    public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
        GraphQLFieldDefinition field = environment.getElement();
        GraphQLFieldsContainer parentType = environment.getFieldsContainer();

        // build a data fetcher that first checks authorisation roles before then calling the original data fetcher
        DataFetcher originalDataFetcher = environment.getCodeRegistry().getDataFetcher(parentType, field);

        if (field.getDirective(SECURED_DIRECTIVE) == null) {
            return field;
        }

        DataFetcher authDataFetcher = dataFetchingEnvironment -> {
            DgsRequestData requestData =  DgsContext.getRequestData(dataFetchingEnvironment);
            String userUuid = requestData.getHeaders().getFirst("USER-UUID");

            String expressionValue = (String) dataFetchingEnvironment.getFieldDefinition().getDirective(SECURED_DIRECTIVE).getArgument(REQUIRES_ATTR).getValue();
            DataFetcherResult.Builder<Object> resultBuilder = DataFetcherResult.newResult();
            boolean result = directiveEvaluator.evaluateExpression(expressionValue, userUuid, field.getName());

            if (result) {
                return originalDataFetcher.get(dataFetchingEnvironment);
            } else {
                List<SourceLocation> locations = List.of(field.getDefinition().getSourceLocation());
                ResultPath resultPath = dataFetchingEnvironment.getExecutionStepInfo().getPath();
                return resultBuilder.error(new AccessDeniedError(locations, resultPath)).build();
            }
        };

        // now change the field definition to have the new authorising data fetcher
        environment.getCodeRegistry().dataFetcher(parentType, field, authDataFetcher);

        return field;
    }

}

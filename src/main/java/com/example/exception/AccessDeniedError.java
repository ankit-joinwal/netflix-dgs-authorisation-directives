package com.example.exception;

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.execution.ResultPath;
import graphql.language.SourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents error condition where an unauthorised user tries to access protected content
 */
public class AccessDeniedError implements GraphQLError {

    private static final String ERROR = "UNAUTHORIZED_ACCESS";

    private final String message;
    private final List<SourceLocation> locations;
    private final ResultPath resultPath;

    public AccessDeniedError(List<SourceLocation> locations, ResultPath resultPath) {
        this.message = "Exception while fetching data (" + resultPath.segmentToString() + "): not authorized";
        this.locations = locations;
        this.resultPath = resultPath;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return locations;
    }

    @Override
    public List<Object> getPath() {
        return resultPath.toList();
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.DataFetchingException;
    }

    @Override
    public Map<String, Object> getExtensions() {
        Map<String, Object> errorsMap = new HashMap<>();
        errorsMap.put("errorType", ERROR);
        errorsMap.put("message", "errors.unauthorizedAccess");
        return errorsMap;
    }

}

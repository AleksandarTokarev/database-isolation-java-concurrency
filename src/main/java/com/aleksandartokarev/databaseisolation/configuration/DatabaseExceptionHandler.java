package com.aleksandartokarev.databaseisolation.configuration;

import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class DatabaseExceptionHandler {

    @ExceptionHandler(DatabaseCustomException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleTruckException(DatabaseCustomException exception) {
        ErrorResponse errorResponse = new ErrorResponse();
        String transactionId = MDC.get(DatabaseConstants.CORRELATION_ID);
        if (exception.getCulprit() != null) {
            errorResponse.withHttpStatus(exception.getCulprit().getHttpStatusCode());
        } else {
            errorResponse.withHttpStatus(DatabaseCustomException.Culprit.INTERNAL_ERROR.getHttpStatusCode());
        }
        errorResponse.withErrorDescription(exception.getMessage()).withTransactionId(transactionId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(errorResponse, httpHeaders,
                HttpStatus.valueOf(errorResponse.getHttpStatus()));
    }
}

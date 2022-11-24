package com.aleksandartokarev.databaseisolation.configuration;

import java.io.Serializable;

public class ErrorResponse implements Serializable {
    private static final long serialVersionUID = (20161130L << 32) + ErrorResponse.class.hashCode();
    private String transactionId;
    private String errorDescription;
    private Integer httpStatus;
    public String getTransactionId() {
        return transactionId;
    }
    public ErrorResponse withTransactionId(String transactionId) {
        this.transactionId = transactionId;
        return this;
    }
    public String getErrorDescription() {
        return errorDescription;
    }
    public ErrorResponse withErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
        return this;
    }
    public Integer getHttpStatus() {
        return httpStatus;
    }
    public ErrorResponse withHttpStatus(Integer httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }
    public String toString() {
        return "ErrorResponse [transactionId=" + transactionId + ", errorDescription=" + errorDescription
                + ", httpStatus=" + httpStatus + "]";
    }
}
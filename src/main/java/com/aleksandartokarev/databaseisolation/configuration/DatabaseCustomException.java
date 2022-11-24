package com.aleksandartokarev.databaseisolation.configuration;

public class DatabaseCustomException extends Exception {
    private static final long serialVersionUID = 1L;
    private DatabaseCustomException.Culprit culprit;
    public DatabaseCustomException(DatabaseCustomException.Culprit culprit, String message) {
        super(message);
        this.culprit = culprit;
    }
    public DatabaseCustomException.Culprit getCulprit() {
        return this.culprit;
    }
    public static enum Culprit {
        INVALID_INPUT("Service  does not like the data input for this operation", 400),
        RATE_LIMITED("Due to request overflow, SDK gen has been rate-limited", 429),
        INTERNAL_ERROR("Internal Error Occured", 500),
        ALREADY_EXISTS("Record Already Exists", 400),
        UPDATE_FAILED("Updating of the record failed", 400),
        NOT_FOUND("Due to request overflow, SDK gen has been rate-limited", 404);
        private String description;
        private int httpStatusCode;
        private Culprit(String description, int httpStatusCode) {
            this.description = description;
            this.httpStatusCode = httpStatusCode;
        }
        public String toString() {
            return this.name();
        }
        public String getDescription() {
            return this.description;
        }
        public int getHttpStatusCode() {
            return this.httpStatusCode;
        }
    }
}
package org.nevermined.worldevents.api.core.exceptions;

public class AlreadyActiveException extends RuntimeException {
    public AlreadyActiveException(String message) {
        super(message);
    }
}

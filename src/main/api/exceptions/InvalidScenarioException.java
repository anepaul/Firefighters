package main.api.exceptions;

public class InvalidScenarioException extends RuntimeException {
    public InvalidScenarioException() {
        super("This scenario did not meet the criteria");
    }

    public InvalidScenarioException(Throwable cause) {
        super("This scenario did not meet the criteria", cause);
    }
}

package unisinos.model.execution;

import java.time.Instant;

public class ExecutionWithDuration {

    private final ServiceExecution execution;
    private final Instant executionStart;

    public ExecutionWithDuration(ServiceExecution execution, Instant executionStart) {
        this.execution = execution;
        this.executionStart = executionStart;
    }

    public ServiceExecution execution() {
        return this.execution;
    }

    public Instant executionStart() {
        return this.executionStart;
    }
}
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main
{
    private static final Random random = new Random();
    private static final String[] SERVICES = { "ServiceA", "ServiceB" };

    public static void main (String[] args)
    {
        System.out.println("Decision: " + execute());
        System.out.println("Decision: " + offload());
        System.out.println("Decision: " + inconclusive());

        Decision decision = execute();
        if (decision == Decision.EXECUTE) {
            System.out.println("Executing task locally");
        }

        decision = offload();
        if (decision == Decision.OFFLOAD) {
            System.out.println("Offloading task");
        }
        decision = inconclusive();
        if (decision == Decision.INCONCLUSIVE) {
            System.out.println("Inconclusive decision");
        }
    }

    private static Decision execute() {
        return Decision.EXECUTE;
    }

    private static Decision offload() {
        return Decision.OFFLOAD;
    }

    private static Decision inconclusive() {
        return Decision.INCONCLUSIVE;
    }
}

enum Decision
{
    EXECUTE,
    OFFLOAD,
    INCONCLUSIVE
}

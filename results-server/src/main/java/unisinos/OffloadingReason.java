package unisinos;

public enum OffloadingReason
{

    RANKING("ranking"),
    SERVICE_DURATION("service_duration"),
    UPPER_THRESHOLD("upper_threshold"),
    INCONCLUSIVE("inconclusive");

    private final String reason;

    OffloadingReason (String reason)
    {
        this.reason = reason;
    }

    public String getReason ()
    {
        return reason;
    }

}

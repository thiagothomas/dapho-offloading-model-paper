package unisinos.model.results;

public record OffloadingInfo(String node, OffloadingReason reason)
{
    @Override
    public String toString ()
    {
        return node + ":" + reason;
    }
}

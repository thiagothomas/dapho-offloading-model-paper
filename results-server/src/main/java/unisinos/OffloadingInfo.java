package unisinos;

public record OffloadingInfo(String node, OffloadingReason reason)
{
    @Override
    public String toString ()
    {
        // create a string representation of the OffloadingInfo object without OffloadingInfo class name and without commas
        return node + ":" + reason;
    }
}

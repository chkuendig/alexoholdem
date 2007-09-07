package ao.decision.domain;

/**
 * 
 */
public enum PotRatio
{
    //--------------------------------------------------------------------
    ZERO_TO_FIFTY, MORE_THAN_FIFTY;


    //--------------------------------------------------------------------
    public static PotRatio fromPotRatio(double ratio)
    {
        return (ratio <= 0.5)
                ? ZERO_TO_FIFTY
                : MORE_THAN_FIFTY;
    }
}

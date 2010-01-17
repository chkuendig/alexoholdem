package ao.ai.monte_carlo.uct;

/**
 * Normalizes rewards so that they are in [0, 1]
 * - possibly with <0.5 meaning loss, and >0.5 meaning gain.
 */
public class RewardNormalizer
{
    //--------------------------------------------------------------------
    private double lowest  = Long.MAX_VALUE;
    private double highest = Long.MIN_VALUE;
    private double delta   = Double.NaN;


    //--------------------------------------------------------------------
    public void add(Reward r)
    {
        add( r.value() );
    }
    public void add(double value)
    {
        if (value < lowest)
        {
            lowest  = value;
            recalculateDelta();
        }
        if (value > highest)
        {
            highest = value;
            recalculateDelta();
        }
    }
    private void recalculateDelta()
    {
        delta = highest - lowest;
    }


    //--------------------------------------------------------------------
    // returns in range [0, 1]
    public double normalize(Reward r)
    {
        return normalize( r.value() );
    }
    public double normalize(double value)
    {
        add( value );
        return (value - lowest) / delta;
    }

    public Reward deNormalize(double rewardPercentile)
    {
        return new Reward( lowest + delta / rewardPercentile );
    }
}

package ao.ai.monte_carlo.utc;

/**
 *
 */
public class Reward
{
    //--------------------------------------------------------------------
    public static final Reward LOWEST = new Reward(0);


    //--------------------------------------------------------------------
    private final double val;


    //--------------------------------------------------------------------
    public Reward()
    {
        this(0);
    }
//    public Reward(double expectedReward, int activeOpponents)
//    {
//
//    }
    private Reward(double value)
    {
        val = value;
    }


    //--------------------------------------------------------------------
    public Reward plus(Reward addend)
    {
        return new Reward(val + addend.val);
    }

    public Reward square()
    {
        return new Reward(val * val);
    }


    //--------------------------------------------------------------------
    public boolean greaterThan(Reward reward)
    {
        return val > reward.val;
    }

    public double averagedOver(int visits)
    {
        return val / visits;
    }

    public Reward normalize(int visits)
    {
        return new Reward(averagedOver(visits));
    }

    public Reward averageOverTwo()
    {
        return new Reward(averagedOver(2));
    }


    //--------------------------------------------------------------------
    public double value()
    {
        return val;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return String.valueOf( val );
    }
}

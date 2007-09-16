package ao.holdem.history.state;

/**
 *
 */
public class HoldemRuleBreach extends Error
{
    public HoldemRuleBreach(String reason)
    {
        super(reason);
    }
}

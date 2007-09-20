package ao.holdem.engine;

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

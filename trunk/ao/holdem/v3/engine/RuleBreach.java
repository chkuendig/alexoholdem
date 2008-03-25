package ao.holdem.v3.engine;

/**
 *
 */
public class RuleBreach extends Error
{
    public RuleBreach(String reason)
    {
        super(reason);
    }
}

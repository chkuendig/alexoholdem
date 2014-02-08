package ao.holdem.engine;

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

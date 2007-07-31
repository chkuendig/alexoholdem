package ao.holdem.bots.opp_model.decision;

/**
 *
 */
public interface SplitPoint<T extends SplitPoint>
{
    public Attribute<T> attribute();
}

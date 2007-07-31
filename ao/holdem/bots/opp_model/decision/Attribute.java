package ao.holdem.bots.opp_model.decision;

import java.util.Set;

/**
 *
 */
public interface Attribute<T extends SplitPoint>
{
    public Set<T> splitGiven(CategorySet domain);

//    public boolean describes(SplitPoint split);
}

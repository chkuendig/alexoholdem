package ao.odds.agglom;

import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;

/**
 * Date: Sep 30, 2008
 * Time: 12:29:11 PM
 */
public interface HistFinder
{
    public OddHist compute(Hole      hole,
                           Community community,
                           int       activeOpponents);

    public OddHist compute(CardSequence cards,
                           int          activeOpponents);
}

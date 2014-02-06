package ao.odds.agglom.hist;

import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;

/**
 * Date: Sep 30, 2008
 * Time: 12:29:11 PM
 */
public interface HistFinder
{
    public StrengthHist compute(Hole      hole,
                           Community community,
                           int       activeOpponents);

    public StrengthHist compute(CardSequence cards,
                           int          activeOpponents);
}

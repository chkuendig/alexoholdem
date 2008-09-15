package ao.bucket.index.incremental;

import ao.bucket.index.Indexer;
import ao.bucket.index.flop.Flop;
import ao.bucket.index.flop.FlopOffset;
import ao.bucket.index.post_flop.river.RiverIndexer;
import ao.bucket.index.post_flop.turn.Turn;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.sequence.CardSequence;

/**
 * Date: Aug 16, 2008
 * Time: 2:48:07 PM
 */
public class IndexerImpl implements Indexer
{
    //--------------------------------------------------------------------
    private FlopOffset   flopIndexer  = new FlopOffset();
    private RiverIndexer riverIndexer = new RiverIndexer();


    //--------------------------------------------------------------------
    public long indexOf(CardSequence cards)
    {
        if (cards.community().isPreflop())
        {
            return cards.hole().canonIndex();
        }
        else
        {
            Flop isoFlop = cards.hole().isoFlop(
                            cards.community().flopA(),
                            cards.community().flopB(),
                            cards.community().flopC());
            int flopIndex = isoFlop.canonIndex();

            if (! cards.community().hasTurn())
            {
                return flopIndex;
            }
            else
            {
                Card turnCard = cards.community().turn();
                Turn turn     = isoFlop.isoTurn(turnCard);

//                if (!isoTurn.turnCase().equals( PostFlopCase.TWO )) return -1;

//                int turnIndex = turnIndexer.indexOf(flopIndex, isoTurn);
                int turnIndex = turn.canonIndex( flopIndex );
                if (! cards.community().hasRiver())
                {
                    return turnIndex;
                }
                else
                {
                    return -1;
//                    return riverIndexer.indexOf(
//                            cards.hole(), flopCards,
//                            turnCard, isoTurn, turnIndex,
//                            cards.community().river());
                }
            }
        }
    }
}
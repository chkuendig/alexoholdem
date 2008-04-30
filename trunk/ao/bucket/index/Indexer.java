package ao.bucket.index;

import ao.holdem.model.card.sequence.CardSequence;

/**
 * Gives a suit isomorphic index to each card sequence.
 *
 */
public interface Indexer
{
    public int indexOf(CardSequence cards);
}

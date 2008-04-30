package ao.bucket;

import ao.holdem.model.card.sequence.CardSequence;

import java.util.Collection;

/**
 *
 */
public interface Abstractor
{
    public void add(CardSequence cards);


    public Collection<Bucket> generateSiblings();
}

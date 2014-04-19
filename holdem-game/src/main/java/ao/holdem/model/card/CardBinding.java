package ao.holdem.model.card;

import ao.holdem.persist.EnumBinding;

/**
 *
 */
public class CardBinding
{
    public static final EnumBinding<Card> INSTANCE =
            new EnumBinding<Card>(Card.class);
}

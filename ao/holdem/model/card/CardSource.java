package ao.holdem.model.card;

/**
 *
 */
public interface CardSource
        extends CommunitySource, HoleSource
{
    public CardSource prototype();
}

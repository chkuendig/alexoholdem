package ao.holdem.model.card;

/**
 *
 */
public interface CommunitySource
{
    public Community community();

    public void flop();
    public void turn();
    public void river();
}

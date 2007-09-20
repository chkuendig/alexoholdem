package ao.holdem.history.state;

import ao.holdem.model.Community;
import ao.holdem.model.Hole;
import ao.persist.PlayerHandle;

/**
 *
 */
public interface CardSource
{
    public Hole holeFor(PlayerHandle player);

    public Community community();

    public void flop();
    public void turn();
    public void river();

    public CardSource prototype();
}

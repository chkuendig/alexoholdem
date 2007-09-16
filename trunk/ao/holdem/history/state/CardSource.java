package ao.holdem.history.state;

import ao.holdem.def.model.cards.Community;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.history.PlayerHandle;

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

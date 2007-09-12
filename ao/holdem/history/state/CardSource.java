package ao.holdem.history.state;

import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.model.cards.Community;
import ao.holdem.history.PlayerHandle;

/**
 *
 */
public interface CardSource
{
    public Hole holeFor(PlayerHandle player);

    public Community flop();

    public Community turn();

    public Community river();

    public CardSource prototype();
}

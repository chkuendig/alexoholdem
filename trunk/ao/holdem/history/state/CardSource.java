package ao.holdem.history.state;

import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.model.cards.Community;
import ao.holdem.history.PlayerHandle;

/**
 *
 */
public interface CardSource
{
    Hole holeFor(PlayerHandle player);

    Community flop();

    Community turn();

    Community river();
}

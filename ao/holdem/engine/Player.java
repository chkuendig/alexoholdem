package ao.holdem.engine;

import ao.holdem.engine.analysis.Analysis;
import ao.holdem.engine.state.State;
import ao.holdem.model.Avatar;
import ao.holdem.model.Chips;
import ao.holdem.model.act.Action;
import ao.holdem.model.card.sequence.CardSequence;

import java.util.Map;

/**
 * A Limit Holdem player.
 * Can only play one hand at a time, so it is NOT threadsafe.
 */
public interface Player
{
    /**
     * Requests the player to perform an action.
     * A player will only be asked to act if a valid
     *  action is available for the taking.
     *
     * @param state
     *          hand state from POV of this player.
     * @param cards
     *          private hole cards, and public community cards.
     * @param analysis
     *          shared analysis of the players in this hand.
     *          can be used to predict players' actions.
     * @return
     *          the action you wish to perform.  If it is not
     *          a valid action, then it will be processed though
     *          FallbackAction.
     */
    public Action act(State        state,
                      CardSequence cards,
                      Analysis     analysis);

    /**
     * There has to be a special case for QUIT actions because
     *  they are the only ones that can happen out-of-turn.
     *
     * @return weather or not the given player has quit.
     */
    public boolean hasQuit();

    /**
     * Signals that the hand is over.
     * The next time that Player.act() is called, it will
     *  be in the context of a new hand.
     *
     * @param deltas
     *          the change in stack size for each player.
     */
    public void handEnded(Map<Avatar, Chips> deltas);
}

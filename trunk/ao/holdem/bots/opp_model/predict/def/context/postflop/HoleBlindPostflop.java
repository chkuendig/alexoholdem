package ao.holdem.bots.opp_model.predict.def.context.postflop;

import static ao.holdem.bots.opp_model.predict.def.NeuralUtils.asDouble;
import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.cards.Community;
import ao.holdem.def.state.env.TakenAction;
import ao.holdem.history.Snapshot;

/**
 *
 */
public class HoleBlindPostflop extends HoldemPostflop
{
    //--------------------------------------------------------------------
    public HoleBlindPostflop(
            Snapshot    prev,
            TakenAction prevAct,
            Snapshot    curr,
            Community   community)
    {
        super(prev, prevAct, curr);
        
        double flushPossibleBool   =
                asDouble(community.flushPossible());
        double aceOnBoardBool      =
                asDouble(community.contains(Card.Rank.ACE));
        double kingOnBoardBool     =
                asDouble(community.contains(Card.Rank.KING));
        double aceQueenKingPercent =
                (asDouble(community.contains(Card.Rank.ACE)) +
                 asDouble(community.contains(Card.Rank.KING)) +
                 asDouble(community.contains(Card.Rank.QUEEN))) /
                        (community.knownCount());

        addNeuralInput(
                flushPossibleBool,
                aceOnBoardBool,
                kingOnBoardBool,
                aceQueenKingPercent);
    }
}

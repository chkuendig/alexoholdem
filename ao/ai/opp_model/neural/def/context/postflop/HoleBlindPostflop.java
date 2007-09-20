package ao.ai.opp_model.neural.def.context.postflop;

import static ao.ai.opp_model.neural.def.NeuralUtils.asDouble;
import ao.ai.opp_model.neural.def.context.GenericContext;
import ao.holdem.model.Card;

/**
 *
 */
public class HoleBlindPostflop extends HoldemPostflop
{
    //--------------------------------------------------------------------
    public HoleBlindPostflop(GenericContext ctx)
    {
        super(ctx);
        
        double flushPossibleBool   =
                asDouble(ctx.community().flushPossible());
        double aceOnBoardBool      =
                asDouble(ctx.community().contains(Card.Rank.ACE));
        double kingOnBoardBool     =
                asDouble(ctx.community().contains(Card.Rank.KING));
        double aceQueenKingPercent =
                (asDouble(ctx.community().contains(Card.Rank.ACE)) +
                 asDouble(ctx.community().contains(Card.Rank.KING)) +
                 asDouble(ctx.community().contains(Card.Rank.QUEEN))) /
                        (ctx.community().knownCount());

        addNeuralInput(
                flushPossibleBool,
                aceOnBoardBool,
                kingOnBoardBool,
                aceQueenKingPercent);
    }
}

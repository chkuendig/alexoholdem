package ao.ai.opp_model.decision.domain;

import static ao.ai.opp_model.neural.def.NeuralUtils.asDouble;
import ao.holdem.model.Card;
import ao.holdem.model.Community;

/**
 *
 */
public enum AceQueenKing
{
    NONE, FEW, SOME, MANY;

    public static AceQueenKing fromCommunity(Community community)
    {
        double aceQueenKingPercent =
                (asDouble(community.contains(Card.Rank.ACE)) +
                 asDouble(community.contains(Card.Rank.KING)) +
                 asDouble(community.contains(Card.Rank.QUEEN))) /
                         (community.knownCount());
        return (aceQueenKingPercent < 0.01)
                ? NONE
                : (aceQueenKingPercent < 0.34)
                   ? FEW
                   : (aceQueenKingPercent < 0.67)
                      ? SOME
                      : MANY;
    }
}

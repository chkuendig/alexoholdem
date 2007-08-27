package ao.decision.domain;

import static ao.holdem.bots.opp_model.predict.def.NeuralUtils.asDouble;
import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.cards.Community;

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

package ao.holdem.abs.odds.agglom.impl;

import ao.holdem.abs.bucket.index.detail.CanonDetail;
import ao.holdem.abs.bucket.index.detail.flop.FlopDetails;
import ao.holdem.abs.bucket.index.detail.turn.TurnDetails;
import ao.holdem.abs.odds.Odds;
import ao.holdem.ai.odds.OddsBy5;
import ao.holdem.ai.odds.OddsEvaluator;
import ao.holdem.canon.flop.Flop;
import ao.holdem.canon.turn.Turn;
import ao.holdem.model.card.canon.hole.CanonHole;
import ao.holdem.model.card.sequence.CardSequence;


public enum OddsFinderEvaluator implements OddsEvaluator
{
    INSTANCE;

    private static final PreciseHeadsUpOdds oddsCalculator = new PreciseHeadsUpOdds();


    @Override
    public double approximateHeadsUpHandStrength(CardSequence cards) {
        if (cards.community().knownCount() == 0) {
            return OddsBy5.INSTANCE.approximateHeadsUpHandStrength(cards);
        }

        if (cards.community().knownCount() == 3) {
            Flop flop = new Flop(
                    CanonHole.create(cards.hole()),
                    cards.community());

            CanonDetail detail =
                    FlopDetails.lookup(flop.canonIndex());

            return detail.strength();
        }

        if (cards.community().knownCount() == 4) {
            Turn turn = new Flop(
                    CanonHole.create(cards.hole()),
                    cards.community()
                ).addTurn(cards.community().turn());

            CanonDetail detail =
                    TurnDetails.lookup(turn.canonIndex());

            return detail.strength();
        }

        Odds odds = oddsCalculator
                .compute(cards.hole(), cards.community(), 1);

        return odds.strengthVsRandom();
    }
}

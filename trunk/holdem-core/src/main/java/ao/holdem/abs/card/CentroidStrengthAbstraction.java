package ao.holdem.abs.card;

import ao.holdem.engine.eval.odds.OddsEvaluator;
import ao.holdem.model.Round;
import ao.holdem.model.card.sequence.CardSequence;

import java.io.Serializable;

/**
 *
 */
public class CentroidStrengthAbstraction implements CardAbstraction, Serializable
{
    private static final long serialVersionUID = 20140701L;


    private final double[] holeCentroids;
    private final double[] flopCentroids;
    private final double[] turnCentroids;
    private final double[] riverCentroids;


    public CentroidStrengthAbstraction(
            double[] holeCentroids,
            double[] flopCentroids,
            double[] turnCentroids,
            double[] riverCentroids)
    {
        this.holeCentroids = holeCentroids;
        this.flopCentroids = flopCentroids;
        this.turnCentroids = turnCentroids;
        this.riverCentroids = riverCentroids;
    }


    @Override
    public int indexInRound(CardSequence cards, OddsEvaluator oddsEvaluator) {
        Round round = cards.community().round();
        double[] centroids = centroidsFor(round);
        double strength = oddsEvaluator.approximateHeadsUpHandStrength(cards);

        return indexIn(cards, centroids, strength);
    }

    @Override
    public int count(Round round) {
        return centroidsFor(round).length;
    }

    private double[] centroidsFor(Round round) {
        switch (round) {
            case PREFLOP: return holeCentroids;
            case FLOP: return flopCentroids;
            case TURN: return turnCentroids;
            case RIVER: return riverCentroids;
            default: throw new Error();
        }
    }

    private int indexIn(CardSequence cards, double[] centroids, double strength) {
        int closestIndex = -1;
        double lowestDistance = Double.POSITIVE_INFINITY;

        for (int i = 0; i < centroids.length; i++) {
            double distance = Math.abs(centroids[i] - strength);

            if (distance < lowestDistance) {
                lowestDistance = distance;
                closestIndex = i;
            }
        }

        return closestIndex;
    }
}

package ao.holdem.abs.card;

import ao.holdem.ai.abs.card.CardAbstraction;
import ao.holdem.ai.odds.OddsEvaluator;
import ao.holdem.model.Round;
import ao.holdem.model.card.sequence.CardSequence;

import java.io.*;

/**
 *
 */
public class CentroidStrengthAbstraction implements CardAbstraction, Serializable
{
    private static final long serialVersionUID = 20140701L;


    public static CentroidStrengthAbstraction load(byte[] in) {
        InputStream inputStream = new ByteArrayInputStream(in);
        try {
            return load(inputStream);
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public static CentroidStrengthAbstraction load(InputStream in) throws IOException {
        ObjectInputStream objectIn = new ObjectInputStream(in);

        try {
            double[] holeCentroids = (double[]) objectIn.readObject();
            double[] flopCentroids = (double[]) objectIn.readObject();
            double[] turnCentroids = (double[]) objectIn.readObject();
            double[] riverCentroids = (double[]) objectIn.readObject();

            return new CentroidStrengthAbstraction(
                    holeCentroids, flopCentroids, turnCentroids, riverCentroids);
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
    }

    public static void save(CentroidStrengthAbstraction abstraction, OutputStream out) throws IOException {
        ObjectOutputStream objectOut = new ObjectOutputStream(out);

        objectOut.writeObject(abstraction.holeCentroids);
        objectOut.writeObject(abstraction.flopCentroids);
        objectOut.writeObject(abstraction.turnCentroids);
        objectOut.writeObject(abstraction.riverCentroids);

        objectOut.flush();
    }


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

        return indexIn(centroids, strength);
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

    private int indexIn(double[] centroids, double strength) {
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

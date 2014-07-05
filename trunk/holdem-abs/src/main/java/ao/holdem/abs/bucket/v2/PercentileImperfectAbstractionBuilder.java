package ao.holdem.abs.bucket.v2;

import ao.holdem.abs.bucket.index.detail.CanonDetail;
import ao.holdem.abs.bucket.index.detail.flop.FlopDetails;
import ao.holdem.abs.bucket.index.detail.preflop.HoleDetails;
import ao.holdem.abs.bucket.index.detail.river.CanonRiverDetail;
import ao.holdem.abs.bucket.index.detail.river.RiverEvalLookup;
import ao.holdem.abs.bucket.index.detail.turn.TurnDetails;
import ao.holdem.abs.card.CentroidStrengthAbstraction;
import ao.holdem.canon.flop.Flop;
import ao.holdem.canon.river.River;
import ao.holdem.canon.turn.Turn;
import ao.holdem.engine.eval.odds.OddsBy5;
import ao.holdem.model.Round;
import ao.holdem.model.card.canon.hole.CanonHole;
import com.google.common.io.Files;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 */
public enum PercentileImperfectAbstractionBuilder
{;
    public static void save(CentroidStrengthAbstraction abstraction) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(out);
        objectOut.writeObject(abstraction);
        objectOut.close();

        byte[] serialized = out.toByteArray();

        File path = pathFor(abstraction);
        Files.createParentDirs(path);
        Files.write(serialized, path);
    }

    public static CentroidStrengthAbstraction load(int holeCount, int flopCount, int turnCount, int riverCount) throws IOException {
        File path = pathFor(holeCount, flopCount, turnCount, riverCount);
        if (! path.exists()) {
            return null;
        }

        InputStream serialized = Files.asByteSource(path).openBufferedStream();
        ObjectInputStream objectInput = new ObjectInputStream(serialized);

        try {
            return (CentroidStrengthAbstraction) objectInput.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOError(e);
        } finally {
            serialized.close();
        }
    }


    public static CentroidStrengthAbstraction loadOrBuildAndSave(int holeCount, int flopCount, int turnCount, int riverCount) {
        try {
            return loadOrBuildAndSaveChecked(holeCount, flopCount, turnCount, riverCount);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static CentroidStrengthAbstraction loadOrBuildAndSaveChecked(int holeCount, int flopCount, int turnCount, int riverCount) throws IOException {
        CentroidStrengthAbstraction abstraction = load(holeCount, flopCount, turnCount, riverCount);
        if (abstraction != null) {
            return abstraction;
        }

        abstraction = build(holeCount, flopCount, turnCount, riverCount);
        save(abstraction);

        return abstraction;
    }



    private static File pathFor(CentroidStrengthAbstraction abstraction) {
        return pathFor(
                abstraction.count(Round.PREFLOP),
                abstraction.count(Round.FLOP),
                abstraction.count(Round.TURN),
                abstraction.count(Round.RIVER));
    }
    private static File pathFor(int holeCount, int flopCount, int turnCount, int riverCount) {
        return new File(String.format("lookup/bucket/pis;%s;%s;%s;%s.bin",
                holeCount, flopCount, turnCount, riverCount));
    }


    public static CentroidStrengthAbstraction build(int holeCount, int flopCount, int turnCount, int riverCount) {
        double[] holeCentroids = holeCentroids(holeCount);
        double[] flopCentroids = flopCentroids(flopCount);
        double[] turnCentroids = turnCentroids(turnCount);
        double[] riverCentroids = riverCentroids(riverCount);

        return new CentroidStrengthAbstraction(
                holeCentroids, flopCentroids, turnCentroids, riverCentroids);
    }
    
    
    private static double[] holeCentroids(int holeBuckets) {
        SortedMap<Double, Integer> strengthCounts = holeStrengthCounts();
        return partitionStrengthCounts(strengthCounts, holeBuckets);
    }
    private static double[] flopCentroids(int count) {
        SortedMap<Double, Integer> strengthCounts = flopStrengthCounts();
        return partitionStrengthCounts(strengthCounts, count);
    }
    private static double[] turnCentroids(int count) {
        SortedMap<Double, Integer> strengthCounts = turnStrengthCounts();
        return partitionStrengthCounts(strengthCounts, count);
    }
    private static double[] riverCentroids(int count) {
        SortedMap<Double, Integer> strengthCounts = riverStrengthCounts();
        return partitionStrengthCounts(strengthCounts, count);
    }


    private static SortedMap<Double, Integer> holeStrengthCounts() {
        StrengthCounter counter = new StrengthCounter();
        for (char i = 0; i < CanonHole.CANONS; i++) {
            counter.add(HoleDetails.lookup(i));
        }
        return counter.strengthCounts;
    }
    private static SortedMap<Double, Integer> flopStrengthCounts() {
        StrengthCounter counter = new StrengthCounter();
        for (int i = 0; i < Flop.CANONS; i++) {
            counter.add(FlopDetails.lookup(i));
        }
        return counter.strengthCounts;
    }
    private static SortedMap<Double, Integer> turnStrengthCounts() {
        StrengthCounter counter = new StrengthCounter();
        for (int i = 0; i < Turn.CANONS; i++) {
            counter.add(TurnDetails.lookup(i));
        }
        return counter.strengthCounts;
    }
    private static SortedMap<Double, Integer> riverStrengthCounts() {
        final StrengthCounter counter = new StrengthCounter();

//        OddFinder odds = new PreciseHeadsUpOdds();
//        for (long i = 0; i < River.CANONS; i++) {
//            List<River> examples = RiverExamples.examplesOf(i);
//
//            River example = examples.get(0);
//            Hole hole = example.turn().hole().reify();
//            Community community = new Community(
//                    example.turn().flop().flopA(),
//                    example.turn().flop().flopB(),
//                    example.turn().flop().flopC(),
//                    example.turn().turnRealCard(),
//                    example.riverRealCard());
//
//            double strength = odds.compute(hole, community, 1).strengthVsRandom();
//            CanonDetail detail = new CanonRiverDetail(strength, i, (byte) examples.size());
//            counter.add(detail);
//        }

        RiverEvalLookup.traverse(0, River.CANONS, new RiverEvalLookup.VsRandomVisitor() {
            @Override public void traverse(long canonIndex, double strengthVsRandom, byte represents) {
                CanonRiverDetail detail = new CanonRiverDetail(strengthVsRandom, canonIndex, represents);
                counter.add(detail);
            }
        });

        return counter.strengthCounts;
    }



    private static double[] partitionStrengthCounts(SortedMap<Double, Integer> strengthCounts, int count) {
        long totalCount = 0;
        for (Integer represents : strengthCounts.values()) {
            totalCount += represents;
        }

        long perBucket = (long) Math.ceil((double) totalCount / count);

        double[] centroids = new double[count];
        int nextCentroid = 0;

        Map<Double, Integer> bucket = new HashMap<>();
        long nextBucketRemaining = perBucket;

        for (Map.Entry<Double, Integer> strengthCount : strengthCounts.entrySet()) {
            nextBucketRemaining -= strengthCount.getValue();
            bucket.put(strengthCount.getKey(), strengthCount.getValue());

            if (nextBucketRemaining <= 0) {
                centroids[nextCentroid++] = centroid(bucket);

                bucket.clear();
                nextBucketRemaining = perBucket + nextBucketRemaining;
            }
        }

        if (! bucket.isEmpty()) {
            centroids[nextCentroid++] = centroid(bucket);
        }

        if (nextCentroid != centroids.length) {
            throw new Error();
        }

        return centroids;
    }

    private static double centroid(Map<Double, Integer> bucket) {
        if (bucket.isEmpty()) {
            throw new Error();
        }

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;

        for (Double strength : bucket.keySet()) {
            min = Math.min(min, strength);
            max = Math.max(max, strength);
        }

        return (min + max) / 2;
    }


    private static class StrengthCounter {
        final SortedMap<Double, Integer> strengthCounts = new TreeMap<>();

        public void add(CanonDetail detail) {
            int represents = detail.represents();

            Integer current = strengthCounts.get(detail.strength());
            if (current != null) {
                represents += current;
            }

            strengthCounts.put(detail.strength(), represents);
        }
    }
}

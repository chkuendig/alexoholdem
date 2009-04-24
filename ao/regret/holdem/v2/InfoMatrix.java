package ao.regret.holdem.v2;

import ao.holdem.engine.state.tree.StateTree;
import ao.holdem.model.act.AbstractAction;
import ao.regret.holdem.InfoBranch;
import ao.util.data.Pack;
import static ao.util.data.Pack.flatten;
import ao.util.persist.PersistentChars;
import ao.util.persist.PersistentDoubles;
import ao.util.persist.PersistentInts;

import java.io.File;
import java.util.Arrays;

/**
 * User: alex
 * Date: 20-Apr-2009
 * Time: 10:07:01 PM
 *
 * Email from Michael Johanson:
 *
 *  IIRC, you can treat each round as a matrix game,
 *    where each player's action is an intent to produce a certain
 *    betting sequence.
 *  Then, you just need a double for regret and a double for the
 *      average strategy for each betting sequence, instead of needing
 *      a regret and an average for each action at each information set.
 *
 * This saves on having to store an unused start/regret for
 *  an impossible firth raise.
 * And potentially saves space on folding when chacking is possible. 
 */
public class InfoMatrix
{
    //--------------------------------------------------------------------
//    private static final String    COUNT_FILE = "count.int";
//    private static final String CFREGRET_FILE = "cfreg.float";
//    private static final String STRATEGY_FILE = "strat.float";

    private static final String    COUNT_FILE = "count.int";
    private static final String CFREGRET_FILE = "cfreg.double";
    private static final String STRATEGY_FILE = "strat.double";


    //--------------------------------------------------------------------
    public static InfoMatrix retrieveOrCreate(
            File dir, int nBuckets, int nIntents)
    {
        char counts[] = PersistentChars.retrieve(
                            new File(dir, COUNT_FILE));
        return (counts == null)
                ? newInstance(nBuckets, nIntents)
                : new InfoMatrix(
//                Pack.square(PersistentFloats.retrieve(
//                        new File(dir,  STRATEGY_FILE)), nIntents)
//                Pack.square(PersistentFloats.retrieve(
//                        new File(dir,  CFREGRET_FILE)), nIntents),

                Pack.square(PersistentDoubles.retrieve(
                        new File(dir,  STRATEGY_FILE)), nIntents),
                Pack.square(PersistentDoubles.retrieve(
                        new File(dir,  CFREGRET_FILE)), nIntents)
                );
    }
    public static InfoMatrix newInstance(int nBuckets, int nIntents) {
        return new InfoMatrix(nBuckets, nIntents);
    }


    public static void persist(File dir, InfoMatrix matrix)
    {
        PersistentInts.persist(new int[]{
                matrix.averageStrategy.length,
                matrix.averageStrategy[0].length},
                new File(dir, COUNT_FILE));

//        PersistentFloats.persist(
//                flatten(matrix.averageStrategy),
//                new File(dir, STRATEGY_FILE));
//        PersistentFloats.persist(
//                flatten(matrix.cumulativeRegret),
//                new File(dir, CFREGRET_FILE));

        PersistentDoubles.persist(
                flatten(matrix.averageStrategy),
                new File(dir, STRATEGY_FILE));
        PersistentDoubles.persist(
                flatten(matrix.cumulativeRegret),
                new File(dir, CFREGRET_FILE));
    }


    //--------------------------------------------------------------------
    private static final double defaultEqual  [] = {
                                        1.0 / 3, 1.0 / 3, 1.0 / 3};
    private static final double defaultNoRaise[] = {0.5, 0.5, 0  };
    private static final double defaultNoFold [] = {0  , 0.5, 0.5};
//    private static final double defaultCall   [] = {0  , 1.0, 0  };


    //--------------------------------------------------------------------
    private final double averageStrategy [][]; // [bucket][intent]
    private final double cumulativeRegret[][];


    //--------------------------------------------------------------------
    private InfoMatrix(int nBuckets,
                       int nIntents)
    {
        this(new double[ nBuckets ][ nIntents ],
             new double[ nBuckets ][ nIntents ]);
    }

    private InfoMatrix(
            double copyAverageStrategy [][],
            double copyCumulativeRegret[][])
    {
        averageStrategy  = copyAverageStrategy;
        cumulativeRegret = copyCumulativeRegret;
    }



    //--------------------------------------------------------------------
    public void displayHeadsUpRoot() {
        for (int bucket = 0; bucket < averageStrategy.length; bucket++) {
            System.out.println(
                    StateTree.headsUpRoot().infoSet(bucket, this));
        }
    }


    //--------------------------------------------------------------------
    public InfoSet infoSet(int bucket,
                           int foldIntent,
                           int callIntent,
                           int raiseIntent)
    {
        return new InfoSet(bucket, foldIntent, callIntent, raiseIntent);
    }


    //--------------------------------------------------------------------
    public class InfoSet
    {
        //----------------------------------------------------------------
        private final int bucket;
        private final int fIntent;
        private final int cIntent;
        private final int rIntent;


        //----------------------------------------------------------------
        private InfoSet(int roundBucket,
                        int foldIntent,
                        int callIntent,
                        int raiseIntent) {
            bucket  = roundBucket;
            fIntent = foldIntent;
            cIntent = callIntent;
            rIntent = raiseIntent;
        }


        //----------------------------------------------------------------
        public AbstractAction nextProbableAction()
        {
            return InfoBranch.nextProbableAction( averageStrategy() );
        }

        public double[] averageStrategy() {
            double fAvg = average(fIntent);
            double cAvg = average(cIntent);
            double rAvg = average(rIntent);

            double sum = fAvg + cAvg + rAvg;
            return (sum == 0)
                   ? defaultProbabilities()
                   : new double[] {
                        fAvg/sum, cAvg/sum, rAvg/sum};
        }

        private double average(int intent) {
            return intent == -1
                   ? 0 : Math.max(averageStrategy[bucket][intent], 0);
        }


        //----------------------------------------------------------------
        public void add(double strategy[],
                        double proponentReachProbability) {
            if (fIntent != -1)
                averageStrategy [bucket][fIntent] +=
                        proponentReachProbability * strategy[0];

          //if (cIntent != -1)
                averageStrategy [bucket][cIntent] +=
                        proponentReachProbability * strategy[1];

            if (rIntent != -1)
                averageStrategy [bucket][rIntent] +=
                        proponentReachProbability * strategy[2];
        }

        public void add(double counterfactualRegret[]) {
            if (fIntent != -1)
                cumulativeRegret[bucket][fIntent] +=
                        counterfactualRegret[0];

          //if (cIntent != -1)
                cumulativeRegret[bucket][cIntent] +=
                        counterfactualRegret[1];

            if (rIntent != -1)
                cumulativeRegret[bucket][rIntent] +=
                        counterfactualRegret[2];
        }


        //----------------------------------------------------------------
        public double[] strategy() {
            double fRegret = positiveRegret(fIntent);
            double cRegret = positiveRegret(cIntent);
            double rRegret = positiveRegret(rIntent);

            double cumRegret = fRegret + cRegret + rRegret;
            return (cumRegret <= 0)
                   ? defaultProbabilities()
                   : new double[]{
                        fRegret / cumRegret,
                        cRegret / cumRegret,
                        rRegret / cumRegret};
        }

        private double positiveRegret(int intent) {
            return Math.max(regret(intent), 0);
        }
        private double regret(int intent) {
            return intent == -1 ? 0 : cumulativeRegret[bucket][intent];
        }

        private double[] defaultProbabilities() {
            if (fIntent != -1) {
                if (rIntent != -1) {
                    return defaultEqual;
                } else {
                    return defaultNoRaise;
                }
            } else {
//                if (rIntent != -1) {
                    return defaultNoFold;
//                } else {
//                    // never happens
//                    System.out.println("InfoMatrix !!! Yes it does!");
//                    return defaultCall;
//                }
            }
        }


        //----------------------------------------------------------------
        public double[] regret() {
            return new double[]{
                    regret(fIntent), regret(cIntent), regret(rIntent)};
        }


        //----------------------------------------------------------------
        @Override public String toString() {
            return Arrays.toString( averageStrategy() );
        }
    }
}

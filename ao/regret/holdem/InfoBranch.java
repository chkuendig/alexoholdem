package ao.regret.holdem;

import ao.holdem.model.act.AbstractAction;
import ao.util.data.Pack;
import static ao.util.data.Pack.flatten;
import ao.util.math.rand.Rand;
import ao.util.persist.PersistentChars;
import ao.util.persist.PersistentFloats;

import java.io.File;
import java.util.Arrays;

/**
 * Date: Feb 19, 2009
 * Time: 4:58:01 PM
 */
public class InfoBranch
{
    //--------------------------------------------------------------------
    private static final String   COUNT_FILE =  "count.char";
    private static final String    FOLD_FILE =   "fold.float";
    private static final String    CALL_FILE =   "call.float";
    private static final String   RAISE_FILE =  "raise.float";
//    private static final String   REACH_FILE =  "reach.float";
    private static final String  R_FOLD_FILE =  "rfold.float";
    private static final String  R_CALL_FILE =  "rcall.float";
    private static final String R_RAISE_FILE = "rraise.float";

//    private static final String COUNT_FILE = "count.char";
//    private static final String  FOLD_FILE =  "fold.double";
//    private static final String  CALL_FILE =  "call.double";
//    private static final String RAISE_FILE = "raise.double";
//    private static final String REACH_FILE  = "reach.double";
//    private static final String R_FOLD_FILE = "rfold.double";
//    private static final String R_CALL_FILE = "rcall.double";


    //--------------------------------------------------------------------
    public static InfoBranch retrieveOrCreate(
            File dir, char nBuckets, char nBettingSequences)
    {
        char counts[] = PersistentChars.retrieve(
                            new File(dir, COUNT_FILE));
        if (counts == null) {
            return new InfoBranch(nBuckets, nBettingSequences);
        }
        return new InfoBranch(
                Pack.square(PersistentFloats.retrieve(
                        new File(dir,  FOLD_FILE)), nBettingSequences),
                Pack.square(PersistentFloats.retrieve(
                        new File(dir,  CALL_FILE)), nBettingSequences),
                Pack.square(PersistentFloats.retrieve(
                        new File(dir, RAISE_FILE)), nBettingSequences),
//                Pack.square(PersistentFloats.retrieve(
//                        new File(dir, REACH_FILE)), nBettingSequences),
                Pack.square(PersistentFloats.retrieve(
                        new File(dir, R_FOLD_FILE)), nBettingSequences),
                Pack.square(PersistentFloats.retrieve(
                        new File(dir, R_CALL_FILE)), nBettingSequences),
                Pack.square(PersistentFloats.retrieve(
                        new File(dir, R_RAISE_FILE)), nBettingSequences)

//                Pack.square(PersistentDoubles.retrieve(
//                        new File(dir,  FOLD_FILE)), nBettingSequences),
//                Pack.square(PersistentDoubles.retrieve(
//                        new File(dir,  CALL_FILE)), nBettingSequences),
//                Pack.square(PersistentDoubles.retrieve(
//                        new File(dir, RAISE_FILE)), nBettingSequences),
//                Pack.square(PersistentInts.retrieve(
//                        new File(dir, REACH_FILE)), nBettingSequences)
        );
    }

    public static void persist(File dir, InfoBranch branch)
    {
        PersistentChars.persist(new char[]{
                (char) branch.regretFold.length,
                (char) branch.regretFold[0].length},
                new File(dir, COUNT_FILE));

        PersistentFloats.persist(
                flatten(branch.regretFold ), new File(dir, FOLD_FILE));
        PersistentFloats.persist(
                flatten(branch.regretCall ), new File(dir, CALL_FILE));
        PersistentFloats.persist(
                flatten(branch.regretRaise), new File(dir, RAISE_FILE));
//        PersistentFloats.persist(
//                flatten(branch.reachSum ),  new File(dir, REACH_FILE));
        PersistentFloats.persist(
                flatten(branch.avgFold), new File(dir, R_FOLD_FILE));
        PersistentFloats.persist(
                flatten(branch.avgCall), new File(dir, R_CALL_FILE));
        PersistentFloats.persist(
                flatten(branch.avgRaise), new File(dir, R_RAISE_FILE));


//        PersistentDoubles.persist(
//                flatten(branch.regretFold) , new File(dir, FOLD_FILE));
//        PersistentDoubles.persist(
//                flatten(branch.regretCall) , new File(dir, CALL_FILE));
//        PersistentDoubles.persist(
//                flatten(branch.regretRaise), new File(dir, RAISE_FILE));
//        PersistentInts.persist(
//                flatten(branch.visits), new File(dir, REACH_FILE));
    }


    //--------------------------------------------------------------------
    private final float[][] regretFold;
    private final float[][] regretCall;
    private final float[][] regretRaise;
//    private final float[][] reachSum;
    private final float[][] avgFold;
    private final float[][] avgCall;
    private final float[][] avgRaise;


//    private final double[][] regretFold;
//    private final double[][] regretCall;
//    private final double[][] regretRaise;

//    private final   int[][] visits;



    //--------------------------------------------------------------------
    public InfoBranch(
            char nBuckets,
            char nBettingSequences)
    {
        regretFold  = new float[ nBuckets ][ nBettingSequences ];
        regretCall  = new float[ nBuckets ][ nBettingSequences ];
        regretRaise = new float[ nBuckets ][ nBettingSequences ];
//        reachSum    = new float[ nBuckets ][ nBettingSequences ];
        avgFold  = new float[ nBuckets ][ nBettingSequences ];
        avgCall  = new float[ nBuckets ][ nBettingSequences ];
        avgRaise = new float[ nBuckets ][ nBettingSequences ];

//        regretFold  = new double[ nBuckets ][ nBettingSequences ];
//        regretCall  = new double[ nBuckets ][ nBettingSequences ];
//        regretRaise = new double[ nBuckets ][ nBettingSequences ];
//        visits      = new   int[ nBuckets ][ nBettingSequences ];
    }

    private InfoBranch(
            float copyRegretFold [][],
            float copyRegretCall [][],
            float copyRegretRaise[][],
//            float copyReachSum   [][],
            float copyReachFold  [][],
            float copyReachCall  [][],
            float copyReachRaise [][]
//            double copyRegretFold [][],
//            double copyRegretCall [][],
//            double copyRegretRaise[][],
//            int    copyVisits     [][]
            )
    {
        regretFold  = copyRegretFold;
        regretCall  = copyRegretCall;
        regretRaise = copyRegretRaise;
//        reachSum    = copyReachSum;
        avgFold = copyReachFold;
        avgCall = copyReachCall;
        avgRaise = copyReachRaise;
    }


    //--------------------------------------------------------------------
    public void displaySequence(char seq)
    {
        for (char bucket = 0;
                  bucket < regretFold.length;
                  bucket++) {
            InfoSet info = get(bucket, seq);
            System.out.println(Arrays.toString(
                    info.averageStrategy()));
        }
    }


    //--------------------------------------------------------------------
    public InfoSet get(char roundBucket,
                       char bettingSequence)
    {
        return new InfoSet(roundBucket, bettingSequence);
    }


    //--------------------------------------------------------------------
    public static AbstractAction nextProbableAction(
                double probabilities[]) {
        double toFold = probabilities[0];
        double toCall = probabilities[1];

        double rand = Rand.nextDouble();
        if (rand <= toFold) {
            return AbstractAction.QUIT_FOLD;
        } else {
            rand -= toFold;
            return (rand <= toCall)
                   ? AbstractAction.CHECK_CALL
                   : AbstractAction.BET_RAISE;
        }
    }


    //--------------------------------------------------------------------
    public class InfoSet
    {
        //----------------------------------------------------------------
        private final char bucket;
        private final char state;


        //----------------------------------------------------------------
        private InfoSet(char buckets, char sequences)
        {
            bucket = buckets;
            state  = sequences;
        }


        //----------------------------------------------------------------
        public AbstractAction nextProbableAction(
                boolean canRaise, boolean canCheck)
        {
            return InfoBranch.nextProbableAction(
                    averageStrategy());
        }


        //----------------------------------------------------------------
        public void add(double  counterfactualRegret[],
                        boolean canRaise,
                        //double  actionProbabilities[],
                        double  proponentReachProbability)
        {
            regretFold[bucket][state] += counterfactualRegret[0];
            regretCall[bucket][state] += counterfactualRegret[1];
            if (canRaise) {
                regretRaise[bucket][state] += counterfactualRegret[2];
            }
            
            double actionProbabilities[] = probabilities(canRaise);
            avgFold [bucket][state] +=
                    proponentReachProbability * actionProbabilities[0];
            avgCall [bucket][state] +=
                    proponentReachProbability * actionProbabilities[1];
            avgRaise[bucket][state] +=
                    proponentReachProbability * actionProbabilities[2];
        }


        //----------------------------------------------------------------
        private double positiveCounterfactualRegret()
        {
            return Math.max(regretFold [bucket][state], 0) +
                   Math.max(regretCall [bucket][state], 0) +
                   Math.max(regretRaise[bucket][state], 0);
        }


        //----------------------------------------------------------------
        public double[] probabilities(boolean canRaise)
        {
            return probabilities(canRaise, false);
        }
        public double[] probabilities(
                boolean canRaise, boolean canCheck)
        {
            double prob[] = new double[ 3 ];
            probabilities(prob, canRaise, canCheck);
            return prob;
        }

        private void probabilities(
                double into[], boolean canRaise, boolean canCheck)
        {
            double cumRegret = positiveCounterfactualRegret();

            if (cumRegret <= 0) {
                defaultProbabilities(into, canRaise, canCheck);
            } else {
                double foldProb  = Math.max(0,
                        regretFold [bucket][state] / cumRegret);
                double callProb  = Math.max(0,
                        regretCall [bucket][state] / cumRegret);
                double raiseProb = Math.max(0,
                        regretRaise[bucket][state] / cumRegret);

                double sum = foldProb + callProb + raiseProb;
                into[0] =  foldProb / sum;
                into[1] =  callProb / sum;
                into[2] = raiseProb / sum;

//                if (canCheck && foldProb != 0) {
//                    into[0] = 0;
//                    into[1] = callProb / (callProb + raiseProb);
//                    into[2] = 1.0 - into[1];
//                } else {
//                    into[0] =  foldProb;
//                    into[1] =  callProb;
//                    into[2] = raiseProb;
//                }
            }
        }

        private void defaultProbabilities(
                double into[], boolean canRaise, boolean canCheck)
        {
            if (canRaise) {
//                if (canCheck) {
//                    into[0] = 0;
//                    into[1] = into[2] = 0.5;
//                } else {
                    into[0] = into[1] = into[2] = (1.0 / 3);
//                }
            } else {
//                if (canCheck) {
//                    into[0] = 0;
//                    into[1] = 1.0;
//                } else {
                    into[0] = into[1] = 0.5;
//                }
                into[2] = 0;
            }
        }


        //----------------------------------------------------------------
        public double[] averageStrategy() {
            double  foldSum = avgFold[bucket][state];
            double  callSum = avgCall[bucket][state];
            double raiseSum = avgRaise[bucket][state];

            double sum = foldSum + callSum + raiseSum;
            if (sum == 0) return probabilities(true);

            return new double[]{
                    foldSum / sum, callSum / sum, raiseSum / sum};

//            double sum = reachSum [bucket][state];
//            if (sum == 0) return probabilities(true);
//
//            double avgFold = avgFold[bucket][state];
//            double avgCall = avgCall[bucket][state];
//
//            double normFold  = avgFold / sum;
//            double normCall  = avgCall / sum;
//            double normRaise = 1.0 - normFold - normCall;
//
//            return new double[] {
//                    normFold, normCall, normRaise};
        }

        public double[] cumulativeRegret() {
            return new double[]{
                    regretFold [bucket][state],
                    regretCall [bucket][state],
                    regretRaise[bucket][state]};
        }
    }
}

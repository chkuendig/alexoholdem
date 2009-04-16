package ao.regret.holdem;

import ao.holdem.model.act.AbstractAction;
import ao.util.data.Pack;
import static ao.util.data.Pack.flatten;
import ao.util.math.rand.Rand;
import ao.util.persist.PersistentChars;
import ao.util.persist.PersistentDoubles;
import ao.util.persist.PersistentInts;

import java.io.File;
import java.util.Arrays;

/**
 * Date: Feb 19, 2009
 * Time: 4:58:01 PM
 */
public class InfoBranch
{
    //--------------------------------------------------------------------
//    private static final String COUNT_FILE = "count.char";
//    private static final String  FOLD_FILE =  "fold.float";
//    private static final String  CALL_FILE =  "call.float";
//    private static final String RAISE_FILE = "raise.float";
//    private static final String VISIT_FILE = "visit.int";
    private static final String COUNT_FILE = "count.char";
    private static final String  FOLD_FILE =  "fold.double";
    private static final String  CALL_FILE =  "call.double";
    private static final String RAISE_FILE = "raise.double";
    private static final String VISIT_FILE = "visit.int";

//    private final double        raiseMin   = 0.001; // .07 in UofA paper
//    private final double        raiseBias  = 1.500; // .07 in UofA paper


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
//                Pack.square(PersistentFloats.retrieve(
//                        new File(dir,  FOLD_FILE)), nBettingSequences),
//                Pack.square(PersistentFloats.retrieve(
//                        new File(dir,  CALL_FILE)), nBettingSequences),
//                Pack.square(PersistentFloats.retrieve(
//                        new File(dir, RAISE_FILE)), nBettingSequences),
//                Pack.square(PersistentInts.retrieve(
//                        new File(dir, VISIT_FILE)), nBettingSequences)
                Pack.square(PersistentDoubles.retrieve(
                        new File(dir,  FOLD_FILE)), nBettingSequences),
                Pack.square(PersistentDoubles.retrieve(
                        new File(dir,  CALL_FILE)), nBettingSequences),
                Pack.square(PersistentDoubles.retrieve(
                        new File(dir, RAISE_FILE)), nBettingSequences),
                Pack.square(PersistentInts.retrieve(
                        new File(dir, VISIT_FILE)), nBettingSequences)
        );
    }

    public static void persist(File dir, InfoBranch branch)
    {
        PersistentChars.persist(new char[]{
                (char) branch.regretFold.length,
                (char) branch.regretFold[0].length},
                new File(dir, COUNT_FILE));

//        PersistentFloats.persist(
//                flatten(branch.regretFold) , new File(dir, FOLD_FILE));
//        PersistentFloats.persist(
//                flatten(branch.regretCall) , new File(dir, CALL_FILE));
//        PersistentFloats.persist(
//                flatten(branch.regretRaise), new File(dir, RAISE_FILE));
        PersistentDoubles.persist(
                flatten(branch.regretFold) , new File(dir, FOLD_FILE));
        PersistentDoubles.persist(
                flatten(branch.regretCall) , new File(dir, CALL_FILE));
        PersistentDoubles.persist(
                flatten(branch.regretRaise), new File(dir, RAISE_FILE));

        PersistentInts.persist(
                flatten(branch.visits), new File(dir, VISIT_FILE));
    }


    //--------------------------------------------------------------------
//    private final float[][] regretFold;
//    private final float[][] regretCall;
//    private final float[][] regretRaise;
    private final double[][] regretFold;
    private final double[][] regretCall;
    private final double[][] regretRaise;

    private final   int[][] visits;



    //--------------------------------------------------------------------
    public InfoBranch(
            char nBuckets,
            char nBettingSequences)
    {
//        regretFold  = new float[ nBuckets ][ nBettingSequences ];
//        regretCall  = new float[ nBuckets ][ nBettingSequences ];
//        regretRaise = new float[ nBuckets ][ nBettingSequences ];
        regretFold  = new double[ nBuckets ][ nBettingSequences ];
        regretCall  = new double[ nBuckets ][ nBettingSequences ];
        regretRaise = new double[ nBuckets ][ nBettingSequences ];

        visits      = new   int[ nBuckets ][ nBettingSequences ];
    }

    private InfoBranch(
//            float copyRegretFold [][],
//            float copyRegretCall [][],
//            float copyRegretRaise[][],
            double copyRegretFold [][],
            double copyRegretCall [][],
            double copyRegretRaise[][],
            int    copyVisits     [][])
    {
        regretFold  = copyRegretFold;
        regretCall  = copyRegretCall;
        regretRaise = copyRegretRaise;
        visits      = copyVisits;
    }


    //--------------------------------------------------------------------
    public void displaySequence(char seq)
    {
        for (char bucket = 0;
                  bucket < regretFold.length;
                  bucket++) {
            InfoSet info = get(bucket, seq);
            System.out.println(Arrays.toString(
                    info.probabilities(true)) + " :: " +
                    Arrays.toString(info.cumulativeRegret()) + " :: " +
                    info.visits());
        }
    }


    //--------------------------------------------------------------------
    public InfoSet get(char roundBucket,
                       char bettingSequence)
    {
        return new InfoSet(roundBucket, bettingSequence);
    }

    public boolean validate(char roundBucket,
                            char bettingSequence)
    {
        return regretFold.length > roundBucket &&
               regretFold[roundBucket].length > bettingSequence;
    }


    //--------------------------------------------------------------------
    public static AbstractAction nextProbableAction(
                double probabilities[]) {
        double toFold = probabilities[0];
        double toCall = probabilities[1];

        double rand = Rand.nextDouble();
        if (toFold >= rand) {
            return AbstractAction.QUIT_FOLD;
        } else {
            rand -= toFold;
            return (toCall >= rand)
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
                    probabilities(canRaise, canCheck));
        }


        //----------------------------------------------------------------
        public void add(double  counterfactualRegret[],
                        boolean canRaise)
        {
            regretFold[bucket][state] += counterfactualRegret[0];
            regretCall[bucket][state] += counterfactualRegret[1];

            if (canRaise) {
                regretRaise[bucket][state] += counterfactualRegret[2];
            }

            visits[bucket][state]++;
        }

//        public void add(AbstractAction act,
//                        double         counterfactualRegret)
//        {
//            switch (act) {
//                case QUIT_FOLD:
//                    regretFold[bucket][state] += counterfactualRegret;
//                    break;
//
//                case CHECK_CALL:
//                    regretCall[bucket][state] += counterfactualRegret;
//                    break;
//
//                case BET_RAISE:
//                    regretRaise[bucket][state] += counterfactualRegret;
//                    break;
//            }
//        }


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
//            probabilitiesAgro(prob, canRaise, canCheck);
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
//                            * raiseBias;
//                if (raiseProb == 0) raiseProb = raiseMin;

                if (canCheck && foldProb != 0) {
                    into[0] = 0;
                    into[1] = callProb / (callProb + raiseProb);
                    into[2] = 1.0 - into[1];
                } else {
//                    double total = foldProb + callProb + raiseProb;
//                    into[0] =  foldProb / total;
//                    into[1] =  callProb / total;
//                    into[2] = raiseProb / total;

                    into[0] =  foldProb;
                    into[1] =  callProb;
                    into[2] = raiseProb;
                }
            }
        }

        private void defaultProbabilities(
                double into[], boolean canRaise, boolean canCheck)
        {
//            if (canRaise) {
//                if (canCheck) {
//                    into[0] = 0;
//
//                    double min = Math.min(
//                            regretCall [bucket][state],
//                            regretRaise[bucket][state]);
//                    if (min == 0) {
////                        probabilitiesAgro(into, canRaise, canCheck);
//                        into[1] = into[2] = 0.5;
//                        return;
//                    }
//
//                    double c = regretCall [bucket][state] - min;
//                    double r = regretRaise[bucket][state] - min;
//
//                    into[1] = c / (c + r);
//                    into[2] = r / (c + r);
//                } else {
//                    double min = Math.min(Math.min(
//                            regretFold [bucket][state],
//                            regretCall [bucket][state]),
//                            regretRaise[bucket][state]);
//                    if (min == 0) {
////                        probabilitiesAgro(into, canRaise, canCheck);
//                        into[0] = into[1] = into[2] = 1.0/3;
//                        return;
//                    }
//
//                    double f = regretFold [bucket][state] - min;
//                    double c = regretCall [bucket][state] - min;
//                    double r = regretRaise[bucket][state] - min;
//
//                    into[0] = f / (f + c + r);
//                    into[1] = c / (f + c + r);
//                    into[2] = r / (f + c + r);
//                }
//            } else {
//                if (canCheck) {
//                    into[0] = 0;
//                    into[1] = 1.0;
//                } else {
//                    double min = Math.min(
//                            regretFold[bucket][state],
//                            regretCall[bucket][state]);
//                    if (min == 0) {
////                        probabilitiesAgro(into, canRaise, canCheck);
//                        into[0] = into[1] = 0.5;
//                        return;
//                    }
//
//                    double f = regretFold[bucket][state] - min;
//                    double c = regretCall[bucket][state] - min;
//
//                    into[0] = f / (f + c);
//                    into[1] = c / (f + c);
//                }
//                into[2] = 0;
//            }

            if (canRaise) {
                if (canCheck) {
                    into[0] = 0;
                    into[1] = into[2] = 0.5;

//                    into[2] = 0.5 * raiseBias;
//                    into[1] = 1.0 - into[2];
                } else {
                    into[0] = 0.1;

//                    into[1] = into[2] = (1.0 / 3);
                    into[1] = into[2] = (1.0 - into[0]) / 2;
                }
            } else {
                if (canCheck) {
                    into[0] = 0;
                    into[1] = 1.0;
                } else {
                    into[0] = 0.1;
                    into[1] = 1.0 - into[1];
                }
                into[2] = 0;
            }
        }

        private void probabilitiesAgro(
                double into[], boolean canRaise, boolean canCheck)
        {
            if (canRaise) {
                into[0] = into[1] = 0;
                into[2] = 1.0;
            } else {
                into[0] = into[2] = 0;
                into[1] = 1.0;
            }
        }


        //----------------------------------------------------------------
        public int visits() {
            return visits[bucket][state];
        }

        public double[] cumulativeRegret() {
            return new double[]{
                    regretFold [bucket][state],
                    regretCall [bucket][state],
                    regretRaise[bucket][state]};
        }
    }
}

package ao.regret.holdem;

import ao.holdem.model.act.AbstractAction;
import ao.util.data.Pack;
import static ao.util.data.Pack.flatten;
import ao.util.math.rand.Rand;
import ao.util.persist.PersistentChars;
import ao.util.persist.PersistentFloats;
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
    private static final String COUNT_FILE = "count.char";
    private static final String  FOLD_FILE =  "fold.float";
    private static final String  CALL_FILE =  "call.float";
    private static final String RAISE_FILE = "raise.float";
    private static final String VISIT_FILE = "visit.int";


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

        PersistentFloats.persist(
                flatten(branch.regretFold) , new File(dir, FOLD_FILE));
        PersistentFloats.persist(
                flatten(branch.regretCall) , new File(dir, CALL_FILE));
        PersistentFloats.persist(
                flatten(branch.regretRaise), new File(dir, RAISE_FILE));
        PersistentInts.persist(
                flatten(branch.visits), new File(dir, VISIT_FILE));
    }


    //--------------------------------------------------------------------
    private final float[][] regretFold;
    private final float[][] regretCall;
    private final float[][] regretRaise;
    private final   int[][] visits;



    //--------------------------------------------------------------------
    public InfoBranch(
            char nBuckets,
            char nBettingSequences)
    {
        regretFold  = new float[ nBuckets ][ nBettingSequences ];
        regretCall  = new float[ nBuckets ][ nBettingSequences ];
        regretRaise = new float[ nBuckets ][ nBettingSequences ];
        visits      = new   int[ nBuckets ][ nBettingSequences ];
    }

    private InfoBranch(
            float copyRegretFold [][],
            float copyRegretCall [][],
            float copyRegretRaise[][],
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
                    info.probabilities(true)));
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
        public AbstractAction nextProbableAction(boolean canRaise)
        {
            return InfoBranch.nextProbableAction(probabilities(canRaise));
        }


        //----------------------------------------------------------------
        public void add(double[] counterfactualRegret,
                        boolean  canRaise)
        {
            regretFold[bucket][state] += counterfactualRegret[0];
            regretCall[bucket][state] += counterfactualRegret[1];

            if (canRaise) {
                regretRaise[bucket][state] += counterfactualRegret[2];
            }

            visits[bucket][state]++;
        }

        public void add(AbstractAction act,
                        double         counterfactualRegret)
        {
            switch (act) {
                case QUIT_FOLD:
                    regretFold[bucket][state] += counterfactualRegret;
                    break;

                case CHECK_CALL:
                    regretCall[bucket][state] += counterfactualRegret;
                    break;

                case BET_RAISE:
                    regretRaise[bucket][state] += counterfactualRegret;
                    break;
            }
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
            return probabilities(canRaise, false, false);
        }
        public double[] probabilities(
                boolean canRaise, boolean canCheck, boolean fudge)
        {
            //double prob[]    = new double[ canRaise ? 3 : 2 ];
            double cumRegret = positiveCounterfactualRegret();

            if (cumRegret <= 0) {
                if (canRaise) {
                    if (canCheck) {
                        return new double[]{0, 0.5, 0.5};
                    } else {
                        return new double[]{1.0/3, 1.0/3, 1.0/3};
                    }
                } else {
                    if (canCheck) {
                        return new double[]{0, 1.0};
                    } else {
                        return new double[]{0.5, 0.5};
                    }
                }
            } else {

                double foldProb, callProb, raiseProb;

                if (fudge) {
                    foldProb  = Math.max(0.001,
                            regretFold [bucket][state] / cumRegret);
                    callProb  = Math.max(0.001,
                            regretCall [bucket][state] / cumRegret);
                    raiseProb = Math.max(0.001,
                            regretRaise[bucket][state] / cumRegret);

                    if (!canRaise) {
                        raiseProb = 0;
                    }
                } else {
                    foldProb = Math.max(0,
                            regretFold [bucket][state] / cumRegret);
                    callProb = Math.max(0,
                            regretCall [bucket][state] / cumRegret);
                    raiseProb = Math.max(0,
                            regretRaise[bucket][state] / cumRegret);
                }
                if (canCheck) {
                    foldProb = 0;
                }

                double sum = foldProb + callProb + raiseProb;
                if (canRaise) {
                    return new double[]{foldProb / sum,
                            callProb / sum, raiseProb / sum};
                } else {
                    return new double[]{foldProb / sum, callProb / sum};
                }
            }
        }


        //----------------------------------------------------------------
        public int visits() {
            return visits[bucket][state];
        }
    }
}

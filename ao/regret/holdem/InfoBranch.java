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
    private static final String COUNT_FILE = "count.char";
    private static final String  FOLD_FILE =  "fold.double";
    private static final String  CALL_FILE =  "call.double";
    private static final String RAISE_FILE = "raise.double";
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
    private final double[][] regretFold;
    private final double[][] regretCall;
    private final double[][] regretRaise;
    private final    int[][] visits;



    //--------------------------------------------------------------------
    public InfoBranch(
            char nBuckets,
            char nBettingSequences)
    {
        regretFold  = new double[ nBuckets ][ nBettingSequences ];
        regretCall  = new double[ nBuckets ][ nBettingSequences ];
        regretRaise = new double[ nBuckets ][ nBettingSequences ];
        visits      = new    int[ nBuckets ][ nBettingSequences ];
    }

    private InfoBranch(
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
            double prob[] = probabilities(canRaise);

            double toFold = prob[0];
            double toCall = prob[1];

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


        //--------------------------------------------------------------------
        private double positiveCounterfactualRegret()
        {
            return Math.max(regretFold [bucket][state], 0) +
                   Math.max(regretCall [bucket][state], 0) +
                   Math.max(regretRaise[bucket][state], 0);
        }


        //----------------------------------------------------------------
        public double[] probabilities(boolean canRaise)
        {
            double prob[]    = new double[ canRaise ? 3 : 2 ];
            double cumRegret = positiveCounterfactualRegret();

            if (cumRegret <= 0) {
                for (int i = 0; i < prob.length; i++) {
                    prob[ i ] = 1.0 / prob.length;
                }
            } else {
                double foldProb = Math.max(0,
                        regretFold [bucket][state] / cumRegret);
                double callProb = Math.max(0,
                        regretCall [bucket][state] / cumRegret);
                double raiseProb = Math.max(0,
                        regretRaise[bucket][state] / cumRegret);

                double sum = foldProb + callProb + raiseProb;
                prob[0] = foldProb / sum;
                prob[1] = callProb / sum;
                if (canRaise) {
                    prob[2] = raiseProb / sum;
                }
            }
            return prob;
        }


        //----------------------------------------------------------------
        public int visits() {
            return visits[bucket][state];
        }
    }
}

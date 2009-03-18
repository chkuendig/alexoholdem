package ao.regret.holdem.v2;

import ao.holdem.model.act.AbstractAction;

import java.util.Arrays;

/**
 * Date: Feb 19, 2009
 * Time: 4:58:01 PM
 */
public class InfoBranch
{
    //--------------------------------------------------------------------
    private static final int SPARSE_LIMIT = 5;


    //--------------------------------------------------------------------
    private final float[][] regretFold;
    private final float[][] regretCall;
    private final float[][] regretRaise;

//    private final char[][]  probabilityFold;
//    private final char[][]  probabilityCall;
    //private final float[][] probabilityRaise;

    private final int[][]   visits;



    //--------------------------------------------------------------------
    public InfoBranch(
            char nBuckets,
            char nBettingSequences)
    {
        regretFold  = new float[ nBuckets ][ nBettingSequences ];
        regretCall  = new float[ nBuckets ][ nBettingSequences ];
        regretRaise = new float[ nBuckets ][ nBettingSequences ];

//        probabilityFold = new char[ nBuckets ][ nBettingSequences ];
//        probabilityCall = new char[ nBuckets ][ nBettingSequences ];

        visits = new int[ nBuckets ][ nBettingSequences ];
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
        public AbstractAction nextProbableAction()
        {
            return null;
//            if (visits() == 0) {
//                return Rand.nextBoolean()
//                       ? AbstractAction.CHECK_CALL
//                       : AbstractAction.BET_RAISE;
//            }
//
//            char probFold = probabilityFold[bucket][state];
//            char probCall = probabilityCall[bucket][state];
//
//            double realProbFold = (double) probFold / Character.MAX_VALUE;
//            double realProbCall = (double) probCall / Character.MAX_VALUE;
//
//            double rand = Rand.nextDouble();
//            if (realProbFold >= rand) {
//                return AbstractAction.QUIT_FOLD;
//            } else {
//                rand -= realProbFold;
//                return (realProbCall >= rand)
//                       ? AbstractAction.CHECK_CALL
//                       : AbstractAction.BET_RAISE;
//            }
        }


        //--------------------------------------------------------------------
        public void add(double[] counterfactualRegret,
                        boolean  canRaise)
        {
            for (AbstractAction act : AbstractAction.VALUES)
            {
                double cRegret = counterfactualRegret[ act.ordinal() ];
                if (act == AbstractAction.QUIT_FOLD) {
                     regretFold[bucket][state] += cRegret;
                } else if (act == AbstractAction.CHECK_CALL) {
                     regretCall[bucket][state] += cRegret;
                } else if (canRaise) {
                    regretRaise[bucket][state] += cRegret;
                }
            }

            visits[bucket][state]++;
//            updateActionPabilities(canRaise);
        }


        //--------------------------------------------------------------------
//        private void updateActionPabilities(boolean canRaise)
//        {
//            double cumRegret = positiveCounterfactualRegret();
//
//            if (cumRegret <= 0)
//            {
//                char prob = probToChar(1.0 / (canRaise ? 3 : 2));
//                probabilityFold[bucket][state] = prob;
//                probabilityCall[bucket][state] = prob;
//            }
//            else
//            {
//                //probabilityFold[bucket][state]
//
//                double foldProb = Math.max(0,
//                        regretFold[bucket][state] / cumRegret);
//                double callProb = Math.max(0,
//                        regretCall[bucket][state] / cumRegret);
//                double raiseProb = Math.max(0,
//                        regretRaise[bucket][state] / cumRegret);
//
//                double sum = foldProb + callProb + raiseProb;
//
//                probabilityFold[bucket][state] =
//                        probToChar(foldProb / sum);
//                probabilityCall[bucket][state] =
//                        probToChar(callProb / sum);
//            }
//        }

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
                        regretFold[bucket][state] / cumRegret);
                double callProb = Math.max(0,
                        regretCall[bucket][state] / cumRegret);
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
//        public double probabilityOf(
//                AbstractAction key, boolean canRaise)
//        {
//            if (visits() == 0) {
//                return 1.0 / (canRaise ? 3 : 2);
//            }
//
//            char probFold = probabilityFold[bucket][state];
//            char probCall = probabilityCall[bucket][state];
//
//            double realProbFold  = probFromChar(probFold);
//            double realProbCall  = probFromChar(probCall);
//            double realProbRaise = 1.0 - realProbFold - realProbCall;
//
//            return   key == AbstractAction.QUIT_FOLD  ? realProbFold
//                   : key == AbstractAction.CHECK_CALL ? realProbCall
//                                                      : realProbRaise;
//        }
//
//        private double probFromChar(char c) {
//            return (double) c / Character.MAX_VALUE;
//        }
//        private char probToChar(double prob) {
//            return (char)(prob * Character.MAX_VALUE);
//        }



        //----------------------------------------------------------------
        public boolean isInformed()
        {
            return visits() > SPARSE_LIMIT;
        }

        public int visits() {
            return visits[bucket][state];
        }
    }
}

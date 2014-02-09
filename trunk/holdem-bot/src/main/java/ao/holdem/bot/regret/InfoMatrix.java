package ao.holdem.bot.regret;

import ao.holdem.engine.state.tree.StateTree;
import ao.holdem.model.act.AbstractAction;
import ao.holdem.bot.regret.grid.Grid;
import ao.holdem.bot.regret.grid.StoredGrid;
import ao.util.math.rand.Rand;
import ao.util.persist.PersistentChars;
import ao.util.persist.PersistentInts;
import org.apache.log4j.Logger;

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
    private static final Logger LOG =
            Logger.getLogger(InfoMatrix.class);

//    private static final String    COUNT_FILE = "count.int";
//    private static final String CFREGRET_FILE = "cfreg.float";
//    private static final String STRATEGY_FILE = "strat.float";

    private static final String    COUNT_FILE = "count.int";
    private static final String CFREGRET_FILE = "cfreg.double";
    private static final String STRATEGY_FILE = "strat.double";


    //--------------------------------------------------------------------
    public static boolean exists(File dir) {
        return new File(dir, COUNT_FILE).exists();
    }

    public static InfoMatrix retrieveOrCreate(
            File dir, int nBuckets, int nIntents,
            boolean readOnly, boolean doublePrecision) {
        char counts[] = PersistentChars.retrieve(
                            new File(dir, COUNT_FILE));

        LOG.debug((counts == null ? "creating" : "retrieving") +
                  " in " + dir);
        return (counts == null)
                ? newInstance(nBuckets, nIntents, doublePrecision)
                : new InfoMatrix(
                        retrieve(dir, STRATEGY_FILE,
                                 nBuckets, nIntents,
                                 readOnly, doublePrecision),
                        retrieve(dir, CFREGRET_FILE,
                                 nBuckets, nIntents,
                                 readOnly, doublePrecision));
    }

    public static InfoMatrix newInstance(
            int nBuckets, int nIntents, boolean doublePrecision) {
        return new InfoMatrix(nBuckets, nIntents, doublePrecision);
    }
    private static Grid retrieve(
            File dir, String file,
            int nBuckets, int nIntents,
            boolean readOnly, boolean doublePrecision) {
        Grid grid = Grid.Impl.newInstance(
                nBuckets, nIntents, readOnly, doublePrecision);
        grid.load( new File(dir, file) );
        return grid ;
    }


    public static void persist(File dir, InfoMatrix matrix)
    {
        PersistentInts.persist(new int[]{
                matrix.averageStrategy.rows(),
                matrix.averageStrategy.columns()},
                new File(dir, COUNT_FILE));

        persist(matrix.averageStrategy,  dir, STRATEGY_FILE);
        persist(matrix.cumulativeRegret, dir, CFREGRET_FILE);
    }
    private static void persist(Grid vals, File dir, String file) {
        vals.save(new File(dir, file));
    }


    //--------------------------------------------------------------------
    private static final double defaultEqual  [] = {
                                        1.0 / 3, 1.0 / 3, 1.0 / 3};
    private static final double defaultNoRaise[] = {0.5, 0.5, 0  };
    private static final double defaultNoFold [] = {0  , 0.5, 0.5};


    //--------------------------------------------------------------------
    private final Grid averageStrategy;
    private final Grid cumulativeRegret;


    //--------------------------------------------------------------------
    private InfoMatrix(int     nBuckets,
                       int     nIntents,
                       boolean doublePrecision)
    {
        this(Grid.Impl.newInstance(nBuckets, nIntents, doublePrecision),
             Grid.Impl.newInstance(nBuckets, nIntents, doublePrecision));
    }

    private InfoMatrix(
            Grid copyAverageStrategy,
            Grid copyCumulativeRegret)
    {
        averageStrategy  = copyAverageStrategy;
        cumulativeRegret = copyCumulativeRegret;
    }



    //--------------------------------------------------------------------
    public void displayHeadsUpRoot() {
        for (int bucket = 0; bucket < averageStrategy.rows(); bucket++) {
            StateTree.Node node = StateTree.headsUpRoot();
            InfoSet infoSet = infoSet(bucket, node.foldIntent(), node.callIntent(), node.raiseIntent());
            LOG.info(infoSet);
        }
    }

    public boolean isReadOnly() {
        return averageStrategy instanceof StoredGrid;
    }


    //--------------------------------------------------------------------
    public InfoSet infoSet(
            int bucket,
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
            return nextProbableAction( averageStrategy() );
        }

        public AbstractAction nextProbableAction(
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
                   ? 0 : Math.max(averageStrategy.get(bucket, intent), 0);
        }
        public double[] averages() {
            return new double[] {
                    average(fIntent), average(cIntent), average(rIntent)};
        }


        //----------------------------------------------------------------
        public void add(double strategy[],
                        double proponentReachProbability) {
            if (fIntent != -1)
                averageStrategy.add(bucket, fIntent,
                        proponentReachProbability * strategy[0]);

          //if (cIntent != -1)
                averageStrategy.add(bucket, cIntent,
                        proponentReachProbability * strategy[1]);

            if (rIntent != -1)
                averageStrategy.add(bucket, rIntent,
                        proponentReachProbability * strategy[2]);
        }

        public void add(double counterfactualRegret[]) {
            if (fIntent != -1)
                cumulativeRegret.add(bucket, fIntent,
                                     counterfactualRegret[0]);

          //if (cIntent != -1)
                cumulativeRegret.add(bucket, cIntent,
                                     counterfactualRegret[1]);

            if (rIntent != -1)
                cumulativeRegret.add(bucket, rIntent,
                                     counterfactualRegret[2]);
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
            return intent == -1
                   ? 0 : cumulativeRegret.get(bucket, intent);
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
            return Arrays.toString(new int[]{
                    (int) Math.round(averageStrategy()[0] * 100),
                    (int) Math.round(averageStrategy()[1] * 100),
                    (int) Math.round(averageStrategy()[2] * 100)})
                    + "\t" +
                    (long) Math.ceil(
                            average(fIntent) +
                            average(cIntent) +
                            average(rIntent));
        }

        public String toShortString()
        {
            return (int) Math.round(averageStrategy()[0] * 100) + "/" +
                   (int) Math.round(averageStrategy()[1] * 100) + "/" +
                   (int) Math.round(averageStrategy()[2] * 100);
        }
    }
}

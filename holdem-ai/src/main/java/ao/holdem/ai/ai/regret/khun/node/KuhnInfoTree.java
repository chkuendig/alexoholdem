package ao.holdem.ai.ai.regret.khun.node;

import ao.holdem.bucket.Bucket;
import ao.holdem.ai.ai.regret.InfoNode;
import ao.holdem.ai.ai.regret.khun.Equilibrium;
import ao.holdem.ai.ai.regret.khun.JointBucketSequence;
import ao.holdem.ai.ai.simple.kuhn.KuhnAction;
import ao.holdem.ai.ai.simple.kuhn.KuhnCard;
import ao.holdem.ai.ai.simple.kuhn.rules.KuhnBucket;
import ao.holdem.ai.ai.simple.kuhn.rules.KuhnRules;
import ao.holdem.ai.ai.simple.kuhn.rules.KuhnSequencer;
import ao.holdem.ai.ai.simple.kuhn.state.KuhnState;
import ao.util.math.rand.Rand;
import ao.util.text.Txt;
import org.apache.log4j.Logger;

import java.util.Arrays;

/**
 *
 */
public class KuhnInfoTree
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(KuhnInfoTree.class);


    //--------------------------------------------------------------------
    private final double passRegret[][];
    private final double betRegret [][];
    private final double passSum   [][];
    private final double reachSum  [][];


    //--------------------------------------------------------------------
    public KuhnInfoTree()
    {
        int nBuckets = KuhnCard.VALUES.length;
        int nStates  = KuhnState.VALUES.length;

        passRegret = new double[ nBuckets ][ nStates ];
        betRegret  = new double[ nBuckets ][ nStates ];
        passSum    = new double[ nBuckets ][ nStates ];
        reachSum   = new double[ nBuckets ][ nStates ];
    }


    //--------------------------------------------------------------------
    public InfoSet infoSet(KuhnCard card, KuhnState state)
    {
        return new InfoSet(card.ordinal(), state.ordinal());
    }


    //--------------------------------------------------------------------
    public class InfoSet
    {
        //----------------------------------------------------------------
        private final int bucket;
        private final int state;

        public InfoSet(int inBucket, int inState) {
            bucket = inBucket;
            state  = inState;
        }

        //----------------------------------------------------------------
        public void add(double counterfactualRegret[],
                        double actionProbabilities[],
                        double proponentReachProbability) {
            passSum [bucket][state] += actionProbabilities[0]
                                       * proponentReachProbability;
            reachSum[bucket][state] += proponentReachProbability;

            passRegret[bucket][state] += counterfactualRegret[0];
            betRegret [bucket][state] += counterfactualRegret[1];
        }

        //----------------------------------------------------------------
        public KuhnAction nextProbableAction() {
            double prob[] = averageStrategy();
            return Rand.nextBoolean(prob[0])
                   ? KuhnAction.PASS : KuhnAction.BET;
        }
        public double[] averageStrategy() {
            double passProb =
                    passSum[bucket][state] / reachSum[bucket][state];
            return new double[]{
                    passProb, 1.0 - passProb};
        }

        //----------------------------------------------------------------
        public double[] positiveRegretStrategy() {
            double prob[]    = new double[2];
            double cumRegret = positiveCumulativeCounterfactualRegret();

            if (cumRegret <= 0) {
                prob[0] = prob[1] = 1.0 / 2;
            } else {
                prob[0] = Math.max(0,
                            passRegret[bucket][state] / cumRegret);
                prob[1] = Math.max(0,
                            betRegret [bucket][state] / cumRegret);
            }

            return prob;
        }

        private double positiveCumulativeCounterfactualRegret() {
            return Math.max(passRegret[bucket][state], 0) +
                   Math.max(betRegret [bucket][state], 0);
        }

        //----------------------------------------------------------------
        @Override public String toString() {
            return Arrays.toString(averageStrategy());
        }
    }



    //--------------------------------------------------------------------
    @Override public String toString()
    {
        StringBuilder str = new StringBuilder();

        for (KuhnCard b : KuhnCard.VALUES) {
            str.append(b).append("\n")
               .append(infoString(b, KuhnState.FIRST_ACTION, 1))
               .append(infoString(b, KuhnState.AFTER_PASS  , 2))
               .append(infoString(b, KuhnState.AFTER_BET   , 2))
               .append(infoString(b, KuhnState.AFTER_RAISE , 3));
        }

        return str.toString();
    }

    private String infoString(KuhnCard b, KuhnState state, int indent)
    {
        return Txt.nTimes("\t", indent) +
               state +
               infoSet(b, state) +
               "\n";
    }


    //--------------------------------------------------------------------
    public static BucketNode initialize(
            Bucket<KuhnBucket> rootSequences,
            boolean            isDealer)
    {
        return new BucketNode(
                        rootSequences.nextBuckets(),
                        new KuhnRules(),
                        isDealer);
    }


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        KuhnSequencer sequencer = new KuhnSequencer();

        Equilibrium equilibrium = new Equilibrium();

        InfoNode firstRoot =
                KuhnInfoTree.initialize(sequencer.root(), false);
        InfoNode lastRoot  =
                KuhnInfoTree.initialize(sequencer.root(), true);

        for (int i = 0; i < 1000000; i++)
        {
            equilibrium.approximate(
                    firstRoot, lastRoot,
                    new JointBucketSequence(),
                    1.0, 1.0);
        }

        LOG.info("first:\n" + firstRoot);
        LOG.info("last:\n"  + lastRoot);
    }
}

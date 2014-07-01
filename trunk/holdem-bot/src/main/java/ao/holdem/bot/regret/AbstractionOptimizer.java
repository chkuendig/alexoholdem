package ao.holdem.bot.regret;

import ao.holdem.abs.bucket.abstraction.access.BucketDecoder;
import ao.holdem.abs.bucket.abstraction.access.odds.BucketOdds;
import ao.holdem.abs.bucket.abstraction.access.tree.BucketTree;
import ao.holdem.abs.bucket.abstraction.bucketize.def.Bucketizer;
import ao.holdem.abs.bucket.abstraction.bucketize.smart.KMeansBucketizer;
import ao.holdem.abs.bucket.index.detail.flop.FlopDetails;
import ao.holdem.abs.bucket.index.detail.turn.TurnRivers;
import ao.holdem.abs.odds.eval.eval7.Eval7Faster;
import ao.holdem.canon.flop.Flop;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.canon.hole.CanonHole;
import ao.util.data.Arrs;
import ao.util.math.rand.Rand;
import ao.util.math.stats.Combiner;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: alex
 * Date: 24-Jan-2010
 * Time: 11:36:32 AM
 */
public class AbstractionOptimizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG              =
            Logger.getLogger(AbstractionOptimizer.class);

    private static final int    MONTE_CARLO_RUNS = 1000 * 1000;


    //--------------------------------------------------------------------
    public static void main(String[] args) throws IOException
    {
        Rand.randomize();

        int  nHoleBuckets  =   5;
        char nFlopBuckets  =  25;
        char nTurnBuckets  = 125;
        char nRiverBuckets = 625;
//        int  nHoleBuckets  =    48;
//        char nFlopBuckets  =  1024;
//        char nTurnBuckets  =  8192;
//        char nRiverBuckets = 65535;

        if (args.length > 1)
        {
            nHoleBuckets  = (byte) Integer.parseInt(args[0]);
            nFlopBuckets  = (char) Integer.parseInt(args[1]);
            nTurnBuckets  = (char) Integer.parseInt(args[2]);
            nRiverBuckets = (char) Integer.parseInt(args[3]);
        }

        LOG.debug("Using " +
                      nHoleBuckets + ", " + (int) nFlopBuckets + ", " +
                (int) nTurnBuckets + ", " + (int) nRiverBuckets);

        HoldemAbstraction abs = abstractHoldem(
//                new KMeansBucketizer(),
//                new PotentialBucketizer(),
//                new HandStrengthAbs(),
//                new PercentileAbs(),
//                new HistBucketizer((byte) 4),
//                new HistBucketizer((byte) 2),
//                new HistBucketizer((byte) 3),
                nHoleBuckets, nFlopBuckets,
                nTurnBuckets, nRiverBuckets);

//        int[] byBucket = new int[625];
//        for (long river = 0; river < RiverLookup.CANONS; river++) {
//            byBucket[ riverBucket(
//                          abs.tree(false), abs.decoder(), river)
//                    ]++;
//        }
//        System.out.println("byBucket = " + Arrays.toString(byBucket));


        LOG.info("Error is: " + stochasticAbstractionRmsError(
                abs.tree(false), abs.decoder(), abs.odds()));
    }


    //--------------------------------------------------------------------
    private static char riverBucket(
            BucketTree    tree,
            BucketDecoder decoder,
            long          canonRiver)
    {
        int turn = TurnRivers.turnFor(canonRiver);
        int flop = (int) FlopDetails.containing(turn).canonIndex();
        int hole = (int) FlopDetails.lookup(flop)
                                    .holeDetail().canonIndex();

        return decoder.decode(
                tree.getHole(hole),
                tree.getFlop(flop),
                tree.getTurn(turn),
                tree.getRiver(canonRiver));
    }


    //--------------------------------------------------------------------
    private static double stochasticAbstractionRmsError(
            BucketTree    tree,
            BucketDecoder decoder,
            BucketOdds    odds)
    {
//        LOG.debug("computing ");

        double sumSquaredError = 0;
        for (int i = 0; i < MONTE_CARLO_RUNS; i++)
        {
            Card[] cards = Card.VALUES.clone();
            Arrs.shuffle(cards);

            short propValue = Eval7Faster.valueOf(cards[0], cards[1],
                    cards[4], cards[5], cards[6], cards[7], cards[8]);
            short oppValue  = Eval7Faster.valueOf(cards[2], cards[3],
                    cards[4], cards[5], cards[6], cards[7], cards[8]);
            double actualVsRandom =
                            (propValue < oppValue ? 0.0 :
                             propValue > oppValue ? 1.0 : 0.5 );

            long propCanonRiver = new Flop(
                    CanonHole.create(cards[0], cards[1]),
                    cards[4], cards[5], cards[6])
                    .addTurn(cards[7]).addRiver(cards[8])
                    .canonIndex();
            long oppCanonRiver = new Flop(
                    CanonHole.create(cards[2], cards[3]),
                    cards[4], cards[5], cards[6])
                    .addTurn(cards[7]).addRiver(cards[8])
                    .canonIndex();
            double abstractVsRandom =
                    odds.nonLossProb(
                        riverBucket(tree, decoder, propCanonRiver),
                        riverBucket(tree, decoder, oppCanonRiver));

            double error = actualVsRandom - abstractVsRandom;
            sumSquaredError += error * error;
        }
        return Math.sqrt(sumSquaredError / MONTE_CARLO_RUNS);
    }


    //--------------------------------------------------------------------
    private static double abstractionError(
            BucketTree tree, BucketOdds odds)
    {
        double sumSquaredError = 0;

        List<Card> unknown = new ArrayList<Card>(
                    Arrays.asList(Card.VALUES));

        LOG.info("Computing abstraction error");
        for (Card[] community : new Combiner<Card>(
                unknown.toArray(new Card[unknown.size()]), 5))
        {
            System.out.print(".");

            unknown.removeAll( Arrays.asList(community) );

            int communityShortcut =
                    Eval7Faster.shortcutFor(community);

            for (Card[] oppHole : new Combiner<Card>(
                    unknown.toArray(new Card[unknown.size()]), 2))
            {
                unknown.removeAll( Arrays.asList(oppHole) );

                short oppValue = Eval7Faster.fastValueOf(
                        communityShortcut, oppHole[0], oppHole[1]);

                long oppCanonRiver = new Flop(
                        CanonHole.create(oppHole[0], oppHole[1]),
                        community[0], community[1], community[2]
                    ).addTurn(
                        community[3]
                    ).addRiver(
                        community[4]
                    ).canonIndex();


                for (Card[] propHole : new Combiner<Card>(
                        unknown.toArray(new Card[unknown.size()]), 2))
                {
                    short propValue = Eval7Faster.fastValueOf(
                        communityShortcut, propHole[0], propHole[1]);

                    double actualVsRandom =
                            (propValue < oppValue ? 0.0 :
                             propValue > oppValue ? 1.0 : 0.5 );

                    long propCanonRiver = new Flop(
                            CanonHole.create(propHole[0], propHole[1]),
                            community[0], community[1], community[2]
                            ).addTurn(
                                community[3]
                            ).addRiver(
                                community[4]
                            ).canonIndex();

                    double abstractVsRandom =
                            odds.nonLossProb(
                                    (char) tree.getRiver(propCanonRiver),
                                    (char) tree.getRiver(oppCanonRiver));

                    double error = actualVsRandom - abstractVsRandom;
                    sumSquaredError += error * error;
                }

                unknown.addAll( Arrays.asList(oppHole) );
            }

            unknown.addAll( Arrays.asList(community) );
        }

        return sumSquaredError;
    }


    //--------------------------------------------------------------------
    private static HoldemAbstraction abstractHoldem(
            int        nHoleBuckets,
            char       nFlopBuckets,
            char       nTurnBuckets,
            char       nRiverBuckets)
    {
        return abstractHoldem(
                new KMeansBucketizer(),
//                new PotentialBucketizer(),
//                new HandStrengthAbs(),
//                new PercentileAbs(),
//                new HistBucketizer((byte) 4),
//                new HistBucketizer((byte) 2),
//                new HistBucketizer((byte) 3),
                nHoleBuckets, nFlopBuckets, nTurnBuckets, nRiverBuckets);
    }

    private static HoldemAbstraction abstractHoldem(
            Bucketizer bucketizer,
            int        nHoleBuckets,
            char       nFlopBuckets,
            char       nTurnBuckets,
            char       nRiverBuckets)
    {
        return new HoldemAbstraction(
                        bucketizer,
                        nHoleBuckets,
                        nFlopBuckets,
                        nTurnBuckets,
                        nRiverBuckets);
    }
}

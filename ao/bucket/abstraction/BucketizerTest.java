package ao.bucket.abstraction;

import ao.ai.equilibrium.limit_cfr.CfrBot2;
import ao.ai.simple.AlwaysRaiseBot;
import ao.bucket.abstraction.bucketize.def.Bucketizer;
import ao.bucket.abstraction.bucketize.smart.HistBucketizer;
import ao.bucket.index.detail.preflop.HoleOdds;
import ao.holdem.engine.Player;
import ao.holdem.engine.dealer.DealerTest;
import ao.holdem.engine.state.StateFlow;
import ao.holdem.model.Avatar;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.LiteralCardSequence;
import ao.odds.agglom.impl.PreciseHeadsUpOdds;
import ao.regret.holdem.InfoPart;
import ao.regret.holdem.mono.ChainMinimizer;
import ao.regret.holdem.parallel.ParallelMinimizer;
import ao.util.math.rand.Rand;
import ao.util.time.Progress;
import ao.util.time.Stopwatch;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * User: alex
 * Date: 21-Apr-2009
 * Time: 11:02:42 PM
 */
public class BucketizerTest
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(BucketizerTest.class);

//    private static final String BOT_NAME = "agro";
    private static final String BOT_NAME   = "serial";
    private static final String  VS_NAME   = "serial_2000";
    private static final double AGGRESSION = 1.0;


    //--------------------------------------------------------------------
    public static void main(String[] args) throws IOException
    {
//        byte nHoleBuckets  = 3;
//        char nFlopBuckets  = 9;
//        char nTurnBuckets  = 27;
//        char nRiverBuckets = 81;
//        byte nHoleBuckets  = 5;
//        char nFlopBuckets  = 25;
//        char nTurnBuckets  = 125;
//        char nRiverBuckets = 625;
//        byte nHoleBuckets  = 20;
        byte nHoleBuckets  =    32;
        char nFlopBuckets  =   640;
        char nTurnBuckets  =  4480;
        char nRiverBuckets = 31360;

        if (args.length > 1)
        {
            nHoleBuckets  = (byte) Integer.parseInt(args[0]);
            nFlopBuckets  = (char) Integer.parseInt(args[1]);
            nTurnBuckets  = (char) Integer.parseInt(args[2]);
            nRiverBuckets = (char) Integer.parseInt(args[3]);
        }
        System.out.println("Using " +
                (int) nHoleBuckets + ", " + (int) nFlopBuckets + ", " +
                (int) nTurnBuckets + ", " + (int) nRiverBuckets);

        HoldemAbstraction abs = abstractHolem(
//                new KMeansBucketizer(),
                new HistBucketizer(),
                nHoleBuckets, nFlopBuckets, nTurnBuckets, nRiverBuckets);

        // preload
        abs.tree(false);
//        abs.odds();
//        abs.sequence();

//        Rand.randomize();
//        computeCfr(abs);
//        tournament(abs);
//        vsHuman(abs);
    }


    //--------------------------------------------------------------------
    private static HoldemAbstraction abstractHolem(
            Bucketizer bucketizer,
            byte       nHoleBuckets,
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

    public static void precompute(
            final CfrBot2 bot, boolean computeOdds)
    {
        Stopwatch time = new Stopwatch();
//        System.out.println("Loading......");
//        new DealerTest(10).headsUp(new HashMap<Avatar, Player>(){{
//            put(Avatar.local("probe"), new AlwaysCallBot());
//            put(Avatar.local("cfr2"), bot);
//        }});

        StateFlow sf = new StateFlow(Arrays.asList(
                Avatar.local("a"), Avatar.local("a")), true);
        bot.act(sf.head(),
                new LiteralCardSequence(
                        Hole.valueOf(Card.ACE_OF_CLUBS,
                                     Card.FIVE_OF_SPADES)),
                sf.analysis());

        Hole hole = Hole.valueOf(Card.ACE_OF_CLUBS, Card.FIVE_OF_SPADES);
        Community flop = new Community(Card.ACE_OF_HEARTS,
                Card.FIVE_OF_HEARTS, Card.THREE_OF_DIAMONDS);
        hole.asCanon().addFlop(flop)
                .addTurn(Card.NINE_OF_CLUBS)
                .addRiver(Card.TEN_OF_DIAMONDS).canonIndex();

        if (computeOdds)
        {
            new PreciseHeadsUpOdds().compute(hole, Community.PREFLOP);
            new PreciseHeadsUpOdds().compute(hole, flop);
        }
        
        System.out.println("Done Loading!  Took " + time.timing());
    }


    //--------------------------------------------------------------------
    public static void tournament(
            final HoldemAbstraction abs) throws IOException
    {
        LOG.debug("running tournament");

        final CfrBot2 bot = new CfrBot2(
                BOT_NAME, abs, true, false, false);
        precompute(bot, false);
//        final CfrBot2 vsBot = new CfrBot2(
//                VS_NAME, abs, true, false, false);
//        precompute(vsBot, false);

        long before = System.currentTimeMillis();
        new DealerTest(1000 * 1000).headsUp(
                new LinkedHashMap<Avatar, Player>(){{
            // dealer last

//            put(Avatar.local("rc"), new RaiseCallBot());

//            put(Avatar.local("ao-2000"), vsBot);
            put(Avatar.local("raise"), new AlwaysRaiseBot());
//            put(Avatar.local("duane"), new DuaneBot());

//            put(Avatar.local("random"), new RandomBot());
//            put(Avatar.local("math"), new MathBot());
//            put(Avatar.local("human"), new ConsoleBot());
            put(Avatar.local("ao-6000"), bot);
//            put(Avatar.local("cfr2 290"),
//                    new CfrBot2("serial_290", abs, false, false, false));
        }}, true);
        LOG.debug("tournament took " +
                  ((System.currentTimeMillis() - before) / 1000));
    }

    public static void vsHuman(
            final HoldemAbstraction abs) throws IOException
    {
//        CfrBot2 bot = new CfrBot2(BOT_NAME, abs, false, true, false);
        CfrBot2 bot = new CfrBot2(BOT_NAME, abs, true, true, false);
        precompute(bot, true);
        HoleOdds.lookup(0);

        Rand.randomize();
        new DealerTest().vsHuman(bot, false, true);
    }


    //--------------------------------------------------------------------
    public static void computeCfr(
            HoldemAbstraction abs) throws IOException
    {
        System.out.println("computeCfr");

        Stopwatch         t      = new Stopwatch();
        InfoPart          info   = abs.infoPart(BOT_NAME, false, true);
        ParallelMinimizer cfrMin = new ParallelMinimizer(
                ChainMinimizer.newMulti(info, abs.odds(), AGGRESSION));
//        InfoPart        info   = abs.infoPart("mono", false);
//        MonoRegretMin cfrMin =
//                new MonoRegretMin(info, abs.odds() /* abs.oddsCache()*/);

        long itr        = 0;
        long offset     = 0; //(125 + 560) * 1000 * 1000;
//        long offset     =  550 * 1000 * 1000;
        long iterations = 1000 * 1000 * 1000;//1000 * 1000 * 1000;
//        long iterations = 1000 * 1000 * 1000;//1000 * 1000 * 1000;
        int  milestone  =  250 * 1000 * 1000;

        long before     = System.currentTimeMillis();
        Iterator<char[][]> it = abs.sequence().iterator(iterations);

        if (offset > 0) {
            System.out.println("Offsetting " + offset + "...");
            for (; itr < offset; itr++) {
                it.next();
            }
        }

        Progress  prog = new Progress(iterations - offset);
        while (it.hasNext())
        {
            if (itr % (5 * 1000 * 1000) == 0) {
                System.out.println("\t" + itr + " took " + t.timing());
                t = new Stopwatch();

                info.displayHeadsUpRoots();
            }

            if (itr++ % (milestone) == 0) {
                System.out.println(
                        " " + milestone +
                        " totalling " + (itr - 1) +
                        " took " +
                                (System.currentTimeMillis()
                                        - before) / 1000);

                if (itr != (offset + 1)) {
                    cfrMin.flush();
                    info.flush();
                }
                before = System.currentTimeMillis();
            }
            char[][] jbs = it.next();

            cfrMin.iterate( jbs[0], jbs[1] );

            prog.checkpoint();
        }

        System.out.println(" " + (itr - 1));
        info.displayHeadsUpRoots();
        info.flush();
    }
}

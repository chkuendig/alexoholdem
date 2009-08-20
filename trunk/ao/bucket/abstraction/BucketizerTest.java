package ao.bucket.abstraction;

import ao.ai.equilibrium.limit_cfr.CfrBot2;
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
import ao.regret.holdem.IterativeMinimizer;
import ao.regret.holdem.mono.ChainMinimizer;
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
    private static final String  BOT_NAME   = "serial";
    private static final String   VS_NAME   = "serial";
    private static final double  AGGRESSION = 1.0;
    private static final boolean    PRECISE = false;
    private static final boolean VS_PRECISE = true;


    //--------------------------------------------------------------------
    public static void main(String[] args) throws IOException
    {
//        int  nHoleBuckets  = 3;
//        char nFlopBuckets  = 9;
//        char nTurnBuckets  = 27;
//        char nRiverBuckets = 81;
//        int  nHoleBuckets  = 5;
//        char nFlopBuckets  = 25;
//        char nTurnBuckets  = 125;
//        char nRiverBuckets = 625;
//        byte nHoleBuckets  = 20;
//        int  nHoleBuckets  =    32;
//        char nFlopBuckets  =   658;
//        char nTurnBuckets  =  4608;
//        char nRiverBuckets = 32256; // 31360
//        int  nHoleBuckets  =   169;
//        char nFlopBuckets  =  1300;
//        char nTurnBuckets  =  6500;
//        char nRiverBuckets = 32256;
        int  nHoleBuckets  =   169;
        char nFlopBuckets  =  2028;
        char nTurnBuckets  = 10140;
        char nRiverBuckets = 49000;
//        int  nHoleBuckets  =    64;
//        char nFlopBuckets  =   896;
//        char nTurnBuckets  =  5376;
//        char nRiverBuckets = 32256;

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

        HoldemAbstraction abs = abstractHolem(
//                new KMeansBucketizer(),
                new HistBucketizer((byte) 3),
                nHoleBuckets, nFlopBuckets, nTurnBuckets, nRiverBuckets);
//        HoldemAbstraction vsAbs = abstractHolem(
//                new HistBucketizer((byte) 3),
//                127, (char) 1200, (char) 6000, (char) 30000);
//        HoldemAbstraction vsAbs = abstractHolem(
//                new HistBucketizer((byte) 3),
//                64, (char) 896, (char) 5376, (char) 32256);
//        HoldemAbstraction vsAbs = null;

        // preload
//        abs.infoPart(false, false);
//        System.out.println("passed!");
//        BucketDisplay.displayHoleBuckets(
//                abs.tree(false).holes());
//        BucketDisplay.displayCanonHoleBuckets(
//                abs.tree(false).holes());
//        abs.odds();
//        abs.sequence();

//        Rand.randomize();
        computeCfr(abs);

//        tournament(abs, vsAbs);

//        vsHuman(abs);
    }


    //--------------------------------------------------------------------
    private static HoldemAbstraction abstractHolem(
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
        
        LOG.debug("Done Loading!  Took " + time.timing());
    }


    //--------------------------------------------------------------------
    public static void tournament(
            final HoldemAbstraction abs,
            final HoldemAbstraction vsAbs) throws IOException
    {
        LOG.debug("prepairing tournament");

        final CfrBot2 bot = new CfrBot2(
                BOT_NAME, abs, PRECISE, false, false);
        precompute(bot, false);
        final CfrBot2 vsBot = new CfrBot2(
                VS_NAME, vsAbs, VS_PRECISE, false, false);
        precompute(vsBot, false);

        LOG.debug("running tournament");
        long before = System.currentTimeMillis();
        new DealerTest(1000 * 1000).headsUp(
                new LinkedHashMap<Avatar, Player>(){{
            // dealer last

//            put(Avatar.local("rc"), new RaiseCallBot());

//            put(Avatar.local("ao-2000"), vsBot);
//            put(Avatar.local("raise"), new AlwaysRaiseBot());
//            put(Avatar.local("duane"), new DuaneBot());

//            put(Avatar.local("random"), new RandomBot());
//            put(Avatar.local("math"), new MathBot());
//            put(Avatar.local("human"), new ConsoleBot());
//            put(Avatar.local("ao-hist4"), bot);
                    
            put(Avatar.local("ao-hist3-32"), bot);
            put(Avatar.local("ao-hist3-64"), vsBot);
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
        CfrBot2 bot = new CfrBot2(BOT_NAME, abs, PRECISE, true, false);
        precompute(bot, true);
        HoleOdds.lookup(0);

        Rand.randomize();
        new DealerTest().vsHuman(bot, false, true);
    }


    //--------------------------------------------------------------------
    public static void computeCfr(
            HoldemAbstraction abs) throws IOException
    {
        LOG.debug("computeCfr");

        Stopwatch         t      = new Stopwatch();
        InfoPart          info   = abs.infoPart(BOT_NAME, false, PRECISE);

//        IterativeMinimizer cfrMin = new ParallelMinimizer(
//                ChainMinimizer.newMulti(info, abs.odds(), AGGRESSION),
//                abs.tree(false).holes());
        IterativeMinimizer cfrMin =
                ChainMinimizer.newMulti(info, abs.odds(), AGGRESSION);

//        InfoPart        info   = abs.infoPart("mono", false);
//        MonoRegretMin cfrMin =
//                new MonoRegretMin(info, abs.odds() /* abs.oddsCache()*/);

        long itr        = 0;
        long offset     = 0; //(125 + 560) * 1000 * 1000;
//        long offset     = 200 * 1000 * 1000;
        long iterations = 250 * 1000 * 1000;//1000 * 1000 * 1000;
        int  milestone  =  50 * 1000 * 1000;

        long before     = System.currentTimeMillis();
        Iterator<char[][]> it = abs.sequence().iterator(iterations);

        if (offset > 0) {
            LOG.debug("Offsetting " + offset + "...");
            for (; itr < offset; itr++) {
                it.next();
            }
        }

        Progress  prog = new Progress(iterations - offset);
        while (it.hasNext())
        {
            if (itr % (1000 * 1000/*12*/) == 0) {
                LOG.debug("\t" + itr + " took " + t.timing());
                t = new Stopwatch();

//                cfrMin.flush();
                info.displayHeadsUpRoots();

//                if (itr > 0) return;
            }

            if (itr++ % (milestone) == 0) {
                LOG.debug(
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

        LOG.debug(" " + (itr - 1));
        info.displayHeadsUpRoots();
        info.flush();
    }
}

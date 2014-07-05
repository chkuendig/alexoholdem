package ao.holdem.bot.main;

import ao.holdem.abs.bucket.abstraction.bucketize.smart.KMeansBucketizer;
import ao.holdem.bot.regret.HoldemAbstraction;
import ao.holdem.bot.limit_cfr.CfrBot2;
import ao.holdem.abs.bucket.abstraction.access.BucketSequencer;
import ao.holdem.abs.bucket.abstraction.bucketize.def.Bucketizer;
import ao.holdem.abs.bucket.abstraction.bucketize.smart.BucketDisplay;
import ao.holdem.abs.bucket.index.detail.preflop.HoleOdds;
import ao.holdem.canon.flop.Flop;
import ao.holdem.model.card.canon.hole.CanonHole;
import ao.holdem.engine.Player;
import ao.holdem.engine.state.ActionStateFlow;
import ao.holdem.model.Avatar;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;
import ao.holdem.abs.odds.agglom.impl.PreciseHeadsUpOdds;
import ao.holdem.bot.regret.InfoPart;
import ao.holdem.bot.regret.IterativeMinimizer;
import ao.holdem.bot.regret.mono.ChainMinimizer;
import ao.util.math.rand.Rand;
import ao.util.time.Progress;
import ao.util.time.Stopwatch;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Date: 21-Apr-2009
 * Time: 11:02:42 PM
 */
public class CfrOptimizer
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(CfrOptimizer.class);

//    private static final String BOT_NAME = "agro";
    private static final String  BOT_NAME   = "serial";
    private static final String   VS_NAME   = "serial";
    private static final double  AGGRESSION = 1.00;
    private static final boolean    PRECISE = false;
    private static final boolean VS_PRECISE = true;


    //--------------------------------------------------------------------
    public static void main(String[] args) throws IOException
    {
//        int  nHoleBuckets  =    64;
//        char nFlopBuckets  =  1600;
//        char nTurnBuckets  = 10239;
//        char nRiverBuckets = 65535;
//        int  nHoleBuckets  =    48;
//        char nFlopBuckets  =  1024;
//        char nTurnBuckets  =  8192;
//        char nRiverBuckets = 65535;
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

        // preCompute();

        Rand.randomize();

        for (int i = 0; i < 1000; i++)
        {
            HoldemAbstraction abs = computeSamples(
                    nHoleBuckets, nFlopBuckets,
                    nTurnBuckets, nRiverBuckets,
                    true);

            abs.tree(false);

            computeCfr( abs );
        }
    }

    private static void preCompute()
    {
//            Eval5.main(null);       // 1
//            Eval7Faster.main(null); // 2
//            HoleDetails.load();     // 3
//            FlopDetails.lookup(0);
//            TurnRivers.main(null);
//            RiverEvalLookup.main(null);
//            CompactRiverCounts.main(null);//
//            MemProbCounts.main(null);
//            ProbabilityEncoding.main(null);
//            CompactRiverProbabilities.main(null);
//            HoleOdds.main(null);
//            TurnDetails.main(null);
//            FlopLookup.main(null);
//            TurnLookup.main(null);
//            TurnOdds.main(null);
    }



    //------------------------------------------------------------------------
    public static HoldemAbstraction computeSamples(
            int     nHoleBuckets,
            char    nFlopBuckets,
            char    nTurnBuckets,
            char    nRiverBuckets,
            boolean deleteExisting)
    {
        HoldemAbstraction abs = abstractHoldem(
                nHoleBuckets, nFlopBuckets, nTurnBuckets, nRiverBuckets);

//        if (abs.hasInfoPart(BOT_NAME, PRECISE)) {
//
//        }


        if (deleteExisting)
        {
            abs.deleteSequence();

            abs.infoPart(BOT_NAME, false, PRECISE);
            LOG.info("infoPart loaded!");

            BucketDisplay.displayHoleBuckets(
                    abs.tree(false).holes());
            BucketDisplay.displayCanonHoleBuckets(
                    abs.tree(false).holes());

            abs.odds();
            abs.sequence();
        }

        return abs;
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

    public static void preComputeAbs(
            final CfrBot2 bot, boolean computeOdds)
    {
        Stopwatch time = new Stopwatch();
//        System.out.println("Loading......");
//        new DealerTest(10).headsUp(new HashMap<Avatar, Player>(){{
//            put(Avatar.local("probe"), new AlwaysCallBot());
//            put(Avatar.local("cfr2"), bot);
//        }});

        ActionStateFlow sf = new ActionStateFlow(2, true);
        bot.act(sf.head(),
                new CardSequence(
                        Hole.valueOf(Card.ACE_OF_CLUBS,
                                     Card.FIVE_OF_SPADES))/*,
                sf.analysis()*/);

        Hole hole = Hole.valueOf(Card.ACE_OF_CLUBS, Card.FIVE_OF_SPADES);
        Community flop = new Community(Card.ACE_OF_HEARTS,
                Card.FIVE_OF_HEARTS, Card.THREE_OF_DIAMONDS);
        new Flop(CanonHole.create(hole), flop)
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
        LOG.debug("preparing tournament");

        final CfrBot2 bot = new CfrBot2(
                BOT_NAME, abs, PRECISE, false, false);
        preComputeAbs(bot, false);
        final CfrBot2 vsBot = new CfrBot2(
                VS_NAME, vsAbs, VS_PRECISE, false, false);
        preComputeAbs(vsBot, false);

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
                    
            put(Avatar.local("ao-hist3-64b"), bot);
            put(Avatar.local("ao-hist3-64s"), vsBot);
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
        preComputeAbs(bot, true);
        HoleOdds.lookup(0);

        Rand.randomize();
        new DealerTest().vsHuman(bot, false, true);
    }


    //--------------------------------------------------------------------
    public static void computeCfr(
            int        nHoleBuckets,
            char       nFlopBuckets,
            char       nTurnBuckets,
            char       nRiverBuckets)
    {
        computeCfr(abstractHoldem(
                nHoleBuckets,
                nFlopBuckets,
                nTurnBuckets,
                nRiverBuckets));
    }

    public static void computeCfr(
            HoldemAbstraction abs)
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
        long iterations = BucketSequencer.COUNT;
//        long milestone  = iterations * 2; // i.e. never
        long milestone  = iterations / 4;

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
            if (itr % (50 * 1000 * 1000/*12*/) == 0) {
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

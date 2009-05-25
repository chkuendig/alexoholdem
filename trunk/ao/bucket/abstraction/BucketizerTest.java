package ao.bucket.abstraction;

import ao.ai.equilibrium.limit_cfr.CfrBot2;
import ao.ai.simple.AlwaysCallBot;
import ao.bucket.abstraction.bucketize.Bucketizer;
import ao.bucket.abstraction.bucketize.smart.KMeansBucketizer;
import ao.bucket.index.detail.preflop.HoleOdds;
import ao.holdem.engine.Player;
import ao.holdem.engine.dealer.DealerTest;
import ao.holdem.model.Avatar;
import ao.regret.holdem.InfoPart;
import ao.regret.holdem.RegretMinimizer;
import ao.util.math.rand.Rand;
import ao.util.time.Progress;
import ao.util.time.Stopwatch;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
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


    //--------------------------------------------------------------------
    public static void main(String[] args) throws IOException
    {
//        byte nHoleBuckets  = 5;
//        char nFlopBuckets  = 25;
//        char nTurnBuckets  = 125;
//        char nRiverBuckets = 625;
//        byte nHoleBuckets  = 10;
//        char nFlopBuckets  = 200;
//        char nTurnBuckets  = 1000;
//        char nRiverBuckets = 5000;
//        byte nHoleBuckets  = 20;
        byte nHoleBuckets  = 11;
        char nFlopBuckets  = 275;
        char nTurnBuckets  = 1650;
        char nRiverBuckets = 9900;

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

        HoldemAbstraction abs = abstractHolem(new KMeansBucketizer(),
                nHoleBuckets, nFlopBuckets, nTurnBuckets, nRiverBuckets);

        // preload
//        abs.tree();
//        abs.odds();
//        abs.sequence();

//        Rand.randomize();
        computeCfr(abs);
//        tournament(abs);
//        vsHuman(abs, null);
    }


    //--------------------------------------------------------------------
    private static HoldemAbstraction abstractHolem(
            Bucketizer bucketizer,
            byte nHoleBuckets,
            char nFlopBuckets,
            char nTurnBuckets,
            char nRiverBuckets) throws IOException
    {
        return new HoldemAbstraction(
                        bucketizer,
                        nHoleBuckets,
                        nFlopBuckets,
                        nTurnBuckets,
                        nRiverBuckets);
    }

    private static void precompute(final CfrBot2 bot)
    {
        long before = System.currentTimeMillis();
        System.out.println("Loading......");
        new DealerTest(10).headsUp(new HashMap<Avatar, Player>(){{
            put(Avatar.local("probe"), new AlwaysCallBot());
            put(Avatar.local("cfr2"), bot);
        }});

        System.out.println("Done Loading!  Took " +
                (System.currentTimeMillis() - before) / 1000);
    }


    //--------------------------------------------------------------------
    public static void tournament(
            final HoldemAbstraction abs) throws IOException
    {
        LOG.debug("running tournament");

        long before = System.currentTimeMillis();
        new DealerTest().headsUp(new LinkedHashMap<Avatar, Player>(){{
            // dealer last

//            put(Avatar.local("rc"), new RaiseCallBot());

//            put(Avatar.local("cfr2"), new CfrBot2(abs));
//            put(Avatar.local("raise"), new AlwaysRaiseBot());
//            put(Avatar.local("duane"), new DuaneBot());

//            put(Avatar.local("random"), new RandomBot());
//            put(Avatar.local("math"), new MathBot());
//            put(Avatar.local("human"), new ConsoleBot());
            put(Avatar.local("cfr2 full"),
                    new CfrBot2("serial", abs, false, false, false));
            put(Avatar.local("cfr2 290"),
                    new CfrBot2("serial_290", abs, false, false, false));
        }}, true);
        LOG.debug("tournament took " +
                  ((System.currentTimeMillis() - before) / 1000));
    }

    public static void vsHuman(
            final HoldemAbstraction abs,
            final String            name) throws IOException
    {
        CfrBot2 bot = new CfrBot2(name, abs, false, true, false);
        precompute(bot);
        HoleOdds.lookup(0);

        Rand.randomize();
        new DealerTest().vsHuman(bot, false, true);
    }


    //--------------------------------------------------------------------
    public static void computeCfr(
            HoldemAbstraction abs) throws IOException
    {
        System.out.println("computeCfr");

        Stopwatch       t      = new Stopwatch();
        InfoPart        info   = abs.infoPart("serial", false, false);
        RegretMinimizer cfrMin =
                new RegretMinimizer(info, abs.odds() /* abs.oddsCache()*/);
//        InfoPart        info   = abs.infoPart("mono", false);
//        MonoRegretMin cfrMin =
//                new MonoRegretMin(info, abs.odds() /* abs.oddsCache()*/);

        long itr        = 0;
//        long offset     = 0; //(125 + 560) * 1000 * 1000;
        long offset     =        290 * 1000 * 1000;
//        long iterations = 1000 * 1000 * 1000;//1000 * 1000 * 1000;
        long iterations = 1000 * 1000 * 1000;//1000 * 1000 * 1000;
        int  milestone  =   50 * 1000 * 1000;

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
                    info.flush();
                }
                before = System.currentTimeMillis();
            }
            char[][] jbs = it.next();

            cfrMin.minimize( jbs[0], jbs[1] );

            prog.checkpoint();
        }

        System.out.println(" " + (itr - 1));
        info.displayHeadsUpRoots();
        info.flush();
    }
}

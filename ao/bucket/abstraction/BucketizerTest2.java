package ao.bucket.abstraction;

import ao.ai.equilibrium.limit_cfr.CfrBot2;
import ao.ai.simple.AlwaysCallBot;
import ao.ai.simple.DuaneBot;
import ao.bucket.abstraction.bucketize.Bucketizer;
import ao.bucket.abstraction.bucketize.BucketizerImpl;
import ao.holdem.engine.Player;
import ao.holdem.engine.dealer.DealerTest;
import ao.holdem.model.Avatar;
import ao.regret.holdem.v2.InfoPart;
import ao.regret.holdem.v2.RegretMin2;
import ao.util.math.rand.Rand;
import ao.util.time.Progress;
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
public class BucketizerTest2
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(BucketizerTest2.class);


    //--------------------------------------------------------------------
    public static void main(String[] args) throws IOException
    {
        byte nHoleBuckets  = 6;
        char nFlopBuckets  = 144;
        char nTurnBuckets  = 432;
        char nRiverBuckets = 1296;
//        byte nHoleBuckets  = 13;
//        char nFlopBuckets  = 567;
//        char nTurnBuckets  = 1854;
//        char nRiverBuckets = 5786;
//        byte nHoleBuckets  = 10;
//        char nFlopBuckets  = 360;
//        char nTurnBuckets  = 1440;
//        char nRiverBuckets = 5760;

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

        HoldemAbstraction abs = abstractHolem(new BucketizerImpl(),
                nHoleBuckets, nFlopBuckets, nTurnBuckets, nRiverBuckets);

//        Rand.randomize();
//        computeCfr(abs, false);
//        computeCfr(abs, true);
        tournament(abs);
//        vsHuman(abs);
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

    private static void precompute(final HoldemAbstraction abs)
    {
        long before = System.currentTimeMillis();
        System.out.println("Loading......");
        new DealerTest(10).headsUp(new HashMap<Avatar, Player>(){{
            put(Avatar.local("probe"), new AlwaysCallBot());
            put(Avatar.local("cfr2"), new CfrBot2(abs));
        }});

        System.out.println("Done Loading!  Took " +
                (System.currentTimeMillis() - before) / 1000);
    }


    //--------------------------------------------------------------------
    public static void tournament(
            final HoldemAbstraction abs) throws IOException
    {
        precompute(abs);
        abs.infoPart().displayHeadsUpRoot();

        long before = System.currentTimeMillis();
        new DealerTest().headsUp(new LinkedHashMap<Avatar, Player>(){{
            // dealer last

//            put(Avatar.local("cfr2"), new CfrBot2(abs));
//            put(Avatar.local("raise"), new AlwaysRaiseBot());
            put(Avatar.local("duane"), new DuaneBot());

//            put(Avatar.local("random"), new RandomBot());
//            put(Avatar.local("math"), new MathBot());
//            put(Avatar.local("human"), new ConsoleBot());
            put(Avatar.local("cfr2"), new CfrBot2(abs));
//            put(Avatar.local("cfr2b"), new CfrBot2(abs));
        }}, true);
        LOG.debug("tournament took " +
                  ((System.currentTimeMillis() - before) / 1000));
    }

    public static void vsHuman(
            final HoldemAbstraction abs) throws IOException
    {
        precompute(abs);

        Rand.randomize();
        new DealerTest().vsHuman(new CfrBot2(abs, true),
                                 false, false);
    }


    //--------------------------------------------------------------------
    public static void computeCfr(
            HoldemAbstraction abs,
            boolean           exploit) throws IOException
    {
        System.out.println("computeCfr " + (exploit ? "exploit" : ""));

        InfoPart   info   = abs.infoPart();
        RegretMin2 cfrMin = new RegretMin2(info, abs.oddsCache());

        long itr        = 0;
        long offset     =        1000 * 1000; //(125 + 560) * 1000 * 1000;
        long iterations = 1000 * 1000 * 1000;//1000 * 1000 * 1000;

        long before     = System.currentTimeMillis();
        Iterator<char[][]> it = abs.sequence().iterator(iterations);

        if (offset > 0) {
            System.out.println("Offsetting...");
            for (; itr < offset; itr++) {
                it.next();
            }
        }

        Progress prog = new Progress(iterations - offset);
        while (it.hasNext())
        {
            if (itr % (100 * 1000) == 0) {
                System.out.println();
                info.displayHeadsUpRoot();
            }

            if (itr++ % (500 * 1000) == 0) {
                System.out.println(" " + (itr - 1) + " took " +
                        (System.currentTimeMillis() - before) / 1000);

                if (itr != (offset + 1)) {
                    info.flush();
                }
                before = System.currentTimeMillis();
            }
            char[][] jbs = it.next();

//            if (exploit) {
////                if (jbs[0][0] == 5 &&
////                        jbs[0][1] == 139/*19*/) {
////                    System.out.println("check");
////                }
//
//                cfrMin.exploit ( jbs[0], jbs[1] );
//            } else {
                cfrMin.minimize( jbs[0], jbs[1] );
//            }

            prog.checkpoint();
        }

        System.out.println(" " + (itr - 1));
        info.displayHeadsUpRoot();
        info.flush();
    }
}

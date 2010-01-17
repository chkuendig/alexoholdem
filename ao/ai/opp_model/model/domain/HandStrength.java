package ao.ai.opp_model.model.domain;

/**
 *
 */
public enum HandStrength
{
//    /* 11 percentiles from Sagerbot:
//-1
//-0.415469149
//-0.083190177
//0.214164961
//0.398712658
//0.520684369
//0.622246582
//0.717937349
//0.805052363
//0.884113207
//0.94842652
//1
//
////    DELTA_00 (-1.00, -0.42),
////    DELTA_10 (-0.42, -0.08),
////    DELTA_20 (-0.08,  0.21),
////    DELTA_30 ( 0.21,  0.40),
////    DELTA_40 ( 0.40,  0.52),
////    DELTA_50 ( 0.52,  0.62),
////    DELTA_60 ( 0.62,  0.72),
////    DELTA_70 ( 0.72,  0.81),
////    DELTA_80 ( 0.81,  0.88),
////    DELTA_90 ( 0.88,  0.94),
////    DELTA_100( 0.94,  1.00);
//     */
//    //--------------------------------------------------------------------
////    /*
////-0.51306198
////-0.143317145
////-0.023855843
////0.110058279
////0.208388375
////0.270315603
////0.330869874
////0.372546408
////0.426613629
////0.467671292
////0.515314061
////0.769022263
////     */
//    DELTA_00 (-0.493, -0.244),
//    DELTA_10 (-0.244, -0.140),
//    DELTA_20 (-0.140, -0.031),
//    DELTA_30 (-0.031,  0.078),
//    DELTA_40 ( 0.078,  0.167),
//    DELTA_50 ( 0.167,  0.252),
//    DELTA_60 ( 0.252,  0.325),
//    DELTA_70 ( 0.325,  0.390),
//    DELTA_80 ( 0.390,  0.465),
//    DELTA_90 ( 0.465,  0.744);
////    DELTA_00 (-0.731, -0.430),
////    DELTA_10 (-0.430, -0.161),
////    DELTA_20 (-0.161,  0.068),
////    DELTA_30 ( 0.068,  0.253),
////    DELTA_40 ( 0.253,  0.414),
////    DELTA_50 ( 0.414,  0.555),
////    DELTA_60 ( 0.555,  0.677),
////    DELTA_70 ( 0.677,  0.787),
////    DELTA_80 ( 0.787,  0.916),
////    DELTA_90 ( 0.916,  1.000);
//
//    //--------------------------------------------------------------------
////    /*
////-0.51306198
////0.007092969
////0.237599137
////0.355728492
////0.459206907
////0.774503854
////     */
////    DELTA_0(-0.493, -0.219),
////    DELTA_1(-0.044,  0.051),
////    DELTA_2( 0.104,  0.269),
////    DELTA_3( 0.229,  0.446),
////    DELTA_4( 0.337,  0.744);
//
////    DELTA_0(-0.493, 0.167),
////    DELTA_1( 0.167, 0.744);
//
//    //--------------------------------------------------------------------
//    private static BlindOddFinder expectedOdds =
//                        new ApproxBlindOddFinder();
//
//
//    //--------------------------------------------------------------------
//    public static HandStrength fromPercent(double plusMinusOne)
//    {
//        assert -1 <= plusMinusOne && plusMinusOne <= 1
//                : plusMinusOne + " not in [-1, 1]";
//
//        for (HandStrength strength : values())
//        {
//            if (strength.intersects( plusMinusOne ))
//            {
//                return strength;
//            }
//        }
//        return plusMinusOne >= 0
//                ? values()[ values().length - 1 ]
//                : values()[ 0                   ];
//    }
//
//    public static HandStrength fromState(
//            HandState showdown,
//            Community community,
//            Hole      hole)
//    {
//        OddFinder oddFinder = new ApproximateOddFinder();
//        Odds actual    =
//                    oddFinder.compute(
//                            hole, community,
//                            showdown.numActivePlayers()-1);
//        // actual hand strength
//        double act = actual.strength();
//
//        BlindOddFinder.BlindOdds expected =
//                expectedOdds.compute(
//                        community, showdown.numActivePlayers());
//
//        // random expected average hand strength
//        double avg = expected.sum().strength();
////        double min = Math.min(act,
////                              expected.min().strength(
////                                      showdown.numActivePlayers()));
////        double max = Math.max(act,
////                              expected.max().strength(
////                                      showdown.numActivePlayers()));
//
//        // by how much the actual hand is stronger than
//        //  an average random hand. -ve # means its
//        //  weaker than the average random hand.
//        double delta = act - avg;
//
////        double deltaPercent =
////                (delta < 0)
////                 ? delta / (avg - min)
////                 : delta / (max - avg);
////        if (Double.isNaN(deltaPercent))
////        {
////            deltaPercent = 0;
////        }
////        System.out.println(avg   + "\t" +
////                           act   + "\t" +
////                           delta + "\t" +
////                           min   + "\t" +
////                           max   + "\t" +
////                           deltaPercent);
//
//        return HandStrength.fromPercent( delta );
////        return HandStrength.fromPercent( deltaPercent );
//    }
//
//
//
//    //--------------------------------------------------------------------
//    private final double FROM_AND_INCLUDING;
//    private final double UP_TO_AND_INCLUDING;
//
//
//    //--------------------------------------------------------------------
//    private HandStrength(
//            double fromAndIncluding,
//            double toInclusive)
//    {
//        FROM_AND_INCLUDING  = fromAndIncluding;
//        UP_TO_AND_INCLUDING = toInclusive;
//    }
//
//    private boolean intersects(double value)
//    {
//        return FROM_AND_INCLUDING <= value &&
//                                     value <= UP_TO_AND_INCLUDING;
//    }
//
//
//    //--------------------------------------------------------------------
//    public double averageValue()
//    {
//        return (FROM_AND_INCLUDING + UP_TO_AND_INCLUDING) / 2;
//    }
}

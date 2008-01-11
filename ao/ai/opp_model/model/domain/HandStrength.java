package ao.ai.opp_model.model.domain;

/**
 *
 */
public enum HandStrength
{
    /* 11 percentiles from Sagerbot:
-1
-0.415469149
-0.083190177
0.214164961
0.398712658
0.520684369
0.622246582
0.717937349
0.805052363
0.884113207
0.94842652
1

//    DELTA_00 (-1.00, -0.42),
//    DELTA_10 (-0.42, -0.08),
//    DELTA_20 (-0.08,  0.21),
//    DELTA_30 ( 0.21,  0.40),
//    DELTA_40 ( 0.40,  0.52),
//    DELTA_50 ( 0.52,  0.62),
//    DELTA_60 ( 0.62,  0.72),
//    DELTA_70 ( 0.72,  0.81),
//    DELTA_80 ( 0.81,  0.88),
//    DELTA_90 ( 0.88,  0.94),
//    DELTA_100( 0.94,  1.00);
     */
    //--------------------------------------------------------------------
//    /*
//-0.51306198
//-0.143317145
//-0.023855843
//0.110058279
//0.208388375
//0.270315603
//0.330869874
//0.372546408
//0.426613629
//0.467671292
//0.515314061
//0.769022263
//     */
//    DELTA_00 (-1.0,  -0.14),
//    DELTA_10 (-0.14,  0.02),
//    DELTA_20 (-0.02,  0.11),
//    DELTA_30 ( 0.11,  0.21),
//    DELTA_40 ( 0.21,  0.27),
//    DELTA_50 ( 0.27,  0.33),
//    DELTA_60 ( 0.33,  0.37),
//    DELTA_70 ( 0.37,  0.42),
//    DELTA_80 ( 0.42,  0.46),
//    DELTA_90 ( 0.46,  0.51),
//    DELTA_100( 0.51,  1.00);
    
    //--------------------------------------------------------------------
    /*
-0.51306198
0.007092969
0.237599137
0.355728492
0.459206907
0.774503854
     */
    
    DELTA_0(-0.513,  0.007),
    DELTA_1( 0.007,  0.238),
    DELTA_2( 0.238,  0.356),
    DELTA_3( 0.356,  0.459),
    DELTA_4( 0.459,  0.775);


    //--------------------------------------------------------------------
    public static HandStrength fromPercent(double plusMinusOne)
    {
        assert -1 <= plusMinusOne && plusMinusOne <= 1;

        for (HandStrength strength : values())
        {
            if (strength.intersects( plusMinusOne ))
            {
                return strength;
            }
        }
        return plusMinusOne >= 0
                ? values()[ values().length - 1 ]
                : values()[ 0                   ];
    }


    //--------------------------------------------------------------------
    private final double FROM_AND_INCLUDING;
    private final double UP_TO_AND_INCLUDING;


    //--------------------------------------------------------------------
    private HandStrength(
            double fromAndIncluding,
            double upToAndIncluding)
    {
        FROM_AND_INCLUDING  = fromAndIncluding;
        UP_TO_AND_INCLUDING = upToAndIncluding;
    }

    private boolean intersects(double value)
    {
        return FROM_AND_INCLUDING <= value &&
                                     value <= UP_TO_AND_INCLUDING;
    }


    //--------------------------------------------------------------------
    public double averageValue()
    {
        return (FROM_AND_INCLUDING + UP_TO_AND_INCLUDING) / 2;
    }
}

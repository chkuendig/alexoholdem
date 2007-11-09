package ao.ai.opp_model.model.domain;

/**
 * 
 */
public enum HandStrength
{
    //--------------------------------------------------------------------
    PERCENTILE_10(0.1), PERCENTILE_15(0.15),
    PERCENTILE_20(0.2), PERCENTILE_25(0.25),
    PERCENTILE_30(0.3), PERCENTILE_35(0.35),
    PERCENTILE_40(0.4), PERCENTILE_45(0.45),
    PERCENTILE_50(0.5), PERCENTILE_55(0.55),
    PERCENTILE_60(0.6), PERCENTILE_65(0.65),
    PERCENTILE_70(0.7), PERCENTILE_75(0.75),
    PERCENTILE_80(0.8), PERCENTILE_85(0.85),
    PERCENTILE_90(0.9), PERCENTILE_95(0.95),
    PERCENTILE_100(1.0);


    //--------------------------------------------------------------------
    public static HandStrength fromPercent(double percent)
    {
        for (HandStrength strength : values())
        {
            if (percent <= strength.UP_TO_AND_INCLUDING)
            {
                return strength;
            }
        }
        throw new Error("undefined percentage: " + percent);
    }


    //--------------------------------------------------------------------
    private final double UP_TO_AND_INCLUDING;


    //--------------------------------------------------------------------
    private HandStrength(double upToAndIncluding)
    {
        UP_TO_AND_INCLUDING = upToAndIncluding;
    }
}

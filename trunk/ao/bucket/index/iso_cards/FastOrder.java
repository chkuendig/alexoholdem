package ao.bucket.index.iso_cards;

/**
 * Date: Sep 7, 2008
 * Time: 1:51:10 PM
 *
 * Hole:
    [[c], [d, h, s]]
    [[c, d], [h, s]]
    [[d], [c], [h, s]]
    [[c, h], [d, s]]
    [[h], [c], [d, s]]
    [[c, s], [d, h]]
    [[s], [c], [d, h]]
    [[c], [d], [h, s]]
    [[c], [h], [d, s]]
    [[c], [s], [d, h]]
    [[d], [c, h, s]]
    [[d, h], [c, s]]
    [[h], [d], [c, s]]
    [[d, s], [c, h]]
    [[s], [d], [c, h]]
    [[d], [h], [c, s]]
    [[d], [s], [c, h]]
    [[h], [c, d, s]]
    [[h, s], [c, d]]
    [[s], [h], [c, d]]
    [[h], [s], [c, d]]
    [[s], [c, d, h]]
 *
 *
 */
public enum FastOrder
{
    //--------------------------------------------------------------------
    C_D_H_S(0, 1, 2, 3) { public FastOrder refine(FastOrder with) {
        return this; }},
    C_D_S_H(0, 1, 3, 2) { public FastOrder refine(FastOrder with) {
        return this; }},
    C_H_S_D(0, 3, 1, 2) { public FastOrder refine(FastOrder with) {
        return this; }},
    C_H_D_S(0, 2, 1, 3) { public FastOrder refine(FastOrder with) {
        return this; }},
    C_S_D_H(0, 2, 3, 1) { public FastOrder refine(FastOrder with) {
        return this; }},
    C_S_H_D(0, 3, 2, 1) { public FastOrder refine(FastOrder with) {
        return this; }},
    D_C_S_H(1, 0, 3, 2) { public FastOrder refine(FastOrder with) {
        return this; }},
    D_C_H_S(1, 0, 2, 3) { public FastOrder refine(FastOrder with) {
        return this;}},
    D_H_S_C(3, 0, 1, 2) { public FastOrder refine(FastOrder with) {
        return this; }},
    D_H_C_S(2, 0, 1, 3) { public FastOrder refine(FastOrder with) {
        return this; }},
    D_S_C_H(2, 0, 3, 1) { public FastOrder refine(FastOrder with) {
        return this; }},
    D_S_H_C(3, 0, 2, 1) { public FastOrder refine(FastOrder with) {
        return this; }},
    H_C_D_S(1, 2, 0, 3) { public FastOrder refine(FastOrder with) {
        return this; }},
    H_C_S_D(1, 3, 0, 1) { public FastOrder refine(FastOrder with) {
        return this; }},
    H_D_C_S(2, 1, 0, 3) { public FastOrder refine(FastOrder with) {
        return this; }},
    H_D_S_C(3, 1, 0, 2) { public FastOrder refine(FastOrder with) {
        return this; }},
    H_S_D_C(3, 2, 0, 1) { public FastOrder refine(FastOrder with) {
        return this; }},
    H_S_C_D(2, 3, 0, 1) { public FastOrder refine(FastOrder with) {
        return this; }},
    S_C_D_H(1, 2, 3, 0) { public FastOrder refine(FastOrder with) {
        return this; }},
    S_C_H_D(1, 3, 2, 0) { public FastOrder refine(FastOrder with) {
        return this; }},
    S_D_H_C(3, 1, 2, 0) { public FastOrder refine(FastOrder with) {
        return this; }},
    S_D_C_H(2, 1, 3, 0) { public FastOrder refine(FastOrder with) {
        return this; }},
    S_H_C_D(2, 3, 1, 0) { public FastOrder refine(FastOrder with) {
        return this; }},
    S_H_D_C(3, 2, 1, 0) { public FastOrder refine(FastOrder with) {
        return this; }},

    C_S_DH(0, 2, 2, 1) { public FastOrder refine(FastOrder with) {
        return   with.PRECEDENCE[1] < with.PRECEDENCE[2]
               ? valueOf(0, 2, 3, 1)
               : with.PRECEDENCE[1] > with.PRECEDENCE[2]
               ? valueOf(0, 3, 2, 1)
               : this;
    }},
    C_DS_H(0, 1, 2, 1) { public FastOrder refine(FastOrder with) {
        return   with.PRECEDENCE[1] < with.PRECEDENCE[3]
               ? valueOf(0, 1, 3, 2)
               : with.PRECEDENCE[1] > with.PRECEDENCE[3]
               ? valueOf(0, 2, 3, 1)
               : this;
    }},
    C_HS_D(0, 2, 1, 1) { public FastOrder refine(FastOrder with) {
        return   with.PRECEDENCE[2] < with.PRECEDENCE[3]
               ? valueOf(0, 3, 1, 2)
               : with.PRECEDENCE[1] > with.PRECEDENCE[3]
               ? valueOf(0, 3, 2, 1)
               : this;
    }},
    C_DH_S(0, 1, 1, 2) { public FastOrder refine(FastOrder with) {
        return   with.PRECEDENCE[1] < with.PRECEDENCE[2]
               ? valueOf(0, 1, 2, 3)
               : with.PRECEDENCE[1] > with.PRECEDENCE[2]
               ? valueOf(0, 2, 1, 3)
               : this;
    }},
    C_D_HS(0, 1, 2, 2) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    C_H_DS(0, 2, 1, 2) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    CD_HS(0, 0, 1, 1) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    CD_S_H(0, 0, 2, 1) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    D_C_HS(1, 0, 2, 2) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    CD_H_S(0, 0, 1, 2) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    CH_DS(0, 1, 0, 1) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    CH_S_D(0, 2, 0, 1) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    CH_D_S(0, 1, 0, 2) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    H_C_DS(1, 2, 0, 2) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    CS_DH(0, 1, 1, 0) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    S_C_DH(1, 2, 2, 0) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    CS_D_H(0, 1, 2, 0) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    CS_H_D(0, 2, 1, 0) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    D_CH_S(1, 0, 1, 2) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    D_HS_C(2, 0, 1, 1) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    D_CS_H(1, 0, 2, 1) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    D_S_CH(2, 0, 2, 1) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    D_H_CS(2, 0, 1, 2) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    DH_CS(1, 0, 0, 1) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    DH_C_S(1, 0, 0, 2) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    DH_S_C(2, 0, 0, 1) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    H_D_CS(2, 1, 0, 2) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    DS_CH(1, 0, 1, 0) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    DS_C_H(1, 0, 2, 0) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    S_D_CH(2, 1, 2, 0) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    DS_H_C(2, 0, 1, 0) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    H_CD_S(1, 1, 0, 2) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    H_CS_D(1, 2, 0, 1) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    H_DS_C(2, 1, 0, 1) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    H_S_CD(2, 2, 0, 1) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    HS_CD(1, 1, 0, 0) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    HS_C_D(1, 2, 0, 0) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    HS_D_C(2, 1, 0, 0) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    S_H_CD(2, 2, 1, 0) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    S_CD_H(1, 1, 2, 0) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    S_CH_D(1, 2, 1, 0) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    S_DH_C(2, 1, 1, 0) { public FastOrder refine(FastOrder with) {
        return null;
    }},

    C_DHS(0, 1, 1, 1) {public FastOrder refine(FastOrder with){
        if (with.PRECEDENCE[1] < with.PRECEDENCE[2])
        {
            return   with.PRECEDENCE[2] < with.PRECEDENCE[3]
                   ? valueOf(0, 1, 2, 3)
                   : with.PRECEDENCE[2] > with.PRECEDENCE[3]
                   ? valueOf(0, 1, 3, 2)
                   : valueOf(0, 1, 2, 2);
        }
        else if (with.PRECEDENCE[1] < with.PRECEDENCE[3])
        {
            return   with.PRECEDENCE[3] < with.PRECEDENCE[2]
                   ? valueOf(0, 1, 3, 2)
                   : with.PRECEDENCE[3] > with.PRECEDENCE[2]
                   ? valueOf(0, 1, 2, 3)
                   : valueOf(0, 1, 2, 2);
        }
        else // if (with.PRECEDENCE[2] < with.PRECEDENCE[3])
        {
            return   with.PRECEDENCE[3] < with.PRECEDENCE[1]
                   ? valueOf(0, 3, 1, 2)
                   : with.PRECEDENCE[3] > with.PRECEDENCE[1]
                   ? valueOf(0, 2, 1, 3)
                   : valueOf(0, 2, 1, 2);
        }
    }},
    D_CHS(1, 0, 1, 1) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    H_CDS(1, 1, 0, 1) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    S_CDH(1, 1, 1, 0) { public FastOrder refine(FastOrder with) {
        return null;
    }},
    ;


    //--------------------------------------------------------------------
    public static FastOrder valueOf(
            int precedenceClub,
            int precedenceDiamond,
            int precedenceHeart,
            int precedenceSpade)
    {
//        return new FastOrder(precedenceClub,
//                             precedenceDiamond,
//                             precedenceHeart,
//                             precedenceSpade);
        return null;
    }


    //--------------------------------------------------------------------

    // lower precedence means it comes before
//    private final int PRECEDENCE_CLUB;
//    private final int PRECEDENCE_DIAMOND;
//    private final int PRECEDENCE_HEART;
//    private final int PRECEDENCE_SPADE;
    private final int PRECEDENCE[];


    //--------------------------------------------------------------------
    private FastOrder(int precedenceClub,
                      int precedenceDiamond,
                      int precedenceHeart,
                      int precedenceSpade)
    {
        PRECEDENCE = new int[]{
                precedenceClub,
                precedenceDiamond,
                precedenceHeart,
                precedenceSpade
        };
//        PRECEDENCE_CLUB    = precedenceClub;
//        PRECEDENCE_DIAMOND = precedenceDiamond;
//        PRECEDENCE_HEART   = precedenceHeart;
//        PRECEDENCE_SPADE   = precedenceSpade;
    }


    //--------------------------------------------------------------------
    // break suit symmetries
    public abstract FastOrder refine(FastOrder with);
//    {
//
//        if (PRECEDENCE_CLUB == PRECEDENCE_DIAMOND)
//        {
//
//        }
//        else
//        {
//
//        }
//
//        return null;
//    }
//    {
//        if (PRECEDENCE_CLUB == PRECEDENCE_DIAMOND)
//        {
//
//        }
//        else if (PRECEDENCE_CLUB == PRECEDENCE_HEART)
//        {
//
//        }
//        else if (PRECEDENCE_CLUB == )
//
//        return null;
//    }
}

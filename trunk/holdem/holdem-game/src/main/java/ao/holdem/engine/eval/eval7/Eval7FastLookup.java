package ao.holdem.engine.eval.eval7;

import ao.Infrastructure;
import ao.util.persist.PersistentShorts;


/**
 * http://senzee.blogspot.com/search/label/Poker%20Hand%20Evaluation
 * http://www.geocities.com/psenzee/code/index52c7.h
 */
public class Eval7FastLookup
{
    //--------------------------------------------------------------------
    private static final String DIR         = Infrastructure.path(
                                              "lookup/eval/eval7_fast/");
    private static final String F_TABLE7    = DIR + "table7.txt";
    private static final String F_BIT_COUNT = DIR + "bitcount.txt";
    public  static final String F_CACHE     = DIR + "eval7.cache";


    //--------------------------------------------------------------------
    private Eval7FastLookup() {}


    //--------------------------------------------------------------------
    private final static short table[];
    private final static short bitcount[];
    
    private final static int choose16x[] =
            {1, 16, 120, 560, 1820, 4368, 8008, 11440};
    private final static int choose32x[] =
            {1, 32, 496, 4960, 35960, 201376, 906192, 3365856};
    private final static int choose48x[] =
            {1, 48, 1128, 17296, 194580, 1712304, 12271512, 73629072};

    private final static int table4[] =
            {0, 0, 1, 5, 2, 3, 4, 3, 3, 0, 1, 2, 2, 0, 1, 0, 0};

    private final static int offsets52c[] =
            { 0, 73629072, 122715120, 132988944, 133767264 };

    private final static int offsets48c[] =
    {
        0, 3365856, 17864928, 42030048, 62167648, 71194848, 73361376, 73617632, 73629072, 0, 0, 0, 0, 0, 0, 0,
        0,  906192,  4128208,  8443408, 11221008, 12123728, 12263504, 12271512,        0, 0, 0, 0, 0, 0, 0, 0,
        0,  201376,   776736,  1371936,  1649696,  1707936,  1712304,        0,        0, 0, 0, 0, 0, 0, 0, 0,
        0,   35960,   115320,   174840,   192760,   194580,        0,        0,        0, 0, 0, 0, 0, 0, 0, 0,
        0,    4960,    12896,    16736,    17296,        0,        0,        0,        0, 0, 0, 0, 0, 0, 0, 0,
        0,     496,     1008,     1128,        0,        0,        0,        0,        0, 0, 0, 0, 0, 0, 0, 0,
        0,      32,       48,        0,        0,        0,        0,        0,        0, 0, 0, 0, 0, 0, 0, 0,
        0,       1,        0,        0,        0,        0,        0,        0,        0, 0, 0, 0, 0, 0, 0, 0
    };

    private final static int offsets32c[] =
    {
        0, 11440, 139568, 663728, 1682928, 2702128, 3226288, 3354416, 3365856, 0, 0, 0, 0, 0, 0, 0,
        0,  8008,  77896, 296296,  609896,  828296,  898184,  906192,       0, 0, 0, 0, 0, 0, 0, 0,
        0,  4368,  33488, 100688,  167888,  197008,  201376,       0,       0, 0, 0, 0, 0, 0, 0, 0,
        0,  1820,  10780,  25180,   34140,   35960,       0,       0,       0, 0, 0, 0, 0, 0, 0, 0,
        0,   560,   2480,   4400,    4960,       0,       0,       0,       0, 0, 0, 0, 0, 0, 0, 0,
        0,   120,    376,    496,       0,       0,       0,       0,       0, 0, 0, 0, 0, 0, 0, 0,
        0,    16,     32,      0,       0,       0,       0,       0,       0, 0, 0, 0, 0, 0, 0, 0,
        0,     1,      0,      0,       0,       0,       0,       0,       0, 0, 0, 0, 0, 0, 0, 0
    };

    static
    {
        table    = PersistentShorts.asArray(F_TABLE7);
        bitcount = PersistentShorts.asArray(F_BIT_COUNT);
    }


    //--------------------------------------------------------------------
    public static int index52c7(long x)
    {
        int A = (int)(x >> 48 & 0xFFFF);
        int B = (int)(x >> 32 & 0xFFFF);
        int C = (int)(x >> 16 & 0xFFFF);
        int D = (int)(x       & 0xFFFF);

        short bcA = bitcount[ A ];
        short bcB = bitcount[ B ];
        short bcC = bitcount[ C ];
        short bcD = bitcount[ D ];

        int mulA = choose48x[7 - bcA];
        int mulB = choose32x[7 - (bcA + bcB)];
        int mulC = choose16x[bcD];

        return offsets52c[bcA]                      + table4[A] * mulA +
		       offsets48c[ (bcA << 4)        + bcB] + table [B] * mulB +
		       offsets32c[((bcA + bcB) << 4) + bcC] + table [C] * mulC +
		                                              table [D];
    }
}

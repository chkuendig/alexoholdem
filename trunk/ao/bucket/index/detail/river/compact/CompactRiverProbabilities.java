package ao.bucket.index.detail.river.compact;

import ao.Infrastructure;
import ao.bucket.index.detail.river.ProbabilityEncoding;
import ao.util.persist.PersistentChars;

import java.util.Arrays;

/**
 * User: alex
 * Date: 1-Jul-2009
 * Time: 5:58:02 PM
 */
public class CompactRiverProbabilities
{
    //--------------------------------------------------------------------
    private CompactRiverProbabilities() {}

    public static void main(String[] args) {
        for (int raw = 0; raw < ProbabilityEncoding.COUNT; raw++) {
            System.out.println(raw + "\t" + (int) compact((char) raw));
        }
    }


    //--------------------------------------------------------------------
    private static final String storeFile = Infrastructure.path(
                        "lookup/eval/river/compact_prob.char");

    private static final char COMPACT[] = compact();
    public  static final int  COUNT     = 1950;
                                    // COMPACT[ COMPACT.length - 1 ] + 1;


    //--------------------------------------------------------------------
    private static char[] compact()
    {
        char compact[] = PersistentChars.retrieve(storeFile);
        if (compact != null) return compact;

        compact = computeCompact();
        PersistentChars.persist(compact, storeFile);
        return compact;
    }


    //--------------------------------------------------------------------
    private static char[] computeCompact()
    {
        char usedStrengths[] = usedStrengths();

        char nextCompact = 0;
        char compact[]   = new char[ ProbabilityEncoding.COUNT ];
        for (int i = 0; i < compact.length; i++) {
            boolean used = (Arrays.binarySearch(
                                     usedStrengths, (char) i) >= 0);

            if (used) {
                compact[ i ] = nextCompact++;
            } else {
                compact[ i ] = Character.MAX_VALUE;
            }
        }
        return compact;
    }


    //--------------------------------------------------------------------
    private static char[] usedStrengths()
    {
        int usedStrengthCount = 0;
        for (int strength = 0;
                 strength < ProbabilityEncoding.COUNT;
                 strength++) {
            if (ProbabilityCounts.countOf((char) strength) != 0) {
                usedStrengthCount++;
            }
        }

        char nextUsedStrength = 0;
        char usedStrengths[]  = new char[ usedStrengthCount ];
        for (int strength = 0;
                 strength < ProbabilityEncoding.COUNT;
                 strength++) {
            if (ProbabilityCounts.countOf((char) strength) != 0) {
                usedStrengths[ nextUsedStrength++ ] = (char) strength;
            }
        }
        return usedStrengths;
    }


    //--------------------------------------------------------------------
    public static char compact(double nonLossProbability)
    {
        return compact(ProbabilityEncoding.encodeWinProb(
                           nonLossProbability));
    }

    public static char compact(char nonLossProbability)
    {
        assert isValidRaw( nonLossProbability );
        return COMPACT[ nonLossProbability ];
    }

    public static boolean isValidRaw(char nonLossProbability)
    {
        return COMPACT[ nonLossProbability ] != Character.MAX_VALUE;
    }
}

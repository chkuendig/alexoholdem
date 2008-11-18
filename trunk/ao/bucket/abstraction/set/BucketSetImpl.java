package ao.bucket.abstraction.set;

import ao.bucket.index.test.MathUtil;
import ao.util.persist.PersistentChars;
import org.apache.log4j.Logger;

import java.util.Arrays;


/**
 * Date: Oct 29, 2008
 * Time: 7:50:17 PM
 */
public class BucketSetImpl implements BucketSet
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(BucketSetImpl.class);

    //--------------------------------------------------------------------
    public static void persist(BucketSetImpl bucketSet, String toFilename)
    {
        LOG.info("persisting bucket set to " + toFilename);

        PersistentChars.persist(
                new char[]{ bucketSet.BUCKET_COUNT },
                toFilename + ".count");
        PersistentChars.persist(
                bucketSet.BUCKETS_A,
                toFilename + ".buckets_a");
        PersistentChars.persist(
                bucketSet.BUCKETS_B,
                toFilename + ".buckets_b");
    }

    public static BucketSetImpl retrieve(String fromFilename)
    {
        char bucketCounts[] = PersistentChars.retrieve(
                                fromFilename + ".count");
        if (bucketCounts == null) return null;
        LOG.info("retrieving bucket set from " + fromFilename);

        char bucketCount = bucketCounts[ 0 ];
        char bucketsA[]  = PersistentChars.retrieve(
                                fromFilename + ".buckets_a");
        char bucketsB[]  = PersistentChars.retrieve(
                                fromFilename + ".buckets_b");

        return new BucketSetImpl(bucketsA, bucketsB, bucketCount);
    }


    //--------------------------------------------------------------------
//    private final SecureHash uniqueId = new MD5();
    private final char[]     BUCKETS_A;
    private final char[]     BUCKETS_B;
    private final char       BUCKET_COUNT;


    //--------------------------------------------------------------------
    private BucketSetImpl(char[] bucketsA,
                          char[] bucketsB,
                          char bucketCount)
    {
        BUCKETS_A    = bucketsA;
        BUCKETS_B    = bucketsB;
        BUCKET_COUNT = bucketCount;
    }
    public BucketSetImpl(long canonIndexCount, char bucketCount)
    {
        assert canonIndexCount <= (long) (Integer.MAX_VALUE - 1) * 2;

        if (canonIndexCount > Integer.MAX_VALUE)
        {
            BUCKETS_A = new char[ Integer.MAX_VALUE              ];
            BUCKETS_B = new char[ (int)(canonIndexCount
                                            - Integer.MAX_VALUE) ];
            Arrays.fill(BUCKETS_B, Character.MAX_VALUE);
        }
        else
        {
            BUCKETS_A = new char[ (int) canonIndexCount ];
            BUCKETS_B = null;
        }
        Arrays.fill(BUCKETS_A, Character.MAX_VALUE);

        BUCKET_COUNT = bucketCount;
    }


    //--------------------------------------------------------------------
    public char bucketOf(long canonIndex)
    {
        return (canonIndex <= Integer.MAX_VALUE)
                ? BUCKETS_A[ (int) canonIndex                ]
                : BUCKETS_B[ MathUtil.signedPart(canonIndex) ];
    }


    //--------------------------------------------------------------------
    public void add(long canonIndex, char bucket)
    {
        assert bucket != Character.MAX_VALUE;

        char prevValue;
        if (canonIndex <= Integer.MAX_VALUE)
        {
            prevValue = BUCKETS_A[ (int) canonIndex ];
                        BUCKETS_A[ (int) canonIndex ] = bucket;
        }
        else
        {
            int index = BUCKETS_B[ MathUtil.signedPart(canonIndex) ];
            prevValue = BUCKETS_B[ index ];
                        BUCKETS_B[ index ] = bucket;
        }
        assert prevValue == Character.MAX_VALUE;

//        uniqueId.feed( canonIndex );
//        uniqueId.feed( bucket     );
    }


    //--------------------------------------------------------------------
    public void display()
    {
        if (BUCKETS_A.length > 1000000)
        {
            System.out.println( toString() );
            return;
        }

        for (char bucket = 0; bucket < BUCKET_COUNT; bucket++)
        {
            for (int canonIndex = 0; canonIndex < BUCKETS_A.length; canonIndex++)
            {
                if (BUCKETS_A[canonIndex] == bucket)
                {
                    System.out.print( canonIndex + "\t" );
                }
            }
        }
    }


    //--------------------------------------------------------------------
//    public String id()
//    {
//        return uniqueId.hexDigest();
//    }

    @Override public String toString()
    {
//        return id();
        return "BucketSetImpl";
    }


    //--------------------------------------------------------------------
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BucketSetImpl that = (BucketSetImpl) o;

//        return uniqueId.equals( that.uniqueId );
        return Arrays.equals(BUCKETS_A, that.BUCKETS_A) &&
                Arrays.equals(BUCKETS_B, that.BUCKETS_B);
    }

    @Override
    public int hashCode()
    {
//        return uniqueId.bigDigest().intValue();

        int result = Arrays.hashCode(BUCKETS_A);
        result = 31 * result +
                (BUCKETS_B != null ? Arrays.hashCode(BUCKETS_B) : 0);
        return result;
    }
}

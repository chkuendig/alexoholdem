package ao.bucket.abstraction.set.secure_hash;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * Date: Nov 16, 2008
 * Time: 4:02:12 PM
 */
public interface SecureHash extends Serializable
{
    //--------------------------------------------------------------------
    void feed(byte value);

    void feed(int value);

    void feed(long value);

    void feed(byte values[]);


    //--------------------------------------------------------------------
    byte[] digest();

    BigInteger bigDigest();

    String hexDigest();
}

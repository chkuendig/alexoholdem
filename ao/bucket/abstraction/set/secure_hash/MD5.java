package ao.bucket.abstraction.set.secure_hash;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Date: Nov 16, 2008
 * Time: 3:40:54 PM
 */
public class MD5 implements SecureHash
{
    //--------------------------------------------------------------------
    private final MessageDigest md5;


    //--------------------------------------------------------------------
    public MD5()
    {
        MessageDigest digestInstance;
        try
        {
            digestInstance = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
            digestInstance = null;
        }
        md5 = digestInstance;
    }


    //--------------------------------------------------------------------
    public void feed(byte value)
    {
        md5.update(value);
    }
    
    public void feed(byte[] values)
    {
        md5.update(values);
    }

    public void feed(int value)
    {
        byte b[] = new byte[4];
        ByteBuffer buf = ByteBuffer.wrap(b);
        buf.putInt(value);
        md5.update(b);
    }

    public void feed(long value)
    {
        byte b[] = new byte[8];
        ByteBuffer buf = ByteBuffer.wrap(b);
        buf.putLong(value);
        md5.update(b);
    }


    //--------------------------------------------------------------------
    public byte[] digest()
    {
        byte digest[] = new byte[16];
        md5.digest(digest);
        return digest;
    }

    public BigInteger bigDigest()
    {
        return new BigInteger( digest() );
    }

    public String hexDigest()
    {
        return bigDigest().toString( 16 );
    }


    //--------------------------------------------------------------------
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MD5 md51 = (MD5) o;
        return md5.equals(md51.md5);
    }

    @Override
    public int hashCode()
    {
        return md5.hashCode();
    }

    @Override
    public String toString()
    {
        return hexDigest();
    }
}

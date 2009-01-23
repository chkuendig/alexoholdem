package ao.misc;

import ao.util.io.Dir;
import ao.util.math.rand.Rand;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Date: Jan 23, 2009
 * Time: 11:28:41 AM
 */
public class MmapTest
{
    //--------------------------------------------------------------------
    private static final File DIR  = Dir.get("test");
    private static final File FILE = new File(DIR, "mmap.byte");


    //--------------------------------------------------------------------
    public static void main(String[] args) throws IOException {

//        generateFile(1024*1024*1024*8L);
          accessFile();
    }


    //--------------------------------------------------------------------
    public static void accessFile() throws IOException
    {
        FileInputStream f  = new FileInputStream(FILE);
        FileChannel     ch = f.getChannel();

        MappedByteBuffer mb = ch.map(FileChannel.MapMode.READ_ONLY,
                                     0,
                                     1024*1024*1024);

        long checksum = 0;
        while (mb.hasRemaining()) {
            checksum += mb.get();
        }
        System.out.println(checksum);
    }


    //--------------------------------------------------------------------
    public static void generateFile(long size) throws IOException {
        OutputStream out =
                new BufferedOutputStream(
                        new FileOutputStream(FILE));

        for (long i = 0; i < size; i++) {
            out.write( Rand.nextInt() );

            if ( i      %    (1024*1024) == 0) System.out.print(".");
            if ((i + 1) % (50*1024*1024) == 0) System.out.println();
        }
        System.out.println("done generating file of " + size);

        out.close();
    }
}

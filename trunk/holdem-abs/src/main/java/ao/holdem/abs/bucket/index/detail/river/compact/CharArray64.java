package ao.holdem.abs.bucket.index.detail.river.compact;

import ao.util.io.Dirs;
import ao.util.persist.PersistentChars;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * http://stackoverflow.com/questions/878309/java-array-with-more-than-4gb-elements
 */
public class CharArray64
{
    private static final Logger LOG = LoggerFactory.getLogger(CharArray64.class);

    private static final long CHUNK_SIZE = 512*1024*1024; //1GiB

    private final long size;
    private final char[][] data;


    public CharArray64( long size )
    {
        this.size = size;
        if( size == 0 ) {
            data = null;
        } else {
            int fullChunks = (int)(size / CHUNK_SIZE);
            int remainder = (int)(size - ((long) fullChunks) * CHUNK_SIZE);
            int chunks = fullChunks + (remainder == 0 ? 0 : 1);

            data = new char[chunks][];
            for (int idx = fullChunks; --idx >= 0; ) {
                data[idx] = new char[(int) CHUNK_SIZE];
            }
            if (remainder != 0) {
                data[fullChunks] = new char[remainder];
            }
        }
    }

    private CharArray64(long size, char[][] data) {
        this.size = size;
        this.data = data;

        checkSize();
    }

    private void checkSize() {
        long totalSize = 0;
        for (char[] chunk : data) {
            totalSize += chunk.length;
        }
        Preconditions.checkState(totalSize == size, "%s | %s", totalSize, size);
    }


    public char get( long index ) {
        if( index<0 || index>=size ) {
            throw new IndexOutOfBoundsException("Error attempting to access data element "+index+".  Array is "+size+" elements long.");
        }
        int chunk = (int)(index/CHUNK_SIZE);
        int offset = (int)(index - (((long)chunk)*CHUNK_SIZE));
        return data[chunk][offset];
    }

    public void set( long index, char b ) {
        if( index<0 || index>=size ) {
            throw new IndexOutOfBoundsException("Error attempting to access data element "+index+".  Array is "+size+" elements long.");
        }
        int chunk = (int)(index/CHUNK_SIZE);
        int offset = (int)(index - (((long)chunk)*CHUNK_SIZE));
        data[chunk][offset] = b;
    }


    public static CharArray64 read(long size, File dir) {
        int fullChunks = (int)(size / CHUNK_SIZE);
        int remainder = (int)(size - ((long) fullChunks) * CHUNK_SIZE);
        int chunks = fullChunks + (remainder == 0 ? 0 : 1);

        char[][] data = new char[chunks][];

        for (int idx = 0; idx < chunks; idx++) {
            LOG.info("Reading {} of {}", idx + 1, chunks);

            File path = new File(dir, filename(idx));
            data[idx] = PersistentChars.retrieve(path);

            if (data[idx] == null) {
                return null;
            }

            LOG.info("Read {}", data[idx].length);
        }

        return new CharArray64(size, data);
    }

    public void write(File dir) {
        for (int i = 0; i < data.length; i++) {
            File part = new File(dir, filename(i));

            try {
                Files.createParentDirs(part);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }

            PersistentChars.persist(data[i], part);
        }
    }


    private static String filename(int chunk) {
        return chunk + ".char";
    }
}

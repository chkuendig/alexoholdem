package ao.holdem.bot.regret.grid;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * User: alex
 * Date: 29-Apr-2009
 * Time: 3:57:58 PM
 */
public class StoredGrid implements Grid
{
    //--------------------------------------------------------------------
    private final int     rows;
    private final int     cols;
    private final boolean doublePrecision;
    private       File    inFile;

    private RandomAccessFile in;


    //--------------------------------------------------------------------
    public StoredGrid(int     nRows,
                      int     nColumns,
                      boolean useDoubles)
    {
        rows = nRows;
        cols = nColumns;

        doublePrecision = useDoubles;
    }


    //--------------------------------------------------------------------
    public int rows() {
        return rows;
    }

    public int columns() {
        return cols;
    }

    private long index(int row, int col) {
        return (long) row * columns() + col;
    }


    //--------------------------------------------------------------------
    public double get(int row, int col) {
        try {
            return doGet(row, col);
        } catch (IOException e) {
            throw new Error( e );
        }
    }
    private double doGet(int row, int col) throws IOException {
        if (inFile == null) return 0;

        if (in == null) {
            in = new RandomAccessFile(inFile, "r");
        }

        if (doublePrecision) {
            in.seek( index(row, col) * (Double.SIZE / 8) );
            return in.readDouble();
        } else {
            in.seek( index(row, col) * (Float.SIZE / 8) );
            return in.readFloat();
        }
    }

    public void add(int row, int col, double addend) {
        throw new UnsupportedOperationException();
    }


    //--------------------------------------------------------------------
    public void save(File to) {}

    public void load(File from) {
        try {
            doLoad(from);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void doLoad(File from) throws IOException {
        if (inFile != null) {
            in.close();
            in = null;
        }

        inFile = from;
    }
}

package ao.regret.holdem.grid;

import ao.util.persist.PersistentDoubles;

import java.io.File;

/**
 * User: alex
 * Date: 29-Apr-2009
 * Time: 3:12:38 PM
 */
public class ArrayGrid implements Grid
{
    //--------------------------------------------------------------------
    private final double vals[][]; // [bucket][intent]


    //--------------------------------------------------------------------
    public ArrayGrid(int nBuckets,
                     int nIntents)
    {
        this(new double[ nBuckets ][ nIntents ]);
    }

    private ArrayGrid(double values[][])
    {
        vals = values;
    }


    //--------------------------------------------------------------------
    public int rows() {
        return vals.length;
    }

    public int columns() {
        return vals[0].length;
    }


    //--------------------------------------------------------------------
    public double get(int row, int col) {
        return vals[ row ][ col ];
    }

    public void set(int row, int col, double value) {
        vals[ row ][ col ] = value;
    }

    public void add(int row, int col, double addend) {
        vals[ row ][ col ] += addend;
    }


    //--------------------------------------------------------------------
    public void save(File to) {
        PersistentDoubles.persist(vals, to);
    }

    public void load(File from) {
        PersistentDoubles.retrieve(from, vals);
    }
}

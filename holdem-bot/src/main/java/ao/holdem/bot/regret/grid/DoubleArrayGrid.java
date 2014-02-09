package ao.holdem.bot.regret.grid;

import ao.util.persist.PersistentDoubles;

import java.io.File;

/**
 * User: alex
 * Date: 10-May-2009
 * Time: 3:34:19 PM
 */
public class DoubleArrayGrid implements Grid
{
    //--------------------------------------------------------------------
    private final double vals[][]; // [bucket][intent]


    //--------------------------------------------------------------------
    public DoubleArrayGrid(int nBuckets,
                           int nIntents)
    {
        this(new double[ nBuckets ][ nIntents ]);
    }

    private DoubleArrayGrid(
            double values[][])
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

package ao.holdem.bot.regret.grid;

import ao.util.persist.PersistentFloats;

import java.io.File;

/**
 * User: alex
 * Date: 29-Apr-2009
 * Time: 3:12:38 PM
 */
public class FloatArrayGrid implements Grid
{
    //--------------------------------------------------------------------
    private final float vals[][]; // [bucket][intent]


    //--------------------------------------------------------------------
    public FloatArrayGrid(int nBuckets,
                          int nIntents)
    {
        this(new float[ nBuckets ][ nIntents ]);
    }

    private FloatArrayGrid(
            float values[][]
            )
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
        PersistentFloats.persist(vals, to);
    }

    public void load(File from) {
        PersistentFloats.retrieve(from, vals);
    }
}

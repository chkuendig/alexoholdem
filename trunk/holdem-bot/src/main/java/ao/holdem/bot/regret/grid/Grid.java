package ao.holdem.bot.regret.grid;

import java.io.File;

/**
 * User: alex
 * Date: 29-Apr-2009
 * Time: 12:10:47 PM
 */
public interface Grid
{
    //--------------------------------------------------------------------
    public int rows();

    public int columns();


    //--------------------------------------------------------------------
    public double get(int row, int col);

//    public void   set(int row, int col, double value);

    public void   add(int row, int col, double addend);


    //--------------------------------------------------------------------
    public void save(File to);

    public void load(File from);


    //--------------------------------------------------------------------
    public static class Impl
    {
        private Impl() {}

        public static Grid newInstance(
                int     rows,
                int     cols,
                boolean readOnly,
                boolean doublePrecision)
        {
            return readOnly
                   ? new StoredGrid(rows, cols, doublePrecision)
                   : doublePrecision
                     ? new DoubleArrayGrid(rows, cols)
                     : new FloatArrayGrid (rows, cols);
        }

        public static Grid newInstance(
                int     rows,
                int     cols,
                boolean doublePrecision)
        {
            return newInstance(rows, cols, false, doublePrecision);
        }
    }
}

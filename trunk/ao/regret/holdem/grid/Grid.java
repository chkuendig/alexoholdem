package ao.regret.holdem.grid;

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

    public void   set(int row, int col, double value);

    public void   add(int row, int col, double addend);


    //--------------------------------------------------------------------
    public void save(File to);

    public void load(File from);


    //--------------------------------------------------------------------
    public static class Impl
    {
        public static Grid newInstance(
                int rows, int cols, boolean stored)
        {
            return stored
                   ? new StoredGrid(rows, cols)
                   : new  ArrayGrid(rows, cols);
        }
    }
}

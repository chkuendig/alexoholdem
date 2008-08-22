package ao.bucket.index.iso_case;

import ao.holdem.model.card.Hole;

/**
 *
 */
public class HoleCase
{
    //--------------------------------------------------------------------
    public static HoleCase newInstance(Hole hole)
    {
        return new HoleCase( hole );
    }


    //--------------------------------------------------------------------
    private final int  index;
    private final Type type;


    //--------------------------------------------------------------------
    private HoleCase(Hole hole)
    {
        type = hole.paired()
               ? Type.PAIR
               : hole.suited()
                 ? Type.SUITED
                 : Type.UNSUITED;

        switch (type)
        {
            case PAIR:
                index   = hole.a().rank().ordinal();
                break;

            case SUITED:
            case UNSUITED:
                int hi  = hole.hi().rank().ordinal();
                int lo  = hole.lo().rank().ordinal();

                index   = hi * (hi - 1) / 2 + lo;
                break;

            default: throw new Error();
        }
    }


    //--------------------------------------------------------------------
    public int subIndex()
    {
        return index;
    }
    public Type type()
    {
        return type;
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return type + "\t" + index;
    }


    //--------------------------------------------------------------------
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HoleCase holeCase = (HoleCase) o;
        return index == holeCase.index &&
               type == holeCase.type;
    }

    public int hashCode()
    {
        int result;
        result = index;
        result = 31 * result + type.hashCode();
        return result;
    }


    //--------------------------------------------------------------------
    public static enum Type
    {
        PAIR(13), SUITED(78), UNSUITED(78);

        private final int MEMBERS;

        private Type(int members)
        {
            MEMBERS = members;
        }

        public int members()
        {
            return MEMBERS;
        }
    }
}

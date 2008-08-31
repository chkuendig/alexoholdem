package ao.bucket.index.iso_util;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Suit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * n(1) old(1), n(2) old(2), ...
 * m(1) new(1), m(2) new(2), ...
 */
public class CommunityCase
{
    //--------------------------------------------------------------------
    private final int olds[];
    private final int news[];


    //--------------------------------------------------------------------
    public CommunityCase(
            Card    additional[],
            Card... existing)
    {
        List<Integer> oldBuffer = new ArrayList<Integer>();
        List<Integer> newBuffer = new ArrayList<Integer>();

        for (Suit s : Suit.VALUES)
        {
            int incomingCount = 0;
            for (Card in : additional)
            {
                if (in.suit() == s)
                {
                    incomingCount++;
                }
            }

            if (incomingCount > 0)
            {
                int existingCount = 0;
                for (Card ex : existing)
                {
                    if (ex.suit() == s)
                    {
                        existingCount++;
                    }
                }

                (existingCount > 0 ?
                    oldBuffer : newBuffer)
                        .add( incomingCount );
            }
        }

        Collections.sort( oldBuffer );
        Collections.sort( newBuffer );

        olds = new int[ oldBuffer.size() ];
        news = new int[ newBuffer.size() ];
        for (int i = 0; i < olds.length; i++)
        {
            olds[ i ] = oldBuffer.get( i );
        }
        for (int i = 0; i < news.length; i++)
        {
            news[ i ] = newBuffer.get( i );
        }
    }


    //--------------------------------------------------------------------
    public String toString()
    {
        return Arrays.toString(olds) + " | " +
               Arrays.toString(news);
    }

    
    //--------------------------------------------------------------------
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CommunityCase that = (CommunityCase) o;
        return Arrays.equals(news, that.news) &&
               Arrays.equals(olds, that.olds);
    }

    public int hashCode()
    {
        int result;
        result = Arrays.hashCode(olds);
        result = 31 * result + Arrays.hashCode(news);
        return result;
    }
}

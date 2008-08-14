package ao.bucket.index;

import ao.bucket.index.flop.PostPairIndexer;
import ao.bucket.index.iso_cards.IsoFlop;
import ao.bucket.index.iso_cards.IsoHole;
import ao.bucket.index.iso_case.FlopCase;
import ao.bucket.index.iso_case.HoleCase;
import ao.bucket.index.iso_case.HoleCase.Type;
import ao.holdem.model.card.Card;
import ao.holdem.model.card.Hole;
import ao.holdem.model.card.sequence.CardSequence;
import ao.util.stats.Combiner;

import java.util.*;

/**
 *
 */
public class IsoIndexer implements Indexer
{
    //--------------------------------------------------------------------
    public int indexOf(CardSequence cards)
    {
        //IsoHole hole = new IsoHole( cards.hole() );

        //System.out.println(hole);

        return 0;
    }


    //--------------------------------------------------------------------
    public static void main(String[] args)
    {
        Card cards[] = Card.values();

        Map<IsoHole, List< Card[] >> isoHoles =
                new LinkedHashMap<IsoHole, List<Card[]>>();
        for (Card holeCards[] : new Combiner<Card>(Card.VALUES, 2))
        {
            Hole    hole    = Hole.newInstance(
                                    holeCards[0], holeCards[1]);
            if (HoleCase.newInstance(hole).type() != Type.PAIR) continue;

            retrieveOrCreate( isoHoles, hole.isomorphism() )
                    .add( holeCards );
        }

        Map<FlopCase, List<IsoFlop>> flopCases =
                new LinkedHashMap<FlopCase, List<IsoFlop>>();
        for (Map.Entry<IsoHole, List< Card[] >> holeEntry
                : isoHoles.entrySet())
        {
            IsoHole isoHole = holeEntry.getKey();
            System.out.println(isoHole);

            Map<IsoFlop, List< Card[] >> isoFlops =
                    new LinkedHashMap<IsoFlop, List<Card[]>>();
            for (Card holeCards[] : holeEntry.getValue())
            {
                System.out.println(Arrays.toString( holeCards ));

                swap(cards, holeCards[1].ordinal(), 51  );
                swap(cards, holeCards[0].ordinal(), 51-1);

                Hole hole = Hole.newInstance(holeCards[0], holeCards[1]);
                handleFlop(hole, isoFlops, cards);

                swap(cards, holeCards[0].ordinal(), 51-1);
                swap(cards, holeCards[1].ordinal(), 51  );
            }

            for (IsoFlop isoFlop : isoFlops.keySet())
            {
                List<IsoFlop> exiting =
                        flopCases.get( isoFlop.flopCase() );
                if (exiting == null)
                {
                    exiting = new ArrayList<IsoFlop>();
                    flopCases.put(isoFlop.flopCase(), exiting);
                }
                exiting.add( isoFlop );
            }

            System.out.println(flopCases.size());
//            break;
        }

        BitSet indexes = new BitSet();
        System.out.println("------------------------------");
        for (Map.Entry<FlopCase, List<IsoFlop>> e :
                flopCases.entrySet())
        {
            //if (! e.getKey().equals(FlopCase.CASE_12_112)) continue;

            System.out.println(e.getKey() + " :: " + e.getValue().size());

            for (IsoFlop flop : e.getValue())
            {
                int index =
                        new PostPairIndexer().indexOf(e.getKey(), flop);
                if (indexes.get(index))
                {
                    System.out.println("WTF!?!?: " + flop);
                }
                indexes.set( index );
//                System.out.println(index);
            }
//            break;
        }
        System.out.println(indexes.nextClearBit(0));
    }


    //--------------------------------------------------------------------
    private static void displayIsoFlops(
            Map<IsoFlop, List<Card[]>> isoFlops)
    {
        for (Map.Entry<IsoFlop, List< Card[] >> flopEntry
                : isoFlops.entrySet())
        {
            IsoFlop isoFlop = flopEntry.getKey();
            System.out.println("\t" + isoFlop);
            for (Card[] flopCards : flopEntry.getValue())
            {
                System.out.println("\t\t" + Arrays.toString(flopCards));
            }
        }
    }


    //--------------------------------------------------------------------
    private static void handleFlop(
            Hole                         hole,
            Map<IsoFlop, List< Card[] >> isoFlops,
            Card                         cards[])
    {
        for (Card flopCards[] : new Combiner<Card>(cards, 50, 3))
        {
            Arrays.sort(flopCards, Card.BY_RANK_DSC);

            swap(cards, flopCards[2].ordinal(), 51-2);
            swap(cards, flopCards[1].ordinal(), 51-3);
            swap(cards, flopCards[0].ordinal(), 51-4);

            IsoFlop isoFlop = hole.isomorphism()
                                  .flop( hole, flopCards );
            retrieveOrCreate(isoFlops, isoFlop)
                    .add( flopCards );

            swap(cards, flopCards[0].ordinal(), 51-4);
            swap(cards, flopCards[1].ordinal(), 51-3);
            swap(cards, flopCards[2].ordinal(), 51-2);
        }
    }


    //--------------------------------------------------------------------
    private static <K, V> List< V[] > retrieveOrCreate(
            Map<K, List< V[] >> map,
            K                   key)
    {
        List< V[] > val = map.get( key );
        if (val == null)
        {
            val = new ArrayList<V[]>();
            map.put( key, val );
        }
        return val;
    }

    private static void swap(Card cards[], int i, int j)
    {
        Card tmp = cards[i];
        cards[i] = cards[j];
        cards[j] = tmp;
    }
}

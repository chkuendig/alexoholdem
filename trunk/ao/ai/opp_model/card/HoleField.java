package ao.ai.opp_model.card;

/**
 *
 */
public class HoleField
{
//    //--------------------------------------------------------------------
//    private final Map<Hole, double[]> holes =
//            new HashMap<Hole, double[]>();
//
//    //private
//
//
//    //--------------------------------------------------------------------
//    public HoleField()
//    {
//        this(Collections.<Card>emptyList());
//    }
//    public HoleField(final Collection<Card> excluding)
//    {
//        new FastCombiner<Card>(Card.values()).combine(
//                new FastCombiner.CombinationVisitor2<Card>() {
//            public void visit(Card cardA, Card cardB) {
//                if (!(excluding.contains(cardA) ||
//                      excluding.contains(cardB)))
//                {
//                    holes.put(new Hole(cardA, cardB),
//                              new double[]{ 1.0 });
//                }
//            }
//        });
//    }
//
//
//    //--------------------------------------------------------------------
//    public void exclude(Card card)
//    {
//        for (Hole h : new ArrayList<Hole>( holes.keySet() ))
//        {
//            if (h.contains( card ))
//            {
//                holes.remove( h );
//            }
//        }
//    }
//    public void exclude(Collection<Card> cards)
//    {
//        if (cards.size() == 1)
//        {
//            exclude( cards.iterator().next() );
//        }
//        else
//        {
//            excludeMany( cards );
//        }
//    }
//    private void excludeMany(Collection<Card> cards)
//    {
//        for (Hole h : new ArrayList<Hole>( holes.keySet() ))
//        {
//            if (cards.contains( h.first() ) ||
//                    cards.contains( h.second() ))
//            {
//                holes.remove( h );
//            }
//        }
//    }
//
//    public void exclude(Hole hole)
//    {
//        holes.remove( hole );
//    }
}

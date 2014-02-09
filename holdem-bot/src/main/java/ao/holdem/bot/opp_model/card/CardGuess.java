package ao.holdem.bot.opp_model.card;

/**
 * Places players on hole cards.
 */
public class CardGuess
{
//    //--------------------------------------------------------------------
//    private Map<PlayerHandle, HoleField> probable =
//            new HashMap<PlayerHandle, HoleField>();
//
//    private Map<PlayerHandle, Hole> known =
//            new HashMap<PlayerHandle, Hole>();
//
//    private List<Card> knownCommunity = new ArrayList<Card>();
//    private List<Card> allKnown       = new ArrayList<Card>();
//
//    private List<PlayerHandle> allPlayers;
//
//
//    //--------------------------------------------------------------------
//    public CardGuess(List<PlayerHandle> players)
//    {
//        allPlayers = players;
//    }
//
//
//    //--------------------------------------------------------------------
//    public void addKnownHole(PlayerHandle player, Hole hole)
//    {
//        known.put(player, hole);
//        probable.remove( player );
//
//        allKnown.add(hole.first());
//        allKnown.add(hole.second());
//
//        for (HoleField holes : probable.values())
//        {
//            holes.exclude( hole );
//        }
//    }
//
//    public void addKnownCommunity(Community community)
//    {
//        addKnownCommunity( community.known() );
//    }
//    public void addKnownCommunity(Card... community)
//    {
//        Collection<Card> newlyKnown = new ArrayList<Card>();
//        for (Card c : community)
//        {
//            if (! knownCommunity.contains( c ))
//            {
//                newlyKnown.add( c );
//            }
//        }
//
//        if (! newlyKnown.isEmpty())
//        {
//            allKnown.addAll( newlyKnown );
//            knownCommunity.addAll( newlyKnown );
//
//            for (HoleField holes : probable.values())
//            {
//                holes.exclude( newlyKnown );
//            }
//        }
//    }
//
//
//    //--------------------------------------------------------------------
//    public void update(PlayerHandle player,
//                       HandStrength probableStrength)
//    {
//
//    }
//
//
//    //--------------------------------------------------------------------
//    public CardSource nextProbable()
//    {
//
//
//
//        //return new ProbableCardSource();
//        return null;
//    }
//
//
//    //--------------------------------------------------------------------
//    private Hole holeFor(PlayerHandle player)
//    {
//        Hole knownHole = known.get( player );
//        if (knownHole != null) return knownHole;
//
//        return null;
//    }
//
//
//    //--------------------------------------------------------------------
//    private HoleField holes(PlayerHandle forPlayer)
//    {
//        if (known.containsKey( forPlayer )) return null;
//
//        HoleField guess = probable.get( forPlayer );
//        if (guess == null)
//        {
//            guess = new HoleField( allKnown );
//        }
//        return guess;
//    }
}


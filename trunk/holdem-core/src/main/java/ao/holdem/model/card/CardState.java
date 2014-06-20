package ao.holdem.model.card;


import ao.holdem.model.Round;
import ao.holdem.model.card.chance.ChanceCards;

import java.util.*;

public class CardState implements ChanceCards
{
    private final Community community;
    private final List<Hole> holes;


    public CardState(Community community, List<Hole> holes) {
        if (community == null) {
            throw new NullPointerException("Community");
        }

        this.community = community;
        this.holes = new ArrayList<>(holes);

        checkUnique();
    }

    private void checkUnique() {
        Set<Card> cards = EnumSet.copyOf(Arrays.asList(community.toArray()));
        for (Hole hole : holes) {
            cards.addAll(Arrays.asList(hole.toArray()));
        }

        if (cards.size() != (community.knownCount() + holes.size() * 2)) {
            throw new IllegalArgumentException("Duplicate cards detected");
        }
    }


    public Community community() {
        return community;
    }

    @Override
    public Community community(Round asOf) {
        return community.asOf(asOf);
    }

    @Override
    public Hole hole(int playerIndex) {
        return holes.get(playerIndex);
    }

    public boolean hasHole(int playerIndex) {
        return holes.size() > playerIndex;
    }


    public CardState addHole(int playerIndex, Hole hole) {
        if (holes.size() != playerIndex) {
            throw new IllegalArgumentException("Hole already provided for " + playerIndex);
        }

        CardState copy = new CardState(community, holes);
        copy.holes.add(hole);
        return copy;
    }


    public CardState withCommunity(Community community) {
        return new CardState(community, holes);
    }

    public CardState addFlop(Card flopA, Card flopB, Card flopC) {
        return withCommunity(community.addFlop(flopA, flopB, flopC));
    }
    public CardState addTurn(Card turn) {
        return withCommunity(community.addTurn(turn));
    }
    public CardState addRiver(Card river) {
        return withCommunity(community.addRiver(river));
    }


    @Override
    public String toString() {
        return community + " | " + holes;
    }
}

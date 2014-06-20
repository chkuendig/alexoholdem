package ao.holdem.model.card;

import ao.holdem.engine.eval.EvalBy5;

import java.util.*;

public class WinningCardSet
{
    private final Set<Card> commonCommunity;

    private final Set<Card> dealeeCommunity;
    private final Set<Card> dealerCommunity;

    private final Set<Card> dealeeHole;
    private final Set<Card> dealerHole;



    public WinningCardSet(CardState cardState) {
        checkArgument(cardState);

        short dealeeValue = EvalBy5.valueOf(cardState.community(), cardState.hole(0));
        short dealerValue = EvalBy5.valueOf(cardState.community(), cardState.hole(1));

        if (dealeeValue == dealerValue) {
            Collection<Set<Card>> allDealeeStrongest = EvalBy5.strongestSubset(cardState.community(), cardState.hole(0));
            Collection<Set<Card>> allDealerStrongest = EvalBy5.strongestSubset(cardState.community(), cardState.hole(1));

            List<Set<Card>> mostCommonSubsets = mostCommonSubsets(allDealeeStrongest, allDealerStrongest);
            Set<Card> dealeeStrongest = mostCommonSubsets.get(0);
            Set<Card> dealerStrongest = mostCommonSubsets.get(1);

            commonCommunity = intersection(dealeeStrongest, dealerStrongest);
            dealeeCommunity = Collections.emptySet();
            dealerCommunity = Collections.emptySet();

            dealeeHole = intersection(dealeeStrongest, cardState.hole(0).toSet());
            dealerHole = intersection(dealerStrongest, cardState.hole(1).toSet());
        } else {
            commonCommunity = Collections.emptySet();

            boolean dealeeWinner = (dealeeValue > dealerValue);
            int strongerPlayer = dealeeWinner ? 0 : 1;

            Collection<Set<Card>> allWinningCards = EvalBy5.strongestSubset(
                    cardState.community(), cardState.hole(strongerPlayer));

            Set<Card> winningCards = allWinningCards.iterator().next();

            Set<Card> winningCommunity = intersection(winningCards, cardState.community().toSet());
            Set<Card> winningHole = intersection(winningCards, cardState.hole(strongerPlayer).toSet());

            if (dealeeWinner) {
                dealeeCommunity = winningCommunity;
                dealerCommunity = Collections.emptySet();
                dealeeHole = winningHole;
                dealerHole = Collections.emptySet();
            } else {
                dealeeCommunity = Collections.emptySet();
                dealerCommunity = winningCommunity;
                dealeeHole = Collections.emptySet();
                dealerHole = winningHole;
            }
        }

        checkFiveEach(cardState);
    }

    private List<Set<Card>> mostCommonSubsets(Collection<Set<Card>> allDealeeStrongest, Collection<Set<Card>> allDealerStrongest) {
        Set<Card> mostCommon = null;
        Set<Card> mostCommonDealeeStrongest = null;
        Set<Card> mostCommonDealerStrongest = null;

        for (Set<Card> dealeeStrongest : allDealeeStrongest) {
            for (Set<Card> dealerStrongest : allDealerStrongest) {
                Set<Card> common = intersection(dealeeStrongest, dealerStrongest);

                if (mostCommon == null ||
                        mostCommon.size() < common.size() ||
                        mostCommon.size() == common.size() && isMoreCanonicalThan(common, mostCommon))
                {
                    mostCommonDealeeStrongest = dealeeStrongest;
                    mostCommonDealerStrongest = dealerStrongest;

                    mostCommon = common;
                }
            }
        }

        return Arrays.asList(mostCommonDealeeStrongest, mostCommonDealerStrongest);
    }

    // a and b are assumed to be of equal length and in natural order
    private boolean isMoreCanonicalThan(Set<Card> a, Set<Card> b) {
        Iterator<Card> aItems = a.iterator();
        Iterator<Card> bItems = b.iterator();

        while (aItems.hasNext()) {
            Card nextA = aItems.next();
            Card nextB = bItems.next();

            if (nextA != nextB) {
                return nextA.ordinal() < nextB.ordinal();
            }
        }

        return false;
    }


    private void checkFiveEach(CardState cardState) {
        int dealeeOnlyCount = dealeeCommunity.size() + dealeeHole.size();
        int dealerOnlyCount = dealerCommunity.size() + dealerHole.size();

        int dealeeCount = dealeeOnlyCount + commonCommunity.size();
        int dealerCount = dealerOnlyCount + commonCommunity.size();

        if (! (dealeeCount == 5 && dealerCount == 0 ||
                dealeeCount == 0 && dealerCount == 5 ||
                dealeeCount == 5 && dealerCount == 5)) {
            throw new IllegalStateException("Illegal count: " + cardState + " | " + dealeeCount + " | " + dealerCount);
        }
    }


    private Set<Card> intersection(Collection<Card> a, Collection<Card> b) {
        Set<Card> common = EnumSet.copyOf(a);
        common.retainAll(b);
        return Collections.unmodifiableSet(common);
    }


    private void checkArgument(CardState cardState) {
        if (! cardState.community().hasRiver()) {
            throw new IllegalArgumentException("Must be at showdown");
        }

        if (! cardState.hasHole(0) || ! cardState.hasHole(1)) {
            throw new IllegalArgumentException("Must include at least 2 players");
        }

        if (cardState.hasHole(2)) {
            throw new UnsupportedOperationException("Only heads-up supported (for now)");
        }
    }



    public Set<Card> commonCommunity() {
        return commonCommunity;
    }

    public Set<Card> playerCommunity(int player) {
        if (! (0 <= player && player <= 1)) {
            throw new IllegalArgumentException("Must be 0 or 1");
        }

        return player == 0 ? dealeeCommunity : dealerCommunity;
    }

    public Set<Card> hole(int player) {
        if (! (0 <= player && player <= 1)) {
            throw new IllegalArgumentException("Must be 0 or 1");
        }

        return player == 0 ? dealeeHole : dealerHole;
    }
}

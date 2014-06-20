package ao.holdem.model.card;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class WinningCardSetTest
{
    @Test(expected = IllegalArgumentException.class)
    public void showdownRequired() {
        new WinningCardSet(new CardState(Community.PREFLOP, Collections.<Hole>emptyList()));
    }


    @Test
    public void commonBestCards() {
        WinningCardSet winningCardSet = new WinningCardSet(new CardState(
                new Community(
                        Card.ACE_OF_CLUBS, Card.ACE_OF_DIAMONDS, Card.ACE_OF_HEARTS,
                        Card.ACE_OF_SPADES, Card.KING_OF_CLUBS),
                Arrays.asList(
                        Hole.valueOf(Card.THREE_OF_CLUBS, Card.THREE_OF_DIAMONDS),
                        Hole.valueOf(Card.THREE_OF_HEARTS, Card.THREE_OF_SPADES))));

        assertThat(winningCardSet.commonCommunity()).containsOnly(
                Card.ACE_OF_CLUBS, Card.ACE_OF_DIAMONDS, Card.ACE_OF_HEARTS,
                Card.ACE_OF_SPADES, Card.KING_OF_CLUBS);

        assertThat(winningCardSet.playerCommunity(0)).isEmpty();
        assertThat(winningCardSet.playerCommunity(1)).isEmpty();

        assertThat(winningCardSet.hole(0)).isEmpty();
        assertThat(winningCardSet.hole(1)).isEmpty();
    }


    @Test
    public void dealeeBestCards() {
        WinningCardSet winningCardSet = new WinningCardSet(new CardState(
                new Community(
                        Card.ACE_OF_CLUBS, Card.ACE_OF_DIAMONDS, Card.ACE_OF_HEARTS,
                        Card.KING_OF_CLUBS, Card.KING_OF_DIAMONDS),
                Arrays.asList(
                        Hole.valueOf(Card.ACE_OF_SPADES, Card.THREE_OF_DIAMONDS),
                        Hole.valueOf(Card.THREE_OF_HEARTS, Card.THREE_OF_SPADES))));

        assertThat(winningCardSet.commonCommunity()).isEmpty();
        assertThat(winningCardSet.playerCommunity(1)).isEmpty();
        assertThat(winningCardSet.hole(1)).isEmpty();

        Set<Card> dealeeCommunity = winningCardSet.playerCommunity(0);

        assertThat(dealeeCommunity).hasSize(4);
        assertThat(dealeeCommunity).containsAll(Arrays.asList(
                Card.ACE_OF_CLUBS, Card.ACE_OF_DIAMONDS, Card.ACE_OF_HEARTS));

        assertTrue(dealeeCommunity.contains(Card.KING_OF_CLUBS) ||
                dealeeCommunity.contains(Card.KING_OF_DIAMONDS));

        assertThat(winningCardSet.hole(0)).containsOnly(Card.ACE_OF_SPADES);
    }


    @Test
    public void dealerBestCards() {
        WinningCardSet winningCardSet = new WinningCardSet(new CardState(
                new Community(
                        Card.ACE_OF_CLUBS, Card.JACK_OF_CLUBS, Card.TEN_OF_CLUBS,
                        Card.KING_OF_DIAMONDS, Card.KING_OF_HEARTS),
                Arrays.asList(
                        Hole.valueOf(Card.QUEEN_OF_SPADES, Card.TWO_OF_HEARTS),
                        Hole.valueOf(Card.SIX_OF_CLUBS, Card.THREE_OF_CLUBS))));

        assertThat(winningCardSet.commonCommunity()).isEmpty();
        assertThat(winningCardSet.playerCommunity(0)).isEmpty();
        assertThat(winningCardSet.hole(0)).isEmpty();

        Set<Card> dealeeCommunity = winningCardSet.playerCommunity(1);

        assertThat(dealeeCommunity).containsOnly(
                Card.ACE_OF_CLUBS, Card.JACK_OF_CLUBS, Card.TEN_OF_CLUBS);

        assertThat(winningCardSet.hole(1)).containsOnly(
                Card.SIX_OF_CLUBS, Card.THREE_OF_CLUBS);
    }


    @Test
    public void tieWithoutPlayerCommunity() {
        WinningCardSet winningCardSet = new WinningCardSet(new CardState(
                new Community(
                        Card.ACE_OF_CLUBS, Card.ACE_OF_DIAMONDS, Card.FIVE_OF_HEARTS,
                        Card.TWO_OF_CLUBS, Card.TWO_OF_HEARTS),
                Arrays.asList(
                        Hole.valueOf(Card.FOUR_OF_CLUBS, Card.FOUR_OF_DIAMONDS),
                        Hole.valueOf(Card.FOUR_OF_HEARTS, Card.FOUR_OF_SPADES))));

        assertThat(winningCardSet.commonCommunity()).containsOnly(
                Card.ACE_OF_CLUBS, Card.ACE_OF_DIAMONDS, Card.FIVE_OF_HEARTS);

        assertThat(winningCardSet.playerCommunity(0)).isEmpty();
        assertThat(winningCardSet.playerCommunity(1)).isEmpty();

        assertThat(winningCardSet.hole(0)).containsOnly(Card.FOUR_OF_CLUBS, Card.FOUR_OF_DIAMONDS);
        assertThat(winningCardSet.hole(1)).containsOnly(Card.FOUR_OF_HEARTS, Card.FOUR_OF_SPADES);
    }


    @Test
    public void mostCanonicalMostCommonSubsetChosen() {
        WinningCardSet winningCardSet = new WinningCardSet(new CardState(
                new Community(
                        Card.TWO_OF_CLUBS, Card.THREE_OF_CLUBS, Card.FOUR_OF_CLUBS,
                        Card.FIVE_OF_CLUBS, Card.TWO_OF_DIAMONDS),
                Arrays.asList(
                        Hole.valueOf(Card.THREE_OF_DIAMONDS, Card.SIX_OF_DIAMONDS),
                        Hole.valueOf(Card.EIGHT_OF_DIAMONDS, Card.SIX_OF_HEARTS))));

        assertThat(winningCardSet.commonCommunity()).containsOnly(
                Card.TWO_OF_CLUBS, Card.THREE_OF_CLUBS, Card.FOUR_OF_CLUBS, Card.FIVE_OF_CLUBS);

        assertThat(winningCardSet.playerCommunity(0)).isEmpty();
        assertThat(winningCardSet.playerCommunity(1)).isEmpty();

        assertThat(winningCardSet.hole(0)).containsOnly(Card.SIX_OF_DIAMONDS);
        assertThat(winningCardSet.hole(1)).containsOnly(Card.SIX_OF_HEARTS);
    }

    @Test
    public void someCanonicalMostCommonSubsetChosen() {
        WinningCardSet winningCardSet = new WinningCardSet(new CardState(
                new Community(
                        Card.EIGHT_OF_SPADES, Card.EIGHT_OF_CLUBS, Card.NINE_OF_DIAMONDS,
                        Card.QUEEN_OF_SPADES, Card.TEN_OF_DIAMONDS),
                Arrays.asList(
                        Hole.valueOf(Card.TWO_OF_HEARTS, Card.JACK_OF_DIAMONDS),
                        Hole.valueOf(Card.JACK_OF_CLUBS, Card.JACK_OF_HEARTS))));

        assertThat(winningCardSet.commonCommunity()).containsOnly(
                Card.EIGHT_OF_CLUBS, Card.NINE_OF_DIAMONDS, Card.TEN_OF_DIAMONDS, Card.QUEEN_OF_SPADES);

        assertThat(winningCardSet.playerCommunity(0)).isEmpty();
        assertThat(winningCardSet.playerCommunity(1)).isEmpty();

        assertThat(winningCardSet.hole(0)).containsOnly(Card.JACK_OF_DIAMONDS);
        assertThat(winningCardSet.hole(1)).containsOnly(Card.JACK_OF_CLUBS);
    }


//    @Test
//    public void randomLegal() {
//        Random random = new RandomAdaptor(new Well512a());
//        long prev = System.currentTimeMillis();
//
//        for (long i = 0; i < 1000000000000L; i++) {
//            CardState cardState = nextRandomCardState(random);
//            new WinningCardSet(cardState);
//
//            if (i % 1000000 == 0) {
//                long curr = System.currentTimeMillis();
//                System.out.println((curr - prev) + "\t" + i);
//                prev = curr;
//            }
//        }
//    }
//
////    private CardState nextRandomCardState(Random random) {
////        List<Card> deck = new ArrayList<>(Arrays.asList(Card.VALUES));
////        Collections.shuffle(deck, random);
////        return new CardState(
////                new Community(deck.get(0), deck.get(1), deck.get(2), deck.get(3), deck.get(4)),
////                Arrays.asList(
////                        Hole.valueOf(deck.get(5), deck.get(6)),
////                        Hole.valueOf(deck.get(7), deck.get(8))));
////    }
//
//    private CardState nextRandomCardState(Random random) {
//        Set<Card> selected = new LinkedHashSet<>();
////        Set<Card> selected = EnumSet.noneOf(Card.class);
//
//        while (selected.size() < 9) {
//            selected.add(Card.VALUES[ random.nextInt(Card.VALUES.length) ]);
//        }
//
//        Iterator<Card> i = selected.iterator();
//        return new CardState(
//                new Community(i.next(), i.next(), i.next(), i.next(), i.next()),
//                Arrays.asList(
//                        Hole.valueOf(i.next(), i.next()),
//                        Hole.valueOf(i.next(), i.next())));
//    }
}

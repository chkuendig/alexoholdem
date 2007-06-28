package ao.holdem.def.model.card.eval7.other;

import java.util.*;

/**
 * A set of distinct {@link Card2}s.
 * <p>This implementation is a wrapper on {@link ArrayList}<{@link Card2}>
 * that allows no duplicates.  A CardSet's iterator will provide elements
 * in FIFO order -- the order in which the elements were added -- if the
 * instance's shuffle method has not been invoked.
 * <p>It also provides a 52-card deck.
 * <p>Methods not otherwise documented forward to {@link ArrayList}<{@link Card2}> or perform
 * as specified by the {@link Set} interface.
 * @version 2006Dec10.0
 * @author Steve Brecher
 *
 */
public class CardSet implements Set<Card2> {

	private ArrayList<Card2> cards;

	private static final CardSet madeDeck = new CardSet(52);
	static {
		for (Card2.Suit suit : Card2.Suit.values())
			for (Card2.Rank rank : Card2.Rank.values())
				madeDeck.add(new Card2(rank, suit));
	}

	/**
	 * Return an ordered 52-card deck.
	 * @return a 52-card deck in order from clubs to spades and within each suit from deuce to Ace.
	 */
	public static CardSet freshDeck( ) {
		return new CardSet(madeDeck);
	}

	/**
	 * Return a shuffled 52-card deck.
	 * @return a shuffled 52-card deck.
	 */
	public static CardSet shuffledDeck() {
		CardSet result = new CardSet(madeDeck);
		Collections.shuffle(result.cards);
		return result;
	}

	public CardSet() {
		cards = new ArrayList<Card2>();
	}

	public CardSet(int initialCapacity) {
		cards = new ArrayList<Card2>(initialCapacity);
	}

	/**
	 * Copy constructor
	 */
	public CardSet(CardSet source) {
		cards = new ArrayList<Card2>(source.cards);
	}

	/**
	 * Returns <code>true</code> if this CardSet did not already contain the specified Card.
	 * @return <code>true</code> if this CardSet did not already contain the specified Card.
	 */
	public boolean add(Card2 c) {
		if (cards.contains(c))
			return false;
		return cards.add(c);
	}

	/**
	 * Returns <code>true</code> if this CardSet changed as a result of the call.
	 * @return <code>true</code> if this CardSet changed as a result of the call; <code>false</code>
	 * if all of the Cards in the specified Collection were already present in this CardSet.
	 */
	public boolean addAll(Collection<? extends Card2> coll) {
		boolean result = false;
		for (Card2 c : coll)
			result |= add(c);
		return result;
	}

	public void clear() {
		cards.clear();
	}

	public boolean contains(Object o) {
		return cards.contains(o);
	}

	public boolean containsAll(Collection<?> coll) {
		return cards.containsAll(coll);
	}

	@Override public boolean equals(Object that) {
		if (!(that instanceof Set) || ((Set)that).size() != cards.size())
			return false;
		for (Card2 c : cards)
			if (!((Set)that).contains(c))
				return false;
		return true;
	}

	@Override
	public int hashCode() {
		int result = 0;
		for (Card2 c : cards)
			result += c.hashCode();
		return result;
	}

	public boolean isEmpty() {
		return cards.isEmpty();
	}

	public Iterator<Card2> iterator() {
		return cards.iterator();
	}

	public boolean remove(Object o) {
		return cards.remove(o);
	}

	public boolean removeAll(Collection<?> coll) {
		return cards.removeAll(coll);
	}

	public boolean retainAll(Collection<?> coll) {
		return cards.retainAll(coll);
	}

	public void shuffle() {
		Collections.shuffle(cards);
	}

	public int size() {
		return cards.size();
	}

	public Object[] toArray() {
		return cards.toArray(new Card2[cards.size()]);
	}

	public <T> T[] toArray(T[] a) {
		return cards.toArray(a);
	}

	/**
	 * Returns a {@link String} containing a comma-space-separated list of cards.
	 * @return a {@link String} containing a comma-space-separated list of cards,
	 *			each the result of {@link Card2#toString()}.
	 */
	@Override
	public String toString() {
		return cards.toString();
	}
}


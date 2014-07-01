package ao.holdem.model.card;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertFalse;


public class CardStateTest
{
    @Test
    public void emptyCardStateIsValid() {
        CardState empty = new CardState(Community.PREFLOP, Collections.<Hole>emptyList());

        assertFalse(empty.community().hasFlop());
        assertFalse(empty.hasHole(0));
    }
}

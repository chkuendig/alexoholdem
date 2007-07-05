package ao.holdem.bots.util;

import ao.holdem.def.model.card.Card;
import ao.holdem.def.model.card.eval7.Eval7Fast;
import ao.holdem.def.model.cards.Hole;
import ao.holdem.def.model.cards.community.Community;
import ao.util.stats.Combiner;

/**
 *
 */
public class GeneralOddFinder implements OddFinder
{
    //--------------------------------------------------------------------
    public Odds compute(Hole      hole,
                        Community community,
                        int       activeOpponents)
    {
        Card unknown[] = new Card[52 - 2 - community.knownCount()];
        int  win       = 0;
        int  lose      = 0;
        int  split     = 0;

        int nextIndex = 0;
        for (Card card : Card.values())
        {
            if (!(hole.contains(card) ||
                  community.contains(card)))
            {
                unknown[ nextIndex++ ] = card;
            }
        }

        Card fullCommunity[] = new Card[5];
        System.arraycopy(community.known(), 0,
                         fullCommunity,     0,
                         community.knownCount());

        for (Card communityFiller[] :
                new Combiner<Card>(unknown, community.unknownCount()))
        {
            for (int i = 0; i < community.unknownCount(); i++)
            {
                fullCommunity[ 4 - i ] =
                        communityFiller[ communityFiller.length - i - 1 ];
            }

            
        }


        for (Card possibility[] :
                new Combiner<Card>(unknown, 2 * activeOpponents +
                                            community.unknownCount()))
        {
            Card oppHands[][] = new Card[activeOpponents][2];
            for (int i = 0; i < activeOpponents; i++)
            {
                oppHands[i][0] = possibility[i * 2    ];
                oppHands[i][1] = possibility[i * 2 + 1];
            }


            short myVal = Eval7Fast.valueOf(
                            hole.first(),     hole.second(),
                            fullCommunity[0], fullCommunity[1],
                            fullCommunity[2], fullCommunity[3],
                            fullCommunity[4]);
            short maxOppVal = -1;
            for (int i = 0; i < activeOpponents; i++)
            {
                short oppVal = Eval7Fast.valueOf(
                                oppHands[i][0],   oppHands[i][1],
                                fullCommunity[0], fullCommunity[1],
                                fullCommunity[2], fullCommunity[3],
                                fullCommunity[4]);
                if (maxOppVal < oppVal)
                {
                    maxOppVal = oppVal;
                }
            }

            if      (maxOppVal > myVal) { lose++;  }
            else if (maxOppVal < myVal) { win++;   }
            else                        { split++; }
        }

        return new Odds(win, lose, split);
    }
}

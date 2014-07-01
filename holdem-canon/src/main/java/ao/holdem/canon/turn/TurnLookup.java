package ao.holdem.canon.turn;

import ao.Infrastructure;
import ao.holdem.model.card.canon.CanonCard;
import ao.holdem.model.card.canon.CanonSuit;
import ao.holdem.canon.flop.Flop;
import ao.holdem.model.card.canon.hole.CanonHole;
import ao.holdem.canon.enumeration.HandEnum;
import ao.holdem.canon.enumeration.PermisiveFilter;
import ao.holdem.canon.enumeration.UniqueFilter;
import ao.holdem.model.card.Rank;
import ao.util.pass.Traverser;
import ao.util.persist.PersistentBytes;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Date: Sep 14, 2008
 * Time: 9:50:10 PM
 */
/*package-private*/ class TurnLookup
{
    //--------------------------------------------------------------------
    private static final Logger LOG    =
            Logger.getLogger(TurnLookup.class);


    //--------------------------------------------------------------------
    private static final String RAW_CASE_FILE = Infrastructure.path(
                                    "lookup/canon/turn.cases.cache");
    private static final int    CODED_OFFSET[][];

    static
    {
        TurnCase caseSets[][] =
                retrieveOrCalculateCaseSets();
        CODED_OFFSET = encodeOffsets(caseSets);
    }


    //--------------------------------------------------------------------
    private static TurnCase[][] retrieveOrCalculateCaseSets()
    {
        LOG.info("retrieveOrCalculateCaseSets");

        TurnCase[][] caseSets = retrieveCaseSets();
        if (caseSets == null)
        {
            caseSets = calculateCases();
            storeCaseSets(caseSets);
        }
        return caseSets;
    }

    private static TurnCase[][] retrieveCaseSets()
    {
        byte asBytes[] = PersistentBytes.retrieve(RAW_CASE_FILE);
        if (asBytes == null) return null;

        int          flatIndex    = 0;
        TurnCase caseSets[][] =
                new TurnCase
                        [ asBytes.length / Rank.VALUES.length ]
                        [ Rank.VALUES.length                  ];
        for (int i = 0; i < caseSets.length; i++)
        {
            for (int j = 0; j < Rank.VALUES.length; j++)
            {
                int ordinal = asBytes[flatIndex++];
                if (ordinal >= 0)
                {
                    caseSets[ i ][ j ] =
                            TurnCase.VALUES[ ordinal ];
                }
            }
        }
        return caseSets;
    }

    private static void storeCaseSets(TurnCase caseSets[][])
    {
        int  flatIndex = 0;
        byte asBytes[] = new byte[ caseSets.length * Rank.VALUES.length ];
        for (TurnCase[] caseSet : caseSets)
        {
            for (TurnCase aCaseSet : caseSet)
            {
                asBytes[flatIndex++] = (byte) (
                        (aCaseSet == null)
                        ? -1
                        : aCaseSet.ordinal());
            }
        }
        PersistentBytes.persist(asBytes, RAW_CASE_FILE);
    }


    //--------------------------------------------------------------------
    private static TurnCase[][] calculateCases()
    {
        final TurnCase caseSets[][] =
                new TurnCase[ Flop.CANONS      ]
                            [ /*Rank.VALUES.length*/ ];

        final int[]                prevFlop  = {-1};
        final List<Set<CanonSuit>> turnCases =
                new ArrayList<Set<CanonSuit>>( Rank.VALUES.length ){{
                    //noinspection ForLoopReplaceableByForEach
                    for (int i = 0; i < Rank.VALUES.length; i++) {
                        add( EnumSet.noneOf( CanonSuit.class ) );
                    }
                }};

        HandEnum.turns(
                new UniqueFilter<CanonHole>("%1$s"),
                new UniqueFilter<Flop>(),
                new PermisiveFilter<Turn>(),
                new Traverser<Turn>() {
            public void traverse(Turn t) {
                if (prevFlop[0] != t.flop().canonIndex())
                {
                    if (prevFlop[0] != -1) {
                        caseSets[ prevFlop[0] ] = drainBuffers(turnCases);
                    }

                    for (Set<CanonSuit> buff : turnCases) buff.clear();
                    prevFlop[0] = t.flop().canonIndex();
                }
                turnCases.get(t.turnCard().rank().ordinal())
                         .add(t.turnSuit());
            }});
        caseSets[ prevFlop[0] ] = drainBuffers(turnCases);

        return caseSets;
    }

    private static TurnCase[] drainBuffers(
            List<Set<CanonSuit>> turnCases)
    {
        TurnCase asArray[] =
                new TurnCase[ Rank.VALUES.length ];
        for (int rank = 0; rank < turnCases.size(); rank++)
        {
            Set<CanonSuit> buff = turnCases.get(rank);
            if (!buff.isEmpty()) {
                asArray[rank] = TurnCase.valueOf(buff);
            }
        }
        return asArray;
    }


    //--------------------------------------------------------------------
    private static int[][] encodeOffsets(TurnCase caseSets[][])
    {
        LOG.info("encodeOffsets");

        int offset           = 0;
        int codedOffsets[][] = new int[ caseSets.length   ]
                                      [ Rank.VALUES.length ];
        for (int i = 0; i < caseSets.length; i++)
        {
            for (int j = 0; j < Rank.VALUES.length; j++)
            {
                TurnCase caseSet = caseSets[ i ][ j ];
                if (caseSet == null)
                {
                    codedOffsets[ i ][ j ] = -1;
                }
                else
                {
                    codedOffsets[ i ][ j ] =
                            TurnUtil.encodeTurn(caseSet, offset);
                    offset += caseSet.size();
                }
            }
        }

        return codedOffsets;
    }


    //--------------------------------------------------------------------
    public static int canonIndex(
            int flopIndex, CanonCard turn)
    {
        int codedOffset = CODED_OFFSET[ flopIndex             ]
                                      [ turn.rank().ordinal() ];
        return TurnUtil.decodeTurnOffset(codedOffset) +
               TurnUtil.decodeTurnSet(codedOffset).index( turn.suit() );
    }
}

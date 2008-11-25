package ao.odds.agglom.impl;

import ao.holdem.model.card.Card;
import ao.holdem.model.card.Community;
import ao.holdem.model.card.Hole;
import static ao.holdem.model.card.Rank.THREE;
import static ao.holdem.model.card.Rank.TWO;
import static ao.holdem.model.card.Suit.CLUBS;
import static ao.holdem.model.card.Suit.SPADES;
import ao.odds.agglom.OddFinder;
import ao.odds.agglom.Odds;
import ao.odds.eval.eval7.Eval7Faster;
import static ao.util.data.Arr.swap;
import ao.util.persist.PersistentLongs;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.EnumSet;


/**
 * does NOT match numbers from:
 *  http://wizardofodds.com/holdem/2players.html
 * wtf?!?!
 */
public class PreciseHeadsUpOdds implements OddFinder
{
    //--------------------------------------------------------------------
    private static final Logger LOG =
            Logger.getLogger(PreciseHeadsUpOdds.class);

    /**private static final Odds[] hardOdds = new Odds[]{
            new Odds(694128063, 1282101494, 121342843),
            new Odds(711453440, 1263995344, 122123616),
            new Odds(732672291, 1242410576, 122489533),
            new Odds(730742786, 1248047597, 118782017),
            new Odds(743376257, 1240253676, 113942467),
            new Odds(790348964, 1198467986, 108755450),
            new Odds(838461663, 1156656186, 102454551),
            new Odds(892310673, 1108801845, 96459882),
            new Odds(948153923, 1058144226, 91274251),
            new Odds(1008970414, 1001879140, 86722846),
            new Odds(1074795095, 940058441, 82718864),
            new Odds(1164284001, 854728551, 78559848),
            new Odds(1035889822, 1021877238, 39805340),
            new Odds(613318625, 1355723830, 128529945),
            new Odds(631734409, 1336529218, 129308773),
            new Odds(654312693, 1313594570, 129665137),
            new Odds(651897304, 1319968310, 125706786),
            new Odds(665146081, 1311884399, 120541920),
            new Odds(714995931, 1267594076, 114982393),
            new Odds(765970787, 1223328146, 108273467),
            new Odds(823068840, 1172593597, 101909963),
            new Odds(882013792, 1119103759, 96454849),
            new Odds(946213445, 1059673784, 91685171),
            new Odds(1015702916, 994361217, 87508267),
            new Odds(1110608066, 903847893, 83116441),
            new Odds(749407203, 1225893915, 122271282),
            new Odds(771039857, 1203435009, 123097534),
            new Odds(769503449, 1208585765, 119483186),
            new Odds(782469104, 1200476022, 114627274),
            new Odds(803004465, 1185874183, 108693752),
            new Odds(855951949, 1138526088, 103094363),
            new Odds(909895095, 1090600453, 97076852),
            new Odds(965759044, 1039944977, 91868379),
            new Odds(1026518567, 983759701, 87294132),
            new Odds(1092208613, 922096479, 83267308),
            new Odds(1181668536, 836814184, 79089680),
            new Odds(1108341244, 953411342, 35819814),
            new Odds(672613963, 1295765244, 129193193),
            new Odds(695659494, 1271872234, 130040672),
            new Odds(693662773, 1277726314, 126183313),
            new Odds(707257588, 1269312307, 121002505),
            new Odds(728903246, 1253975431, 114693723),
            new Odds(785077590, 1203773415, 108721395),
            new Odds(842284099, 1152953414, 102334887),
            new Odds(901258949, 1099456682, 96856769),
            new Odds(965405538, 1040102775, 92064087),
            new Odds(1034758983, 974949238, 87864179),
            new Odds(1129654161, 884464630, 83453609),
            new Odds(808247748, 1166789139, 122535513),
            new Odds(807166360, 1170746176, 119659864),
            new Odds(820337671, 1162270840, 114963889),
            new Odds(841184813, 1147362116, 109025471),
            new Odds(868544377, 1126043589, 102984434),
            new Odds(927212359, 1072763176, 97596865),
            new Odds(983104243, 1022100069, 92368088),
            new Odds(1043815735, 965983128, 87773537),
            new Odds(1109381784, 904464207, 83726409),
            new Odds(1198506708, 819532598, 79533094),
            new Odds(1180021110, 885403592, 32147698),
            new Odds(735719749, 1232622607, 129230044),
            new Odds(734213031, 1237190410, 126168959),
            new Odds(748030580, 1228381882, 121159938),
            new Odds(770001459, 1212723838, 114847103),
            new Odds(798877137, 1190239950, 108455313),
            new Odds(861166500, 1133678575, 102727325),
            new Odds(920178475, 1080165166, 97228759),
            new Odds(984280928, 1020875843, 92415629),
            new Odds(1053508976, 955868151, 88195273),
            new Odds(1148061584, 865741863, 83768953),
            new Odds(846332445, 1134396836, 116843119),
            new Odds(859564300, 1124884696, 113123404),
            new Odds(880780296, 1109625751, 107166353),
            new Odds(908515397, 1087988954, 101068049),
            new Odds(942628592, 1059410559, 95533249),
            new Odds(1003078936, 1003630618, 90862846),
            new Odds(1063751334, 947570537, 86250529),
            new Odds(1129204024, 886182741, 82185635),
            new Odds(1217936943, 801655845, 77979612),
            new Odds(1250992922, 817847556, 28731922),
            new Odds(776369338, 1198220417, 122982645),
            new Odds(790253778, 1188290423, 119028199),
            new Odds(812626207, 1172253905, 112692288),
            new Odds(841896769, 1149436843, 106238788),
            new Odds(877990854, 1119171845, 100409701),
            new Odds(941907501, 1060173322, 95491577),
            new Odds(1005974746, 1000937099, 90660555),
            new Odds(1075088026, 936062067, 86422307),
            new Odds(1169234974, 846354213, 81983213),
            new Odds(898379280, 1092540371, 106652749),
            new Odds(919124997, 1076725291, 101722112),
            new Odds(947078416, 1054965342, 95528642),
            new Odds(981668027, 1026108216, 89796157),
            new Odds(1018879951, 993461269, 85231180),
            new Odds(1084009562, 932457169, 81105669),
            new Odds(1149567662, 870979191, 77025547),
            new Odds(1220344506, 804780676, 72447218),
            new Odds(1315178390, 757863966, 24530044),
            new Odds(831767413, 1153839298, 111965689),
            new Odds(853640324, 1137194278, 106737798),
            new Odds(883149152, 1114245195, 100178053),
            new Odds(919746160, 1083692281, 94133959),
            new Odds(958874577, 1049310444, 89387379),
            new Odds(1027737078, 984776601, 85058721),
            new Odds(1096969965, 919797298, 80805137),
            new Odds(1171920110, 849630112, 76022178),
            new Odds(958257237, 1044830752, 94484411),
            new Odds(985655348, 1022667806, 89249246),
            new Odds(1020494848, 993686298, 83391254),
            new Odds(1058318785, 960790848, 78462767),
            new Odds(1101718156, 921231985, 74622259),
            new Odds(1171417014, 855198011, 70957375),
            new Odds(1245678450, 784885484, 67008466),
            new Odds(1378636878, 697512212, 21423310),
            new Odds(895522755, 1103148347, 98901298),
            new Odds(924443362, 1079755744, 93373294),
            new Odds(961312472, 1049069216, 87190712),
            new Odds(1001090143, 1014433489, 82048768),
            new Odds(1046780178, 972707935, 78084287),
            new Odds(1120467131, 902850280, 74254989),
            new Odds(1199171976, 828271026, 70129398),
            new Odds(1024795719, 991202871, 81573810),
            new Odds(1059465943, 961535711, 76570746),
            new Odds(1097274797, 928812709, 71484894),
            new Odds(1141436364, 888983752, 67152284),
            new Odds(1191219968, 842504852, 63847580),
            new Odds(1269195105, 768134954, 60242341),
            new Odds(1441396848, 637479762, 18695790),
            new Odds(966316620, 1046148851, 85106929),
            new Odds(1003019374, 1014712347, 79840679),
            new Odds(1042779957, 980265461, 74526982),
            new Odds(1089280295, 938263805, 70028300),
            new Odds(1141742922, 889187966, 66641512),
            new Odds(1224440175, 810270096, 62862129),
            new Odds(1098642842, 929682183, 69247375),
            new Odds(1135038051, 897489212, 65045137),
            new Odds(1179313858, 857784464, 60474078),
            new Odds(1229977429, 810946418, 56648553),
            new Odds(1290209940, 754019509, 53342951),
            new Odds(1503238936, 577905278, 16428186),
            new Odds(1044920282, 980664407, 71987711),
            new Odds(1083173398, 946780981, 67618021),
            new Odds(1129794528, 904916521, 62861351),
            new Odds(1183191163, 855469612, 58911625),
            new Odds(1247001366, 795066539, 55504495),
            new Odds(1177887186, 862082680, 57602534),
            new Odds(1220171285, 822993463, 54407652),
            new Odds(1270857555, 776310470, 50404375),
            new Odds(1331725962, 719134692, 46711746),
            new Odds(1566053110, 516772724, 14746566),
            new Odds(1129039428, 908890685, 59642287),
            new Odds(1173537003, 867678116, 56357281),
            new Odds(1226959959, 818398367, 52214074),
            new Odds(1291435142, 757743309, 48393949),
            new Odds(1239054394, 808665883, 49852123),
            new Odds(1289515286, 762297170, 45759944),
            new Odds(1350786717, 705041543, 41744140),
            new Odds(1618339604, 465955440, 13277356),
            new Odds(1193649409, 852387398, 51535593),
            new Odds(1246822903, 803451257, 47298240),
            new Odds(1311720573, 742722264, 43129563),
            new Odds(1309061790, 746895468, 41615142),
            new Odds(1370002117, 690016869, 37553414),
            new Odds(1670338644, 414934680, 12299076),
            new Odds(1267610255, 787022855, 42939290),
            new Odds(1332142795, 726706236, 38723369),
            new Odds(1389004215, 673957209, 34610976),
            new Odds(1722470558, 363424882, 11676960),
            new Odds(1352291878, 709592683, 35687839),
            new Odds(1781508418, 304661670, 11402312)
    };*/


    //--------------------------------------------------------------------
    private static final int HOLE_A = 51 - 1,
                             HOLE_B = 51,

                             FLOP_A = 51 - 4,
                             FLOP_B = 51 - 3,
                             FLOP_C = 51 - 2,

                             TURN   = 51 - 5,

                             RIVER  = 51 - 6,

//                             OPP_A  = 51 - 8,
                             OPP_B  = 51 - 7;


    //--------------------------------------------------------------------
    private static final String HOLE_DIR = "lookup/odds/";
    static {
        if (new File(HOLE_DIR).mkdirs())
            LOG.info("created " + HOLE_DIR);
    }

    private static final String HOLE_WINS   =
            HOLE_DIR + "wins.preflop.cache";
    private static final String HOLE_LOSES  =
            HOLE_DIR + "losses.preflop.cache";
    private static final String HOLE_SPLITS =
            HOLE_DIR + "splits.preflop.cache";
    private static final Odds   PREFLOP[] = retrieveOrComputeOdds();


    private static Odds[] retrieveOrComputeOdds()
    {
        Odds[] cache = retrieveOdds();
        if (cache == null)
        {
            cache = computeOdds();
            persistOdds( cache );
        }
        return cache;
    }

    private static Odds[] retrieveOdds()
    {
        Odds cache[] = new Odds[ Hole.CANONICAL_COUNT ];

        long wins[]   = PersistentLongs.retrieve(HOLE_WINS  );
        long loses[]  = PersistentLongs.retrieve(HOLE_LOSES );
        long splits[] = PersistentLongs.retrieve(HOLE_SPLITS);

        if (wins == null || loses == null || splits == null) return null;

        for (int i = 0; i < wins.length; i++)
        {
            cache[ i ] = new Odds(wins[i], loses[i], splits[i]);
        }

        return cache;
    }

    private static Odds[] computeOdds()
    {
        Odds cache[] = new Odds[ Hole.CANONICAL_COUNT ];

//        int tempI = 0;
        for (Card a : Card.VALUES)
        {
            for (Card b : Card.VALUES)
            {
                if (a.ordinal() >= b.ordinal()) continue;
                Hole hole = Hole.valueOf(a, b);

                if (cache[ hole.canonIndex() ] != null) continue;
//                Odds odds = hardOdds[ tempI++ ];

                Odds odds = new PreciseHeadsUpOdds().compute(
                                    hole, Community.PREFLOP);
                cache[ hole.canonIndex() ] = odds;
                LOG.info("pre-computed Odds for " + hole + "\t" + odds);
            }
        }

        return cache;
    }

    private static void persistOdds(Odds cache[])
    {
        long wins[]   = new long[ Hole.CANONICAL_COUNT ];
        long loses[]  = new long[ Hole.CANONICAL_COUNT ];
        long splits[] = new long[ Hole.CANONICAL_COUNT ];

        for (int i = 0; i < cache.length; i++)
        {
            wins  [ i ] = cache[ i ].winOdds();
            loses [ i ] = cache[ i ].loseOdds();
            splits[ i ] = cache[ i ].splitOdds();
        }

        PersistentLongs.persist(wins,   HOLE_WINS  );
        PersistentLongs.persist(loses,  HOLE_LOSES );
        PersistentLongs.persist(splits, HOLE_SPLITS);
    }


    //--------------------------------------------------------------------
    public Odds compute(Hole      hole,
                        Community community,
                        int       activeOpponents)
    {
        assert activeOpponents == 1 : "must be heads up";
        return compute(hole, community);
    }

    public Odds compute(Hole      hole,
                        Community community)
    {
        if (community.equals( Community.PREFLOP ) &&
                PREFLOP != null) // called for pre-calculation
        {
            return PREFLOP[ hole.canonIndex() ];
        }

        Card cards[] = initKnownCardsToEnd(hole, community);
        return rollOutCommunity(
                cards,
                community.knownCount());
    }


    //--------------------------------------------------------------------
    private static Odds rollOutCommunity(
            Card cards[],
            int  knownCount)
    {
        return   knownCount == 0
               ? rollOutFlopTurnRiver(cards)
               : knownCount == 3
               ? rollOutTurnRiver(cards)
               : knownCount == 4
               ? rollOutRiver(cards)
               : null;
    }


    //--------------------------------------------------------------------
    private static Odds rollOutFlopTurnRiver(Card cards[])
    {
        Odds odds = new Odds();
        for (int flopIndexC = 4; flopIndexC <= FLOP_C; flopIndexC++)
        {
            Card flopC = cards[ flopIndexC ];
            swap(cards, flopIndexC, FLOP_C);

            for (int flopIndexB = 3;
                     flopIndexB < flopIndexC; flopIndexB++)
            {
                Card flopB = cards[ flopIndexB ];
                swap(cards, flopIndexB, FLOP_B);

                for (int flopIndexA = 2;
                         flopIndexA < flopIndexB; flopIndexA++)
                {
                    Card flopA = cards[ flopIndexA ];
                    swap(cards, flopIndexA, FLOP_A);

                    for (int turnIndex = 1;
                             turnIndex < flopIndexA; turnIndex++)
                    {
                        Card turn = cards[ turnIndex ];
                        swap(cards, turnIndex, TURN);

                        for (int riverIndex = 0;
                                 riverIndex < turnIndex; riverIndex++)
                        {
                            Card river = cards[ riverIndex ];
                            swap(cards, riverIndex, RIVER);

                            int   shortcut =
                            Eval7Faster.shortcutFor(
                                    flopA, flopB, flopC,
                                    turn, river);
                            short thisVal  =
                                    Eval7Faster.fastValueOf(
                                            shortcut,
                                            cards[ HOLE_A ],
                                            cards[ HOLE_B ]);

                            odds = odds.plus(
                                    rollOutOpp(cards, shortcut, thisVal));

                            swap(cards, riverIndex, RIVER);
                        }

                        swap(cards, turnIndex, TURN);
                    }

                    swap(cards, flopIndexA, FLOP_A);
                }

                swap(cards, flopIndexB, FLOP_B);
            }

            swap(cards, flopIndexC, FLOP_C);
        }
        return odds;
    }


    //--------------------------------------------------------------------
    private static Odds rollOutTurnRiver(Card cards[])
    {
        Odds odds = new Odds();
        for (int turnIndex =  1;
                 turnIndex <= TURN;
                 turnIndex++)
        {
            Card turnCard = cards[ turnIndex ];
            swap(cards, turnIndex, TURN);
            for (int riverIndex = 0;
                     riverIndex < turnIndex;
                     riverIndex++)
            {
                Card riverCard = cards[ riverIndex ];
                swap(cards, riverIndex, RIVER);

                int   shortcut =
                    Eval7Faster.shortcutFor(
                            cards[ FLOP_A ],
                            cards[ FLOP_B ],
                            cards[ FLOP_C ],
                            turnCard, riverCard);
                short thisVal  =
                    Eval7Faster.fastValueOf(
                            shortcut,
                            cards[ HOLE_A ], cards[ HOLE_B ]);

                odds = odds.plus(
                        rollOutOpp(cards, shortcut, thisVal));

                swap(cards, riverIndex, RIVER);
            }
            swap(cards, turnIndex, TURN);
        }
        return odds;
    }


    //--------------------------------------------------------------------
    private static Odds rollOutRiver(
            Card  cards[])
    {
        Odds odds = new Odds();
        for (int riverIndex = 0;
                 riverIndex <= RIVER;
                 riverIndex++)
        {
            Card riverCard = cards[ riverIndex ];
            swap(cards, riverIndex, RIVER);

            int   shortcut =
                Eval7Faster.shortcutFor(
                        cards[ FLOP_A ],
                        cards[ FLOP_B ],
                        cards[ FLOP_C ],
                        cards[ TURN ],
                        riverCard);
            short thisVal  =
                Eval7Faster.fastValueOf(
                        shortcut,
                        cards[ HOLE_A ], cards[ HOLE_B ]);

            odds = odds.plus(
                    rollOutOpp(cards, shortcut, thisVal));

            swap(cards, riverIndex, RIVER);
        }
        return odds;
    }


    //--------------------------------------------------------------------
    private static Odds rollOutOpp(
            Card  cards[],
            int   shortcut,
            short thisVal)
    {
        Odds odds = new Odds();
        for (int oppIndexB = 1; oppIndexB <= OPP_B; oppIndexB++)
        {
            for (int oppIndexA = 0;
                     oppIndexA < oppIndexB;
                     oppIndexA++)
            {
                short thatVal =
                        Eval7Faster.fastValueOf(
                                shortcut,
                                cards[ oppIndexA ], cards[ oppIndexB ]);
                odds = odds.plus(
                        Odds.valueOf(thisVal, thatVal));
            }
        }
        return odds;
    }


    //--------------------------------------------------------------------
    public static Card[] initKnownCardsToEnd(
            Hole hole, Community community)
    {
        EnumSet<Card> known = asSet(hole, community);

        int  index   = 0;
        Card cards[] = new Card[ Card.VALUES.length ];
        for (Card card : Card.VALUES)
        {
            if (! known.contains(card))
            {
                cards[ index++ ] = card;
            }
        }

        cards[ HOLE_A ] = hole.a();
        cards[ HOLE_B ] = hole.b();
        switch (community.knownCount())
        {
            case 5:
                cards[ RIVER  ] = community.river();

            case 4:
                cards[ TURN   ] = community.turn();

            case 3:
                cards[ FLOP_A ] = community.flopA();
                cards[ FLOP_B ] = community.flopB();
                cards[ FLOP_C ] = community.flopC();
        }
        return cards;
    }

    private static EnumSet<Card> asSet(
            Hole hole, Community community)
    {
        EnumSet<Card> seq = EnumSet.of(hole.a(), hole.b());
        if (community.hasRiver()) {
            seq.add( community.river() );
        }
        if (community.hasTurn()) {
            seq.add( community.turn() );
        }
        if (community.hasFlop()) {
            seq.add( community.flopA() );
            seq.add( community.flopB() );
            seq.add( community.flopC() );
        }
        return seq;
    }

    
    //--------------------------------------------------------------------
    public static void main(String args[])
    {
        OddFinder oddFinder = new PreciseHeadsUpOdds();

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Odds oddsA =
                oddFinder.compute(
                    Hole.valueOf(Card.valueOf(TWO,   CLUBS),
                                 Card.valueOf(THREE, CLUBS)),
                    new Community(),
                    1);

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Odds oddsB =
                oddFinder.compute(
                    Hole.valueOf(Card.valueOf(TWO,   SPADES),
                                 Card.valueOf(THREE, SPADES)),
                    new Community(),
                    1);

        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println( oddsA );
        System.out.println( oddsB );
    }
}

package ao.irc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * IRC hand reader.
 * See http://games.cs.ualberta.ca/poker/IRC/
 *
 * HAND INFORMATION (in hdb file)
    ------------------------------
    timestamp      hand #     #players/starting potsize
             dealer    #play flop    turn    river  showdn     board
    766303976   1   455  8  6/600   6/1200  6/1800  3/2400  3s Jc Qd 5c Ah

    ROSTER INFORMATION (in hroster file)
    ------------------------------------
    766303976  8  Marzon spiney doublebag neoncap maurer andrea zorak justin

    PLAYER INFORMATION (in pdb.* files)
    -----------------------------------
    player             #play prflop    turn         bankroll    winnings
              timestamp    pos   flop       river          action     cards
    Marzon    766303976  8  1 Bc  bc    kc    kf      12653  300    0
    ...
 *
 */
public class IrcReader
{
    //--------------------------------------------------------------------
    private final List<IrcHand>   hands;
    private final List<IrcRoster> roster;
    private final List<IrcAction> players;


    //--------------------------------------------------------------------
    public IrcReader(File ircDir)
    {
        File handFile   = new File(ircDir, "hdb");
        File rosterFile = new File(ircDir, "hroster");
        File playerDir  = new File(ircDir, "pdb");

        try
        {
            hands   = readHands(handFile);
            roster  = readRoster(rosterFile);
            players = readPlays(playerDir);
        }
        catch (IOException e)
        {
            throw new Error( e );
        }
    }


    //--------------------------------------------------------------------
    private List<IrcHand> readHands(File handFile)
            throws IOException
    {
        BufferedReader handReader =
                new BufferedReader(
                        new FileReader(handFile));

        List<IrcHand> handList = new ArrayList<IrcHand>();

        String handLine;
        while ((handLine = handReader.readLine()) != null)
        {
            IrcHand hand = IrcHand.fromLine(handLine);
            if (hand != null)
            {
                handList.add( hand );
            }
        }

        return handList;
    }


    private List<IrcRoster> readRoster(File rosterFile)
            throws IOException
    {
        BufferedReader handReader =
                new BufferedReader(
                        new FileReader(rosterFile));

        List<IrcRoster> rosterList = new ArrayList<IrcRoster>();

        String handLine;
        while ((handLine = handReader.readLine()) != null)
        {
            IrcRoster ircRoster = IrcRoster.fromLine(handLine);
            if (ircRoster != null)
            {
                rosterList.add( ircRoster );
            }
        }

        return rosterList;
    }

    private List<IrcAction> readPlays(File playerDir)
            throws IOException
    {
        List<IrcAction> playerList = new ArrayList<IrcAction>();

        for (File playerFile : playerDir.listFiles())
        {
            BufferedReader handReader =
                    new BufferedReader(
                            new FileReader(playerFile));
            String handLine;
            while ((handLine = handReader.readLine()) != null)
            {
                IrcAction ircAction = IrcAction.fromLine(handLine);
                if (ircAction != null)
                {
                    playerList.add( ircAction );
                }
            }
        }

        return playerList;
    }


    //--------------------------------------------------------------------
    public List<IrcHand> hands()
    {
        return hands;
    }

    public List<IrcRoster> roster()
    {
        return roster;
    }

    public List<IrcAction> actions()
    {
        return players;
    }
}

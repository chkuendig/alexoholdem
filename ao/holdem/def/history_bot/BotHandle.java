package ao.holdem.def.history_bot;

import ao.holdem.history.PlayerHandle;

/**
 *
 */
public class BotHandle
{
    //--------------------------------------------------------------------
    private final PlayerHandle HANDLE;
    private final HistoryBot   BOT;


    //--------------------------------------------------------------------
//    public BotHandle(String playerName, HistoryBot bot)
//    {
//        HANDLE = PlayerHandleLookup.lookup(playerName);
//        BOT    = bot;
//    }

    public BotHandle(PlayerHandle handle, HistoryBot bot)
    {
        HANDLE = handle;
        BOT    = bot;
    }


    //--------------------------------------------------------------------
    public PlayerHandle handle()
    {
        return HANDLE;
    }

    public HistoryBot bot()
    {
        return BOT;
    }
}

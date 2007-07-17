package ao.holdem.def.bot.history;

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

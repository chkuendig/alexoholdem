package ao.holdem.def.bot;

/**
 *
 */
public interface BotFactory
{
    //--------------------------------------------------------------------
    public Bot newInstance();


    //--------------------------------------------------------------------
    public static class Impl
    {
        public static BotFactory fromClass(
                final Class<? extends Bot> botClass)
        {
            return new BotFactory() {
                public Bot newInstance() {
                    try
                    {
                        return botClass.newInstance();
                    }
                    catch (Exception e)
                    {
                        throw new Error( e );
                    }
                }
            };
        }
    }
}

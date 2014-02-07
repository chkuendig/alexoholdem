package ao.holdem.net;

import ao.holdem.net.msg.Inbound;
import ao.holdem.net.msg.Outbound;
import ao.util.async.Publisher;

import java.util.HashMap;
import java.util.Map;

/**
 * User: alex
 * Date: 6-May-2009
 * Time: 2:52:15 PM
 */
public class Protocol
{
    //--------------------------------------------------------------------
    private final Publisher<InboundListener>
            PUBLISH = new Publisher<InboundListener>();

    private final Map<Integer, String> INSTANCE_ADDRESSES =
            new HashMap<Integer, String>();


    //--------------------------------------------------------------------
    public Protocol()
    {

    }


    //--------------------------------------------------------------------
    public void send(Outbound in)
    {

    }


    //--------------------------------------------------------------------
    public void listen(InboundListener subscriber)
    {
        PUBLISH.subscribe( subscriber );

        //Outbound
    }


    //--------------------------------------------------------------------
    public static interface InboundListener
    {
        public void handle(Inbound msg);
    }


}

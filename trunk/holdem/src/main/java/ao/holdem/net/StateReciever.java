package ao.holdem.net;

import gnu.cajo.utils.Multicast;

/**
 * 
 */
public class StateReciever
{
    final static Object object = new StateReciever();

    public Object multicast(Multicast m)
    { // multicast listener callback

        try { System.out.println(m.item.invoke("doit", "hello!")); }
        catch (Exception x)  { x.printStackTrace(); }
        synchronized(this) { notify(); } // end program
        return this; // stop listening, else return null
    }

    public static void main(String args[]) throws Exception
    {
        Multicast m = new Multicast();
        m.listen(object);
        synchronized(object) { object.wait(); }
    }
}

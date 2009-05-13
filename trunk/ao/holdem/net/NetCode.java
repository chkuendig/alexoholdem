package ao.holdem.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class NetCode
{
    //--------------------------------------------------------------------
    private static final int  IN_PORT = 7911;
    private static final int OUT_PORT = 45067;


    //--------------------------------------------------------------------
    public static void main(String args[]) throws Exception
    {
        DatagramSocket readSocket  = new DatagramSocket( IN_PORT);
        DatagramSocket writeSocket = new DatagramSocket(OUT_PORT);

        //for (int i = 0; i < 100000; i++)
        try
        {
            Pattern ipPat = Pattern.compile(
                    ".*#IP=(.+?)#.*");

            while (true)
            {
                System.out.println("awaiting input...");
                String rec = recieveString(readSocket);
                System.out.println("recieved: " + rec);

                Matcher matcher = ipPat.matcher(rec);
                if (matcher.matches()) {
                    String addr = matcher.group(1);
                    System.out.println("Sending to " + addr);

                    send(writeSocket, addr, "?INSTANCEID=0#ACTION=0");
                } else {
                    System.out.println(
                            "unable to get address from: " + rec);
                }
            }
        }
        finally
        {
            readSocket.close();
            writeSocket.close();
        }

////        System.out.println("recieved: " +
////                           new OverTheWireState(recieveString(readSocket)));
//
//        System.out.println("sending response.");
//        send( writeSocket );
//
//        System.out.println("recieved: " + recieveString(readSocket));
////        System.out.println("recieved: " +
////                           new OverTheWireState(recieveString(readSocket)));
//
//        System.out.println("sending response.");
//        send( writeSocket );


    }

    private static void send(
            DatagramSocket to,
            String         addressName,
            String         message) throws IOException
    {
        byte[] buf = message.getBytes();

        InetAddress    address =
                InetAddress.getByName(addressName); // "127.0.0.1"
        DatagramPacket packet  =
                new DatagramPacket(buf, buf.length,
                                   address, OUT_PORT);
        to.send(packet);
    }

    private static String recieveString(
            DatagramSocket from) throws IOException
    {
        byte readBuffer[] = new byte[2048];
        DatagramPacket packet =
                new DatagramPacket(readBuffer, readBuffer.length);
        from.receive( packet );
        return new String( packet.getData() ).trim();
    }
}

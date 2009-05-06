package ao.holdem.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 */
public class NetCode
{
    public static void main(String args[]) throws Exception
    {
        DatagramSocket readSocket  = new DatagramSocket(9978);
        DatagramSocket writeSocket = new DatagramSocket(9979);

        //for (int i = 0; i < 100000; i++)
        try
        {
            while (true)
            {
                System.out.println("recieved: " + recieveString(readSocket));

    //            System.out.println("sending response.");
    //            send( writeSocket );

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
            DatagramSocket to) throws IOException
    {
        byte[] buf = "Hello World".getBytes();

        InetAddress    address = InetAddress.getByName("127.0.0.1");
        DatagramPacket packet  =
                new DatagramPacket(buf, buf.length,
                                   address, 4445);
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

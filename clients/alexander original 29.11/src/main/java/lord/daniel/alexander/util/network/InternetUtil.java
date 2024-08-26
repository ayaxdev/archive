package lord.daniel.alexander.util.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Written by Daniel. on 17/11/2023
 * Please don't use this code without my permission.
 *
 * @author Daniel.
 */
public class InternetUtil {

    public static boolean isInternetAvailable() throws IOException
    {
        return isHostAvailable("example.com") || isHostAvailable("github.com")
                || isHostAvailable("google.com") || isHostAvailable("amazon.com")
                || isHostAvailable("facebook.com")|| isHostAvailable("apple.com");
    }

    private static boolean isHostAvailable(String hostName) throws IOException
    {
        try(Socket socket = new Socket())
        {
            int port = 80;
            InetSocketAddress socketAddress = new InetSocketAddress(hostName, port);
            socket.connect(socketAddress, 3000);

            return true;
        }
        catch(UnknownHostException unknownHost)
        {
            return false;
        }
    }

}

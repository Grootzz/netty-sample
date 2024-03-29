package bio.sample;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

/**
 * NIO Client
 *
 * @author noodle
 */
public class NIOClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        OutputStream out = socket.getOutputStream();
        String s = new Date() + ": Hello World";
        System.out.println(s);
        out.write(s.getBytes());
        out.close();
    }
}

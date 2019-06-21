package bio.sample;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO Server
 *
 * @author Noodle
 */
public class NIOServer {
    public static void main(String[] args) throws IOException {

        Selector selector = Selector.open();

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        ServerSocket serverSocket = serverSocketChannel.socket();
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", 8888);
        serverSocket.bind(address);

        while (true) {
            selector.select(); // 非阻塞地监听 socket 事件

            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = keys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isAcceptable()) {

                    // 服务器会为每个新连接创建一个 SocketChannel
                    ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
                    SocketChannel channel = ssChannel.accept();
                    channel.configureBlocking(false);

                    // 这个新连接主要用于从客户端读取数据
                    channel.register(selector, SelectionKey.OP_READ);

                } else if (key.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    System.out.println(readDataFromSocketChannel(socketChannel));
                    socketChannel.close();
                }
                keyIterator.remove();
            }

        }
    }

    private static String readDataFromSocketChannel(SocketChannel socketChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        StringBuilder data = new StringBuilder();

        while (true) {
            buffer.clear();
            int n = socketChannel.read(buffer);
            if (n == -1)
                break;

            buffer.flip();
            int limit = buffer.limit();
            char[] chars = new char[limit];
            for (int i = 0; i < limit; i++) {
                chars[i] = (char) buffer.get(i);
            }
            data.append(chars);
            buffer.clear();
        }

        return data.toString();
    }
}

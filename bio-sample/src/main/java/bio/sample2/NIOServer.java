package bio.sample2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * NIOServer
 * 分离IO事件,
 *
 * @author noodle
 */
public class NIOServer {
    public static void main(String[] args) throws IOException {
        // 1. serverSelector负责轮询是否有新的连接，服务端监测到新的连接之后，不再创建一个新的线程，
        // 而是直接将新连接绑定到clientSelector上，这样就不用 IO 模型中 1w 个 while 循环在死等
        Selector serverSelector = Selector.open();

        // 2. clientSelector负责轮询连接是否有数据可读
        Selector clientSelector = Selector.open();

        // 事件监测
        new Thread(() -> {
            // 对应IO编程中服务端启动
            try {
                ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.socket().bind(new InetSocketAddress(3333));
                serverSocketChannel.configureBlocking(false);
                serverSocketChannel.register(serverSelector, SelectionKey.OP_ACCEPT);

                while (true) {

                    // 监测是否有新的连接，这里的1指的是阻塞的时间为 1ms
                    if (serverSelector.select(1) > 0) {

                        Set<SelectionKey> keys = serverSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = keys.iterator();

                        while (keyIterator.hasNext()) {
                            System.out.println(new Date() + ": detect event.");
                            SelectionKey key = keyIterator.next();

                            if (key.isAcceptable()) {
                                // (1) 每来一个新连接，不需要创建一个线程，而是直接注册到clientSelector
                                SocketChannel accept = ((ServerSocketChannel) key.channel()).accept();
                                accept.configureBlocking(false);
                                accept.register(clientSelector, SelectionKey.OP_READ);
                            }
                            keyIterator.remove();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // 事件处理
        new Thread(() -> {
            // (2) 批量轮询是否有哪些连接有数据可读，这里的1指的是阻塞的时间为 1ms
            try {
                while (true) {
                    if (clientSelector.select(1) > 0) {
                        System.out.println(new Date() + ": detect event arrive.");
                        Set<SelectionKey> keys = clientSelector.selectedKeys();
                        Iterator<SelectionKey> keyIterator = keys.iterator();

                        while (keyIterator.hasNext()) {

                            SelectionKey key = keyIterator.next();

                            if (key.isReadable()) {
                                SocketChannel clientChannel = (SocketChannel) key.channel();
                                ByteBuffer buffer = ByteBuffer.allocate(1024);
                                // (3) 面向 Buffer
                                clientChannel.read(buffer);
                                buffer.flip();
                                System.out.println(Charset.defaultCharset().newDecoder().decode(buffer).toString());
                            }

                            keyIterator.remove();
                            key.interestOps(SelectionKey.OP_READ);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();

    }
}

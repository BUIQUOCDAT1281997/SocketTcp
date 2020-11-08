import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class NonblockingServer {
    public static void main(String[] args)
            throws Exception {
        Set<SocketChannel> socketChannels = new HashSet<>();
        InetAddress host = InetAddress.getByName("localhost");
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel =
                ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(host, 1234));
        serverSocketChannel.register(selector, SelectionKey.
                OP_ACCEPT);
        SelectionKey key;
        while (true) {
            if (selector.select() <= 0)
                continue;
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = serverSocketChannel.accept();
                    socketChannels.add(sc);
                    sc.configureBlocking(false);
                    sc.register(selector, SelectionKey.
                            OP_READ);
                    System.out.println("Connection Accepted: "
                            + sc.getLocalAddress() + "\n");
                }
                if (key.isReadable()) {
                    SocketChannel sc = (SocketChannel) key.channel();
                    ByteBuffer bb = ByteBuffer.allocate(1024);
                    sc.read(bb);
                    String result = new String(bb.array()).trim();
                    System.out.println(getMessageDescription() + result);
                    if (result.length() <= 0) {
                        socketChannels.remove(sc);
                        sc.close();
                        System.out.println("Connection closed...");
                        System.out.println(
                                "Server will keep running. " +
                                        "Try running another client to " +
                                        "re-establish connection");
                    } else
                        for (SocketChannel sc1 : socketChannels) {
                            if (sc1 != sc) {
                                sc1.write(bb);
                            }
                        }
                }
            }
        }
    }

    private static String getMessageDescription() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        return "<" + dateTimeFormatter.format(now) + ">";
    }
}
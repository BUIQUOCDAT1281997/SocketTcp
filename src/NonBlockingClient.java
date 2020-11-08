import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Set;

public class NonBlockingClient {
    private static BufferedReader input = null;
    public static void main(String[] args) throws Exception {
        InetSocketAddress addr = new InetSocketAddress(
                InetAddress.getByName("localhost"), 1234);
        Selector selector = Selector.open();
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(addr);
        sc.register(selector, SelectionKey.OP_CONNECT |
                SelectionKey.OP_READ | SelectionKey.
                OP_WRITE);
        input = new BufferedReader(new
                InputStreamReader(System.in));

        Console console = System.console();
        String userName = console.readLine("\nEnter your name: ");
        while (true) {
            if (selector.select() > 0) {
                Boolean doneStatus = processReadySet
                        (selector.selectedKeys(), userName);
                if (doneStatus) {
                    break;
                }
            }
        }
        sc.close();
    }
    public static Boolean processReadySet(Set readySet, String userName)
            throws Exception {
        SelectionKey key = null;
        Iterator iterator = null;
        iterator = readySet.iterator();
        while (iterator.hasNext()) {
            key = (SelectionKey) iterator.next();
            iterator.remove();
        }
        if (key.isConnectable()) {
            Boolean connected = processConnect(key);
            if (!connected) {
                return true;
            }
        }
        if (key.isReadable()) {
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer bb = ByteBuffer.allocate(1024);
            sc.read(bb);
            String result = new String(bb.array()).trim();
            System.out.println(getMessageDescription()+result);
        }
        if (key.isWritable()) {
            System.out.print(getMessageDescription(userName));
            String msg = input.readLine();
            if (msg.equalsIgnoreCase("quit")) {
                return true;
            }
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer bb = ByteBuffer.wrap(("["+userName+"]: "+msg).getBytes());
            sc.write(bb);
        }
        return false;
    }
    public static Boolean processConnect(SelectionKey key) {
        SocketChannel sc = (SocketChannel) key.channel();
        try {
            while (sc.isConnectionPending()) {
                sc.finishConnect();
            }
        } catch (IOException e) {
            key.cancel();
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static String getMessageDescription(String userName) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        return "<"+dateTimeFormatter.format(now)+">"+"["+userName+"]: ";
    }

    private static String getMessageDescription() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        return "<"+dateTimeFormatter.format(now)+">";
    }
}
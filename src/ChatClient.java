import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {
    private String userName;
    private PrintWriter writer;
    private String hostName;
    private int port;
    private MainFrame mainFrame;

    public ChatClient(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    public void execute() {
        try {

            Socket socket = new Socket(hostName, port);
            System.out.println("Connected to the chat server");

            try {
                OutputStream output = socket.getOutputStream();
                writer = new PrintWriter(output, true);
                writer.println(userName);
            } catch (IOException ex) {
                System.out.println("Error getting output stream: " + ex.getMessage());
                ex.printStackTrace();
            }

            new ReadThread(socket, mainFrame).start();

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void sendMessage(String message) {
        writer.println(message);
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

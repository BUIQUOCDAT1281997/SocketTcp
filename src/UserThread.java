import java.io.*;
import java.net.Socket;

public class UserThread extends Thread{
    private Socket socket;
    private ChatServer server;
    private PrintWriter writer;

    public UserThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {

            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            OutputStream outputStream = socket.getOutputStream();
            writer = new PrintWriter(outputStream, true);

//            printUsers();

            String userName = reader.readLine();
            server.addUserName(userName);

            String serverMessage = userName + " connected";
            server.broadcast(serverMessage, this);

            String clientMessages;

            do {
                clientMessages = reader.readLine();
                server.broadcast(clientMessages, this);
            } while (!clientMessages.equals("bye"));

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + " has quited.";
            server.broadcast(serverMessage, this);

        } catch (IOException e) {
            System.out.println("Error in UserThread : " + e.getMessage());
            e.printStackTrace();
        }
    }

//    private void printUsers() {
//        if (server.hasUsers()) {
//            writer.println("Connected users: " + server.getUserNames());
//        } else {
//            writer.println("No other users connected");
//        }
//    }

    public void sendMessage(String message) {
        writer.println(message);
    }
}

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainFrame extends JFrame {
    private Board board;

    private ChatClient chatClient;

    private String elements = "Socket tcp";

    public MainFrame(String s) {
        super(s);

        chatClient = new ChatClient(this);

        setSize(1200, 800);
        this.setLayout(new BorderLayout());
        initMainPanel();
        initHeadPanel();
        initConnectPanel();
        initBottomPanel();
        setVisible(true);
        this.setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void showWarring() {
        JOptionPane.showOptionDialog(this,
                elements, "WARRING", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, null, "default");
    }

    private void initConnectPanel() {
        JPanel connectPanel = new JPanel();
        connectPanel.setBorder(new LineBorder(Color.BLACK, 1));
        connectPanel.setPreferredSize(new Dimension(350, 300));
        connectPanel.setBackground(new Color(249, 175, 57));
        connectPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel head = new JLabel("Server");
        connectPanel.add(head);
        JTextField ipAddress = new JTextField(10);
        connectPanel.add(ipAddress);

        JLabel labelPort = new JLabel("Port   ");
        connectPanel.add(labelPort);
        JTextField port = new JTextField(10);
        connectPanel.add(port);

        JLabel labelName = new JLabel("UserName");
        connectPanel.add(labelName);
        JTextField userName = new JTextField(10);
        connectPanel.add(userName);

        JButton buttonConnect = new JButton("Connect");
        buttonConnect.addActionListener(e -> {
            if (!ipAddress.getText().matches("(([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])")) {
                elements = "You can only enter ip";
                this.showWarring();
                return;
            } else if (!port.getText().matches("[0-9]+")) {
                elements = "You can only enter number";
                this.showWarring();
            } else if (userName.getText().isEmpty()) {
                elements = "Enter your userName";
                this.showWarring();
            }
            chatClient.setPort(Integer.parseInt(port.getText()));
            chatClient.setHostName(ipAddress.getText());
            chatClient.setUserName(userName.getText().trim());
            chatClient.execute();
            board.clearBoard();
            buttonConnect.setEnabled(false);
        });
        connectPanel.add(buttonConnect);
        JButton buttonDisconnect = new JButton("Disconnect");
        buttonDisconnect.addActionListener(e -> {
            //TODO
        });
        connectPanel.add(buttonDisconnect);
        this.add(connectPanel, BorderLayout.WEST);
    }

    private void initMainPanel() {
        board = new Board();
        board.setBackground(new Color(193, 191, 188));
        board.setBorder(new LineBorder(new Color(48, 56, 58), 2));
        this.add(board, BorderLayout.CENTER);
    }

    private void initHeadPanel() {
        JPanel headPanel = new JPanel();
        headPanel.setPreferredSize(new Dimension(200, 30));
        headPanel.setBorder(new LineBorder(new Color(48, 56, 58), 2));
        headPanel.setBackground(new Color(249, 175, 57));
        JLabel headLabel = new JLabel("BINARY AVLTREE");
        headLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headPanel.add(headLabel);
        this.add(headPanel, BorderLayout.NORTH);
    }

    private void initBottomPanel() {
        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(200, 50));
        bottomPanel.setBorder(new LineBorder(new Color(48, 56, 58), 2));
        bottomPanel.setBackground(new Color(249, 175, 57));
        JTextField textMess = new JTextField(50);
        bottomPanel.add(textMess);
        JButton buttonSend = new JButton("Send");
        buttonSend.addActionListener(e -> {
            if (!textMess.getText().isEmpty()) {
                board.addMessage(getMessageDescription()+textMess.getText());
                chatClient.sendMessage(getMessageDescription()+textMess.getText());
            }
        });
        bottomPanel.add(buttonSend);
        this.add(bottomPanel, BorderLayout.SOUTH);
    }

    private String getMessageDescription() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        return "<"+dateTimeFormatter.format(now)+">"+"["+chatClient.getUserName()+"]: ";
    }

    public void addMessageToBoard(String message) {
        board.addMessage(message);
    }

    public static void main(String[] args) {
        MainFrame mainFrame = new MainFrame("Socket Tcp");
    }
}

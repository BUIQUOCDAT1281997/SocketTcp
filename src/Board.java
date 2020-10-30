import javax.swing.*;
import java.awt.*;

public class Board extends JPanel {

    private JTextArea textArea;

    Board() {
        textArea = new JTextArea(20, 50);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }

    public void addMessage(String message) {
        textArea.append(message+"\n");
    }

    public void clearBoard() {
        textArea.setText("");
    }


}

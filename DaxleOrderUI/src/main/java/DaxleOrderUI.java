import javax.swing.*;
import java.awt.*;

public class DaxleOrderUI extends JFrame {

    private JTextField priceField;
    private JTextField quantityField;
    private JComboBox<String> orderTypeComboBox;
    private JTextField lifetimeField;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JButton okButton;
    private JButton cancelButton;
    private JTextArea messageArea;

    public DaxleOrderUI() {
        // Create UI components
        priceField = new JTextField();
        quantityField = new JTextField();
        orderTypeComboBox = new JComboBox<String>(new String[]{"Buy", "Sell"});
        lifetimeField = new JTextField();
        startTimeField = new JTextField();
        endTimeField = new JTextField();
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        messageArea = new JTextArea();

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel("Order Type:"));
        panel.add(orderTypeComboBox);
        panel.add(new JLabel("Lifetime:"));
        panel.add(lifetimeField);
        panel.add(new JLabel("Start Time:"));
        panel.add(startTimeField);
        panel.add(new JLabel("End Time:"));
        panel.add(endTimeField);
        panel.add(okButton);
        panel.add(cancelButton);
        panel.add(new JScrollPane(messageArea));

        // Add components to the UI
        add(panel);

        // Set up OK button ActionListener
        okButton.addActionListener(e -> {
            // Get input values from UI components
            String price = priceField.getText();
            String quantity = quantityField.getText();
            String orderType = (String) orderTypeComboBox.getSelectedItem();
            String lifetime = lifetimeField.getText();
            String startTime = startTimeField.getText();
            String endTime = endTimeField.getText();

            // Create a new instance of FIXClient and use it to send the order and receive the Execution Report
//            FIXClient fixClient = new FIXClient();
//            String sentMessage = fixClient.sendOrder(price, quantity, orderType, lifetime, startTime, endTime);
//            String receivedMessage = fixClient.receiveExecutionReport();
//
//            // Update message panel with the messages that were sent and received
//            messageArea.append(sentMessage + "\n");
//            messageArea.append(receivedMessage + "\n");
        });

        // Set up Cancel button ActionListener
        cancelButton.addActionListener(e -> {
            // Close the UI
            dispose();
        });

        // Set up UI properties
        // ...
    }

    public static void main(String[] args) {
        // Create a new instance of the UI
        DaxleOrderUI ui = new DaxleOrderUI();


        // Show the UI
        ui.setExtendedState(JFrame.MAXIMIZED_BOTH);
        ui.setVisible(true);
    }
}


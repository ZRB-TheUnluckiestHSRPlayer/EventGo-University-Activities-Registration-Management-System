import java.awt.EventQueue;
import javax.swing.*;
import java.awt.Image;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class QRCodeDisplay {

    private JFrame frame;
    private JTextField eventIdField;
    private JLabel qrCodeLabel;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                QRCodeDisplay window = new QRCodeDisplay();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the application.
     */
    public QRCodeDisplay() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame("QR Code Display");
        frame.setBounds(100, 100, 500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblEventId = new JLabel("Event ID:");
        lblEventId.setBounds(30, 30, 80, 25);
        frame.getContentPane().add(lblEventId);

        eventIdField = new JTextField();
        eventIdField.setBounds(100, 30, 200, 25);
        frame.getContentPane().add(eventIdField);

        JButton btnFetchQR = new JButton("Get QR Code");
        btnFetchQR.setBounds(320, 30, 120, 25);
        frame.getContentPane().add(btnFetchQR);

        qrCodeLabel = new JLabel();
        qrCodeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        qrCodeLabel.setBounds(50, 80, 380, 260);
        frame.getContentPane().add(qrCodeLabel);

        btnFetchQR.addActionListener(e -> fetchQRCode());
    }

    /**
     * Fetch and display the QR code image from the backend.
     */
    private void fetchQRCode() {
        String eventId = eventIdField.getText().trim();
        if (eventId.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter an Event ID.");
            return;
        }

        String imageUrl = "http://your-backend-url/api/generateQR?event_id=" + eventId;

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();

            if (con.getResponseCode() != 200) {
                JOptionPane.showMessageDialog(frame, "Failed to retrieve QR code.");
                return;
            }

            InputStream input = con.getInputStream();
            ImageIcon icon = new ImageIcon(javax.imageio.ImageIO.read(input));
            Image scaledImage = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
            qrCodeLabel.setIcon(new ImageIcon(scaledImage));

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error fetching QR code:\n" + e.getMessage());
        }
    }
}

import java.awt.EventQueue;
import javax.swing.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PublishActivity {

    private JFrame frame;
    private JTextField titleField, venueField, maxParticipantsField;
    private JTextArea descriptionArea;
    private JSpinner dateTimeSpinner;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                PublishActivity window = new PublishActivity();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the application.
     */
    public PublishActivity() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame("Publish New Activity");
        frame.setBounds(100, 100, 500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblTitle = new JLabel("Title:");
        lblTitle.setBounds(30, 30, 80, 25);
        frame.getContentPane().add(lblTitle);

        titleField = new JTextField();
        titleField.setBounds(130, 30, 300, 25);
        frame.getContentPane().add(titleField);

        JLabel lblDescription = new JLabel("Description:");
        lblDescription.setBounds(30, 70, 80, 25);
        frame.getContentPane().add(lblDescription);

        descriptionArea = new JTextArea();
        descriptionArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setBounds(130, 70, 300, 80);
        frame.getContentPane().add(scrollPane);

        JLabel lblDateTime = new JLabel("Date/Time:");
        lblDateTime.setBounds(30, 170, 80, 25);
        frame.getContentPane().add(lblDateTime);

        dateTimeSpinner = new JSpinner(new SpinnerDateModel());
        dateTimeSpinner.setBounds(130, 170, 300, 25);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateTimeSpinner, "yyyy-MM-dd HH:mm");
        dateTimeSpinner.setEditor(editor);
        frame.getContentPane().add(dateTimeSpinner);

        JLabel lblVenue = new JLabel("Venue:");
        lblVenue.setBounds(30, 210, 80, 25);
        frame.getContentPane().add(lblVenue);

        venueField = new JTextField();
        venueField.setBounds(130, 210, 300, 25);
        frame.getContentPane().add(venueField);

        JLabel lblMax = new JLabel("Max Participants:");
        lblMax.setBounds(30, 250, 120, 25);
        frame.getContentPane().add(lblMax);

        maxParticipantsField = new JTextField();
        maxParticipantsField.setBounds(160, 250, 100, 25);
        frame.getContentPane().add(maxParticipantsField);

        JButton btnSubmit = new JButton("Submit");
        btnSubmit.setBounds(180, 320, 120, 30);
        frame.getContentPane().add(btnSubmit);

        btnSubmit.addActionListener(e -> handleSubmit());
    }

    /**
     * Handle the submit button click.
     */
    private void handleSubmit() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String venue = venueField.getText().trim();
        String maxParticipantsStr = maxParticipantsField.getText().trim();
        Date dateTime = (Date) dateTimeSpinner.getValue();

        if (title.isEmpty() || description.isEmpty() || venue.isEmpty() || maxParticipantsStr.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
            return;
        }

        int maxParticipants;
        try {
            maxParticipants = Integer.parseInt(maxParticipantsStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Max Participants must be a number.");
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = sdf.format(dateTime);

        // Construct JSON manually (you can also use Gson or Jackson)
        String json = String.format(
            "{\"title\":\"%s\",\"description\":\"%s\",\"datetime\":\"%s\",\"venue\":\"%s\",\"max_participants\":%d}",
            title, description, formattedDate, venue, maxParticipants
        );

        try {
            String response = ApiClient.sendPost("http://your-backend-url/api/createEvent", json);
            JOptionPane.showMessageDialog(frame, "Activity created successfully!\nResponse: " + response);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to create activity.\n" + ex.getMessage());
        }
    }
}

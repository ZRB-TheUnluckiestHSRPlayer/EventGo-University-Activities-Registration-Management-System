import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;
import org.json.JSONArray;
import org.json.JSONObject;

public class SignInData {

    private JFrame frame;
    private JTextField eventIdField;
    private JTable table;
    private DefaultTableModel tableModel;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                SignInData window = new SignInData();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the application.
     */
    public SignInData() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame("Sign-In Data Management");
        frame.setBounds(100, 100, 600, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblEventId = new JLabel("Event ID:");
        lblEventId.setBounds(30, 20, 80, 25);
        frame.getContentPane().add(lblEventId);

        eventIdField = new JTextField();
        eventIdField.setBounds(100, 20, 200, 25);
        frame.getContentPane().add(eventIdField);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(320, 20, 100, 25);
        frame.getContentPane().add(btnRefresh);

        JButton btnExport = new JButton("Export CSV");
        btnExport.setBounds(430, 20, 120, 25);
        frame.getContentPane().add(btnExport);

        tableModel = new DefaultTableModel();
        tableModel.setColumnIdentifiers(new String[]{"Student ID", "Name", "Sign-In Time"});

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 70, 520, 300);
        frame.getContentPane().add(scrollPane);

        btnRefresh.addActionListener(e -> fetchSignInData());
        btnExport.addActionListener(e -> exportToCSV());
    }

    /**
     * Fetch sign-in data from backend.
     */
    private void fetchSignInData() {
        String eventId = eventIdField.getText().trim();
        if (eventId.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter an Event ID.");
            return;
        }

        String url = "http://your-backend-url/api/checkins?event_id=" + eventId;

        try {
            String response = ApiClient.sendGet(url);
            JSONArray jsonArr = new JSONArray(response);	// need a json file
            /*missing backend json file like:
            [
             {
               "student_id": "B032210001",
               "name": "Ali Bin Abu",
               "sign_in_time": "2025-06-18 10:23:00"
             },
             ...
           ]*/
            
            tableModel.setRowCount(0); // clear table
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject obj = jsonArr.getJSONObject(i);
                String sid = obj.getString("student_id");
                String name = obj.getString("name");
                String time = obj.getString("sign_in_time");
                tableModel.addRow(new Object[]{sid, name, time});
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to load data.\n" + e.getMessage());
        }
    }

    /**
     * Export current table to CSV.
     */
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV File");

        int userSelection = fileChooser.showSaveDialog(frame);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    writer.write(tableModel.getColumnName(i) + ",");
                }
                writer.newLine();

                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        writer.write(tableModel.getValueAt(row, col).toString() + ",");
                    }
                    writer.newLine();
                }

                JOptionPane.showMessageDialog(frame, "CSV file saved successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Failed to save CSV.\n" + e.getMessage());
            }
        }
    }
}

import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.event.*;
import java.util.*;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;		//missing backend

public class ReviewRegistration {

    private JFrame frame;
    private JTextField eventIdField;
    private JTable table;
    private DefaultTableModel tableModel;
    private String currentEventId;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ReviewRegistration window = new ReviewRegistration();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the application.
     */
    public ReviewRegistration() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame("Review Registrations");
        frame.setBounds(100, 100, 650, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblEventId = new JLabel("Event ID:");
        lblEventId.setBounds(30, 20, 80, 25);
        frame.getContentPane().add(lblEventId);

        eventIdField = new JTextField();
        eventIdField.setBounds(100, 20, 200, 25);
        frame.getContentPane().add(eventIdField);

        JButton btnLoad = new JButton("Load Registrations");
        btnLoad.setBounds(320, 20, 180, 25);
        frame.getContentPane().add(btnLoad);

        tableModel = new DefaultTableModel(new Object[]{"Student ID", "Name", "Status", "Action"}, 0);
        table = new JTable(tableModel) {
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };

        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), this));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 70, 580, 300);
        frame.getContentPane().add(scrollPane);

        btnLoad.addActionListener(e -> loadRegistrations());
    }

    /**
     * Load registered students from backend.
     */
    public void loadRegistrations() {
        String eventId = eventIdField.getText().trim();
        if (eventId.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter an Event ID.");
            return;
        }

        currentEventId = eventId;
        String url = "http://your-backend-url/api/registrations?event_id=" + eventId;

        try {
            String response = ApiClient.sendGet(url);
            JSONArray arr = new JSONArray(response);
            tableModel.setRowCount(0);
            /*missing backend like:GET /api/registrations?event_id=xxx
            {
            	  "event_id": "E001",
            	  "student_id": "B032300111"
            	}
            AND POST /api/approveRegistration
            {
            	  "event_id": "E001",
            	  "student_id": "B032300111"
            	}*/


            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String studentId = obj.getString("student_id");
                String name = obj.getString("name");
                String status = obj.getString("status");

                tableModel.addRow(new Object[]{studentId, name, status, "Approve"});
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to load registrations.");
        }
    }

    /**
     * Send approval request to backend.
     */
    public void approveStudent(String studentId) {
        String url = "http://your-backend-url/api/approveRegistration";
        String payload = String.format("{\"event_id\":\"%s\", \"student_id\":\"%s\"}", currentEventId, studentId);

        try {
            String response = ApiClient.sendPost(url, payload);
            JOptionPane.showMessageDialog(frame, "Approved: " + studentId);
            loadRegistrations();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Approval failed.\n" + e.getMessage());
        }
    }

    /**
     * Renderer for Approve button inside JTable.
     */
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Approve");
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    /**
     * Editor for Approve button inside JTable.
     */
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String studentId;
        private ReviewRegistration parent;

        public ButtonEditor(JCheckBox checkBox, ReviewRegistration parent) {
            super(checkBox);
            this.parent = parent;
            button = new JButton("Approve");
            button.addActionListener(e -> {
                parent.approveStudent(studentId);
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            studentId = (String) table.getValueAt(row, 0);
            return button;
        }
    }
}

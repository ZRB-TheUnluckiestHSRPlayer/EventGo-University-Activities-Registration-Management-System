//You’ll need JFreeChart (download JAR: https://sourceforge.net/projects/jfreechart/)
//Include in Eclipse: Project > Properties > Java Build Path > Add External JARs
import java.awt.EventQueue;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import org.json.*;	//need backend
import java.net.*;
import java.io.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class AttendanceStatistics {

    private JFrame frame;
    private JComboBox<String> eventComboBox;
    private JLabel lblRegistered, lblSignedIn, lblPercentage;
    private JPanel chartPanel;
    private Map<String, String> eventMap = new HashMap<>(); // name -> id

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                AttendanceStatistics window = new AttendanceStatistics();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the application.
     */
    public AttendanceStatistics() {
        initialize();
        loadEventList(); // load comboBox with available events
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame("Attendance Statistics");
        frame.setBounds(100, 100, 700, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblSelectEvent = new JLabel("Select Event:");
        lblSelectEvent.setBounds(30, 20, 100, 25);
        frame.getContentPane().add(lblSelectEvent);

        eventComboBox = new JComboBox<>();
        eventComboBox.setBounds(130, 20, 250, 25);
        frame.getContentPane().add(eventComboBox);

        JButton btnLoadStats = new JButton("Load Statistics");
        btnLoadStats.setBounds(400, 20, 150, 25);
        frame.getContentPane().add(btnLoadStats);

        lblRegistered = new JLabel("Registered: ");
        lblRegistered.setBounds(30, 60, 200, 25);
        frame.getContentPane().add(lblRegistered);

        lblSignedIn = new JLabel("Signed-In: ");
        lblSignedIn.setBounds(30, 90, 200, 25);
        frame.getContentPane().add(lblSignedIn);

        lblPercentage = new JLabel("Attendance: ");
        lblPercentage.setBounds(30, 120, 200, 25);
        frame.getContentPane().add(lblPercentage);

        chartPanel = new JPanel();
        chartPanel.setBounds(250, 60, 400, 350);
        frame.getContentPane().add(chartPanel);

        btnLoadStats.addActionListener(e -> loadAttendanceStats());
    }

    /**
     * Load events into JComboBox
     */
    private void loadEventList() {
        String url = "http://your-backend-url/api/events";

        try {
            String response = ApiClient.sendGet(url);
            JSONArray arr = new JSONArray(response);
            eventComboBox.removeAllItems();
            /*require backend: GET /api/events
            [
             { "event_id": "E001", "title": "Sports Day" },
             { "event_id": "E002", "title": "Coding Hackathon" }
            ]*/

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String id = obj.getString("event_id");
                String name = obj.getString("title");
                eventMap.put(name, id);
                eventComboBox.addItem(name);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to load event list.");
        }
    }

    /**
     * Load attendance statistics for selected event.
     */
    private void loadAttendanceStats() {
        String selectedEvent = (String) eventComboBox.getSelectedItem();
        if (selectedEvent == null) return;

        String eventId = eventMap.get(selectedEvent);
        String url = "http://your-backend-url/api/attendanceStats?event_id=" + eventId;

        try {
            String response = ApiClient.sendGet(url);
            JSONObject obj = new JSONObject(response);
            /*require backend: GET /api/attendanceStats?event_id=E001
            {
            	"registered": 30,
            	"signed_in": 20
            }*/
            
            int registered = obj.getInt("registered");
            int signedIn = obj.getInt("signed_in");
            double percentage = registered == 0 ? 0 : (signedIn * 100.0 / registered);

            lblRegistered.setText("Registered: " + registered);
            lblSignedIn.setText("Signed-In: " + signedIn);
            lblPercentage.setText("Attendance: " + String.format("%.2f", percentage) + "%");

            // Pie chart
            DefaultPieDataset dataset = new DefaultPieDataset();
            dataset.setValue("Signed In", signedIn);
            dataset.setValue("Not Signed In", registered - signedIn);

            JFreeChart pieChart = ChartFactory.createPieChart(
                    "Attendance Chart", dataset, true, true, false);

            ChartPanel chart = new ChartPanel(pieChart);
            chart.setPreferredSize(new Dimension(380, 300));

            chartPanel.removeAll();
            chartPanel.add(chart);
            chartPanel.revalidate();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Failed to load attendance data.");
        }
    }
}

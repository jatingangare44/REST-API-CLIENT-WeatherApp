package task_2;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalTime;

public class WeatherGUIApp extends JFrame {

    private JComboBox<String> stateDropdown;
    private JTextField cityField;
    private JTextArea resultArea;

    public WeatherGUIApp() {
        setTitle("🌤️ Weather App");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(mainPanel, BorderLayout.CENTER);

        JLabel indiaLabel = new JLabel("INDIA", SwingConstants.CENTER);
        indiaLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        indiaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(indiaLabel);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setMaximumSize(new Dimension(400, 100));

        inputPanel.add(new JLabel("State:"));
        String[] indianStates = {
            "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh",
            "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jharkhand",
            "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur",
            "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab",
            "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura",
            "Uttar Pradesh", "Uttarakhand", "West Bengal", "Delhi", "Jammu and Kashmir",
            "Ladakh", "Puducherry", "Chandigarh", "Andaman and Nicobar Islands", "Dadra and Nagar Haveli and Daman and Diu"
        };
        stateDropdown = new JComboBox<>(indianStates);
        inputPanel.add(stateDropdown);

        inputPanel.add(new JLabel("City:"));
        cityField = new JTextField();
        inputPanel.add(cityField);

        JButton fetchButton = new JButton("Get Weather");
        fetchButton.addActionListener(this::onFetchWeather);
        inputPanel.add(fetchButton);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(inputPanel);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(450, 200));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(scrollPane);
    }

    private void onFetchWeather(ActionEvent e) {
        String state = (String) stateDropdown.getSelectedItem();
        String city = cityField.getText().trim();

        if (state == null || city.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a state and enter a city.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double[] coords = getCoordinatesFromLocation("India", state, city);
            if (coords == null) {
                JOptionPane.showMessageDialog(this, "Location not found. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String apiUrl = buildApiUrl(coords[0], coords[1]);
            String jsonData = fetchWeatherData(apiUrl);

            if (!jsonData.isEmpty()) {
                String report = parseWeather(jsonData, state, city);
                resultArea.setText(report);
                saveReportToFile(report);
            } else {
                JOptionPane.showMessageDialog(this, "Weather data not available.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double[] getCoordinatesFromLocation(String country, String state, String city) throws Exception {
        String urlStr = "https://nominatim.openstreetmap.org/search?"
                + "country=" + URLEncoder.encode(country, "UTF-8")
                + "&state=" + URLEncoder.encode(state, "UTF-8")
                + "&city=" + URLEncoder.encode(city, "UTF-8")
                + "&format=json";

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder jsonResult = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            jsonResult.append(line);
        }
        in.close();

        JSONArray arr = new JSONArray(jsonResult.toString());

        if (arr.length() == 0) return null;

        JSONObject location = arr.getJSONObject(0);
        return new double[]{
                Double.parseDouble(location.getString("lat")),
                Double.parseDouble(location.getString("lon"))
        };
    }

    private String buildApiUrl(double lat, double lon) {
        return "https://api.open-meteo.com/v1/forecast?latitude=" + lat
                + "&longitude=" + lon
                + "&current=temperature_2m,relative_humidity_2m,weather_code"
                + "&timezone=auto";
    }

    private String fetchWeatherData(String apiUrl) throws Exception {
        StringBuilder response = new StringBuilder();
        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String input;
            while ((input = in.readLine()) != null) {
                response.append(input);
            }
            in.close();
        }

        return response.toString();
    }

    private String parseWeather(String jsonData, String state, String city) {
        JSONObject obj = new JSONObject(jsonData);
        JSONObject current = obj.getJSONObject("current");
        LocalTime curr = LocalTime.now();

        double temp = current.getDouble("temperature_2m");
        int humidity = current.getInt("relative_humidity_2m");
        int code = current.getInt("weather_code");

        return "📍 Weather Report for " + city + ", " + state + "\n"
                + "🌡️ Temperature: " + temp + " °C\n"
                + "💧 Humidity: " + humidity + " %\n"
                + "🌤️ Weather: " + getWeatherDescription(code) + " (Code: " + code + ")\n"
                + "⌚ TimeStamp: ["+curr.getHour()+":"+curr.getMinute()+":"+curr.getSecond()+"]\n"
                + "----------------------------------------\n";
    }

    private String getWeatherDescription(int code) {
        return switch (code) {
            case 0 -> "Clear sky";
            case 1 -> "Mainly clear";
            case 2 -> "Partly cloudy";
            case 3 -> "Overcast";
            case 45 -> "Fog";
            case 51 -> "Light drizzle";
            case 61 -> "Light rain";
            case 71 -> "Light snow";
            case 80 -> "Rain showers";
            default -> "Unknown condition";
        };
    }

    private void saveReportToFile(String report) {
        try (FileWriter writer = new FileWriter("weather_report.txt", true)) {
            writer.write(report);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save report.", "File Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WeatherGUIApp().setVisible(true));
    }
}

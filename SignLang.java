import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.sql.Statement;
import javax.swing.border.EmptyBorder;

public class SignLang extends JFrame {
    private Map<String, String> wordToImagePath;
    private JScrollPane imageScrollPane;
    private JPanel imagePanel;
    private JButton displayButton;
    private JTextField inputField;
    private Connection connection;

    public SignLang() {
        // Initialize the JFrame
        setTitle("Sign Language Chatbot");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize the database connection
        initializeDatabaseConnection();

        // Create a map of words to image paths and populate it from a dataset file
        wordToImagePath = readDatasetFile("C:\\D drive\\Projects\\SignLang\\images.txt");

        // Create a JPanel as the main content pane with a BorderLayout
        JPanel contentPane = new JPanel(new BorderLayout());

        // Create a JPanel to display the images
        imagePanel = new JPanel(new FlowLayout());

        // Create a JScrollPane to wrap the JPanel
        imageScrollPane = new JScrollPane(imagePanel);

        // Create a JTextField for entering sentences
        inputField = new JTextField(30);

        // Create a button to trigger image display
        displayButton = new JButton("Sign Language");

        // Create a button to reset the displayed images and text field
        JButton resetButton = new JButton("Reset");

        // Create a delete history button
        JButton deleteHistoryButton = new JButton("Delete History");

        // Create a button to view text data
        JButton viewTextDataButton = new JButton("View History");

        // Add the input field to the top of the content pane
        contentPane.add(inputField, BorderLayout.NORTH);

        // Add the JScrollPane to the center
        contentPane.add(imageScrollPane, BorderLayout.CENTER);

        // Add the buttons to the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(displayButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(viewTextDataButton);
        buttonPanel.add(deleteHistoryButton);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        // Set the custom content pane
        setContentPane(contentPane);

        // Add event listeners to buttons
        displayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveTextToDatabase(inputField.getText()); // Save the text to the database
                displayImagesFromInput();
            }
        });

        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetDisplay();
            }
        });

        viewTextDataButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewTextData();
            }
        });

        deleteHistoryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteTextDataHistory();
            }
        });
        inputField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // When "Enter" is pressed, trigger the "Sign Language" button
                displayButton.doClick();
            }
        });
    }

    private void initializeDatabaseConnection() {
        try {
            // Replace with your database details
            String jdbcUrl = "jdbc:mysql://localhost/signlang";
            String username = "root";
            String password = "jigs21";

            connection = DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> readDatasetFile(String filePath) {
        Map<String, String> datasetMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String word = parts[0].trim().toLowerCase();
                    String imagePath = parts[1].trim();
                    datasetMap.put(word, imagePath);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return datasetMap;
    }

    private void saveTextToDatabase(String text) {
        try {
            String insertQuery = "INSERT INTO text_data (text_content) VALUES (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, text);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayImagesFromInput() {
        String sentence = inputField.getText().toLowerCase();
        String[] wordsInSentence = sentence.split("\\s+");

        String[] imagePaths = new String[wordsInSentence.length];
        for (int i = 0; i < wordsInSentence.length; i++) {
            if (wordToImagePath.containsKey(wordsInSentence[i])) {
                imagePaths[i] = wordToImagePath.get(wordsInSentence[i]);
            }
        }

        displayImages(imagePaths);
    }

    private void resetDisplay() {
        // Clear the JPanel
        imagePanel.removeAll();

        // Revalidate and repaint the JPanel
        imagePanel.revalidate();
        imagePanel.repaint();

        // Clear the text field
        inputField.setText("");
    }

    // Method to view text data
    private void viewTextData() {
        try {
            // Database connection parameters (already initialized in your code)
            String jdbcUrl = "jdbc:mysql://localhost/signlang";
            String username = "root";
            String password = "jigs21";

            // Create a connection
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            // Create a statement
            Statement statement = connection.createStatement();

            // Execute the query to retrieve text data
            ResultSet resultSet = statement.executeQuery("SELECT text_content FROM text_data");

            // Display the text data in a dialog box
            StringBuilder textContent = new StringBuilder();
            while (resultSet.next()) {
                String text = resultSet.getString("text_content");
                textContent.append(text).append("\n");
            }

            if (textContent.length() > 0) {
                JOptionPane.showMessageDialog(this, "History:\n" + textContent.toString(), "History",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No History found.", "Hisotry",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            // Close the statement and connection
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving text data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean deleteTextDataHistory() {
        try {
            String deleteQuery = "DELETE FROM text_data";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
            int rowsDeleted = preparedStatement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showDeletionConfirmation() {
        JOptionPane.showMessageDialog(this, "Text data history has been deleted.", "Deletion Complete",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void displayImages(String[] imagePaths) {
        imagePanel.removeAll();

        for (String imagePath : imagePaths) {
            System.out.println("Loading image from path: " + imagePath);

            ImageIcon imageIcon = new ImageIcon(imagePath);
            if (imageIcon.getIconWidth() == -1) {
                System.err.println("Error loading image from path: " + imagePath);
                continue;
            }

            double widthScale = (double) 100 / imageIcon.getIconWidth();
            double heightScale = (double) 100 / imageIcon.getIconHeight();
            double scale = Math.min(widthScale, heightScale);

            int scaledWidth = (int) (imageIcon.getIconWidth() * scale);
            int scaledHeight = (int) (imageIcon.getIconHeight() * scale);
            Image scaledImage = imageIcon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);

            ImageIcon scaledImageIcon = new ImageIcon(scaledImage);

            JLabel imageLabel = new JLabel(scaledImageIcon);
            imagePanel.add(imageLabel);
        }

        imagePanel.revalidate();
        imagePanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignLang().setVisible(true));
    }
}

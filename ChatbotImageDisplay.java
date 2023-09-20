import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChatbotImageDisplay extends JFrame {
    private Map<String, String> wordToImagePath;
    private JLabel imageLabel;
    private JTextField inputField;

    public ChatbotImageDisplay() {
        // Initialize the JFrame
        setTitle("Chatbot Image Display");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a map of words to image paths and populate it from a dataset file
        wordToImagePath = readDatasetFile("C:\\D drive\\Projects\\SignLang\\images.txt"); // Replace with the path to
                                                                                          // your dataset file

        // Create a JLabel to display the image
        imageLabel = new JLabel();
        add(imageLabel, BorderLayout.CENTER);

        // Create a JTextField for entering sentences
        inputField = new JTextField(40);
        add(inputField, BorderLayout.NORTH);

        // Create a button to trigger image display
        JButton displayButton = new JButton("Display Images");
        add(displayButton, BorderLayout.SOUTH);

        // Add action listener to the button
        displayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sentence = inputField.getText().toLowerCase();
                String[] wordsInSentence = sentence.split("\\s+"); // Split the sentence into words

                for (String word : wordsInSentence) {
                    if (wordToImagePath.containsKey(word)) {
                        String imagePath = wordToImagePath.get(word);
                        displayImage(imagePath);
                    }
                }
            }
        });
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

    private void displayImage(String imagePath) {
        // Load and display the image
        ImageIcon imageIcon = new ImageIcon(imagePath);
        Image image = imageIcon.getImage();
        Image scaledImage = image.getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(scaledImage);
        imageLabel.setIcon(imageIcon);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatbotImageDisplay().setVisible(true));
    }
}

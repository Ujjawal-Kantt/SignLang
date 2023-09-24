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
  private JScrollPane imageScrollPane;
  private JPanel imagePanel;
  private JTextField inputField;

  public ChatbotImageDisplay() {
    // Initialize the JFrame
    setTitle("Chatbot Image Display");
    setSize(800, 600);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    // Create a map of words to image paths and populate it from a dataset file
    wordToImagePath = readDatasetFile("C:\\Users\\samak\\Downloads\\SignLang\\images.txt"); // Replace with the path to
    // your dataset file

    // Create a JPanel to display the images
    imagePanel = new JPanel(new FlowLayout());

    // Create a JScrollPane to wrap the JPanel
    // This will allow us to scroll through the images if there are many of them
    imageScrollPane = new JScrollPane(imagePanel);

    // Add the JScrollPane to the JFrame
    add(imageScrollPane, BorderLayout.CENTER);

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

        // Get the image paths for each word in the sentence
        String[] imagePaths = new String[wordsInSentence.length];
        for (int i = 0; i < wordsInSentence.length; i++) {
          if (wordToImagePath.containsKey(wordsInSentence[i])) {
            imagePaths[i] = wordToImagePath.get(wordsInSentence[i]);
          }
        }

        // Display the images
        displayImages(imagePaths);
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

  private void displayImages(String[] imagePaths) {
    // Clear the JPanel
    imagePanel.removeAll();

    // Load and display each image
    for (String imagePath : imagePaths) {
      // Load the image
      ImageIcon imageIcon = new ImageIcon(imagePath);
      Image image = imageIcon.getImage();

      // Scale the image to fit the JPanel
      Image scaledImage = image.getScaledInstance(imagePanel.getWidth(), imagePanel.getHeight(), Image.SCALE_SMOOTH);
      imageIcon = new ImageIcon(scaledImage);

      // Add the image to the JPanel
      JLabel imageLabel = new JLabel(imageIcon);
      imagePanel.add(imageLabel);
    }

    // Revalidate and repaint the JPanel
    imagePanel.revalidate();
    imagePanel.repaint();
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> new ChatbotImageDisplay().setVisible(true));
  }
}

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.border.EmptyBorder;

public class SignLang extends JFrame {
    private Map<String, String> wordToImagePath;
    private JScrollPane imageScrollPane;
    private JPanel imagePanel;
    private JTextField inputField;
    private JLabel nameLabel; // Label to display the user's name
    private String userName = ""; // Store the user's name
    private final int IMAGE_WIDTH = 100; // Desired width for scaled images
    private final int IMAGE_HEIGHT = 100; // Desired height for scaled images

    public SignLang() {
        // Initialize the JFrame
        setTitle("Chatbot Image Display");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create a map of words to image paths and populate it from a dataset file
        wordToImagePath = readDatasetFile("C:\\D drive\\Projects\\SignLang\\images.txt"); // Replace with the path to
                                                                                          // your dataset file

        // Create a JPanel as the main content pane with a BorderLayout
        JPanel contentPane = new JPanel(new BorderLayout());

        // Set a background image for the content pane
        contentPane.setOpaque(false);
        setContentPane(new BackgroundPanel());

        // Create a JPanel to display the images
        imagePanel = new JPanel(new FlowLayout());

        // Create a JScrollPane to wrap the JPanel
        // This will allow us to scroll through the images if there are many of them
        imageScrollPane = new JScrollPane(imagePanel);

        // Create a JTextField for entering sentences (left-middle)
        inputField = new JTextField(40);

        // Create a JPanel for the input field and display button
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);

        // Create a button to trigger image display
        JButton displayButton = new JButton("Display Images");
        inputPanel.add(displayButton, BorderLayout.EAST);

        // Add the JScrollPane to the content pane
        contentPane.add(imageScrollPane, BorderLayout.CENTER);

        // Add the input panel to the left-middle
        contentPane.add(inputPanel, BorderLayout.WEST);

        // Create a button to reset the displayed images and text field
        JButton resetButton = new JButton("Reset");
        contentPane.add(resetButton, BorderLayout.EAST);

        // Ask for the user's name and age
        String name = JOptionPane.showInputDialog("Please enter your name:");
        String age = JOptionPane.showInputDialog("Please enter your age:");

        // Store the user's name
        userName = name != null ? name : "";

        // Create a label to display the user's name
        nameLabel = new JLabel("Hello, " + userName);
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Set a blue background color for the label
        nameLabel.setBackground(new Color(0, 0, 255));
        nameLabel.setForeground(Color.WHITE); // Set white text color
        nameLabel.setOpaque(true); // Make the label opaque to display the background color

        contentPane.add(nameLabel, BorderLayout.NORTH);

        // Add an ActionListener to the displayButton
        displayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayImagesFromInput();
            }
        });

        // Add action listener to the reset button
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetDisplay();
            }
        });

        // Add a KeyListener to the inputField to listen for Enter key presses
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    displayImagesFromInput();
                }
            }
        });

        // Set the custom content pane
        setContentPane(contentPane);
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

    private void displayImagesFromInput() {
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

    private void resetDisplay() {
        // Clear the JPanel
        imagePanel.removeAll();

        // Revalidate and repaint the JPanel
        imagePanel.revalidate();
        imagePanel.repaint();

        // Clear the text field
        inputField.setText("");
    }

    private void displayImages(String[] imagePaths) {
        // Clear the JPanel
        imagePanel.removeAll();

        for (String imagePath : imagePaths) {
            System.out.println("Loading image from path: " + imagePath); // Debug statement

            ImageIcon imageIcon = new ImageIcon(imagePath);
            if (imageIcon.getIconWidth() == -1) {
                System.err.println("Error loading image from path: " + imagePath);
                continue; // Skip this image if there's an error
            }

            // Calculate the scaling factors to fit the desired dimensions
            double widthScale = (double) IMAGE_WIDTH / imageIcon.getIconWidth();
            double heightScale = (double) IMAGE_HEIGHT / imageIcon.getIconHeight();
            double scale = Math.min(widthScale, heightScale);

            // Scale the image with the calculated scale factor
            int scaledWidth = (int) (imageIcon.getIconWidth() * scale);
            int scaledHeight = (int) (imageIcon.getIconHeight() * scale);
            Image scaledImage = imageIcon.getImage().getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_SMOOTH);

            // Create a new ImageIcon with the scaled image
            ImageIcon scaledImageIcon = new ImageIcon(scaledImage);

            // Add the image to the JPanel
            JLabel imageLabel = new JLabel(scaledImageIcon);
            imagePanel.add(imageLabel);
        }

        // Revalidate and repaint the JPanel
        imagePanel.revalidate();
        imagePanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignLang().setVisible(true));
    }

    // Custom JPanel for setting a background image
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            // Load the background image
            backgroundImage = new ImageIcon("C:\\D drive\\Projects\\SignLang\\databse\\background.jpeg").getImage(); // Replace
                                                                                                                     // with
                                                                                                                     // your
                                                                                                                     // image
                                                                                                                     // path
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Paint the background image
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

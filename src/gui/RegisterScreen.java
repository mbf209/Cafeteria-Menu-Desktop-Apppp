package gui;

import users.UserDAO;

import javax.swing.*;
import java.awt.*;

public class RegisterScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserDAO userDAO;

    public RegisterScreen() {
        userDAO = new UserDAO();
        initializeGUI();
    }

    private void initializeGUI() {
        // Frame settings
        setTitle("Register");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Background panel with image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon background = new ImageIcon("images/background.jpg");
                Image bgImage = background.getImage();
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new GridBagLayout()); // Center the form
        add(backgroundPanel, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false); // Transparent for background

        // Username field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField(20);
        usernameField.setMaximumSize(new Dimension(300, 30));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(300, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons
        JButton registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> handleRegister());

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> openLoginScreen());

        // Add components to the form panel
        formPanel.add(usernameLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacing
        formPanel.add(passwordLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacing
        formPanel.add(registerButton);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing
        formPanel.add(backButton);

        // Add form panel to background panel
        backgroundPanel.add(formPanel);

        setVisible(true);
    }

    private void handleRegister() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (userDAO.register(username, password)) {
            JOptionPane.showMessageDialog(this, "Registration successful!");
            openLoginScreen();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Username may already exist.");
        }
    }
    private void openLoginScreen() {
        new LoginScreen(); // Launch LoginScreen
        dispose(); // Close the register screen
    }
}
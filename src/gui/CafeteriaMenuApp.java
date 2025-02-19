package gui;

import database.DBConnection;
import models.FoodItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class CafeteriaMenuApp extends JFrame {
    private JList<FoodItem> itemList;
    private DefaultListModel<FoodItem> listModel;
    private JPanel cartPanel;
    private JLabel totalPriceLabel;
    private HashMap<String, JPanel> categoryPanels;
    private HashMap<FoodItem, Integer> cartItems;
    private ArrayList<FoodItem> menuItems;
    private JComboBox<String> categoryComboBox;

    public CafeteriaMenuApp() {
        menuItems = fetchMenuItems();
        cartItems = new HashMap<>();
        initializeGUI();
    }

    private ArrayList<FoodItem> fetchMenuItems() {
        ArrayList<FoodItem> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM menu_items")) {
            while (rs.next()) {
                items.add(new FoodItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("category"),
                        rs.getString("image_path")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    private void initializeGUI() {
        setTitle("Cafeteria Menu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Category selection JComboBox
        categoryComboBox = new JComboBox<>(new String[]{"All", "Beverages", "Main Dishes", "Desserts"});
        categoryComboBox.setFont(new Font("Arial", Font.BOLD, 14));
        categoryComboBox.setBackground(Color.LIGHT_GRAY);
        categoryComboBox.addActionListener(e -> filterMenuItems());
        add(categoryComboBox, BorderLayout.NORTH);

        // Menu List
        listModel = new DefaultListModel<>();
        itemList = new JList<>(listModel);
        itemList.setCellRenderer(new MenuItemRenderer());
        itemList.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(itemList);
        add(scrollPane, BorderLayout.WEST);

        // Cart Panel with Background
        cartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon background = new ImageIcon("images/cart_background.jpg");
                Image bgImage = background.getImage();
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(200, 200, 200, 200));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        JScrollPane cartScrollPane = new JScrollPane(cartPanel);
        add(cartScrollPane, BorderLayout.CENTER);

        // Initialize category panels
        categoryPanels = new HashMap<>();
        for (String category : new String[]{"Beverages", "Main Dishes", "Desserts"}) {
            JLabel categoryLabel = new JLabel(category);
            categoryLabel.setFont(new Font("Arial", Font.BOLD, 16));
            categoryLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            categoryLabel.setForeground(Color.DARK_GRAY);
            cartPanel.add(categoryLabel);

            JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
            categoryPanel.setOpaque(false);
            categoryPanels.put(category, categoryPanel);
            cartPanel.add(categoryPanel);
        }

        // Total Price Label
        totalPriceLabel = new JLabel("Total Price: $0.00");
        totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalPriceLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        totalPriceLabel.setAlignmentX(LEFT_ALIGNMENT);
        totalPriceLabel.setForeground(Color.BLUE);
        cartPanel.add(totalPriceLabel);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton addButton = createStyledButton("Add to Cart");
        addButton.addActionListener(e -> addToCart());
        buttonPanel.add(addButton);

        JButton finishButton = createStyledButton("Finish Order");
        finishButton.addActionListener(e -> finishOrder());
        buttonPanel.add(finishButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadMenuItems();
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(Color.GREEN);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEtchedBorder());
        button.setPreferredSize(new Dimension(150, 40));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.DARK_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.GREEN);
            }
        });
        return button;
    }

    private void loadMenuItems() {
        listModel.clear();
        for (FoodItem item : menuItems) {
            listModel.addElement(item);
        }
        filterMenuItems(); // Ensure items are filtered on load
    }

    private void filterMenuItems() {
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        listModel.clear();

        for (FoodItem item : menuItems) {
            boolean matchesCategory = selectedCategory.equals("All") || item.getCategory().equals(selectedCategory);

            if (matchesCategory) {
                listModel.addElement(item);
            }
        }
    }

    private void addToCart() {
        FoodItem selectedItem = itemList.getSelectedValue();
        if (selectedItem != null) {
            cartItems.put(selectedItem, cartItems.getOrDefault(selectedItem, 0) + 1);
            updateCart();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item!", "Selection Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void updateCart() {
        for (JPanel categoryPanel : categoryPanels.values()) {
            categoryPanel.removeAll();
        }

        double totalPrice = 0.0;
        for (FoodItem item : cartItems.keySet()) {
            JPanel categoryPanel = categoryPanels.get(item.getCategory());
            if (categoryPanel != null) {
                categoryPanel.add(createCartItemPanel(item));
                totalPrice += item.getPrice() * cartItems.get(item);
            }
        }

        totalPriceLabel.setText(String.format("Total Price: $%.2f", totalPrice));
        cartPanel.revalidate();
        cartPanel.repaint();
    }

    private JPanel createCartItemPanel(FoodItem item) {
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        itemPanel.setOpaque(false);

        ImageIcon originalIcon = new ImageIcon("images/" + item.getImagePath());
        Image scaledImage = originalIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));

        int quantity = cartItems.get(item);
        JLabel nameLabel = new JLabel(item.getName() + " - $" + String.format("%.2f", item.getPrice()) + " (x" + quantity + ")");
        nameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        JLabel descriptionLabel = new JLabel(item.getDescription());
        descriptionLabel.setHorizontalAlignment(SwingConstants.LEFT);
        descriptionLabel.setFont(new Font("Arial", Font.ITALIC, 12));

        JButton removeButton = new JButton("Remove");
        removeButton.addActionListener(e -> removeFromCart(item));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(nameLabel);
        textPanel.add(descriptionLabel);
        textPanel.add(removeButton);

        itemPanel.add(imageLabel, BorderLayout.WEST);
        itemPanel.add(textPanel, BorderLayout.CENTER);

        return itemPanel;
    }

    private void removeFromCart(FoodItem item) {
        if (cartItems.containsKey(item)) {
            cartItems.remove(item);
            updateCart();
        }
    }

    private void finishOrder() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your cart is empty!", "Cart Status", JOptionPane.WARNING_MESSAGE);
        } else {
            StringBuilder orderSummary = new StringBuilder("Your order has been placed:\n\n");
            double totalPrice = 0.0;

            for (FoodItem item : cartItems.keySet()) {
                int quantity = cartItems.get(item);
                totalPrice += item.getPrice() * quantity;
                orderSummary.append(item.getName()).append(" (x").append(quantity).append(")\n");
            }

            orderSummary.append("\nTotal Price: $").append(String.format("%.2f", totalPrice));

            // Show confirmation message
            JOptionPane.showMessageDialog(this, orderSummary.toString(), "Order Confirmation", JOptionPane.INFORMATION_MESSAGE);

            // Clear the cart
            cartItems.clear();
            updateCart(); // Refresh the cart display
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CafeteriaMenuApp::new);
    }
}
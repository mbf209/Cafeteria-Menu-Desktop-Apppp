package gui;

import models.FoodItem;

import javax.swing.*;
import java.awt.*;

public class MenuItemRenderer extends JLabel implements ListCellRenderer<FoodItem> {
    private static final int IMAGE_WIDTH = 50; // Desired width
    private static final int IMAGE_HEIGHT = 50; // Desired height

    public MenuItemRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends FoodItem> list, FoodItem value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.getName() + " - $" + String.format("%.2f", value.getPrice()));

        // Scale the image to a fixed size
        ImageIcon originalIcon = new ImageIcon("images/" + value.getImagePath());
        Image scaledImage = originalIcon.getImage().getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(scaledImage)); // Set the scaled image as the icon

        // Adjust background and foreground colors for selection
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }
}
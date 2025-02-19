package models;

public class FoodItem {
    private int id;
    private String name;
    private String description;
    private double price;
    private String category;
    private String imagePath;

    public FoodItem(int id, String name, String description, double price, String category, String imagePath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imagePath = imagePath;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getImagePath() { return imagePath; }
}

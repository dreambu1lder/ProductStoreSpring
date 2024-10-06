package productstore.controller.dto.input;

public class ProductInputDTO {

    private String name;
    private double price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "ProductInputDTO{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}

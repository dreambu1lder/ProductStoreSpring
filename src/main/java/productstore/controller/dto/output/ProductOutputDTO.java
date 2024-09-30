package productstore.controller.dto.output;

import java.util.ArrayList;
import java.util.List;

public class ProductOutputDTO {

    private long id;
    private String name;
    private double price;
    private List<Long> orderIds = new ArrayList<>();

    public ProductOutputDTO() {}

    public ProductOutputDTO(long id, String name, double price, List<Long> orderIds) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.orderIds = orderIds;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public List<Long> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<Long> orderIds) {
        this.orderIds = orderIds;
    }

    @Override
    public String toString() {
        return "ProductOutputDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", orderIds=" + orderIds +
                '}';
    }
}

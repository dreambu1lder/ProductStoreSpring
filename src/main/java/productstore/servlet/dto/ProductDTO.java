package productstore.servlet.dto;

import java.util.List;

public class ProductDTO {

    private long id;
    private String name;
    private double price;
    private List<Long> orderIds;

    private ProductDTO(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.price = builder.price;
        this.orderIds = builder.orderIds;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public List<Long> getOrderIds() {return orderIds;}

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setOrderIds(List<Long> orderIds) {
        this.orderIds = orderIds;
    }

    public static class Builder {
        private long id;
        private String name;
        private double price;
        private List<Long> orderIds;

        public Builder withId(long id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withPrice(double price) {
            this.price = price;
            return this;
        }

        public Builder withOrderIds(List<Long> orderIds) {
            this.orderIds = orderIds;
            return this;
        }

        public ProductDTO build() {
            return new ProductDTO(this);
        }
    }
}

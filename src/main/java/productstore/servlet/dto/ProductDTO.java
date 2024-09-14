package productstore.servlet.dto;

public class ProductDTO {

    private long id;
    private String name;
    private double price;

    private ProductDTO(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.price = builder.price;
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

    public static class Builder {
        private long id;
        private String name;
        private double price;

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

        public ProductDTO build() {
            return new ProductDTO(this);
        }
    }
}

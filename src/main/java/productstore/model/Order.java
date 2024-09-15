package productstore.model;

import java.util.List;

public class Order {

    private long id;
    private User user;
    private List<Product> products;

    public Order() {}

    private Order(Builder builder) {
        this.id = builder.id;
        this.user = builder.user;
        this.products = builder.products;
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Builder toBuilder() {
        return new Builder()
                .withId(this.id)
                .withUser(this.user)
                .withProducts(this.products);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", user=" + user +
                ", products=" + products +
                '}';
    }

    public static class Builder {
        private long id;
        private User user;
        private List<Product> products;

        public Builder withId(long id) {
            this.id = id;
            return this;
        }

        public Builder withUser(User user) {
            this.user = user;
            return this;
        }

        public Builder withProducts(List<Product> products) {
            this.products = products;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}

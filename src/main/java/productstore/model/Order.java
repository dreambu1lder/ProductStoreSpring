package productstore.model;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private long id;
    private User user;
    private List<Product> products = new ArrayList<>();

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
        if (this.products == null) {
            this.products = new ArrayList<>();
        }
        return this.products; 
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setProducts(List<Product> products) {
        if (products != null) {
            this.products = new ArrayList<>(products); 
        } else {
            this.products = new ArrayList<>();
        }
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
                '}';
    }

    public static class Builder {
        private long id;
        private User user;
        private List<Product> products = new ArrayList<>();

        public Builder withId(long id) {
            this.id = id;
            return this;
        }

        public Builder withUser(User user) {
            this.user = user;
            return this;
        }

        public Builder withProducts(List<Product> products) {
            if (products != null) {
                this.products = new ArrayList<>(products); 
            } else {
                this.products = new ArrayList<>();
            }
            return this;
        }

        public Order build() {
            if (this.products == null) {
                this.products = new ArrayList<>();
            }
            return new Order(this);
        }
    }
}


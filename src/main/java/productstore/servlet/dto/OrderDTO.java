package productstore.servlet.dto;

import java.util.List;

public class OrderDTO {

    private long id;
    private long userId;
    private List<Long> productIds;
    private List<ProductDTO> productDTOS;

    private OrderDTO(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.productIds = builder.productIds;
        this.productDTOS = builder.productDTOS;
    }

    // Геттеры
    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public List<ProductDTO> getProductDTOS() {
        return productDTOS;
    }

    public static class Builder {
        private long id;
        private long userId;
        private List<Long> productIds;
        private List<ProductDTO> productDTOS;

        public Builder withId(long id) {
            this.id = id;
            return this;
        }

        public Builder withUserId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder withProductIds(List<Long> productIds) {
            this.productIds = productIds;
            return this;
        }

        public Builder withProductDTOS(List<ProductDTO> productDTOS) {
            this.productDTOS = productDTOS;
            return this;
        }

        public OrderDTO build() {
            return new OrderDTO(this);
        }
    }
}

package productstore.controller.dto.input;

import java.util.List;

public class OrderInputDTO {
    private Long id; 
    private long userId;
    private List<Long> productIds;

    public Long getId() {
        return id; 
    }

    public void setId(Long id) {
        this.id = id; 
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }

    @Override
    public String toString() {
        return "OrderInputDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", productIds=" + productIds +
                '}';
    }
}


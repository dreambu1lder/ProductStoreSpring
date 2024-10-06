package productstore.controller.dto.input;

import java.util.ArrayList;
import java.util.List;

public class OrderInputDTO {

    private Long userId;
    private List<Long> productIds = new ArrayList<>();

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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
                "userId=" + userId +
                ", productIds=" + productIds +
                '}';
    }
}

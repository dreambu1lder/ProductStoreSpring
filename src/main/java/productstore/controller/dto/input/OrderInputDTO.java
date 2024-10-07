package productstore.controller.dto.input;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class OrderInputDTO {

    @NotNull(message = "User ID cannot be null")
    private Long userId;
    @NotEmpty(message = "Product IDs cannot be empty")
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

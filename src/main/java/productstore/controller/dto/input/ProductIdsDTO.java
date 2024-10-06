package productstore.controller.dto.input;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

public class ProductIdsDTO {

    @NotEmpty(message = "Product IDs cannot be empty")
    @Size(min = 1, message = "There must be at least one product ID")
    List<Long> productIds = new ArrayList<>();

    public List<Long> getProductIds() {
        return productIds;
    }

    public void setProductIds(List<Long> productIds) {
        this.productIds = productIds;
    }

    @Override
    public String toString() {
        return "ProductIdsDTO{" +
                "productIds=" + productIds +
                '}';
    }
}

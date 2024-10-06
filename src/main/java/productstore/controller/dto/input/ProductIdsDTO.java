package productstore.controller.dto.input;

import java.util.ArrayList;
import java.util.List;

public class ProductIdsDTO {

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

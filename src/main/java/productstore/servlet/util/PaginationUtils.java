package productstore.servlet.util;

import jakarta.servlet.http.HttpServletRequest;

public class PaginationUtils {

    private PaginationUtils() {}

    public static int getPageNumber(HttpServletRequest req) {
        String pageNumberParam = req.getParameter("pageNumber");
        if (pageNumberParam != null && !pageNumberParam.isEmpty()) {
            int pageNumber = Integer.parseInt(pageNumberParam);
            if (pageNumber <= 0) {
                throw new NumberFormatException("Page number must be greater than 0");
            }
            return pageNumber;
        }
        return 1;
    }

    public static int getPageSize(HttpServletRequest req) {
        String pageSizeParam = req.getParameter("pageSize");
        if (pageSizeParam != null && !pageSizeParam.isEmpty()) {
            int pageSize = Integer.parseInt(pageSizeParam);
            if (pageSize <= 0) {
                throw new NumberFormatException("Page size must be greater than 0");
            }
            return pageSize;
        }
        return 10;
    }
}

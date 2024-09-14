package productstore.dao.util;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface GeneratedKeyHandler<T> {
    T handle(ResultSet generatedKeys) throws SQLException;
}

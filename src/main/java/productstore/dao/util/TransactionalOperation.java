package productstore.dao.util;

import java.sql.SQLException;

@FunctionalInterface
public interface TransactionalOperation<T> {
    T execute() throws SQLException;
}

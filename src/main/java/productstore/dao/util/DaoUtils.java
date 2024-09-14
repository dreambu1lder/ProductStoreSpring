package productstore.dao.util;

import productstore.db.DataBaseUtil;

import java.sql.*;

public class DaoUtils {

    public static <T> T executeInsert(String sql, PreparedStatementSetter setter, GeneratedKeyHandler<T> handler) throws SQLException {
        try(Connection connection = DataBaseUtil.getConnection();
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setter.setParameters(stmt);
            stmt.executeUpdate();
            try(ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                return handler.handle(generatedKeys);
            }
        }
    }

    public static <T> T executeInTransaction(Connection connection, TransactionalOperation<T> operation) throws SQLException {
        try {
            connection.setAutoCommit(false);
            T result = operation.execute();
            connection.commit();
            return result;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public static <T> T executeQuery(String sql, PreparedStatementSetter setter, ResultSetHandler<T> handler) throws SQLException {
        try (Connection connection = DataBaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            setter.setParameters(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                return handler.handle(rs);
            }
        }
    }

    public static void executeUpdate(String sql, PreparedStatementSetter setter) throws SQLException {
        try (Connection connection = DataBaseUtil.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            setter.setParameters(stmt);
            stmt.executeUpdate();
        }
    }
}

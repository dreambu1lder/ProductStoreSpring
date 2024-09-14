package productstore.dao;

public enum SqlQueries {

    INSERT_INTO("INSERT INTO %s (%s) VALUES (%s)"),
    SELECT_FROM("SELECT %s FROM %s WHERE %s"),
    SELECT_ALL_FROM("SELECT %s FROM %s"),
    SELECT_WITH_PAGINATION("SELECT %s FROM %s ORDER BY %s LIMIT %s OFFSET %s"),
    DELETE_FROM("DELETE FROM %s WHERE %s"),
    UPDATE_SET("UPDATE %s SET %s WHERE %s");

    private final String sql;

    SqlQueries(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}

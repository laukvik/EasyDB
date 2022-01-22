package no.laukvik.easydb;

import no.laukvik.easydb.exception.QueryException;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Query<K> {

    private final Class<K> klass;
    Integer limit = null;
    ArrayList<WhereColumn> columns = new ArrayList<>();
    ArrayList<SortColumn> sorts = new ArrayList<>();
    HashMap<String, Field> map;

    public Query(Class<K> klass) {
        this.klass = klass;
        this.map = Mapper.extractFields(klass);
    }

    Class<K> getQueryClass() {
        return klass;
    }

    PreparedStatement getPreparedStatement(DatabaseType databaseType, Connection connection) throws SQLException {
        PreparedStatement st = connection.prepareStatement(toSQL(databaseType), PreparedStatement.NO_GENERATED_KEYS);
        st.setInt(1, 0);
        int index = 1;
        for (WhereColumn it : columns) {
            st.setObject(index, it.value);
            index++;
        }
        return st;
    }

    String toSQL(DatabaseType databaseType) {
        StringBuffer buffer = new StringBuffer();
        String tableName = EasyDB.getModel(klass).table();
        buffer.append("SELECT * FROM " + tableName + " ");
        if (!columns.isEmpty()) {
            buffer.append("WHERE ");
            int index = 0;
            for (WhereColumn it : columns) {
                if (index > 0) {
                    buffer.append(" AND ");
                }
                buffer.append(it.toSQL());
                index++;
            }
            buffer.append(" ");
        }
        if (!sorts.isEmpty()) {
            buffer.append("ORDER BY ");
            int index = 0;
            for (SortColumn it : sorts) {
                if (index > 0) {
                    buffer.append(", ");
                }
                buffer.append(it.toSQL());
                index++;
            }
            buffer.append(" ");
        }
        if (limit != null) {
            if (databaseType == DatabaseType.Postgres){
                buffer.append("LIMIT " + limit);
            }
            if (databaseType == DatabaseType.ApacheDerby){
                buffer.append("FETCH FIRST " + limit + " ROWS ONLY ");
            }
        }
        return buffer.toString().trim();
    }

    public Query where(String column, Comparison comparison, Object value) {
        columns.add(new WhereColumn(column, comparison, value));
        return this;
    }

    public Query sort(String column, SortOrder order) {
        sorts.add(new SortColumn(column, order));
        return this;
    }

    public Query limit(int limit) {
        this.limit = limit;
        return this;
    }

    class WhereColumn {
        String column;
        Comparison comparison;
        Object value;

        WhereColumn(String column, Comparison comparison, Object value) {
            if (!map.containsKey(column)) {
                throw new QueryException("Invalid column: " + column);
            }
            this.column = column;
            this.comparison = comparison;
            this.value = value;
        }

        String toSQL() {
            return column + " " + comparisonSQL(comparison) + " ?";
        }

        private String comparisonSQL(Comparison comparison) {
            switch (comparison) {
                case Equal:
                    return "=";
                case NotEqual:
                    return "!=";
                case GreaterThan:
                    return ">";
                case GreaterThanOrEqual:
                    return ">=";
                case LessThan:
                    return "<";
                case LessThanOrEqual:
                    return "<=";
            }
            return null;
        }
    }

    class SortColumn {
        String column;
        SortOrder sortOrder;

        SortColumn(String column, SortOrder sortOrder) {
            if (!map.containsKey(column)) {
                throw new QueryException("Invalid column: " + column);
            }
            this.column = column;
            this.sortOrder = sortOrder;
        }

        String toSQL() {
            return column + " " + (sortOrder == SortOrder.Ascending ? "ASC" : "DESC");
        }
    }

}

enum Comparison {
    Equal, NotEqual, LessThan, LessThanOrEqual, GreaterThan, GreaterThanOrEqual
}

enum SortOrder {
    Ascending, Descending
}


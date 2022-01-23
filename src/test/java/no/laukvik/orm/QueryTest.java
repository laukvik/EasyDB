package no.laukvik.orm;

import no.laukvik.orm.exception.QueryException;
import org.junit.jupiter.api.Test;
import static no.laukvik.orm.SortOrder.Ascending;
import static no.laukvik.orm.SortOrder.Descending;
import static org.junit.jupiter.api.Assertions.*;

class QueryTest {

    Query mockQuery(){
        Query query = new Query(Employee.class)
                .where("EMPLOYEE_NR", Comparison.Equal, 12)
                .sort("EMPLOYEE_NR", Ascending)
                .limit(5);
        return query;
    }

    @Test
    void shouldQuery() {
        Query query = mockQuery();
        assertEquals(1, query.columns.size());
        assertEquals(1, query.sorts.size());
        assertEquals(5, query.limit);
    }

    @Test
    void shouldLimit() {
        Query query = new Query(Employee.class).limit(5);
        assertEquals("SELECT * FROM EMPLOYEE FETCH FIRST 5 ROWS ONLY", query.toSQL(DatabaseType.ApacheDerby));
        assertEquals("SELECT * FROM EMPLOYEE LIMIT 5", query.toSQL(DatabaseType.Postgres));
    }

    @Test
    void shouldSort() {
        Query query = new Query(Employee.class)
                .sort("ID", Ascending)
                .sort("CASH", Descending);
        assertEquals("SELECT * FROM EMPLOYEE ORDER BY ID ASC, CASH DESC", query.toSQL(DatabaseType.ApacheDerby));
    }

    @Test
    void shouldWhere() {
        Query query = new Query(Employee.class)
            .where("FIRST_NAME", Comparison.Equal, "Bill")
            .where("CASH", Comparison.GreaterThan, 350);

        assertEquals("SELECT * FROM EMPLOYEE WHERE FIRST_NAME = ? AND CASH > ?", query.toSQL(DatabaseType.ApacheDerby));
    }

    @Test
    void shouldValidateWhere() {
        assertThrows(QueryException.class, () -> {
            new Query(Employee.class).where("fake", Comparison.Equal, "Gates");
        });
    }

    @Test
    void shouldValidateSort() {
        assertThrows(QueryException.class, () -> {
            new Query(Employee.class).sort("fake", Ascending);
        });
    }

}
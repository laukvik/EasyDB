package no.laukvik.orm;

import no.laukvik.orm.exception.NoEntityException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EntityManagerTest {

    EntityManager db;

    @BeforeAll
    void before() throws SQLException {
        String url = "jdbc:derby:./target/Derby;create=true";
        String user = "";
        String password = "";
        Connection connection = DriverManager.getConnection(url, user, password);
        db = new EntityManager(DatabaseType.ApacheDerby, connection);
        try {
            db.deleteModel(Employee.class);
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        db.createModel(Employee.class);
    }

    @Test
    void shouldMapLocalDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 22);
        assertEquals(LocalDate.of(2022, 1, 22), EntityManager.mapLocalDate(cal.getTime()));
    }

    @Test
    void shouldFindAnnotation() {
        assertEquals("EMPLOYEE", EntityManager.getModel(Employee.class).table());
        assertThrows(NoEntityException.class, () -> {
            EntityManager.getModel(String.class).table();
        });
    }

    @Test
    void shouldCreateTable() {
        assertEquals("CREATE TABLE EMPLOYEE (EMPLOYEE_TYPE VARCHAR(20), created TIMESTAMP, EMPLOYEE_NR INT, hired DATE, ID INT NOT NULL GENERATED ALWAYS AS IDENTITY, CASH REAL, IS_ACTIVE BOOLEAN, FIRST_NAME VARCHAR(20), PRIMARY KEY(ID))", DDL.createTable(Employee.class));
    }

    @Test
    void shouldFindId() {
        Employee mock = mockEmployee();
        mock.id = 12345;
        assertEquals(mock.id, db.getPrimaryKeyValue(mock));
    }

    @Test
    void shouldRemoveAll() {
        assertDoesNotThrow(() ->  db.removeAll(Employee.class) );
    }

    @Test
    void shouldNotAdd() {
        assertThrows(NoEntityException.class, () -> {
            db.add(String.class);
        });
    }

    @Test
    void shouldAdd() throws SQLException {
        assertDoesNotThrow(() ->  db.removeAll(Employee.class) );
        Employee mock = mockEmployee();
        mock.active = false;
        mock.created = new Date();
        mock.hired = LocalDate.now();
        mock.type = EmployeeType.Boss;
        mock.employeeNr = 24124;
        assertDoesNotThrow(() -> {
            db.add(mock);
        } );
        Optional<Employee> op = db.findById(mock.id, Employee.class);
        assertTrue(op.isPresent());
        Employee employee = op.get();
        assertEquals(mock.id, employee.id);
        assertEquals(mock.employeeNr, employee.employeeNr);
        assertEquals(mock.first, employee.first);
        assertEquals(mock.active, employee.active);
        assertEquals(mock.hired, employee.hired);
        assertEquals(mock.created, employee.created);
        assertEquals(EmployeeType.Boss, employee.type);
    }

    @Test
    void shouldFindAll() throws SQLException {
        db.removeAll(Employee.class);
        Employee mock = mockEmployee();
        mock.active = false;
        mock.created = new Date();
        mock.hired = LocalDate.now();
        mock.type = EmployeeType.Boss;
        mock.employeeNr = 24124;
        db.add(mock);
        List<Employee> employees = db.findAll(Employee.class);
        assertEquals(1, employees.size());
        Employee employee = employees.get(employees.size() - 1);
        assertEquals(mock.id, employee.id);
        assertEquals(mock.active, employee.active);
        assertEquals(mock.created, employee.created);
        assertEquals(mock.first, employee.first);
        assertEquals(mock.hired, employee.hired);
        assertEquals(mock.type, employee.type);
        assertEquals(mock.employeeNr, employee.employeeNr);
    }

    @Test
    void shouldUpdate() throws SQLException {
        Employee mock = mockEmployee();
        assertDoesNotThrow(() ->  db.add(mock) );
        mock.employeeNr = 666;
        mock.type = EmployeeType.Genious;
        mock.hired = LocalDate.now();
        mock.first = "FirstFirstFirst";
        mock.created = new Date();
        mock.active = false;
        assertDoesNotThrow(() -> db.update(mock));
        Employee employee = (Employee) db.findById(mock.id, Employee.class).get();
        assertEquals(mock.id, employee.id);
        assertEquals(mock.employeeNr, employee.employeeNr);
        assertEquals(mock.first, employee.first);
        assertEquals(mock.active, employee.active);
        assertEquals(mock.hired, employee.hired);
        assertEquals(mock.created, employee.created);
        assertEquals(mock.type, employee.type);

    }

    @Test
    void shouldRemoveSingle() {
        Employee emp1 = mockEmployee();
        emp1.id = 5;
        Employee emp2 = mockEmployee();
        emp2.id = 6;
        assertDoesNotThrow(() -> db.remove(emp1) );
    }

    @Test
    void shouldFindById() throws SQLException {
        Employee mock = mockEmployee();
        mock.id = null;
        mock.employeeNr = 15;
        mock.type = EmployeeType.Normal;
        mock.hired = LocalDate.now();
        mock.created = new Date();
        mock.active = false;
        mock.first = "Last";
        assertDoesNotThrow(() ->  db.add(mock) );
        Integer resultID = mock.id;
        Employee employee = (Employee) db.findById(resultID, Employee.class).get();
        assertEquals(mock.id, employee.id);
        assertEquals(mock.first, employee.first);
        assertEquals(mock.active, employee.active);
        assertEquals(mock.type, employee.type);
        assertEquals(mock.employeeNr, employee.employeeNr);
    }

    @Test()
    void shouldFindByQuery() throws SQLException {
        for (int x=1; x<6; x++){
            Employee mock = mockEmployee();
            mock.id = null;
            mock.first = "First " + x;
            mock.cash = 1000f * x;
            db.add(mock);
        }
        Query query = new Query(Employee.class)
                .where("CASH", Comparison.GreaterThan, 2500)
                .sort("CASH", SortOrder.Descending)
                .limit(2);
        List<Employee> list = db.findByQuery(query);
        assertEquals(2, list.size());
        assertEquals(5000f, list.get(0).cash);
        assertEquals("First 5", list.get(0).first);
    }

    @Test()
    void shouldBuildReport() throws SQLException, IllegalAccessException {
        for (int x=1; x<6; x++){
            Employee mock = mockEmployee();
            mock.id = null;
            mock.first = "First " + x;
            mock.cash = 1000f * x;
            db.add(mock);
        }
        TimeReport report = new TimeReport();
        report.active = true;
        List list = db.buildReport(report);
        assertEquals(5, list.size());
    }

    Employee mockEmployee(){
        Employee emp = new Employee();
        emp.active = true;
        emp.created = new Date();
        emp.employeeNr = 515;
        emp.first = "Morten";
        emp.hired = LocalDate.now();
        emp.type = EmployeeType.Unknown;
        emp.cash = 25000.0f;
        return emp;
    }

}
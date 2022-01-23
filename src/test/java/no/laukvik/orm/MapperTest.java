package no.laukvik.orm;

import no.laukvik.orm.exception.MappingException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class MapperTest {

    @Test
    void shouldFailExtractFields(){
        Invalid obj = new Invalid();
        assertThrowsExactly(MappingException.class, ()-> {
           Mapper.extractFields(obj.getClass());
        });
    }

    @Test
    void shouldExtractFields() throws IllegalAccessException {
        Employee emp = mockEmployee();
        HashMap<String,Field> map = Mapper.extractFields(emp.getClass());
        Field f =  map.get("EMPLOYEE_NR");
        map.get("EMPLOYEE_NR").get(emp);

    }

    @Test
    void shouldExtractValues() {
        HashMap<String,Object> map = Mapper.extractData(mockEmployee());
        assertEquals(true, map.get("IS_ACTIVE"));
        assertEquals(new Date(2020,1,1,0,0,0), map.get("created"));
        assertEquals(515, map.get("EMPLOYEE_NR"));
        assertEquals("Morten", map.get("FIRST_NAME"));
        assertEquals(LocalDate.of(2004,1,1), map.get("hired"));
        assertEquals(EmployeeType.Boss, map.get("EMPLOYEE_TYPE"));
        assertEquals(7, map.size());
    }



    Employee mockEmployee(){
        Employee emp = new Employee();
        emp.active = true;
        emp.created = new Date(2020,1,1,0,0,0);
        emp.employeeNr = 515;
        emp.first = "Morten";
        emp.hired = LocalDate.of(2004,1,1);
        emp.type = EmployeeType.Boss;
        return emp;
    }

}
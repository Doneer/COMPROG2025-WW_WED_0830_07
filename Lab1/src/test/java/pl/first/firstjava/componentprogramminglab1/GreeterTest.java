/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package pl.first.firstjava.componentprogramminglab1;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author zhuma
 */
public class GreeterTest {
    
    @Test
    public void testGreet() {
        System.out.println("greet");
        
        //test with null
        String name = null;
        Greeter instance = new Greeter();
        String result = instance.greet(name);
        String expectedResult = "Hello null";
        assertEquals(expectedResult, result);
        
        //test with empty string
        name = "";
        result = instance.greet(name);
        expectedResult = "Hello ";
        assertEquals(expectedResult, result);
        
        //test with Student
        name = "Student";
        result = instance.greet(name);
        expectedResult = "Hello Student";
        assertEquals(expectedResult, result);
        
        assertSame(name, instance.getLastWho());
    }
 
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pl.first.firstjava.componentprogramminglab1;

/**
 *
 * @author zhuma
 */
public class App {
    public static void main(final String[] args) {
        Greeter greeter = new Greeter();
        
        // Check if an argument was provided
        if (args.length > 0) {
            System.out.println(greeter.greet(args[0]));
        } else {
            System.out.println("No name provided. Usage: java -cp target pl.first.firstjava.App <name>");
        }
    }
}

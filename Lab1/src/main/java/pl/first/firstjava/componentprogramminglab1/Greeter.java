/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pl.first.firstjava.componentprogramminglab1;

/**
 *
 * @author zhuma
 */
public class Greeter {
    private MessageFormatter formatter;
    private String lastWho;
    
    public Greeter(MessageFormatter formatter) {
        this.formatter = formatter;
    }
    
    public Greeter() {
        this.formatter = new MessageFormatter();
    }
    
    public String greet(String who) {
        this.lastWho = formatter.format(who);
        return "Hello " + this.lastWho;
    }
    
    public String getLastWho() {
        return lastWho;
    }
}

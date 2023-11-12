package core;

import java.util.HashMap;
import java.util.Map;

public class Employee {
    private String id; // You can keep certain common fields as fixed attributes
    private String name;
    private String lastname;// Another common field
    //Because employee characteristics varies greatly based on organization
    private Map<String, Object> attributes; // A map to hold any number of additional attributes

    public Employee(String id, String name, String lastname) {
        this.id = id;
        this.name = name;
        this.lastname = lastname;
        this.attributes = new HashMap<>();
    }

    // Getters and setters for fixed attributes
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    

    public String getlastName() {
        return lastname;
    }

    public void setlastName(String lastname) {
        this.lastname = lastname;
    }


    // Methods to manage dynamic attributes
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    public Object removeAttribute(String key) {
        return attributes.remove(key);
    }

    // Example usage of a toString method to print out the employee's properties
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Employee{id='").append(id).append("', name='").append(name).append("', attributes=").append(attributes).append('}');
        return sb.toString();
    }
}
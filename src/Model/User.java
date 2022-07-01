package Model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.concurrent.ThreadLocalRandom;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = Developer.class, name = "developer")})
public abstract class User {
    private static boolean masterCreated = false;
    private String id;
    private String name;
    private String password;
    private AccessLevel accessLevel;

    public User(String name, String password, AccessLevel accessLevel) throws IllegalArgumentException {
        if (name.isBlank() || password.isBlank()) {
            throw new IllegalArgumentException("Name and password cannot be blank. ");
        }

        // replaceAll taken from https://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java
        String idName = name.replaceAll("\\s","");
        // ThreadLocalRandom taken from https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
        do {
            int idNumber = ThreadLocalRandom.current().nextInt(1, 1000);
            id = idName + "#" + idNumber;
        } while (Users.getUser(id) != null);
        this.name = name;
        this.password = password;
        this.accessLevel = accessLevel;
    }

    public User() {
        name = "Unnamed User";
        password = "default";
        accessLevel = AccessLevel.Default;
        // replaceAll taken from https://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java
        String idName = name.replaceAll("\\s","");
        // ThreadLocalRandom taken from https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
        do {
            int idNumber = ThreadLocalRandom.current().nextInt(1, 1000);
            id = idName + "#" + idNumber;
        } while (Users.getUser(id) != null);
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public String getID() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public void setAccessLevel(AccessLevel accessLevel) {
        this.accessLevel = accessLevel;
    }

    public AccessLevel getAccessLevel() {
        return accessLevel;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof User && (id.equals(((User) object).getID()));
    }

    public int hashCode() {
        return id.hashCode();
    }

    public abstract String toString();

    private void setID(String id) {
        this.id = id;
    }

    public void createMaster(String id, AccessLevel accessLevel) {
        if (masterCreated) {
            throw new RuntimeException("Only one master user allowed! ");
        }
        setID(id);
        setAccessLevel(accessLevel);
        masterCreated = true;
    }
}
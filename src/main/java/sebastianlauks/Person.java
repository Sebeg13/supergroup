package sebastianlauks;

import java.util.HashSet;

public class Person implements Comparable<Person> {
    private String name;
    private HashSet<Person> friends = new HashSet<Person>();

    Person(String name) {
        this.name = name;
    }


    void addFriend(Person newFriend) {
        friends.add(newFriend);
    }


    HashSet<Person> getFriends() {
        return friends;
    }

    private String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }


    public int compareTo(Person person) {
        return this.name.compareTo(person.getName());
    }
}

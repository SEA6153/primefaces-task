/*
 * The TestObject class represents a data model used to store information about a music item,
 * including details like the song title, artist, release year, and the first and last names of the person related to the song.
 * It implements Serializable, making it suitable for session management and data transfer.
 * Each TestObject instance is assigned a unique identifier (UUID) when created, and it includes validation
 * logic to ensure all required fields are populated before it can be considered valid.
 */


package org.primefaces.test;

import java.io.Serializable;
import java.util.UUID;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode

public class TestObject implements Serializable {

    private String id;
    private String firstName;
    private String lastName;
    private String song;
    private String artist;
    private Integer released;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Integer getReleased() {
        return released;
    }

    public void setReleased(Integer released) {
        this.released = released;
    }

    public TestObject(String firstName, String lastName, String song, String artist, Integer released) {
        this.id = UUID.randomUUID().toString();
        this.firstName = firstName;
        this.lastName = lastName;
        this.song = song;
        this.artist = artist;
        this.released = released;
    }

    public TestObject() {
    }

    public TestObject(TestObject item) {
    }

    public boolean isValid() {
        return firstName != null && !firstName.isEmpty() &&
                lastName != null && !lastName.isEmpty() &&
                song != null && !song.isEmpty() &&
                artist != null && !artist.isEmpty() &&
                released != null;
    }

}

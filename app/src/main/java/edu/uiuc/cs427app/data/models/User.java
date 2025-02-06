package edu.uiuc.cs427app.data.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.UUID;

// User entity stores the user information
// It has the following fields:
// - id: UUID (Primary Key)
// - username: String (Username of the user)
// - password: String (Password of the user, stored as a hash)
// - email: String (Email of the user)
// - firstName: String (First name of the user)
// - lastName: String (Last name of the user)
// - themeColor: String (The user's preferred UI color)
// - themeMOde: String (The user's preference of Light/Dark mode)
// The entity is named "users" and the table name is "users"
@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private UUID id;

    @ColumnInfo(name = "username")
    private String username;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "First Name")
    private String firstName;

    @ColumnInfo(name = "Last Name")
    private String lastName;

    //TODO: Add theme field to the User entity to store the user's preferred theme
    @ColumnInfo(name = "themeColor")
    private String themeColor;

    @ColumnInfo(name = "themeMode")
    private String themeMode;

    // Constructors for User

    /**
     * constructor
     * @param username the username
     * @param password the passowrd
     * @param email users email
     * @param firstName the first name
     * @param lastName the last name
     * @param themeColor the color theme
     * @param themeMode the mode (light or dark)
     */
    @Ignore
    public User(String username, String password, String email, String firstName, String lastName, String themeColor, String themeMode) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.themeColor = themeColor;
        this.themeMode = themeMode;
    }

    /**
     * constructor
     * @param username the username
     * @param password the passowrd
     * @param email users email
     * @param firstName the first name
     * @param lastName the last name
     */
    @Ignore
    public User(String username, String password, String email, String firstName, String lastName) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * constructor
     * @param id  the user id
     * @param username the username
     * @param password the passowrd
     * @param email users email
     * @param firstName the first name
     * @param lastName the last name
     */
    public User(UUID id, String username, String password, String email, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * gets the id
     * @return the id
     */
    public UUID getId() {
        return id;
    }

    /**
     * get the username
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * sets the username
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * get the password
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * sets the password
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * get the email
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * sets the email
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * get the first name
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * sets the first name
     * @param firstName the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * gets the last name
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * sets the last name
     * @param lastName the last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * gets the full name
     * @return the full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * gets the theme color
     * @return the theme color
     */
    public String getThemeColor() {
        return themeColor;
    }

    /**
     * gets the theme mode
     * @return the theme mode
     */
    public String getThemeMode() {
        return themeMode;
    }

    /**
     * gets the theme color
     * @param themeColor the theme color
     */
    public void setThemeColor(String themeColor) {
        this.themeColor = themeColor;
    }

    /**
     * sets the theme mode
     * @param themeMode the theme mode
     */
    public void setThemeMode(String themeMode) {
        this.themeMode = themeMode;
    }

}

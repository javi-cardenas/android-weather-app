package edu.uiuc.cs427app.data.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;

import java.util.UUID;

import lombok.ToString;

// Locations entity stores the city location added by the user
// It has a foreign key to the User entity
// It has the following fields:
// - id: UUID (Primary Key)
// - userId: UUID (Foreign Key) (References User.id)
// - city: String (City name for display)
// - latitude: double (Latitude of the city location)
// - longitude: double (Longitude of the city location)
// - address: String (Full address of the city location including city, state, and country)
// The entity is named "locations" and the table name is "locations"
@Entity(
        tableName = "locations",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {
                @Index(value = "id"),
                @Index(value = "user_id")
        }
)
@ToString
public class Location {
    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private UUID id;

    @ColumnInfo(name = "user_id")
    private UUID userId;

    @ColumnInfo(name = "city")
    private String city; // City name for display

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "address")
    private String address; // Full address of the city location including city, state, and country

    /**
     * Constructors for Location
     * @param userId the user id
     * @param city the city
     * @param latitude the longitude
     * @param longitude the latitude
     * @param address the address
     */
    @Ignore
    public Location(UUID userId, String city, double latitude, double longitude, String address) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

     /**
     * Constructors for Location
      *  @param id the id of the entity
     * @param userId the user id
     * @param city the city
     * @param latitude the longitude
     * @param longitude the latitude
     * @param address the address
     */
    public Location(UUID id, UUID userId, String city, double latitude, double longitude, String address) {
        this.id = id;
        this.userId = userId;
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    /**
     * Gets the id of the location
     * @return the id
     */
    public UUID getId() {
        return id;
    }

    /**
     * returns the user id
     * @return the uuid of the user
     */
    public UUID getUserId() {
        return userId;
    }

    /**
     * returns the city
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * returns the latitude
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * returns the longitude
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * returns the address
     * @return the address
     */
    public String getAddress() {
        return address;
    }
}

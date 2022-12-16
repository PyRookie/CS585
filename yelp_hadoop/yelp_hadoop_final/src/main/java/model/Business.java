package model;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Business {

    @SerializedName("business_id")
    @Expose
    public String businessId;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("address")
    @Expose
    public String address;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("state")
    @Expose
    public String state;
    @SerializedName("postal code")
    @Expose
    public String postalCode;
    @SerializedName("latitude")
    @Expose
    public Double latitude;
    @SerializedName("longitude")
    @Expose
    public Double longitude;
    @SerializedName("stars")
    @Expose
    public Double stars;
    @SerializedName("review_count")
    @Expose
    public Integer reviewCount;
    @SerializedName("is_open")
    @Expose
    public Integer isOpen;
    @SerializedName("categories")
    @Expose
    public String categories = null;
    @SerializedName("hours")
    @Expose
    public Hours hours;
    @SerializedName("attributes")
    @Expose
    public Attributes attributes;

}
package model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Review {

    @SerializedName("review_id")
    @Expose
    public String reviewId;
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("business_id")
    @Expose
    public String businessId;
    @SerializedName("stars")
    @Expose
    public Integer stars;
    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("text")
    @Expose
    public String text;
    @SerializedName("useful")
    @Expose
    public Integer useful;
    @SerializedName("funny")
    @Expose
    public Integer funny;
    @SerializedName("cool")
    @Expose
    public Integer cool;

}
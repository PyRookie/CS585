package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("jsonschema2pojo")
public class Hours
{
    @SerializedName("Monday")
    @Expose
    public String monday;
    @SerializedName("Tuesday")
    @Expose
    public String tuesday;
    @SerializedName("Friday")
    @Expose
    public String friday;
    @SerializedName("Wednesday")
    @Expose
    public String wednesday;
    @SerializedName("Thursday")
    @Expose
    public String thursday;
    @SerializedName("Sunday")
    @Expose
    public String sunday;
    @SerializedName("Saturday")
    @Expose
    public String saturday;
}
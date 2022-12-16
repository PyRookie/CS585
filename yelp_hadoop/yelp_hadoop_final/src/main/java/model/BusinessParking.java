package model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class BusinessParking
{
    @SerializedName("garage")
    @Expose
    public Boolean garage;
    @SerializedName("street")
    @Expose
    public Boolean street;
    @SerializedName("validated")
    @Expose
    public Boolean validated;
    @SerializedName("lot")
    @Expose
    public Boolean lot;
    @SerializedName("valet")
    @Expose
    public Boolean valet;
}
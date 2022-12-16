import com.google.gson.Gson;
import model.Business;

public class Test
{
    public static void main(String[] args)
    {
        Gson gson = new Gson();

        // Business business = gson.fromJson("{\"business_id\":\"JNWefn5E1m_lU0UyVFZ2Yw\",\"name\":\"Meineke Car Care Center\",\"address\":\"13311 W Hillsborough Ave\",\"city\":\"Tampa\",\"state\":\"FL\",\"postal_code\":\"33635\",\"latitude\":28.0312684994,\"longitude\":-82.6395899417,\"stars\":4.5,\"review_count\":11,\"is_open\":1,\"attributes\":{\"WiFi\":\"u'free'\",\"BusinessAcceptsCreditCards\":\"True\",\"ByAppointmentOnly\":\"False\"},\"categories\":\"Automotive, Auto Repair, Tires, Oil Change Stations\",\"hours\":{\"Monday\":\"7:30-18:0\",\"Tuesday\":\"7:30-18:0\",\"Wednesday\":\"7:30-18:0\",\"Thursday\":\"7:30-18:0\",\"Friday\":\"7:30-18:0\",\"Saturday\":\"7:30-16:0\"}}", Business.class);

        Business business = gson.fromJson("{\"business_id\":\"SMYXOLPyM95JvZ-oqnsWUA\",\"name\":\"A A Berlin Glass & Mirror Co\",\"address\":\"60 W White Horse Pike\",\"city\":\"Berlin\",\"state\":\"NJ\",\"postal_code\":\"08009\",\"latitude\":39.8004163,\"longitude\":-74.9371806,\"stars\":3.0,\"review_count\":5,\"is_open\":1,\"attributes\":null,\"categories\":null,\"hours\":null}", Business.class);

        System.out.println(business.businessId);
    }
}

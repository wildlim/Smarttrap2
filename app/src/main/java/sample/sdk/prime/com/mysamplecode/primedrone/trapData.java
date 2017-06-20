package sample.sdk.prime.com.mysamplecode.primedrone;

/**
 * Created by Ngtims-01 on 2017-04-27.
--dev1--
 */

public class trapData {
    private double latitude;
    private double longitude;
    private double altitude;
    private int id;

    public trapData(int id , double latitude, double longitude, double altitude){
        this. id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public int getid(){
        return this.id;
    }
    public double getLatitude(){
        return this.latitude;
    }
    public double getLongitude(){
        return this.longitude;
    }
    public double getAltitude(){
        return this.altitude;
    }

}

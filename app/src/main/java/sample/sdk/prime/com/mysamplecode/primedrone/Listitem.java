package sample.sdk.prime.com.mysamplecode.primedrone;

public class Listitem {
    private String[] mData;

    public Listitem(String[] data){
        mData = data;
    }

    public Listitem(int id, double latitude, double longitude, double altitude){
        mData = new String[4];
        mData[0] = id+"";
        mData[1] = latitude+"";
        mData[2] = longitude+"";
        mData[3] = altitude+"";
        //mData[3] = status+"";

    }

    public String[] getData(){
        return mData;
    }
    public String getData(int index){
        return mData[index];
    }

    public void setData(String[] data){
        mData = data;
    }
}

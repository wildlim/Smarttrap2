package sample.sdk.prime.com.mysamplecode.primedrone;

import android.graphics.Bitmap;

/**
 * Created by JungYoungHoon on 2017-06-27.
 */

public class ImageData {
    String name;
    Boolean air_img;

    public ImageData(String name,Boolean air_img) {
        // TODO Auto-generated constructor stub
        //생성자함수로 전달받은 Member의 정보를 멤버변수에 저장..
        this.name = name;
        this.air_img = air_img;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAir_img(Boolean air_img){
        this.air_img = air_img;
    }
    public String getName() {
        return name;
    }
    public Boolean getAir_img(){
        return air_img;
    }
}

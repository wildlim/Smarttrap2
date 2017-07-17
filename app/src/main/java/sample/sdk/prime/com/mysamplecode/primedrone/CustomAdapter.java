package sample.sdk.prime.com.mysamplecode.primedrone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import sample.sdk.prime.com.mysamplecode.R;
import java.util.ArrayList;


/**
 * Created by JungYoungHoon on 2017-06-27.
 */

public class CustomAdapter extends BaseAdapter {
    ArrayList<ImageData> datas;
    LayoutInflater inflater;

    public CustomAdapter(LayoutInflater inflater, ArrayList<ImageData> datas) {
        // TODO Auto-generated constructor stub
        this.datas= datas;
        this.inflater= inflater;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return datas.size(); //datas의 개수를 리턴
    }
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return datas.get(position);//datas의 특정 인덱스 위치 객체 리턴.
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.simple_list_item, null);
        }
        TextView text_name = (TextView) convertView.findViewById(R.id.file_name);
        ImageView bitimg = (ImageView) convertView.findViewById(R.id.Bitmapid);
        Context ctx = bitimg.getContext();

        if(datas.get(position).getAir_img()){
            int img_id = ctx.getResources().getIdentifier("aircraft","drawable",ctx.getPackageName());
            bitimg.setImageResource(img_id);

        }else{
            int img_id = ctx.getResources().getIdentifier("aircraft","drawable",ctx.getPackageName());
            //bitimg.setImageResource(img_id);
        }
        text_name.setText(datas.get(position).getName());
        return convertView;
    }
}

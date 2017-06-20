package sample.sdk.prime.com.mysamplecode.primedrone;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import sample.sdk.prime.com.mysamplecode.R;

/**
 * Created by Ngtims-01 on 2017-05-07.
 */

public class MissionNotification {
    missionView.Status status;
    Activity activity;
    public MissionNotification(missionView.Status status, Activity activity){
        this.status = status;
        this.activity = activity;
    }

    public Notification makeNotification(String id, Intent noIntent){
        int icon = R.drawable.aircraft;
        CharSequence tickerText = "Hello";
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon,tickerText,when);

        Context context = activity.getApplicationContext();
        String title = "Mission Message";
        String text;
        switch(status){
            case NONE:
                text = "trap id : " + id + " 가 대기 중입니다.";
                break;
            case ARRIVED:
                text = "trap id : " + id + " 의 위치로 날아가는중 입니다.";
                break;
            case SOCK_BINDED:
                text = "trap id : " + id + " 에 도착하여 연결에 성공했습니다.";
                break;
            case SOCK_CLOSED:
                text = "trap id : " + id + " 와 데이터 전송을 완료하고 연결을 종료했습니다.";
                break;
            case ERR_SOCK_CLOSED:
                text = "trap id : " + id + " 의 소켓과 연결시도중 에러가 발생했습니다.\n 재연결을 시도합니다.";
                break;
            case ERR_UNEXPECTED:
                text = "trap id : " + id + " 와 통신중 예기치 않은 에러가 발생했습니다.\n 재연결을 시도합니다.";
                break;
            case ERR_CONN_TRY:
                text = "trap id : " + id + " 와 연결중 에러가 발생했습니다.\n 재연결을 시도합니다.";
                break;
            case ERR_FILE_NOT_CREATED:
                text = "trap id : " + id + " 에서 전송받은 파일이 정상적으로 만들어지지 않았습니다.\n 재연결을 시도합니다.";
        }
        Intent notificationIntent = noIntent;
        return notification;
    }
}

        package sample.sdk.prime.com.mysamplecode.primedrone;

        import android.app.Activity;
        import android.app.NotificationManager;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.pm.ActivityInfo;
        import android.os.AsyncTask;
        import android.os.Handler;
        import android.os.Looper;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.util.StringBuilderPrinter;
        import android.view.KeyEvent;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.Toast;

        import java.io.BufferedReader;
        import java.io.InputStreamReader;
        import java.io.OutputStreamWriter;
        import java.net.URL;
        import java.net.URLConnection;
        import java.net.URLEncoder;
        import java.util.Timer;
        import java.util.TimerTask;

        import sample.sdk.prime.com.mysamplecode.R;

        import static sample.sdk.prime.com.mysamplecode.primedrone.missionView.Status.NONE;

public class missionView extends AppCompatActivity {
    private String tspRoute;
    private ListView trapListView;
    private final String GET_STATUS = "http://220.149.235.139/getStatus.php";

    enum Status {NONE, ARRIVED, SOCK_BINDED, SOCK_CLOSED, ERR_SOCK_CLOSED, ERR_UNEXPECTED, ERR_CONN_TRY, ERR_FILE_NOT_CREATED, RUNNING};
    public Status status = Status.RUNNING;



    phpStatus task;
    Context context;


    @Override
    public void onBackPressed(){
        timertask.cancel();
        super.onBackPressed();
    }

    TimerTask timertask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_view);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        context = this;
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        trapListView = (ListView)findViewById(R.id.trapListView);

        Intent intent = getIntent();
        tspRoute = intent.getStringExtra("tspRoute");
        Log.e("tspRoute",tspRoute);

        String[] tspTrapList = tspRoute.split(",");

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,tspTrapList);



        trapListView.setAdapter(adapter);

        final Handler mHandler = new Handler(Looper.getMainLooper());

        Timer timer = new Timer();
        final String id = "1";

        timertask = new TimerTask() {
            Status currentStat = Status.RUNNING;

            @Override
            public void run() {
                task = new phpStatus();
                task.execute(id);
                if(currentStat!=status) {
                    switch (status) {
                        case NONE:
                            currentStat = status;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.setMessage("trap id : " + id + " (이)가 대기 중입니다.");
                                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).create().show();
                                }
                            });
                            break;
                        case ARRIVED:
                            currentStat = status;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.setMessage("trap id : " + id + " 의 위치로 날아가는중 입니다.");
                                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).create().show();
                                }
                            });
                            break;
                        case SOCK_BINDED:
                            currentStat = status;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.setMessage("trap id : " + id + " 에 도착하여 연결에 성공했습니다.");
                                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).create().show();
                                }
                            });
                            //this.cancel();
                            break;
                        case SOCK_CLOSED:
                            currentStat = status;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.setMessage("trap id : " + id + " 와(과) 데이터 전송을 완료하고 연결을 종료했습니다.");
                                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).create().show();
                                }
                            });
                            this.cancel();
                            break;
                        case ERR_SOCK_CLOSED:
                            currentStat = status;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.setMessage("trap id : " + id + " 의 소켓과 연결시도중 에러가 발생했습니다.\n 재연결을 시도합니다.");
                                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).create().show();
                                }
                            });
                            status = Status.ARRIVED;
                            break;
                        case ERR_UNEXPECTED:
                            currentStat = status;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.setMessage("trap id : " + id + " 와(과) 통신중 예기치 않은 에러가 발생했습니다.\n 재연결을 시도합니다.");
                                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).create().show();
                                }
                            });
                            break;
                        case ERR_CONN_TRY:
                            currentStat = status;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.setMessage("trap id : " + id + " 와(과) 연결중 에러가 발생했습니다.\n 재연결을 시도합니다.");
                                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).create().show();
                                }
                            });
                            break;
                        case ERR_FILE_NOT_CREATED:
                            currentStat = status;
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.setMessage("trap id : " + id + " 에서 전송받은 파일이 정상적으로 만들어지지 않았습니다.\n 재연결을 시도합니다.");
                                    dialog.setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).create().show();
                                }
                            });
                            break;


                    }
                }
            }

        };
        timer.schedule(timertask,1000,1000);
    }

    private class phpStatus extends AsyncTask<String, Integer , String>{

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder sb = new StringBuilder();
            try{
                String trapId = (String)strings[0];
                String data = URLEncoder.encode("id","UTF-8") + "=" + URLEncoder.encode(trapId,"UTF-8");
                URL url = new URL(GET_STATUS);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                OutputStreamWriter ow = new OutputStreamWriter(conn.getOutputStream());

                ow.write(data);
                ow.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = null;

                while((line = reader.readLine())!=null){
                    sb.append(line);
                    break;
                }
                Log.e("getStatus","id["+trapId+"] status : "+sb.toString());
                return sb.toString();
            }catch(Exception e){
                e.getStackTrace();
                return "get Status failed";
            }
        }

        @Override
        protected void onPostExecute(String trapStatus) {
            String stat = trapStatus;
            if(!stat.equals(status.toString())) {
                switch (stat) {
                    case "0":
                        Log.e("postExcute","status changed : " + stat);
                        status = missionView.Status.NONE;
                        break;
                    case "1":
                        Log.e("postExcute","status changed : " + stat);
                        status = missionView.Status.ARRIVED;
                        break;
                    case "2":
                        Log.e("postExcute","status changed : " + stat);
                        status = missionView.Status.SOCK_BINDED;
                        break;
                    case "3":
                        Log.e("postExcute","status changed : " + stat);
                        status = missionView.Status.SOCK_CLOSED;
                        break;
                    case "4":
                        Log.e("postExcute","status changed : " + stat);
                        status = missionView.Status.ERR_SOCK_CLOSED;
                        break;
                    case "5":
                        Log.e("postExcute","status changed : " + stat);
                        status = missionView.Status.ERR_UNEXPECTED;
                        break;
                    case "6":
                        Log.e("postExcute","status changed : " + stat);
                        status = missionView.Status.ERR_CONN_TRY;
                        break;
                    case "7":
                        Log.e("postExcute","status changed : " + stat);
                        status = missionView.Status.ERR_FILE_NOT_CREATED;
                        break;
                }
            }
        }
    }
}
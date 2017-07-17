package sample.sdk.prime.com.mysamplecode.primedrone;

import sample.sdk.prime.com.mysamplecode.R;
import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.pm.ActivityInfo;
        import android.os.AsyncTask;
        import android.os.Build;
        import android.os.Environment;
        import android.os.Handler;
        import android.os.Looper;
        import android.support.annotation.RequiresApi;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.widget.ArrayAdapter;
        import android.widget.ListView;
        import android.widget.TableLayout;
        import android.widget.TableRow;
        import android.widget.Toast;
        import java.io.BufferedReader;
        import java.io.FileInputStream;
        import java.io.InputStreamReader;
        import java.io.OutputStreamWriter;
        import java.io.Reader;
        import java.net.URL;
        import java.net.URLConnection;
        import java.net.URLEncoder;
        import java.util.ArrayList;
        import java.util.Timer;
        import java.util.TimerTask;
        import android.graphics.SurfaceTexture;
        import android.view.TextureView;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.TextureView.SurfaceTextureListener;
        import android.widget.Button;
        import android.widget.CompoundButton;
        import android.widget.TextView;
        import android.widget.ToggleButton;
        import dji.common.camera.SettingsDefinitions;
        import dji.common.camera.SystemState;
        import dji.common.error.DJIError;
        import dji.common.product.Model;
        import dji.common.util.CommonCallbacks;
        import dji.sdk.base.BaseProduct;
        import dji.sdk.camera.Camera;
        import dji.sdk.camera.VideoFeeder;
        import dji.sdk.codec.DJICodecManager;
        import sample.sdk.prime.com.mysamplecode.internal.controller.DJISampleApplication;

public class missionView extends AppCompatActivity implements SurfaceTextureListener,OnClickListener{
    private String tspRoute;
    private ListView trapListView;
    private final String GET_STATUS = "http://220.149.235.139/getStatus.php";

    enum Status {NONE, ARRIVED, SOCK_BINDED, SOCK_CLOSED, ERR_SOCK_CLOSED, ERR_UNEXPECTED, ERR_CONN_TRY, ERR_FILE_NOT_CREATED, RUNNING};
    public Status status = Status.RUNNING;
    phpStatus task;
    Context context;
    TimerTask timertask;
    protected VideoFeeder.VideoDataCallback mReceivedVideoDataCallBack = null;

    // Codec for video live view
    protected DJICodecManager mCodecManager = null;

    protected TextureView mVideoSurface = null;
    private Button mCaptureBtn, mShootPhotoModeBtn, mRecordVideoModeBtn;
    private ToggleButton mRecordBtn;
    private TextView recordingTime;

    private Handler handler;
    private String dbpath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/trapdb";
    private String dbFilePath = "/trapdb.txt";
    private String[] tspTrapList;
    private ArrayAdapter<String> adapter;
    private ArrayList<ImageData> datas = new ArrayList<ImageData>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_view2);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        handler = new Handler();
        initUI();

        // The callback for receiving the raw H264 video data for camera live view
        mReceivedVideoDataCallBack = new VideoFeeder.VideoDataCallback() {

            @Override
            public void onReceive(byte[] videoBuffer, int size) {
                if (mCodecManager != null) {
                    mCodecManager.sendDataToDecoder(videoBuffer, size);
                }
            }
        };
        Camera camera = DJISampleApplication.getCameraInstance();

        if (camera != null) {
            camera.setSystemStateCallback(new SystemState.Callback() {
                @Override
                public void onUpdate(SystemState cameraSystemState) {
                    if (null != cameraSystemState) {

                        int recordTime = cameraSystemState.getCurrentVideoRecordingTimeInSeconds();
                        int minutes = (recordTime % 3600) / 60;
                        int seconds = recordTime % 60;

                        final String timeString = String.format("%02d:%02d", minutes, seconds);
                        final boolean isVideoRecording = cameraSystemState.isRecording();

                        missionView.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                recordingTime.setText(timeString);

                                /*
                                 * Update recordingTime TextView visibility and mRecordBtn's check state
                                 */
                                if (isVideoRecording) {
                                    recordingTime.setVisibility(View.VISIBLE);
                                } else {
                                    recordingTime.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }
                }
            });
        }

            context = this;
            final AlertDialog.Builder dialog = new AlertDialog.Builder(context);


            final Handler mHandler = new Handler(Looper.getMainLooper());
            Timer timer = new Timer();
            final String id = "1";
            timertask = new TimerTask() {
                Status currentStat = Status.RUNNING;

                @Override
                public void run() {
                    task = new phpStatus();
                    task.execute(id);
                    if (currentStat != status) {
                        switch (status) {
                            case NONE:
                                currentStat = status;
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.setMessage("trap id : " + id + " (이)가 대기 중입니다.");
                                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
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
                                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
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
                                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
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
                                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
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
                                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
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
                                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
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
                                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
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
                                        dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
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
            timer.schedule(timertask, 1000, 1000);

    }
    protected void onProductChange() {
        initPreviewer();
    }

    @Override
    public void onResume() {
        Log.e("TAG", "onResume");
        super.onResume();
        initPreviewer();
        onProductChange();

        if(mVideoSurface == null) {
            Log.e("TAG", "mVideoSurface is null");
        }
    }

    @Override
    public void onPause() {
        Log.e("TAG", "onPause");
        uninitPreviewer();
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e("TAG", "onStop");
        super.onStop();
    }

    public void onReturn(View view){
        Log.e("TAG", "onReturn");
        this.finish();
    }

    @Override
    protected void onDestroy() {
        Log.e("TAG", "onDestroy");
        uninitPreviewer();
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        timertask.cancel();
        super.onBackPressed();
    }

    private void initUI() {
        // init mVideoSurface
        mVideoSurface = (TextureView)findViewById(R.id.video_previewer_surface);

        recordingTime = (TextView) findViewById(R.id.timer);
        mCaptureBtn = (Button) findViewById(R.id.btn_capture);
        mRecordBtn = (ToggleButton) findViewById(R.id.btn_record);
        mShootPhotoModeBtn = (Button) findViewById(R.id.btn_shoot_photo_mode);
        mRecordVideoModeBtn = (Button) findViewById(R.id.btn_record_video_mode);
        trapListView = (ListView) findViewById(R.id.trapListView);

        if (null != mVideoSurface) {
            mVideoSurface.setSurfaceTextureListener(this);
        }
        if(null != trapListView){
            set_trapList();
        }
        mCaptureBtn.setOnClickListener(this);
        mRecordBtn.setOnClickListener(this);
        mShootPhotoModeBtn.setOnClickListener(this);
        mRecordVideoModeBtn.setOnClickListener(this);

        recordingTime.setVisibility(View.INVISIBLE);

        mRecordBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startRecord();
                } else {
                    stopRecord();
                }
            }
        });
    }
    private void initPreviewer() {

        BaseProduct product = DJISampleApplication.getProductInstance();

        if (product == null || !product.isConnected()) {
            showToast(getString(R.string.disconnected));
        } else {
            if (null != mVideoSurface) {
                mVideoSurface.setSurfaceTextureListener(this);
            }
            if (!product.getModel().equals(Model.UNKNOWN_AIRCRAFT)) {
                if (VideoFeeder.getInstance().getVideoFeeds() != null
                        && VideoFeeder.getInstance().getVideoFeeds().size() > 0) {
                    VideoFeeder.getInstance().getVideoFeeds().get(0).setCallback(mReceivedVideoDataCallBack);
                }
            }
        }
    }
    private void uninitPreviewer() {
        Camera camera = DJISampleApplication.getCameraInstance();
        if (camera != null){
            // Reset the callback
            VideoFeeder.getInstance().getVideoFeeds().get(0).setCallback(null);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.e("TAG", "onSurfaceTextureAvailable");
        if (mCodecManager == null) {
            mCodecManager = new DJICodecManager(this, surface, width, height);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.e("TAG", "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.e("TAG","onSurfaceTextureDestroyed");
        if (mCodecManager != null) {
            mCodecManager.cleanSurface();
            mCodecManager = null;
        }

        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(missionView.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_capture:{
                captureAction();
                break;
            }
            case R.id.btn_shoot_photo_mode:{
                switchCameraMode(SettingsDefinitions.CameraMode.SHOOT_PHOTO);
                break;
            }
            case R.id.btn_record_video_mode:{
                switchCameraMode(SettingsDefinitions.CameraMode.RECORD_VIDEO);
                break;
            }
            default:
                break;
        }
    }

    private void switchCameraMode(SettingsDefinitions.CameraMode cameraMode){

        Camera camera = DJISampleApplication.getCameraInstance();
        if (camera != null) {
            camera.setMode(cameraMode, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError error) {

                    if (error == null) {
                        showToast("Switch Camera Mode Succeeded");
                    } else {
                        showToast(error.getDescription());
                    }
                }
            });
        }
    }

    // Method for taking photo
    private void captureAction(){

        final Camera camera = DJISampleApplication.getCameraInstance();
        if (camera != null) {

            SettingsDefinitions.ShootPhotoMode photoMode = SettingsDefinitions.ShootPhotoMode.SINGLE; // Set the camera capture mode as Single mode
            camera.setShootPhotoMode(photoMode, new CommonCallbacks.CompletionCallback(){
                @Override
                public void onResult(DJIError djiError) {
                    if (null == djiError) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                camera.startShootPhoto(new CommonCallbacks.CompletionCallback() {
                                    @Override
                                    public void onResult(DJIError djiError) {
                                        if (djiError == null) {
                                            showToast("take photo: success");
                                        } else {
                                            showToast(djiError.getDescription());
                                        }
                                    }
                                });
                            }
                        }, 2000);
                    }
                }
            });
        }
    }

    // Method for starting recording
    private void startRecord(){
        final Camera camera = DJISampleApplication.getCameraInstance();
        if (camera != null) {
            camera.startRecordVideo(new CommonCallbacks.CompletionCallback(){
                @Override
                public void onResult(DJIError djiError)
                {
                    if (djiError == null) {
                        showToast("Record video: success");
                    }else {
                        showToast(djiError.getDescription());
                    }
                }
            }); // Execute the startRecordVideo API
        }
    }

    // Method for stopping recording
    private void stopRecord(){

        Camera camera = DJISampleApplication.getCameraInstance();
        if (camera != null) {
            camera.stopRecordVideo(new CommonCallbacks.CompletionCallback(){

                @Override
                public void onResult(DJIError djiError)
                {
                    if(djiError == null) {
                        showToast("Stop recording: success");
                    }else {
                        showToast(djiError.getDescription());
                    }
                }
            }); // Execute the stopRecordVideo API
        }

    }

    private void set_trapList(){
        final Intent intent = getIntent();

        tspRoute = intent.getStringExtra("tspRoute");
        Log.e("tspRoute", "RECV : tsp Route" +tspRoute+" Intent : " +intent.toString());

        if(tspRoute!=null){
            Log.e("0627","NULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULLNULL");
        }else{
            tspRoute = "0,4,6,3,8,5,9,7,10,1,2,";
        }
        tspRoute = "Trap 0,Trap 4,Trap 6";
        tspTrapList = tspRoute.split(",");
        datas.add(new ImageData(tspTrapList[0],true));
        datas.add(new ImageData(tspTrapList[1],false));
        datas.add(new ImageData(tspTrapList[2],false));

        /*if(datas.size()>0){
            datas.get(0).setAir_img(true);
        }*/
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, tspTrapList);
        final CustomAdapter adapter2 = new CustomAdapter(getLayoutInflater(), datas);
        if(adapter != null){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    trapListView.setAdapter(adapter2);
                }
            });
        }
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    public void addTableItem(Context con, TableLayout table, String str){
        String strArr[] = str.split("[,]");
        String data = loadDataFile(dbpath+dbFilePath);
        String[] dataArr = data.split("\\r?\\n");
        TableRow tableRow;
        TextView startText, text;
        String[] start = {"0","0","37.0","127.0"};
        tableRow = new TableRow(con);
        tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
        for (int l = 0; l < start.length; l++) {
            startText = new TextView(con,null,0, R.style.table_item);
            startText.setText(start[l]);
            tableRow.addView(startText);
        }
        table.addView(tableRow);
        for(int i=1;i<strArr.length;i++){
            for(int j = 1; j< dataArr.length;j++) {
                String[] dataItem = dataArr[j].split("[,]");
                if(strArr[i].equals(dataItem[0])){
                    Log.e("table","strArr["+i+"] = "+strArr[i]+", "+"dataItem["+j+"] = "+dataItem[0]);
                    tableRow = new TableRow(con);
                    tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));
                    for(int k=0;k<dataItem.length;k++){
                        text = new TextView(con,null,0, R.style.table_item);
                        if(k == 0){
                            text.setText(""+i);
                            tableRow.addView(text);
                            continue;
                        }
                        if(k == dataItem.length)
                            continue;
                        text.setText(dataItem[k-1]);
                        tableRow.addView(text);
                    }
                    table.addView(tableRow);
                }
            }
        }
    }

    public static String loadDataFile(String path){
        //File dataPath = new File(path);
        String text = "";
        try{
            FileInputStream fis = new FileInputStream(path);
            Reader reader = new InputStreamReader(fis);
            int size = fis.available();
            char[] buffer = new char[size];
            reader.read(buffer);
            reader.close();
            text = new String(buffer);
            //state = State.LOADED;
        }catch(Exception e){
            e.getStackTrace();
        }
        return text;
    }
}
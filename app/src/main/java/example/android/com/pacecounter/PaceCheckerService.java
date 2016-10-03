package example.android.com.pacecounter;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class PaceCheckerService extends Service implements SensorEventListener {


    private boolean isStop = false;
    private boolean isCreate = false;

    private TextView mPopupView;
    private LinearLayout mLinearLayout;

    private WindowManager.LayoutParams mParams;
    private WindowManager mWindowManager;

    private float START_X, START_Y;
    private int PREV_X, PREV_Y;
    private int MAX_X = -1, MAX_Y = -1;

    private Thread thread;
    private UIUpdateHandler handler = new UIUpdateHandler();
    private static final int EVENT_ADD_PACE_INFO = 1;
    private static final int EVENT_REMOVE_PACE_INFO = 2;


    public PaceCheckerService() {
    }

    private View.OnTouchListener mViewTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (MAX_X == -1)
                        setMaxPosition();
                    START_X = event.getRawX();
                    START_Y = event.getRawY();
                    PREV_X = mParams.x;
                    PREV_Y = mParams.y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int x = (int) (event.getRawX() - START_X);
                    int y = (int) (event.getRawY() - START_Y);

                    mParams.x = PREV_X + x;
                    mParams.y = PREV_Y + y;

                    optimizePosition();
                    mWindowManager.updateViewLayout(mPopupView, mParams);
                    break;
            }

            return true;
        }
    };


    class UIUpdateHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case EVENT_ADD_PACE_INFO:
                    mPopupView.setText("걸음 수 : " + String.valueOf(count) + "\n이동 거리 : " + Utils.transToKM(distance));
                    if (!isCreate) {
                        mWindowManager.addView(mPopupView, mParams);
                        isCreate = true;
                    }
                    //mWindowManager.updateViewLayout(mPopupView, mParams);
                    break;
                case EVENT_REMOVE_PACE_INFO:
                    if (isCreate) {
                        mWindowManager.removeView(mPopupView);
                        isCreate = false;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    ;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("qioip service create", "add view clicked");
        mLinearLayout = new LinearLayout(this);


        mPopupView = new TextView(this);
        mPopupView.setText("Pace Count Diaplay");
        mPopupView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        mPopupView.setTextColor(Color.WHITE);
        mPopupView.setTextSize(30);
        mPopupView.setBackgroundColor(Color.BLACK);

        mPopupView.setOnTouchListener(mViewTouchListener);
        mParams = new WindowManager.LayoutParams(900,
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);


        mPopupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RecordingFragment.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        /*ACCELEROMETER SENSOR*/
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelometer != null) {
            sensorManager.registerListener(this, accelometer, SensorManager.SENSOR_DELAY_UI);
        }

        thread = new PaceCountThread();
        thread.start();
    }

    public boolean isTopActivity() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task = am.getRunningTasks(1);
        ComponentName componentInfo = task.get(0).topActivity;

        if (componentInfo.getPackageName().equals("example.android.com.pacecounter")) {
            Log.v("qioip TopActivity", "top activity is true");
            return true;
        }

        Log.v("qioip TopActivity", "return false");
        return false;
    }


    class PaceCountThread extends Thread {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (!isStop) {
                if (isTopActivity()) {
                    handler.sendEmptyMessage(EVENT_REMOVE_PACE_INFO);
                } else {
                    handler.sendEmptyMessage(EVENT_ADD_PACE_INFO);
                    Log.v("qioip child thread", "add view clicked");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    private void setMaxPosition() {
        DisplayMetrics matrix = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(matrix);
        MAX_X = matrix.widthPixels - mPopupView.getWidth();
        MAX_Y = matrix.heightPixels - mPopupView.getHeight();
    }

    /**
     * 뷰의 위치가 화면 안에 있게 하기 위해서 검사하고 수정한다.
     */
    private void optimizePosition() {
        // 최대값 넘어가지 않게 설정
        if (mParams.x > MAX_X)
            mParams.x = MAX_X;
        if (mParams.y > MAX_Y)
            mParams.y = MAX_Y;
        if (mParams.x < 0)
            mParams.x = 0;
        if (mParams.y < 0)
            mParams.y = 0;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        setMaxPosition();
        optimizePosition();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isStop = true;
        if (mWindowManager != null) {
            if (mPopupView != null && isCreate)
                mWindowManager.removeView(mPopupView);
        }

        if (accelometer != null) {
            sensorManager.unregisterListener(this);
        }
    }


    private SensorManager sensorManager;
    private Sensor accelometer;

    private float[] gravity_data = new float[3];
    private float[] accel_data = {0, 0, 0};
    final float alpha = 0.8f;

    private static final int SHAKE_THRESHOLD = 9;
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;


    private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;

    private long lastTime;
    private float speed;

    protected static int count;
    protected static float distance;



    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    long currentTime = System.currentTimeMillis();
                    long gabOfTime = (currentTime - lastTime);

                    if (gabOfTime > 300) {
                        lastTime = currentTime;

                        gravity_data[0] = alpha * gravity_data[0] + (1 - alpha) * event.values[0];
                        gravity_data[1] = alpha * gravity_data[1] + (1 - alpha) * event.values[1];
                        gravity_data[2] = alpha * gravity_data[2] + (1 - alpha) * event.values[2];

                        accel_data[0] = event.values[0] - gravity_data[0];
                        accel_data[1] = event.values[1] - gravity_data[1];
                        accel_data[2] = event.values[2] - gravity_data[2];


                        //speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;
                        speed = Math.abs(accel_data[0] + accel_data[1] + accel_data[2]);
                        Log.v("accel data", String.valueOf(speed));

                        if (speed > SHAKE_THRESHOLD) {
                            Intent intent = new Intent("example.android.com.pacecounter");
                            count++;
                            distance += calculateDistance(accel_data[0], accel_data[1], accel_data[2]);
                            intent.putExtra("count", count);
                            intent.putExtra("distance", distance);
                            sendBroadcast(intent);
                            Log.v("Pace Service", "send intent");
                        }

                    }
                }

        }
    }


    protected float calculateDistance(float x, float y, float z) {
        /*TO DO*/
        //Not implemented

        return 0.7f;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

}

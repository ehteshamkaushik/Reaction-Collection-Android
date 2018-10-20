package com.example.kaushik.imagedatacollection;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PhotoTakingService extends Service {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    String imageName;
    static Camera camera = null;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            takePhoto(PhotoTakingService.this);
            stopSelf(msg.arg1);
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand (final Intent intent, int flags, final int startId) {
        showMessage("Started Service for " + imageName);
        new Thread(new Runnable() {
            @Override
            public void run() {
                imageName = intent.getStringExtra("imageName");
                showMessage("Image Name : " + imageName);
                HandlerThread thread = new HandlerThread("ServiceStartArguments",
                        Process.THREAD_PRIORITY_BACKGROUND);
                thread.start();
                mServiceLooper = thread.getLooper();
                mServiceHandler = new ServiceHandler(mServiceLooper);

                Message msg = mServiceHandler.obtainMessage();
                msg.arg1 = startId;
                mServiceHandler.sendMessage(msg);
            }
        }).start();
        return START_STICKY;
    }

    @SuppressWarnings("deprecation")
    private void takePhoto(final Context context) {
        final SurfaceView preview = new SurfaceView(context);
        SurfaceHolder holder = preview.getHolder();
        // deprecated setting, but required on Android versions prior to 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            //The preview must happen at or after this point or takePicture fails
            public void surfaceCreated(SurfaceHolder holder) {
                showMessage("Surface created");

                try {
                    camera = Camera.open(1);
                    showMessage("Opened camera");

                    try {
                        camera.setPreviewDisplay(holder);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    camera.startPreview();
                    showMessage("Started preview for " + imageName);

                    camera.takePicture(null, null, new android.hardware.Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(final byte[] data, Camera camera) {
                            camera.release();
                            showMessage("Stopped Preview For " + imageName);
                            showMessage("Took picture");
                            if (data != null)
                            {
                                Bitmap bitmapTemp = BitmapFactory.decodeByteArray(data , 0, data .length);
                                Matrix matrix = new Matrix();
                                matrix.postRotate(-90);
                                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmapTemp, 0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight(),
                                        matrix, true);
                                int width = rotatedBitmap.getWidth();
                                int height = rotatedBitmap.getHeight();
                                float scaleWidth = ((float) 600) / width;
                                float scaleHeight = ((float) 800) / height;
                                Matrix matrix1 = new Matrix();
                                matrix1.postScale(scaleWidth, scaleHeight);
                                Bitmap bitmap = Bitmap.createBitmap(rotatedBitmap, 0, 0, width, height,
                                        matrix1, false);

                                if (bitmap != null)
                                {
                                    File file=new File(Environment.getExternalStorageDirectory()+"/dirr");
                                    if(!file.isDirectory()){
                                        file.mkdir();
                                    }
                                    file=new File(Environment.getExternalStorageDirectory()+"/dirr",imageName+".jpg");
                                    try
                                    {
                                        FileOutputStream fileOutputStream=new FileOutputStream(file);
                                        bitmap.compress(Bitmap.CompressFormat.JPEG,100, fileOutputStream);
                                        fileOutputStream.flush();
                                        fileOutputStream.close();
                                        showMessage("Saved File " + file.getAbsolutePath());
                                    }
                                    catch(IOException e){
                                        e.printStackTrace();
                                    }
                                    catch(Exception exception)
                                    {
                                        exception.printStackTrace();
                                    }
                                }
                                else
                                {
                                    showMessage("Null Bitmap");
                                }
                            }
                            else
                            {
                                showMessage("Null Data");
                            }
                        }
                    });
                } catch (Exception e)
                {
                    e.printStackTrace();
                    if (camera != null)
                    {
                        camera.release();
                    }
                    //throw new RuntimeException(e);
                }
            }

            @Override public void surfaceDestroyed(SurfaceHolder holder) {}
            @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
        });

        WindowManager wm = (WindowManager)context
                .getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                1, 1, //Must be at least 1x1
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                0,
                //Don't know if this is a safe default
                PixelFormat.UNKNOWN);

        //Don't set the preview visibility to GONE or INVISIBLE
        wm.addView(preview, params);
    }

    private static void showMessage(String message) {
        Log.i("Camera", message);
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
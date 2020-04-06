package com.demo.demos.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.preference.TwoStatePreference;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.demo.demos.MainActivity;
import com.demo.demos.R;
import com.demo.demos.base.BaseActivity;
import com.demo.demos.base.BaseFragment;
import com.demo.demos.utils.CameraUtils;
import com.demo.demos.utils.FlashlightUtils;
import com.demo.demos.views.AutoFitTextureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Parameter;
import java.nio.ByteBuffer;
import java.security.Policy;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoFragment extends BaseFragment {

    private static final SparseIntArray PHOTO_ORITATION = new SparseIntArray();

    static {
        PHOTO_ORITATION.append(Surface.ROTATION_0, 90);
        PHOTO_ORITATION.append(Surface.ROTATION_90, 0);
        PHOTO_ORITATION.append(Surface.ROTATION_180, 270);
        PHOTO_ORITATION.append(Surface.ROTATION_270, 180);
    }

    Button btnPhoto;
    AutoFitTextureView previewView;

    Bitmap temp;
    ImageView imageView;
    Switch aSwitch;
    boolean isWaterMarked=false;
    int isDelay;
    Switch bSwitch;
    Switch cSwitch;
    Switch dSwitch;
    boolean isFlash=false;
    SeekBar seekBar;

    ImageView gridImageView;
    TextView mTimeText;

    String cameraId;
    CameraManager cameraManager;
    List<Size> outputSizes;
    Size photoSize;
    CameraDevice cameraDevice;
    CameraCaptureSession captureSession;
    CaptureRequest.Builder previewRequestBuilder;
    CaptureRequest previewRequest;
    CaptureRequest.Builder photoRequestBuilder;
    CaptureRequest photoRequest;
    ImageReader photoReader;
    Surface previewSurface;
    Surface photoSurface;
    Rect zoom;

    int cameraOritation;
    int displayRotation;

    FlashlightUtils flashlight = new FlashlightUtils();
    public int zoom_level=1;

    public PhotoFragment() {
        // Required empty public constructor
    }
    //闪光模式
    public void toggleFlashMode(boolean enable){
        try {
            if (enable) {
                previewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
            } else {
                previewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
            }
            captureSession.setRepeatingRequest(previewRequestBuilder.build(), null, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    //变焦
    public boolean handleZoom() {
        try {
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            float maxZoom = (characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM))*10;
            Rect m = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
            //计算
            int minW = (int) (m.width() / maxZoom);
            int minH = (int) (m.height() / maxZoom);
            int difW = m.width() - minW;
            int difH = m.height() - minH;
            int cropW = difW /100 *(int)zoom_level;
            int cropH = difH /100 *(int)zoom_level;
            cropW -= cropW & 3;
            cropH -= cropH & 3;
            zoom = new Rect(cropW, cropH, m.width() - cropW, m.height() - cropH);
            System.out.println(maxZoom);
            System.out.println(m.width());
            System.out.println(zoom_level);
            System.out.println(zoom.width());
            previewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoom);
            //冲
            try {
//            captureSession.setRepeatingRequest(photoRequestBuilder.build(), sessionCaptureCallback,null);
                captureSession.setRepeatingRequest(previewRequestBuilder.build(), sessionCaptureCallback,null);
            }
            catch (CameraAccessException e) {
                e.printStackTrace();
            }
            catch (NullPointerException ex)
            {
                ex.printStackTrace();
            }
        }
        catch (CameraAccessException e)
        {
            throw new RuntimeException("can not access camera.", e);
        }
        return true;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initCamera();

        initViews(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initCamera() {
        cameraManager = CameraUtils.getInstance().getCameraManager();
        cameraId = CameraUtils.getInstance().getBackCameraId();
        outputSizes = CameraUtils.getInstance().getCameraOutputSizes(cameraId, SurfaceTexture.class);
        photoSize = outputSizes.get(0);
    }

    private void initViews(final View view) {
        btnPhoto = view.findViewById(R.id.btn_photo);
        imageView=(ImageView)view.findViewById(R.id.image_view);
        gridImageView=(ImageView)view.findViewById(R.id.gridImage);

        aSwitch=(Switch)view.findViewById(R.id.s_v);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    isWaterMarked=true;
                }
                else
                {
                    isWaterMarked=false;
                }
            }
        });

        bSwitch=(Switch)view.findViewById(R.id.s_v2);
        bSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    gridImageView.setVisibility(View.VISIBLE);
                }
                else
                {
                    gridImageView.setVisibility(View.INVISIBLE);
                }
            }
        });

        cSwitch=(Switch)view.findViewById(R.id.s_v3);
        cSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    isDelay=3000;
                }
                else
                {
                    isDelay=0;
                }
            }
        });

        dSwitch=(Switch)view.findViewById(R.id.s_v4);
        dSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isFlash=b;
                toggleFlashMode(isFlash);
//                    cameraManager.setTorchMode(cameraId, isFlash);
                try{
                    toggleFlashMode(isFlash);
                    cameraManager.setTorchMode(cameraId, isFlash);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                    Log.d(TAG, "啦啦啦");
                }
            }
        });
        seekBar = view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                zoom_level = progress;
                handleZoom();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mTimeText=(TextView)view.findViewById(R.id.mTimeText);
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CountDownTimer(isDelay, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                        mTimeText.setVisibility(View.VISIBLE);
                        mTimeText.setText("" + millisUntilFinished / 1000); }
                    @Override
                    public void onFinish() {
                        Toast toast=Toast.makeText(getActivity(),"图片已保存至/DCIM/",Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        takePhoto();
                        mTimeText.setVisibility((View.INVISIBLE));
                        dSwitch.setChecked(false);}  }.start();

            }
        });

        previewView = view.findViewById(R.id.preview_view);
    }

    private void initReaderAndSurface() {

        //初始化拍照 ImageReader
        photoReader = ImageReader.newInstance(photoSize.getWidth(), photoSize.getHeight(), ImageFormat.JPEG, 2);
        photoReader.setOnImageAvailableListener(photoReaderImgListener, null);
        photoSurface = photoReader.getSurface();
    }

    @Override
    public void onResume() {
        super.onResume();

        ((BaseActivity) getActivity()).requestPermission("请给予相机、存储权限，以便app正常工作",
                new BaseActivity.Callback() {
                    @Override
                    public void success() {
                        if (previewView.isAvailable()) {
                            openCamera();
                        } else {
                            previewView.setSurfaceTextureListener(surfaceTextureListener);
                        }
                    }

                    @Override
                    public void failed() {
                        Toast.makeText(getContext(), "未授予相机、存储权限！", Toast.LENGTH_SHORT).show();
                    }
                },
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    @Override
    public void onPause() {
        releaseCamera();
        super.onPause();
    }

    @SuppressLint("MissingPermission")
    private void openCamera() {
        try {
//            previewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
//            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON);
//            // Then Send request to current camera session
//            captureSession.setRepeatingRequest(previewRequestBuilder.build(), null, null);

            displayRotation = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getOrientation();
            if (displayRotation == Surface.ROTATION_0 || displayRotation == Surface.ROTATION_180) {
                previewView.setAspectRation(photoSize.getHeight(), photoSize.getWidth());
            } else {
                previewView.setAspectRation(photoSize.getWidth(), photoSize.getHeight());
            }
            configureTransform(previewView.getWidth(), previewView.getHeight());
            cameraManager.openCamera(cameraId, cameraStateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.d(TAG, "相机访问异常");
        }
    }

    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = getActivity();
        if (null == previewView || null == photoSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, photoSize.getHeight(), photoSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / photoSize.getHeight(),
                    (float) viewWidth / photoSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        previewView.setTransform(matrix);
    }

    private void takePhoto() {
        try {
            System.out.println(isFlash);
            System.out.println(zoom_level);
//            if (isFlash) {
//                flashlight.lightsOn(getContext());
//            }
            photoRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            cameraOritation = PHOTO_ORITATION.get(displayRotation);
            photoRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, cameraOritation);

            photoRequestBuilder.addTarget(photoSurface);

            photoRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoom);
            photoRequest = photoRequestBuilder.build();

            captureSession.stopRepeating();
            captureSession.capture(photoRequest, sessionCaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.d(TAG, "相机访问异常");
        }
    }

    private static Bitmap AddTimeWatermark(Bitmap mBitmap) {
        //获取原始图片与水印图片的宽与高
        int mBitmapWidth = mBitmap.getWidth();
        int mBitmapHeight = mBitmap.getHeight();
        Bitmap mNewBitmap = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(mNewBitmap);
        //向位图中开始画入MBitmap原始图片
        mCanvas.drawBitmap(mBitmap,0,0,null);
        //添加文字
        Paint mPaint = new Paint();
        String mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss EEEE").format(new Date());
        //String mFormat = TingUtils.getTime()+"\n"+" 纬度:"+GpsService.latitude+"  经度:"+GpsService.longitude;
        mPaint.setARGB(255,137,137,137);
        mPaint.setTextSize(140);
        //水印的位置坐标
        mCanvas.drawText("A ZJUTer's Day", (mBitmapWidth * 1) / 10,(mBitmapHeight*14)/16,mPaint);

        mPaint.reset();
        mPaint.setARGB(255,137,137,137);
        mPaint.setTextSize(100);
        mCanvas.drawText(mFormat, (mBitmapWidth * 1) / 10,(mBitmapHeight*15)/16,mPaint);
        mCanvas.drawRect((mBitmapWidth * 1) / 16, (mBitmapHeight*15)/18, (mBitmapWidth * 1) / 12, (mBitmapHeight*15)/16, mPaint);



        mCanvas.save();
        mCanvas.restore();

        return mNewBitmap;
    }
    private void writeImageToFile() {
        String filePath = Environment.getExternalStorageDirectory() + "/DCIM/Camera/001.jpg";
        Image image = photoReader.acquireNextImage();
        if (image == null) {
            return;
        }
        ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
        byte[] data = new byte[byteBuffer.remaining()];
        byteBuffer.get(data);

        temp = BitmapFactory.decodeByteArray(data,0,data.length);

        if(isWaterMarked)
            temp=AddTimeWatermark(temp);
        imageView.setImageBitmap(temp);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        temp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data2 = baos.toByteArray();



        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(filePath));
            fos.write(data2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
                fos = null;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                image.close();
                image = null;
            }
        }
    }

    private void releaseCamera() {
//        if (isFlash) {
//            flashlight.lightsOff();
//        }
        CameraUtils.getInstance().releaseImageReader(photoReader);
        CameraUtils.getInstance().releaseCameraSession(captureSession);
        CameraUtils.getInstance().releaseCameraDevice(cameraDevice);
    }

    /********************************** listener/callback **************************************/
    TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //启动相机
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            Log.d(TAG, "相机已启动");
            //初始化 ImageReader 和 Surface
            initReaderAndSurface();

            cameraDevice = camera;
            try {
                //初始化预览 Surface
                SurfaceTexture surfaceTexture = previewView.getSurfaceTexture();
                if (surfaceTexture == null) {
                    return;
                }


                surfaceTexture.setDefaultBufferSize(photoSize.getWidth(), photoSize.getHeight());//设置SurfaceTexture缓冲区大小
                previewSurface = new Surface(surfaceTexture);

                previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

                previewRequestBuilder.addTarget(previewSurface);

                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH);

                previewRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, zoom);
                previewRequest = previewRequestBuilder.build();


                cameraDevice.createCaptureSession(Arrays.asList(previewSurface, photoSurface), sessionsStateCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                Log.d(TAG, "相机访问异常");
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            Log.d(TAG, "相机已断开连接");
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Log.d(TAG, "相机打开出错");
        }
    };

    CameraCaptureSession.StateCallback sessionsStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            if (null == cameraDevice) {
                return;
            }

            captureSession = session;
            try {
                captureSession.setRepeatingRequest(previewRequest, null, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                Log.d(TAG, "相机访问异常");
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            Log.d(TAG, "会话注册失败");
        }
    };

    CameraCaptureSession.CaptureCallback sessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            try {
                captureSession.setRepeatingRequest(previewRequest, null, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                Log.d(TAG, "相机访问异常");
            }
        }
    };

    ImageReader.OnImageAvailableListener photoReaderImgListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            writeImageToFile();
        }
    };
}

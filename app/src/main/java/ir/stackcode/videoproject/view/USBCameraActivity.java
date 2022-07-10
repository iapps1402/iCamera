package ir.stackcode.videoproject.view;

import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import com.jiangdg.usbcamera.UVCCameraHelper;
import com.jiangdg.usbcamera.utils.FileUtils;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.Size;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;
import com.serenegiant.usb.encoder.RecordParams;
import com.serenegiant.usb.widget.CameraViewInterface;

import java.util.ArrayList;
import java.util.List;

import helper.HelperFile;
import ir.stackcode.videoproject.AlertCustomDialog;
import ir.stackcode.videoproject.DeviceInfo;
import ir.stackcode.videoproject.R;
import ir.stackcode.videoproject.application.BaseActivity;
import ir.stackcode.videoproject.application.MyApplication;

public class USBCameraActivity extends BaseActivity implements CameraDialog.CameraDialogParent, CameraViewInterface.Callback {
    private static final String TAG = "Debug";
    public View mTextureView;
    public Toolbar mToolbar;
    public SeekBar mSeekBrightness;
    public SeekBar mSeekContrast;
 //   public SwitchCompat mSwitchVoice;

    private UVCCameraHelper mCameraHelper;
    private CameraViewInterface mUVCCameraView;
    private AlertDialog mDialog;

    private boolean isRequest;
    private boolean isPreview;

    private List<DeviceInfo> getUSBDevInfo() {
        if (mCameraHelper == null)
            return null;
        List<DeviceInfo> devInfos = new ArrayList<>();
        List<UsbDevice> list = mCameraHelper.getUsbDeviceList();
        for (UsbDevice dev : list) {
            DeviceInfo info = new DeviceInfo();
            info.setPID(dev.getProductId());
            info.setVID(dev.getVendorId());
            devInfos.add(info);
        }
        return devInfos;
    }

    private void popCheckDevDialog() {
        List<DeviceInfo> infoList = getUSBDevInfo();
        if (infoList == null || infoList.isEmpty()) {
            Toast.makeText(USBCameraActivity.this, "Find devices failed.", Toast.LENGTH_SHORT).show();
            return;
        }
        final List<String> dataList = new ArrayList<>();
        for (DeviceInfo deviceInfo : infoList) {
            dataList.add("Device：PID_" + deviceInfo.getPID() + " & " + "VID_" + deviceInfo.getVID());
        }
        AlertCustomDialog.createSimpleListDialog(this, "Please select USB device", dataList, position -> mCameraHelper.requestPermission(position));
    }

    private UVCCameraHelper.OnMyDevConnectListener listener = new UVCCameraHelper.OnMyDevConnectListener() {

        @Override
        public void onAttachDev(UsbDevice device) {
            // request open permission
            if (!isRequest) {
                isRequest = true;
                popCheckDevDialog();
            }
        }

        @Override
        public void onDettachDev(UsbDevice device) {
            // close camera
            if (isRequest) {
                isRequest = false;
                mCameraHelper.closeCamera();
                showShortMsg(device.getDeviceName() + " is out");
            }
        }

        @Override
        public void onConnectDev(UsbDevice device, boolean isConnected) {
            if (!isConnected) {
                showShortMsg("fail to connect,please check resolution params");
                isPreview = false;
            } else {
                isPreview = true;
                showShortMsg("connecting");
                // initialize seekbar
                // need to wait UVCCamera initialize over
                new Thread(() -> {
                    try {
                        Thread.sleep(2500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Looper.prepare();
                    if (mCameraHelper != null && mCameraHelper.isCameraOpened()) {
                        mSeekBrightness.setProgress(mCameraHelper.getModelValue(UVCCameraHelper.MODE_BRIGHTNESS));
                        mSeekContrast.setProgress(mCameraHelper.getModelValue(UVCCameraHelper.MODE_CONTRAST));
                    }
                    Looper.loop();
                }).start();
            }
        }

        @Override
        public void onDisConnectDev(UsbDevice device) {
            showShortMsg("disconnecting");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usbcamera);
        initView();

        // step.1 initialize UVCCameraHelper
        mUVCCameraView = (CameraViewInterface) mTextureView;
        mUVCCameraView.setCallback(this);
        mCameraHelper = UVCCameraHelper.getInstance();
        mCameraHelper.setDefaultFrameFormat(UVCCameraHelper.FRAME_FORMAT_MJPEG);
        mCameraHelper.initUSBMonitor(this, mUVCCameraView, listener);

        mCameraHelper.setOnPreviewFrameListener(nv21Yuv -> Log.d(TAG, "onPreviewResult: " + nv21Yuv.length));
    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mSeekContrast = findViewById(R.id.seekbar_contrast);

        //mSwitchVoice = findViewById(R.id.switch_rec_voice);
        mTextureView = findViewById(R.id.camera_view);
        mSeekBrightness = findViewById(R.id.seekbar_brightness);

        mSeekBrightness.setMax(100);
        mSeekBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mCameraHelper != null && mCameraHelper.isCameraOpened()) {
                    mCameraHelper.setModelValue(UVCCameraHelper.MODE_BRIGHTNESS, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mSeekContrast.setMax(100);
        mSeekContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mCameraHelper != null && mCameraHelper.isCameraOpened()) {
                    mCameraHelper.setModelValue(UVCCameraHelper.MODE_CONTRAST, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // step.2 register USB event broadcast
        if (mCameraHelper != null) {
            mCameraHelper.registerUSB();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // step.3 unregister USB event broadcast
        if (mCameraHelper != null) {
            mCameraHelper.unregisterUSB();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.main_toobar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_takepic:
                if (mCameraHelper == null || !mCameraHelper.isCameraOpened()) {
                    showShortMsg(getString(R.string.can_not_open_camera));
                    return super.onOptionsItemSelected(item);
                }
                String picPath = HelperFile.Companion.getFileDir() + "images/"
                        + System.currentTimeMillis() + UVCCameraHelper.SUFFIX_JPEG;

                mCameraHelper.capturePicture(picPath, path -> {
                    if (TextUtils.isEmpty(path)) {
                        return;
                    }
                    new Handler(getMainLooper()).post(() -> Toast.makeText(USBCameraActivity.this, "save path:" + path, Toast.LENGTH_SHORT).show());
                });

                break;

            case R.id.about:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.url))));
                break;
            case R.id.menu_recording:
                if (mCameraHelper == null || !mCameraHelper.isCameraOpened()) {
                    showShortMsg(getString(R.string.can_not_open_camera));
                    return super.onOptionsItemSelected(item);
                }
                if (!mCameraHelper.isPushing()) {
                    String videoPath = HelperFile.Companion.getFileDir() + "videos/" + System.currentTimeMillis()
                            + UVCCameraHelper.SUFFIX_MP4;

//                    FileUtils.createfile(FileUtils.ROOT_PATH + "test666.h264");
                    // if you want to record,please create RecordParams like this
                    RecordParams params = new RecordParams();
                    params.setRecordPath(videoPath);
                    params.setRecordDuration(0);                        // auto divide saved,default 0 means not divided
                   // params.setVoiceClose(mSwitchVoice.isChecked());    // is close voice

                    params.setSupportOverlay(true); // overlay only support armeabi-v7a & arm64-v8a
                    mCameraHelper.startPusher(params, new AbstractUVCCameraHandler.OnEncodeResultListener() {
                        @Override
                        public void onEncodeResult(byte[] data, int offset, int length, long timestamp, int type) {
                            // type = 1,h264 video stream
                            if (type == 1) {
                                FileUtils.putFileStream(data, offset, length);
                            }
                            // type = 0,aac audio stream
                            if (type == 0) {

                            }
                        }

                        @Override
                        public void onRecordResult(String videoPath) {
                            if (TextUtils.isEmpty(videoPath)) {
                                return;
                            }
                            new Handler(getMainLooper()).post(() -> Toast.makeText(USBCameraActivity.this, "save videoPath:" + videoPath, Toast.LENGTH_SHORT).show());
                        }
                    });
                    // if you only want to push stream,please call like this
                    // mCameraHelper.startPusher(listener);
                    showShortMsg("start record...");
              //      mSwitchVoice.setEnabled(false);
                } else {
                    FileUtils.releaseFile();
                    mCameraHelper.stopPusher();
                    showShortMsg("stop record...");
                 //   mSwitchVoice.setEnabled(true);
                }
                break;
            case R.id.menu_resolution:
                if (mCameraHelper == null || !mCameraHelper.isCameraOpened()) {
                    showShortMsg(getString(R.string.can_not_open_camera));
                    return super.onOptionsItemSelected(item);
                }
                showResolutionListDialog();
                break;
            case R.id.menu_focus:
                if (mCameraHelper == null || !mCameraHelper.isCameraOpened()) {
                    showShortMsg(getString(R.string.can_not_open_camera));
                    return super.onOptionsItemSelected(item);
                }
                mCameraHelper.startCameraFoucs();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showResolutionListDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(USBCameraActivity.this);
        View rootView = LayoutInflater.from(USBCameraActivity.this).inflate(R.layout.layout_dialog_list, null);
        ListView listView = rootView.findViewById(R.id.listview_dialog);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(USBCameraActivity.this, android.R.layout.simple_list_item_1, getResolutionList());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            if (mCameraHelper == null || !mCameraHelper.isCameraOpened())
                return;
            final String resolution = (String) adapterView.getItemAtPosition(position);
            String[] tmp = resolution.split("x");
            if (tmp.length >= 2) {
                int width = Integer.parseInt(tmp[0]);
                int height = Integer.parseInt(tmp[1]);
                mCameraHelper.updateResolution(width, height);
            }
            mDialog.dismiss();
        });

        builder.setView(rootView);
        mDialog = builder.create();
        mDialog.show();
    }

    // example: {640x480,320x240,etc}
    private List<String> getResolutionList() {
        List<Size> list = mCameraHelper.getSupportedPreviewSizes();
        List<String> resolutions = null;
        if (list != null && list.size() != 0) {
            resolutions = new ArrayList<>();
            for (Size size : list) {
                if (size != null) {
                    resolutions.add(size.width + "x" + size.height);
                }
            }
        }
        return resolutions;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileUtils.releaseFile();
        // step.4 release uvc camera resources
        if (mCameraHelper != null) {
            mCameraHelper.release();
        }
    }

    private void showShortMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public USBMonitor getUSBMonitor() {
        return mCameraHelper.getUSBMonitor();
    }

    @Override
    public void onDialogResult(boolean canceled) {
        if (canceled) {
            showShortMsg("取消操作");
        }
    }

    public boolean isCameraOpened() {
        return mCameraHelper.isCameraOpened();
    }

    @Override
    public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
        if (!isPreview && mCameraHelper.isCameraOpened()) {
            mCameraHelper.startPreview(mUVCCameraView);
            isPreview = true;
        }
    }

    @Override
    public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {

    }

    @Override
    public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
        if (isPreview && mCameraHelper.isCameraOpened()) {
            mCameraHelper.stopPreview();
            isPreview = false;
        }
    }

    @Override
    public void onUSBNewData(@NonNull String data) {

    }
}

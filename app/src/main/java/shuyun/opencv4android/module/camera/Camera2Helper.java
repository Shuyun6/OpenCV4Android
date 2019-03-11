package shuyun.opencv4android.module.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.Surface;
import android.view.TextureView;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import shuyun.opencv4android.util.Log;
import shuyun.opencv4android.util.RxPermissionFactory;

public class Camera2Helper {

    private Context context;
    private HandlerThread handlerThread;
    private Handler handler;
    private CameraManager cameraManager;
    private CameraDevice cameraDevice;
    private ImageReader imageReader;
    private CaptureRequest.Builder builder;
    private CaptureRequest request;
    private int cameraId = 0;
    private TextureView.SurfaceTextureListener surfaceTextureListener;
    private List<Disposable> disposables;

    private int width, height;
    private CameraRenderView cameraRenderView;

    private static Camera2Helper helper;

    public static Camera2Helper getInstance(Context context) {
        if (null == helper) {
            synchronized (Camera2Helper.class) {
                if (null == helper) {
                    helper = new Camera2Helper(context);
                }
            }
        }
        return helper;
    }

    private Camera2Helper(Context context) {
        this.context = context;
    }

    public void init(CameraRenderView cameraRenderView, int width, int height){
        this.cameraRenderView = cameraRenderView;
        this.width = width;
        this.height = height;
        handlerThread = new HandlerThread("Camera2Helper");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        imageReader = ImageReader.newInstance(width, height, ImageFormat.YUV_420_888, 2);
        imageReader.setOnImageAvailableListener(onImageAvailableListener, handler);
        disposables = new LinkedList<>();
    }

    public void openCamera(int id){
        @SuppressLint("MissingPermission")
        Disposable d = RxPermissionFactory.from((FragmentActivity) context)
                .request(Manifest.permission.CAMERA)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        cameraManager.openCamera(String.valueOf(id), stateCallback, null);
                    } else {
                        Log.i("Camera Permission denied!");
                    }
                });
        disposables.add(d);
    }

    private void setUpCamera() throws CameraAccessException {
        builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        final Surface surface = imageReader.getSurface();
        builder.addTarget(surface);
        cameraDevice.createCaptureSession(Arrays.asList(surface), sessionCallback, null);
    }

    private void render(Frame frame) {

    }

    private ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireLatestImage();
            if (null == image) {
                return;
            }
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer y = planes[0].getBuffer();
            ByteBuffer uv = planes[1].getBuffer();
            cameraRenderView.draw(y, uv);
//            Mat yMat = new Mat(height, width, CvType.CV_8UC1, y);
//            Mat uvMat = new Mat(height / 2, width / 2, CvType.CV_8UC2, uv);
//            Frame frame = new Frame(yMat, uvMat, width, height);
//            render(frame);
//            frame.release();
            image.close();
        }
    };

    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            try {
                setUpCamera();
            } catch (CameraAccessException e) {
                Log.e(e.getMessage());
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

        }
    };

    private CameraCaptureSession.StateCallback sessionCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
            try {
                session.setRepeatingRequest(builder.build(), null, handler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

        }
    };

    private class Frame {
        Mat y;
        Mat uv;
        Mat rgba;
        int width, height;

        Frame(Mat y, Mat uv, int width, int height) {
            this.y = y;
            this.uv = uv;
            this.width = width;
            this.height = height;
        }

        Mat gray(){
            return y.submat(0, height, 0, width);
        }

        Mat rgba() {
            rgba = new Mat();
            Imgproc.cvtColorTwoPlane(y, uv, rgba, Imgproc.COLOR_YUV2RGBA_NV21);
            return rgba;
        }

        void release(){
            rgba.release();
        }

    }


}

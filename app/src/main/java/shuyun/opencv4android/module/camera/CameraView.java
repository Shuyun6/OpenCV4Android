package shuyun.opencv4android.module.camera;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.TextureView;

import shuyun.opencv4android.R;
import shuyun.opencv4android.common.CVBaseActivity;

public class CameraView extends CVBaseActivity {

    private TextureView tv;
    private Camera2Helper camera2Helper;
    private CameraRenderView cameraRenderView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity_cameraview);
        cameraRenderView = new CameraRenderView(this);
        tv = findViewById(R.id.tv);
        tv.setSurfaceTextureListener(cameraRenderView);
        camera2Helper = Camera2Helper.getInstance(this);
        camera2Helper.init(cameraRenderView, 1080, 1920);
        camera2Helper.openCamera(0);
    }
}

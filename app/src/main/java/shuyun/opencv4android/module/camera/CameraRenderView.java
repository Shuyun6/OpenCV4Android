package shuyun.opencv4android.module.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.TextureView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import shuyun.opencv4android.R;
import shuyun.opencv4android.constant.Const;
import shuyun.opencv4android.util.GLHelper;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LUMINANCE;
import static android.opengl.GLES20.GL_LUMINANCE_ALPHA;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE1;
import static android.opengl.GLES20.GL_TEXTURE2;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glFlush;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * Render camera raw data by OpenGLES
 * @Author shuyun
 * @Create at 2019/3/11 0011 22:25
 * @Update at 2019/3/11 0011 22:25
*/
public class CameraRenderView implements TextureView.SurfaceTextureListener {

    private Context context;
    private SurfaceTexture st;
    private int pBuffer, pDisplay;
    private FloatBuffer vBuffer, vDisplay, cBuffer, cDisplay;
    private ByteBuffer yBuffer, uvBuffer;
    private int width = 1080, height = 1920;
    private int bufferWidth = 1080, bufferHeight = 1920;
    private GLHelper glHelper;
    private int tY, tUV;

    public CameraRenderView(Context context){
        this.context = context;
        glHelper = new GLHelper(context);
        tY = glHelper.genTexture();
        tUV = glHelper.genTexture();
    }

    private void makeBufferData(){
        vBuffer = ByteBuffer.allocateDirect(Const.VERTEX.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        cBuffer = ByteBuffer.allocateDirect(Const.COORD1.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vBuffer.put(Const.VERTEX);
        vBuffer.position(0);
        cBuffer.put(Const.COORD1);
        cBuffer.position(0);
        pBuffer = glHelper.getProgram(R.raw.vertex_shader, R.raw.fragment_shader_yuv);

    }

    private void makeDisplayData(){
        vDisplay = ByteBuffer.allocateDirect(Const.VERTEX.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        cDisplay = ByteBuffer.allocateDirect(Const.COORD2.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vDisplay.put(Const.VERTEX);
        vDisplay.position(0);
        cDisplay.put(Const.COORD2);
        cDisplay.position(0);
        pDisplay = glHelper.getProgram(R.raw.vertex_shader, R.raw.fragment_shader);
    }

    public synchronized void draw(ByteBuffer y, ByteBuffer uv){
        glViewport(0, 0, bufferWidth, bufferHeight);
        glClear(GL_COLOR_BUFFER_BIT);
        yBuffer.clear();
        uvBuffer.clear();
        yBuffer = y;
        uvBuffer = uv;

//        yBuffer.put(data, 0, bufferWidth * bufferHeight);
        yBuffer.position(0);
//        uvBuffer.put(data, bufferWidth * bufferHeight, bufferWidth * bufferHeight / 2);
        uvBuffer.position(0);

        //render data into frame buffer
        glUseProgram(pBuffer);
        glBindTexture(GL_TEXTURE_2D, tY);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, bufferWidth, bufferHeight, 0, GL_LUMINANCE, GL_UNSIGNED_BYTE, yBuffer);
        glTexParameteri ( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR );
        glTexParameteri ( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR );
        glTexParameteri ( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE );
        glTexParameteri ( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE );
        glBindTexture(GL_TEXTURE_2D, 0);

        glBindTexture(GL_TEXTURE_2D, tUV);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE_ALPHA, bufferWidth/2, bufferHeight/2, 0, GL_LUMINANCE_ALPHA, GL_UNSIGNED_BYTE, uvBuffer);
        glTexParameteri ( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR );
        glTexParameteri ( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR );
        glTexParameteri ( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE );
        glTexParameteri ( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE );
        glBindTexture(GL_TEXTURE_2D, 0);

        int aPosition = glGetAttribLocation(pBuffer, "aPosition");
        glEnableVertexAttribArray(aPosition);
        glVertexAttribPointer(aPosition, 2, GL_FLOAT, false, 0, vBuffer);

        int aTextureCoord = glGetAttribLocation(pBuffer, "aTextureCoord");
        glEnableVertexAttribArray(aTextureCoord);
        glVertexAttribPointer(aTextureCoord, 2, GL_FLOAT, false, 0, cBuffer);

        int texY = glGetUniformLocation(pBuffer, "u_TextureY");
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, tY);
        glUniform1i(texY, 0);

        int texUV = glGetUniformLocation(pBuffer, "u_TextureUV");
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, tUV);
        glUniform1i(texUV, 1);

        glHelper.bindFrameBuffer();
        glClear(GL_COLOR_BUFFER_BIT);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        glHelper.unBindFrameBuffer();
        glClear(GL_COLOR_BUFFER_BIT);

        //draw data from frame buffer
        glUseProgram(pDisplay);
        int aPosition2 = glGetAttribLocation(pDisplay, "aPosition");
        glEnableVertexAttribArray(aPosition2);
        glVertexAttribPointer(aPosition2, 2, GL_FLOAT, false, 0, vDisplay);

        int aTextureCoord2 = glGetAttribLocation(pDisplay, "aTextureCoord");
        glEnableVertexAttribArray(aTextureCoord2);
        glVertexAttribPointer(aTextureCoord2, 2, GL_FLOAT, false, 0, cDisplay);

        int texture2 = glGetUniformLocation(pDisplay, "u_Texture");
        glActiveTexture(GL_TEXTURE2);
        glBindTexture(GL_TEXTURE_2D, glHelper.getTexture2Framebuffer());
        glUniform1i(texture2, 2);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        glFlush();
        glHelper.swapBuffer();

    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        this.st = surface;
        this.width = width ;
        this.height = height;
        yBuffer = ByteBuffer.allocateDirect(bufferWidth * bufferHeight * 4).order(ByteOrder.nativeOrder());
        uvBuffer = ByteBuffer.allocateDirect(bufferWidth * bufferHeight * 4).order(ByteOrder.nativeOrder());
        glHelper.setDisplaySize(width, height);
        glHelper.initEGL(st);
        makeBufferData();
        makeDisplayData();
        glHelper.setupFrameBuffer();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        //draw

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        glHelper = null;
        yBuffer.clear();
        uvBuffer.clear();
        vBuffer.clear();
        cBuffer.clear();
        vDisplay.clear();
        cDisplay.clear();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}

package com.example.yoha;

import ai.fritz.core.Fritz;
import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import java.nio.ByteBuffer;
import java.util.Arrays;
import mehdi.sakout.fancybuttons.FancyButton;


public class MainActivity extends Activity  {

  private static final String API_KEY = "2ad01f63d60645fcabd14b4d8549c429";

  public static MainActivity instance = null;
  private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
  public transient final static int cameraRequestCode = 10000;
  public transient final static int albumRequestCode = 10001;

  transient public final static int album_request_code = 10001;
  transient public final static int camera_request_code = 10002;


  private static int  flash_mode = 0;
  private static int  default_camera = 0;
  private int count;
  transient private SurfaceView msurfaceview;
  transient private SurfaceHolder msurfaceholder;
  transient private ImageView ivShow;
  transient private CameraManager mcameramanager;//摄像头管理器
  transient private Handler childHandler;
  transient private Handler mainHandler;
  transient private String mcameraid;//摄像头Id 0 为后  1 为前
  transient private ImageReader mimagereader;
  transient private CameraCaptureSession mcameracapturesession;
  transient private CameraDevice mcameradevice;
  transient private Button btnTakePhoto;
  transient private FancyButton btnForAlbum;
  transient private FancyButton shareBnt;
  transient private FancyButton btnSwitchCamera;
  transient private FancyButton btnFlash;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Fritz.configure(this, API_KEY);
    setContentView(R.layout.activity_main);
    shareBnt = findViewById(R.id.shareBnt);
    instance = this;
    initVIew();
    count = 0;
    onClickRegister();
  }

    @Override
    protected void onResume() {
        super.onResume();
        if (count == 0){
            count++;
        }else {
            Intent it = new Intent(MainActivity.this,MainActivity.class);
            startActivity(it);
        }
//
//        if (mcameradevice != null){
//            mcameradevice.close();
//            mcameracapturesession.close();
//            try {
//                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, camera_request_code);
//                    return;
//                }
//                mcameramanager.openCamera(mcameraid,stateCallback,mainHandler);
//            }catch (CameraAccessException e) {
//                e.printStackTrace();
//            }
//        }
    }

  private void initVIew() {
    ivShow = findViewById(R.id.iv_show_camera2_activity);
    //msurfaceview
    msurfaceview = findViewById(R.id.surface_view_camera2_activity);
    msurfaceholder = msurfaceview.getHolder();
    msurfaceholder.setKeepScreenOn(true);
    // msurfaceview添加回调
    msurfaceholder.addCallback(new SurfaceHolder.Callback() {
      @Override
      public void surfaceCreated(SurfaceHolder holder) { //SurfaceView创建
        // 初始化Camera
        initCamera2();
      }

      @Override
      public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

      }

      @Override
      public void surfaceDestroyed(SurfaceHolder holder) { //SurfaceView销毁
        // 释放Camera资源
        if (null != mcameradevice) {
          mcameradevice.close();
          MainActivity.this.mcameradevice = null;
        }
      }
    });

    btnForAlbum = findViewById(R.id.button_for_album);
    btnForAlbum.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, album_request_code);
      }
    });

    btnTakePhoto = findViewById(R.id.button_take_photo);
    btnTakePhoto.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        takePicture();
      }
    });

      btnSwitchCamera=  findViewById(R.id.button_for_switch);
      btnSwitchCamera.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              switchCamera();
          }
      });

      btnFlash=  findViewById(R.id.button_for_flash);
      if(flash_mode==0) {
          btnFlash.setIconResource("\uf0eb");
      }
      if(flash_mode==1) {
          btnFlash.setIconResource("\uf0e7");
      }
      if(flash_mode==2) {
          btnFlash.setIconResource("\uf021");
      }
      btnFlash.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              flash_mode  = (flash_mode+1)%3;
              if(flash_mode==0) {
                  btnFlash.setIconResource("\uf0eb");
              }
              if(flash_mode==1) {
                  btnFlash.setIconResource("\uf0e7");
              }
              if(flash_mode==2) {
                  btnFlash.setIconResource("\uf021");
              }
          }
      });

      if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
          ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},camera_request_code);
      }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    super.onActivityResult(requestCode, resultCode, data);
    //在相册里面选择好相片之后调回到现在的这个activity中
    //这里的requestCode是我自己设置的，就是确定返回到那个Activity的标志
    if (requestCode == album_request_code) {
      if (resultCode == RESULT_OK) { //resultcode是setResult里面设置的code值
        try {
          Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
          Intent it = new Intent(MainActivity.this, StyleActivity.class);
          it.putExtra("URI", selectedImage.toString());
          int type = 2;
          it.putExtra("type", type);
          startActivity(it);
          finish();
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        Intent it = new Intent(MainActivity.this, MainActivity.class);
        startActivity(it);
        finish();
      }
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void initCamera2() {
    HandlerThread handlerThread = new HandlerThread("Camera2");
    handlerThread.start();
    childHandler = new Handler(handlerThread.getLooper());
    mainHandler = new Handler(getMainLooper());
    if(default_camera == 0){
          mcameraid= "" + CameraCharacteristics.LENS_FACING_FRONT;//后摄像头
    }else {
          mcameraid= "" + CameraCharacteristics.LENS_FACING_BACK;//后摄像头
    }
    mimagereader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG, 1);
    mimagereader.setOnImageAvailableListener(
        new ImageReader.OnImageAvailableListener() { //可以在这里处理拍照得到的临时照片 例如，写入本地
          @Override
          public void onImageAvailable(ImageReader reader) {
            // 拿到拍照照片数据
            Image image = reader.acquireNextImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);//由缓冲区存入字节数组
            Intent it = new Intent(MainActivity.this, StyleActivity.class);
            it.putExtra("image", bytes);
            int type = 1;
            it.putExtra("type", type);
            startActivity(it);
            finish();
          }
        }, mainHandler);
    //获取摄像头管理
    mcameramanager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    try {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
          != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
            camera_request_code);
        return;
      }
      //打开摄像头
      mcameramanager.openCamera(mcameraid, stateCallback, mainHandler);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

    public void switchCamera(){
        mcameradevice.close();
        mcameracapturesession.close();
        if (mcameraid == ""+CameraCharacteristics.LENS_FACING_FRONT){
            mcameraid = ""+CameraCharacteristics.LENS_FACING_BACK;
        }else {
            mcameraid = "" + CameraCharacteristics.LENS_FACING_FRONT;
        }
        default_camera = (default_camera + 1) %2;
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, camera_request_code);
                return;
            }
            mcameramanager.openCamera(mcameraid,stateCallback,mainHandler);
        }catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

  /**
   * The function is used to request Camera Permission.
   *
   * @author syc
   */
//  @Override
//  public void onRequestPermissionsResult(int requestCode, String[] permissions,
//      int[] grantResults) {
//    // If request is cancelled, the result arrays are empty.
//    if (requestCode == camera_request_code) {
//      if (grantResults.length > 0) {
//        try {
//          if (ActivityCompat.checkSelfPermission(this, permission.CAMERA)
//              != PackageManager.PERMISSION_GRANTED) {
////            mcameramanager.openCamera(mcameraid, stateCallback, mainHandler);
//          }
////        } catch (CameraAccessException e) {
////          e.printStackTrace();
////        }
//
//      } else {
//        Toast.makeText(MainActivity.this, "hello!", Toast.LENGTH_SHORT).show();
//      }
//    }
//  }
  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         String permissions[], int[] grantResults) {
      switch (requestCode) {
          case 10002:
              if (grantResults != null && grantResults.length > 0) {
                  if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                      //如果没有授权的话，可以给用户一个友好提示
                      Toast.makeText(this, "camera denied", Toast.LENGTH_SHORT).show();
                  }
              }
              return;

      }
  }

  private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
    @Override
    public void onOpened(CameraDevice camera) {
      //打开摄像头
      mcameradevice = camera;
      //开启预览
      takePreview();
    }

    @Override
    public void onDisconnected(CameraDevice camera) {
      //关闭摄像头
      if (null != mcameradevice) {
        mcameradevice.close();
        MainActivity.this.mcameradevice = null;
      }
    }

    @Override
    public void onError(CameraDevice camera, int error) {
      //发生错误
      Toast.makeText(MainActivity.this, "摄像头开启失败", Toast.LENGTH_SHORT).show();
    }
  };

  private void takePreview() {
    try {
      // 创建预览需要的CaptureRequest.Builder
      final CaptureRequest.Builder previewRequestBuilder = mcameradevice
          .createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
      // 将SurfaceView的surface作为CaptureRequest.Builder的目标
      previewRequestBuilder.addTarget(msurfaceholder.getSurface());
      // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
      mcameradevice.createCaptureSession(
          Arrays.asList(msurfaceholder.getSurface(), mimagereader.getSurface()),
          new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(CameraCaptureSession cameraCaptureSession) {
              if (null == mcameradevice) {
                return;
              }
              // 当摄像头已经准备好时，开始显示预览
              mcameracapturesession = cameraCaptureSession;
              try {
                // 自动对焦
                previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                // 打开闪光灯
                previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                // 显示预览
                CaptureRequest previewRequest = previewRequestBuilder.build();
                mcameracapturesession.setRepeatingRequest(previewRequest, null, childHandler);
              } catch (CameraAccessException e) {
                e.printStackTrace();
              }
            }

            @Override
            public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
              Toast.makeText(MainActivity.this, "配置失败", Toast.LENGTH_SHORT).show();
            }
          }, childHandler);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }


  private void takePicture() {
    if (mcameradevice == null) {
      return;
    }
    // 创建拍照需要的CaptureRequest.Builder
    final CaptureRequest.Builder captureRequestBuilder;
    try {
      captureRequestBuilder = mcameradevice
          .createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
      // 将imageReader的surface作为CaptureRequest.Builder的目标
      captureRequestBuilder.addTarget(mimagereader.getSurface());
      // 自动对焦
      captureRequestBuilder
          .set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
      // 自动曝光
      captureRequestBuilder
          .set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
      // 获取手机方向
        CameraCharacteristics mCameraCharistic = mcameramanager.getCameraCharacteristics(mcameraid);
        int orientation = mCameraCharistic.get(CameraCharacteristics.SENSOR_ORIENTATION);
        captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, orientation);

        switch (flash_mode){
            case 0:
                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_OFF);
                break;
            case 1:
                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH);
                captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_SINGLE);
                break;
            case 2:
                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                captureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_SINGLE);
                break;
        }
      CaptureRequest mcapturerequest = captureRequestBuilder.build();
      mcameracapturesession.capture(mcapturerequest, null, childHandler);
    } catch (CameraAccessException e) {
      e.printStackTrace();
    }
  }

  /**
   * The function is to open the register page.
   *
   * @author syc
   */


  protected void onClickRegister() {
    shareBnt.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
        startActivity(intent);
        finish();
      }
    });
  }


}

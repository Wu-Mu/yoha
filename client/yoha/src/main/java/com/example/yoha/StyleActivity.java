package com.example.yoha;


import android.graphics.PixelFormat;
import android.os.Handler;
import ai.fritz.fritzvisionstylepaintings.PaintingStyles;
import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionImage;
import ai.fritz.vision.styletransfer.FritzVisionStylePredictor;
import ai.fritz.vision.styletransfer.FritzVisionStyleResult;
import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.yoha.utils.BitmapUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;
import com.kaopiz.kprogresshud.KProgressHUD;
import java.util.ArrayList;
import java.util.List;


public class StyleActivity extends AppCompatActivity implements
    FiltersListFragment.FiltersListFragmentListener, EditImageFragment.EditImageFragmentListener {

  private FritzVisionStyleResult styleResult;

  private static final String TAG = MainActivity.class.getSimpleName();

  public static final String IMAGE_NAME = "image_preview.jpg";

  public static final int SELECT_GALLERY_IMAGE = 101;

  transient private ImageView imageView;

  @BindView(R.id.tabs)
  transient TabLayout tabLayout;

  @BindView(R.id.viewpager)
  transient ViewPager viewPager;

  @BindView(R.id.coordinator_layout)
  transient CoordinatorLayout coordinatorLayout;

  @BindView(R.id.image_preview)
  transient ImageView imageView1;

  public static Bitmap originalImage;
  // to backup image with filter applied
  transient public Bitmap currentImage;

  // the final image after applying
  // brightness, saturation, contrast
  //public Bitmap finalImage;

  transient public Bitmap style1;
  transient public Bitmap style2;
  transient public Bitmap style3;
  transient public Bitmap style4;
  transient public Bitmap style5;

  static int position_of_style;

  transient FiltersListFragment filtersListFragment;
  transient EditImageFragment editImageFragment;

  // modified image values
  transient int brightnessFinal = 0;
  transient float saturationFinal = 1.0f;
  transient float contrastFinal = 1.0f;

  // load native image filters library
  static {
    System.loadLibrary("NativeImageProcessor");
  }


  private Drawable zoomDrawable(Drawable drawable, int w, int h) {
    int width = drawable.getIntrinsicWidth();
    int height = drawable.getIntrinsicHeight();
    Bitmap oldbmp = drawableToBitmap(drawable);
    Matrix matrix = new Matrix();
    float scaleWidth = ((float) w / width);
    float scaleHeight = ((float) h / height);
    matrix.postScale(scaleWidth, scaleHeight);
    Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
    return new BitmapDrawable(null, newbmp);
  }

  private Bitmap drawableToBitmap(Drawable drawable) {
    int width = drawable.getIntrinsicWidth();
    int height = drawable.getIntrinsicHeight();
    Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
            : Bitmap.Config.RGB_565;
    Bitmap bitmap = Bitmap.createBitmap(width, height, config);
    Canvas canvas = new Canvas(bitmap);
    drawable.setBounds(0, 0, width, height);
    drawable.draw(canvas);
    return bitmap;
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_style);
    ButterKnife.bind(this);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("");
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(StyleActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
      }
    });

    imageView = findViewById(R.id.image_preview);
    int type = getIntent().getIntExtra("type", 1);



    if (type == 1) {
      byte[] images = getIntent().getByteArrayExtra("image");
       Bitmap bitmap = BitmapFactory.decodeByteArray(images, 0, images.length);
      if (bitmap != null) {
//        bitmap = Bitmap.createScaledBitmap(bitmap,300,400,false);
        //bitmap = Bitmap.createScaledBitmap(bitmap, 300, 400, false);
        loadImage(bitmap);
      }
    } else if (type == 2) {
      Uri selectedImage = Uri.parse(getIntent().getStringExtra("URI"));
      String[] filePathColumn = {MediaStore.Images.Media.DATA};
      Cursor cursor = getContentResolver().query(selectedImage,
          filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
      assert cursor != null;
      cursor.moveToFirst();
      int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
      String path = cursor.getString(columnIndex);  //获取照片路径
      cursor.close();
      Bitmap bitmap = BitmapFactory.decodeFile(path);
      //bitmap = Bitmap.createScaledBitmap(bitmap, 300, 400, false);
      //imageView.setImageBitmap(bitmap);
      loadImage(bitmap);
    }

    setupViewPager(viewPager);
    tabLayout.setupWithViewPager(viewPager);
  }




  private void setupViewPager(ViewPager viewPager) {

    // adding filter list fragment
    filtersListFragment = new FiltersListFragment();
    filtersListFragment.setListener(this);

    // adding edit image fragment
    editImageFragment = new EditImageFragment();
    editImageFragment.setListener(this);
    ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

    adapter.addFragment(editImageFragment, getString(R.string.tab_edit));
    adapter.addFragment(filtersListFragment, getString(R.string.tab_filters));

    viewPager.setAdapter(adapter);
  }



  /**
   * send the the image.
   *
   * @return return the bitmap
   */
  protected static Bitmap sendtheimage() {
    return originalImage;
  }


  private void scheduleDismiss(final KProgressHUD hud) {
    Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        hud.dismiss();
      }
    }, 700);
  }

  /**
   * select the file.
   *
   * @param filter the filter
   */
  @Override
  public void onFilterSelected(Filter filter) {
    FritzVisionStylePredictor predictor;
    KProgressHUD hud = KProgressHUD.create(StyleActivity.this);
    hud.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
    hud.setLabel("Transforming");
    hud.setMaxProgress(100);

    // reset image controls
    resetControls();

    // applying the selected filter
    currentImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
    // preview filtered image

    if (position_of_style == 1) {
      hud.show();
      hud.setProgress(75);
      predictor = FritzVision.StyleTransfer.getPredictor(PaintingStyles.POPPY_FIELD);
      currentImage = currentImage.copy(Bitmap.Config.ARGB_8888, true);
      currentImage = Bitmap.createScaledBitmap(currentImage, 300, 400, false);
      FritzVisionImage fritzImage = FritzVisionImage.fromBitmap(currentImage);
      FritzVisionStyleResult styleResult0 = predictor.predict(fritzImage);
      style1 = styleResult0.toBitmap();
      imageView1.setImageBitmap(style1);
      currentImage = style1.copy(Bitmap.Config.ARGB_8888, true);
      scheduleDismiss(hud);
    } else if (position_of_style == 2) {
      hud.show();
      hud.setProgress(75);
      predictor = FritzVision.StyleTransfer.getPredictor(PaintingStyles.HORSES_ON_SEASHORE);
      currentImage = currentImage.copy(Bitmap.Config.ARGB_8888, true);
      currentImage = Bitmap.createScaledBitmap(currentImage, 300, 400, false);
      FritzVisionImage fritzImage = FritzVisionImage.fromBitmap(currentImage);
      FritzVisionStyleResult styleResult1 = predictor.predict(fritzImage);
      style2 = styleResult1.toBitmap();
      //hud.dismiss();
      imageView1.setImageBitmap(style2);
      currentImage = style2.copy(Bitmap.Config.ARGB_8888, true);
      scheduleDismiss(hud);
    } else if (position_of_style == 3) {
      hud.show();
      hud.setProgress(75);
      predictor = FritzVision.StyleTransfer.getPredictor(PaintingStyles.THE_SCREAM);
      currentImage = currentImage.copy(Bitmap.Config.ARGB_8888, true);
      currentImage = Bitmap.createScaledBitmap(currentImage, 300, 400, false);
      FritzVisionImage fritzImage = FritzVisionImage.fromBitmap(currentImage);
      FritzVisionStyleResult styleResult2 = predictor.predict(fritzImage);
      style3 = styleResult2.toBitmap();
      imageView1.setImageBitmap(style3);
      currentImage = style3.copy(Bitmap.Config.ARGB_8888, true);
      scheduleDismiss(hud);
    }
    else if (position_of_style == 4) {
      hud.show();
      hud.setProgress(75);
      predictor = FritzVision.StyleTransfer.getPredictor(PaintingStyles.PINK_BLUE_RHOMBUS);
      currentImage = currentImage.copy(Bitmap.Config.ARGB_8888, true);
      currentImage = Bitmap.createScaledBitmap(currentImage, 300, 400, false);
      FritzVisionImage fritzImage = FritzVisionImage.fromBitmap(currentImage);
      FritzVisionStyleResult styleResult3 = predictor.predict(fritzImage);
      style4 = styleResult3.toBitmap();
      imageView1.setImageBitmap(style4);
      currentImage = style4.copy(Bitmap.Config.ARGB_8888, true);
      scheduleDismiss(hud);
    }
    else if (position_of_style == 5) {
      hud.show();
      hud.setProgress(75);
      predictor = FritzVision.StyleTransfer.getPredictor(PaintingStyles.RITMO_PLASTICO);
      currentImage = currentImage.copy(Bitmap.Config.ARGB_8888, true);
      currentImage = Bitmap.createScaledBitmap(currentImage, 300, 400, false);
      FritzVisionImage fritzImage = FritzVisionImage.fromBitmap(currentImage);
      FritzVisionStyleResult styleResult4 = predictor.predict(fritzImage);
      style5 = styleResult4.toBitmap();
      imageView1.setImageBitmap(style5);
      currentImage = style5.copy(Bitmap.Config.ARGB_8888, true);
      scheduleDismiss(hud);
    }
    else {
      //scheduleDismiss(hud);
      imageView1.setImageBitmap(filter.processFilter(currentImage));
      currentImage = currentImage.copy(Bitmap.Config.ARGB_8888, true);
    }
  }

  /**
   * on the brightness.
   *
   * @param brightness chage the bright
   */
  @Override
  public void onBrightnessChanged(final int brightness) {
    brightnessFinal = brightness;
    Filter myFilter = new Filter();
    myFilter.addSubFilter(new BrightnessSubFilter(brightness));
    imageView1
        .setImageBitmap(myFilter.processFilter(currentImage.copy(Bitmap.Config.ARGB_8888, true)));
  }

  /**
   * change the saturation.
   *
   * @param saturation change
   */
  @Override
  public void onSaturationChanged(final float saturation) {
    saturationFinal = saturation;
    Filter myFilter = new Filter();
    myFilter.addSubFilter(new SaturationSubfilter(saturation));
    imageView1
        .setImageBitmap(myFilter.processFilter(currentImage.copy(Bitmap.Config.ARGB_8888, true)));
  }

  /**
   * change the contrast.
   *
   * @param contrast chang the contrast
   */
  @Override
  public void onContrastChanged(final float contrast) {
    contrastFinal = contrast;
    Filter myFilter = new Filter();
    myFilter.addSubFilter(new ContrastSubFilter(contrast));
    imageView1
        .setImageBitmap(myFilter.processFilter(currentImage.copy(Bitmap.Config.ARGB_8888, true)));
  }

  @Override
  public void onBackPressed() {
    Intent intent = new Intent(StyleActivity.this, MainActivity.class);
    startActivity(intent);
    finish();
  }

  /**
   * Start Edit.
   */
  @Override
  public void onEditStarted() {

  }

  /**
   * edit the complete.
   */
  @Override
  public void onEditCompleted() {
    // once the editing is done i.e seekbar is drag is completed,
    // apply the values on to filtered image
    final Bitmap bitmap = currentImage.copy(Bitmap.Config.ARGB_8888, true);

    Filter myFilter = new Filter();
    myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
    myFilter.addSubFilter(new ContrastSubFilter(contrastFinal));
    myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
    currentImage = myFilter.processFilter(bitmap);
  }

  /**
   * Resets image edit controls to normal when new filter is selected.
   */
  private void resetControls() {
    if (editImageFragment != null) {
      editImageFragment.resetControls();
    }
    brightnessFinal = 0;
    saturationFinal = 1.0f;
    contrastFinal = 1.0f;
  }

  class ViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mfragmentlist = new ArrayList<>();
    private final List<String> mfragmenttitlelist = new ArrayList<>();

    /**
     * Page Adapter.
     *
     * @param manager magaerthe adapter
     */
    public ViewPagerAdapter(FragmentManager manager) {
      super(manager);
    }

    /**
     * get the item.
     *
     * @param position the position of it
     * @return the fragment
     */
    @Override
    public Fragment getItem(int position) {
      return mfragmentlist.get(position);
    }

    /**
     * get the number.
     *
     * @return return the number
     */
    @Override
    public int getCount() {
      return mfragmentlist.size();
    }

    /**
     * add the fragment.
     *
     * @param fragment add the fragment
     * @param title the tile of the view
     */

    public void addFragment(Fragment fragment, String title) {
      mfragmentlist.add(fragment);
      mfragmenttitlelist.add(title);
    }

    /**
     * sequence of the view.
     *
     * @param position add the position
     * @return return the page
     */

    @Override
    public CharSequence getPageTitle(int position) {
      return mfragmenttitlelist.get(position);
    }
  }


  // load the default image from assets on app launch
  public void loadImage(Bitmap bitmap) {

    originalImage = bitmap;
    //originalImage = BitmapUtils.getBitmapFromAssets(this, IMAGE_NAME, 300, 300);
    currentImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
    //currentImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
    imageView1.setImageBitmap(originalImage);
    //prepareforimage(originalImage);

  }

  private Bitmap getOriginalImage() {

    return originalImage;
  }

  /**
   * create the menu.
   *
   * @param menu create the option
   * @return true of false.
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  /**
   * on the option.
   *
   * @param item the item
   * @return return the?
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
/*
    if (id == R.id.action_open) {
      //openImageFromGallery();
      return true;
    }*/

    if (id == R.id.action_save) {
      saveImageToGallery();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }



  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    if (requestCode == SELECT_GALLERY_IMAGE) {
      if (resultCode == RESULT_OK) {
        Bitmap bitmap = getOriginalImage();
        originalImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        currentImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        //finalImage = originalImage.copy(Bitmap.Config.ARGB_8888, true);
        imageView1.setImageBitmap(originalImage);
        bitmap.recycle();

        // render selected image thumbnails
        filtersListFragment.prepareThumbnail(originalImage);
      }
    }
  }

  private void openImageFromGallery() {
    Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
        .withListener(new MultiplePermissionsListener() {
          @Override
          public void onPermissionsChecked(MultiplePermissionsReport report) {
            if (report.areAllPermissionsGranted()) {
              Intent intent = new Intent(Intent.ACTION_PICK);
              intent.setType("image/*");
              startActivityForResult(intent, SELECT_GALLERY_IMAGE);
            } else {
              Toast.makeText(getApplicationContext(), "Permissions are not granted!",
                  Toast.LENGTH_SHORT).show();
            }
          }

          @Override
          public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
              PermissionToken token) {
            token.continuePermissionRequest();
          }
        }).check();
  }

  /*
   * saves image to camera gallery
   * */
  private void saveImageToGallery() {
    Dexter.withActivity(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
        .withListener(new MultiplePermissionsListener() {
          @Override
          public void onPermissionsChecked(MultiplePermissionsReport report) {
            if (report.areAllPermissionsGranted()) {
              final String path = BitmapUtils.insertImage(getContentResolver(), currentImage,
                  System.currentTimeMillis() + "_profile.jpg", null);
              if (!TextUtils.isEmpty(path)) {
                Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Image saved to gallery!", Snackbar.LENGTH_LONG)
                    .setAction("OPEN", new View.OnClickListener() {
                      @Override
                      public void onClick(View view) {
                        openImage(path);
                      }
                    });

                snackbar.show();
              } else {
                Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Unable to save image!", Snackbar.LENGTH_LONG);

                snackbar.show();
              }
            } else {
              Toast.makeText(getApplicationContext(), "Permissions are not granted!",
                  Toast.LENGTH_SHORT).show();
            }
          }

          @Override
          public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
              PermissionToken token) {
            token.continuePermissionRequest();
          }
        }).check();

  }

  // opening image in default image viewer app
  private void openImage(String path) {
    Intent intent = new Intent();
    intent.setAction(Intent.ACTION_VIEW);
    intent.setDataAndType(Uri.parse(path), "image/*");
    startActivity(intent);
  }
}
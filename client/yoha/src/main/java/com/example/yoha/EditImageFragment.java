package com.example.yoha;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import butterknife.BindView;
import butterknife.ButterKnife;


public class EditImageFragment extends Fragment implements SeekBar.OnSeekBarChangeListener {

  private EditImageFragmentListener listener;

  @BindView(R.id.seekbar_brightness)
  transient SeekBar seekBarBrightness;

  @BindView(R.id.seekbar_contrast)
  transient SeekBar seekBarContrast;

  @BindView(R.id.seekbar_saturation)
  transient SeekBar seekBarSaturation;

  /**
   * the listener of the page.
   *
   * @param listener the listener of the page.
   */
  protected void setListener(EditImageFragmentListener listener) {
    this.listener = listener;
  }


  /**
   * save the instance state.
   *
   * @param savedInstanceState save the instance state.
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  /**
   * Create View.
   *
   * @param inflater the inflater of the view
   * @param container the container of the view
   * @param savedInstanceState save the instance state
   * @return a view of the init
   */

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_edit_image, container, false);

    ButterKnife.bind(this, view);

    // keeping brightness value b/w -100 / +100
    seekBarBrightness.setMax(200);
    seekBarBrightness.setProgress(100);

    // keeping contrast value b/w 1.0 - 3.0
    seekBarContrast.setMax(20);
    seekBarContrast.setProgress(0);

    // keeping saturation value b/w 0.0 - 3.0
    seekBarSaturation.setMax(30);
    seekBarSaturation.setProgress(10);

    seekBarBrightness.setOnSeekBarChangeListener(this);
    seekBarContrast.setOnSeekBarChangeListener(this);
    seekBarSaturation.setOnSeekBarChangeListener(this);

    return view;
  }

  /**
   * Progress Change.
   *
   * @param seekBar the bar of the view
   * @param progress the progress
   * @param b if it is on process
   */
  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
    int p = progress;
    if (listener != null) {

      if (seekBar.getId() == R.id.seekbar_brightness) {
        // brightness values are b/w -100 to +100
        listener.onBrightnessChanged(p - 100);
      }

      if (seekBar.getId() == R.id.seekbar_contrast) {
        // converting int value to float
        // contrast values are b/w 1.0f - 3.0f
        // p = p > 10 ? p : 10;
        p += 10;
        float floatVal = .10f * p;
        listener.onContrastChanged(floatVal);
      }

      if (seekBar.getId() == R.id.seekbar_saturation) {
        // converting int value to float
        // saturation values are b/w 0.0f - 3.0f
        float floatVal = .10f * p;
        listener.onSaturationChanged(floatVal);
      }
    }
  }

  /**
   * Touch start tracking.
   */
  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {
    if (listener != null) {
      listener.onEditStarted();
    }
  }

  /**
   * touch stop tracking.
   *
   * @param seekBar the seek bar
   */

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
    if (listener != null) {
      listener.onEditCompleted();
    }
  }

  /**
   * control reset. reset the control of the page.
   */

  protected void resetControls() {
    seekBarBrightness.setProgress(100);
    seekBarContrast.setProgress(0);
    seekBarSaturation.setProgress(10);
  }

  /**
   * Image Listener. edit fragment listener.
   */
  public interface EditImageFragmentListener {

    void onBrightnessChanged(int brightness);

    void onSaturationChanged(float saturation);

    void onContrastChanged(float contrast);

    void onEditStarted();

    void onEditCompleted();
  }
}

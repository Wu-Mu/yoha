package com.example.yoha;


import static org.junit.Assert.*;

import android.view.Menu;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;
import org.junit.Test;


public class StyleActivityTest {

  @Test
  public void onBrightnessChanged() {
    int brightness = 10;
    Filter f = new Filter();
    f.addSubFilter(new BrightnessSubFilter(brightness));
    assertNotNull(f);
  }

  @Test
  public void onSaturationChanged() {
    float saturation = 10;
    Filter f = new Filter();
    f.addSubFilter(new SaturationSubfilter(saturation));
    assertNotNull(f);
  }

  @Test
  public void onContrastChanged() {
    float contrast = 10;
    Filter f = new Filter();
    f.addSubFilter(new ContrastSubFilter(contrast));
    assertNotNull(f);
  }

  @Test
  public void onEditCompleted() {
    int brightness = 10;
    float saturation = 10;
    float contrast = 10;
    Filter f = new Filter();
    f.addSubFilter(new BrightnessSubFilter(brightness));
    f.addSubFilter(new ContrastSubFilter(contrast));
    f.addSubFilter(new SaturationSubfilter(saturation));
    assertNotNull(f);
  }
}
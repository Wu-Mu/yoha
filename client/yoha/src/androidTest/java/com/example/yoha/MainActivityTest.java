package com.example.yoha;

import static org.junit.Assert.*;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import androidx.test.rule.ActivityTestRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

  @Rule
  public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

  @Test
  public void onRequestPermissionsResult() {

  }
}
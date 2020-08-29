package com.example.yoha.utils;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;
import java.lang.reflect.Field;

/**
 * Custom viewpager disabling the swipe https://stackoverflow.com/questions/9650265/how-do-disable-paging-by-swiping-with-finger-in-viewpager-but-still-be-able-to-s
 */

/**
 * Created by LinSen 2019/05/01 Custom viewpager disabling the swipe.
 */

public class NonSwipeableViewPager extends ViewPager {

  /**
   * Swipeable view pager.
   *
   * @param context the context of the page.
   */
  public NonSwipeableViewPager(Context context) {
    super(context);
    setMyScroller();
  }

  /**
   * Swipeable view pager.
   *
   * @param context the context of the page
   * @param attrs the attribute set using in the page
   */

  public NonSwipeableViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
    setMyScroller();
  }

  /**
   * Event intercept.
   *
   * @param event the motion event of the page
   * @return turn off the touch event
   */
  @Override
  public boolean onInterceptTouchEvent(MotionEvent event) {
    // Never allow swiping to switch between pages
    return false;
  }

  /**
   * Event touch listener.
   *
   * @param event the motion event of the page
   * @return turn off the touch event
   */

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    // Never allow swiping to switch between pages
    return false;
  }

  //down one is added for smooth scrolling

  private void setMyScroller() {
    try {
      Class<?> viewpager = ViewPager.class;
      Field scroller = viewpager.getDeclaredField("mScroller");
      scroller.setAccessible(true);
      scroller.set(this, new MyScroller(getContext()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public class MyScroller extends Scroller {

    /**
     * Custom Scoller.
     *
     * @param context the context of the scroller
     */
    public MyScroller(Context context) {
      super(context, new DecelerateInterpolator());
    }

    /**
     * Strart Scroll.
     *
     * @param startX the start position x of the point
     * @param startY the start position y of the point
     * @param dx the distance of x
     * @param dy the distance of y
     * @param duration the passing value
     */
    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
      super.startScroll(startX, startY, dx, dy, 350 /*1 secs*/);
    }
  }
}
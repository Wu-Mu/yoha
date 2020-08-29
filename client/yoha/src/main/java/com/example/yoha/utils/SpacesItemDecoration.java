package com.example.yoha.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

  private int space;

  /**
   * Space Item Decorataion.
   *
   * @param space the space used in the page
   */
  public SpacesItemDecoration(int space) {
    this.space = space;
  }

  /**
   * Get the item offsets.
   *
   * @param outRect the react if the page
   * @param view the view page
   * @param parent the parent class
   * @param state the state of the page
   */
  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    if (parent.getChildAdapterPosition(view) == state.getItemCount() - 1) {
      outRect.left = space;
      outRect.right = 0;
    } else {
      outRect.right = space;
      outRect.left = 0;
    }
  }
}
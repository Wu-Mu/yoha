package com.example.yoha;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import java.util.List;

/**
 * Created by ravi on 23/10/17.
 */

public class ThumbnailsAdapter extends RecyclerView.Adapter<ThumbnailsAdapter.MyViewHolder> {

  private List<ThumbnailItem> thumbnailItemList;
  private ThumbnailsAdapterListener listener;
  private Context mcontext;
  private int selectedIndex = 0;

  public class MyViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.thumbnail)
    ImageView thumbnail;

    @BindView(R.id.filter_name)
    TextView filterName;

    /**
     * the view holder.
     *
     * @param view the we used
     */
    public MyViewHolder(View view) {
      super(view);

      ButterKnife.bind(this, view);
    }
  }

  /**
   * adapt to the list.
   *
   * @param context the cintext
   * @param thumbnailItemList the list
   * @param listener the listener
   */

  protected ThumbnailsAdapter(Context context, List<ThumbnailItem> thumbnailItemList,
      ThumbnailsAdapterListener listener) {
    mcontext = context;
    this.thumbnailItemList = thumbnailItemList;
    this.listener = listener;
  }


  /**
   * To create view holder.
   *
   * @param parent the parent
   * @param viewType the view type
   */
  @Override
  public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.thumbnail_list_item, parent, false);

    return new MyViewHolder(itemView);
  }

  /**
   * the holder of the view.
   *
   * @param holder the holder
   * @param position the position of the holder
   */
  @Override
  public void onBindViewHolder(MyViewHolder holder, final int position) {

    final ThumbnailItem thumbnailItem = thumbnailItemList.get(position);

    holder.thumbnail.setImageBitmap(thumbnailItem.image);

    holder.thumbnail.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        StyleActivity.position_of_style = position;
        listener.onFilterSelected(thumbnailItem.filter);
        selectedIndex = position;
        notifyDataSetChanged();
      }
    }
    );

    holder.filterName.setText(thumbnailItem.filterName);

    if (selectedIndex == position) {
      holder.filterName
          .setTextColor(ContextCompat.getColor(mcontext, R.color.filter_label_selected));
    } else {
      holder.filterName.setTextColor(ContextCompat.getColor(mcontext, R.color.filter_label_normal));
    }
  }

  /**
   * get the number of the item.
   *
   * @return the size
   */
  @Override
  public int getItemCount() {
    return thumbnailItemList.size();
  }

  public interface ThumbnailsAdapterListener {

    void onFilterSelected(Filter filter);
  }
}
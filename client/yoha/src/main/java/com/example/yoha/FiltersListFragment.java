package com.example.yoha;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.example.yoha.utils.BitmapUtils;
import com.example.yoha.utils.SpacesItemDecoration;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;
import java.util.ArrayList;
import java.util.List;


public class FiltersListFragment extends Fragment implements
    ThumbnailsAdapter.ThumbnailsAdapterListener {

  @BindView(R.id.recycler_view)
  transient RecyclerView recyclerView;

  transient ThumbnailsAdapter madapter;

  transient List<ThumbnailItem> thumbnailItemList;

  transient FiltersListFragmentListener listener;

  /**
   * Required empty public constructor.
   */
  public FiltersListFragment() {
    // Required empty public constructor
  }

  /**
   * set the listener.
   *
   * @param listener set the listener
   */

  protected void setListener(FiltersListFragmentListener listener) {
    this.listener = listener;
  }

  /**
   * create the fragment.
   *
   * @param savedInstanceState save the instance page
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  /**
   * create the view.
   *
   * @param inflater the inflater
   * @param container the container of the view
   * @param savedInstanceState the stage of the instance
   * @return the view
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_filters_list, container, false);

    ButterKnife.bind(this, view);

    thumbnailItemList = new ArrayList<>();
    madapter = new ThumbnailsAdapter(getActivity(), thumbnailItemList, this);

    RecyclerView.LayoutManager mlayoutmanager = new LinearLayoutManager(getActivity(),
        LinearLayoutManager.HORIZONTAL, false);
    recyclerView.setLayoutManager(mlayoutmanager);
    recyclerView.setItemAnimator(new DefaultItemAnimator());
    int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
        getResources().getDisplayMetrics());
    recyclerView.addItemDecoration(new SpacesItemDecoration(space));
    recyclerView.setAdapter(madapter);
    Bitmap bitmap = StyleActivity.sendtheimage();
    prepareThumbnail(bitmap);

    return view;
  }

  /**
   * Renders thumbnails in horizontal list loads default image from Assets if passed param is null.
   */
  protected void prepareThumbnail(final Bitmap bitmap) {

    final Bitmap poppy_field = BitmapFactory.decodeResource(getResources(), R.drawable.poppy_field);
    final Bitmap horsesOnSeashore = BitmapFactory
        .decodeResource(getResources(), R.drawable.horses_on_seashore);
    final Bitmap theScream = BitmapFactory.decodeResource(getResources(), R.drawable.the_scream);
    final Bitmap pink_blue = BitmapFactory
        .decodeResource(getResources(), R.drawable.pink_blue_rhombus);
    final Bitmap ritmo = BitmapFactory.decodeResource(getResources(), R.drawable.ritmo_plastico);
    Runnable r = new Runnable() {
      public void run() {
        Bitmap thumbImage;

        if (bitmap == null) {
          thumbImage = BitmapUtils
              .getBitmapFromAssets(getActivity(), StyleActivity.IMAGE_NAME, 100, 100);

        } else {

          thumbImage = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
        }

        if (thumbImage == null) {
          return;
        }

        ThumbnailsManager.clearThumbs();
        thumbnailItemList.clear();

        // add normal bitmap first
        ThumbnailItem thumbnailItem = new ThumbnailItem();
        thumbnailItem.image = thumbImage;
        thumbnailItem.filterName = getString(R.string.filter_normal);
        ThumbnailsManager.addThumb(thumbnailItem);

        // add Starry Night style
        ThumbnailItem fieldItem = new ThumbnailItem();
        fieldItem.image = poppy_field;
        fieldItem.filterName = getString(R.string.style_poppy_field);
        ThumbnailsManager.addThumb(fieldItem);
        // add Horses on Seashore style
        ThumbnailItem horsesItem = new ThumbnailItem();
        horsesItem.image = horsesOnSeashore;
        horsesItem.filterName = getString(R.string.style_horses);
        ThumbnailsManager.addThumb(horsesItem);
        // add the Scream style
        ThumbnailItem screamItem = new ThumbnailItem();
        screamItem.image = theScream;
        screamItem.filterName = getString(R.string.style_scream);
        ThumbnailsManager.addThumb(screamItem);
        // add the pink_blue
        ThumbnailItem pinkBlueItem = new ThumbnailItem();
        pinkBlueItem.image = pink_blue;
        pinkBlueItem.filterName = getString(R.string.style_pink_blue);
        ThumbnailsManager.addThumb(pinkBlueItem);
        //add the ritmo
        ThumbnailItem ritmoItem = new ThumbnailItem();
        ritmoItem.image = ritmo;
        ritmoItem.filterName = getString(R.string.style_ritmo);
        ThumbnailsManager.addThumb(ritmoItem);

        List<Filter> filters = FilterPack.getFilterPack(getActivity());

        int i = 0;
        int numbersFilter = 4;
        for (Filter filter : filters) {
          i = i + 1;
          ThumbnailItem item = new ThumbnailItem();
          item.image = thumbImage;
          item.filter = filter;
          item.filterName = filter.getName();
          ThumbnailsManager.addThumb(item);
          if (i > numbersFilter) {
            break;
          }
        }

        thumbnailItemList.addAll(ThumbnailsManager.processThumbs(getActivity()));

        getActivity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            madapter.notifyDataSetChanged();
          }
        });
      }
    };

    new Thread(r).start();
  }

  /**
   * filer selecter.
   *
   * @param filter the filter used
   */
  @Override
  public void onFilterSelected(Filter filter) {

    if (listener != null) {
      listener.onFilterSelected(filter);
    }


  }

  public interface FiltersListFragmentListener {

    void onFilterSelected(Filter filter);
  }
}

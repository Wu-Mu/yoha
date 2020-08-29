package com.example.yoha;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import com.example.yoha.utils.HttpUtil;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserUploadActivity extends AppCompatActivity {


  private String[] imagesUrl;
  private SharedPreferences sharedPreferences;


  private ListView lv;
  private GridView gridView;
  private Button switchButton;
  @SuppressLint("HandlerLeak")
  private Handler uiHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      if (msg.what == 1) {
        lv.setAdapter(new ImageListAdapter(UserUploadActivity.this, imagesUrl));
        gridView.setAdapter(new ImageListAdapter(UserUploadActivity.this, imagesUrl));
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);

    DownloadThread downloadThread = new DownloadThread();
    downloadThread.start();
    setContentView(R.layout.actitivy_userupload);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        UserUploadActivity.this.finish();
      }
    });
    lv = findViewById(R.id.lv);
    gridView = findViewById(R.id.grid);
    switchButton = findViewById(R.id.bt);
    switchButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (lv.getVisibility() == View.VISIBLE) {
          lv.setVisibility(View.INVISIBLE);
          gridView.setVisibility(View.VISIBLE);
        } else {
          lv.setVisibility(View.VISIBLE);
          gridView.setVisibility(View.INVISIBLE);
        }
      }
    });


  }

  class DownloadThread extends Thread {

    @Override
    public void run() {
      Message msg = new Message();
      msg.what = 1;
      String baseUrl = "http://106.14.1.150:8080/file/getUserFiles";
      String token = sharedPreferences.getString("token", " ");
      Map<String, String> data = new HashMap<>();
      if (token != null) {
        data.put("token", token);
      }
      try {

        JSONObject jsonObject = HttpUtil.submitPostData(baseUrl, data);
        if (jsonObject.get("message").equals("Success")) {
          JSONArray jsonArray = jsonObject.getJSONArray("filenames");
          imagesUrl = new String[jsonArray.length()];
          int length = jsonArray.length();
          String downloadUrl = "http://106.14.1.150:8080/file/download/";
          for (int i = 0; i < length; i++) {
            String filename = jsonArray.get(i).toString();
            String url = downloadUrl + "?filename=" + filename + "&token=" + token;
            imagesUrl[i] = url;

          }

        }

      } catch (JSONException e) {
        e.printStackTrace();
      }

      uiHandler.sendMessage(msg);

    }
  }

  private class ImageListAdapter extends ArrayAdapter {

    private Context context;

    private String[] imageUrls;

    ImageListAdapter(Context context, String[] imageUrls) {

      super(context, R.layout.item_picasso, imageUrls);

      this.context = context;
      this.imageUrls = imageUrls;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        convertView = View.inflate(context, R.layout.item_picasso, null);
      }
      ImageView imageView = (ImageView) convertView;
      if (TextUtils.isEmpty(imageUrls[position])) {
        Picasso
            .with(context)
            .cancelRequest(imageView);
        imageView.setImageDrawable(null);

      } else {
        Picasso
            .with(context)
            .load(imageUrls[position])
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .noFade()
            .into((ImageView) convertView);
      }
      return convertView;
    }
  }


}

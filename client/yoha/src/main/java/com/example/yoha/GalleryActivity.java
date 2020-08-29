package com.example.yoha;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yoha.utils.HttpUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

public class GalleryActivity extends AppCompatActivity {

    transient TextView iconGallery;


    transient Button randomBnt;

    //    private SearchView search;
    transient ImageView img1;
    transient ImageView img2;
    transient ImageView img3;
    transient ImageView img4;
    transient ImageView img5;
    transient ImageView img6;


    private static long DOUBLE_CLICK_TIME = 0L;

    //声明相关变量
    transient private Toolbar toolbar;
    transient private DrawerLayout mdrawerlayout;
    transient private ListView lvLeftMenu;
    transient private String[] menu = {"My Upload"};
    transient private ArrayAdapter arrayAdapter;


    /**
     * To create the view.
     *
     * @param savedInstanceState The saved State
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        findViews(); //获取控件

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        //创建返回键，并实现打开关/闭监听
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GalleryActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //设置菜单列表
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, menu);
        lvLeftMenu.setAdapter(arrayAdapter);

        lvLeftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
                if (sharedPreferences.getBoolean("isLogin", false)) {
                    Intent intent = new Intent();

                    switch (position) {
                        case 0:
                            intent = new Intent(GalleryActivity.this, UserUploadActivity.class);
                            break;
                        case 1:
                            intent = new Intent(GalleryActivity.this, SettingsActivity.class);
                            break;
                        default:
                            break;
                    }
                    startActivity(intent);
                } else {
                    AlertDialog alertDialog2 = new AlertDialog.Builder(GalleryActivity.this)
                            .setMessage("Not logged in yet\nLogin Now?")
                            .setIcon(R.mipmap.ic_launcher)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(GalleryActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .create();
                    alertDialog2.show();
                }
            }
        });
        init();
//        initialListener();
    }

    private void init() {
        img1 = findViewById(R.id.imageview1);
        img2 = findViewById(R.id.imageview2);
        img3 = findViewById(R.id.imageview3);
        img4 = findViewById(R.id.imageview4);
        img5 = findViewById(R.id.imageview5);
        img6 = findViewById(R.id.imageview6);

        iconGallery = findViewById(R.id.iconGallery);
        randomBnt = findViewById(R.id.randomBnt);

        DownloadThread downloadThread = new DownloadThread();
        downloadThread.start();
    }


    /**
     * The listener of the login.
     */
    private void initialListener() {
        onClickRefresh();
    }

    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {

            }
        }
    };

    class DownloadThread extends Thread {

        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 1;
            randomBnt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
                    String token = sharedPreferences.getString("token", "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJBUFAiLCJpc3MiOiJMQ0gtU0VSVkVSIiwibmFtZSI6InRlc3QxIiwiaWQiOiIxIn0.bTtguOzPTb7GJpPWH2kTCSp9xgOlVW1uRyoHzuDgGU0");
                    try {
                        String baseUrl = "http://106.14.1.150:8080/file/getGalleryFiles";
                        Map<String, String> data = new HashMap<>();
                        System.out.println(token);
                        data.put("token", "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJBUFAiLCJpc3MiOiJMQ0gtU0VSVkVSIiwibmFtZSI6InRlc3QxIiwiaWQiOiIxIn0.bTtguOzPTb7GJpPWH2kTCSp9xgOlVW1uRyoHzuDgGU0");
                        JSONObject jsonObject = HttpUtil.submitPostData(baseUrl, data);
                        JSONArray jsonArray = jsonObject.getJSONArray("filenames");
                        String downloadUrl = "http://106.14.1.150:8080/file/download/";
                        int len = jsonArray.length();
                        for (int i = 0; i < len; i++) {
                            String filename = jsonArray.get(i).toString();
                            String Url = downloadUrl + "?filename=" + filename + "&token=" + token;
                            switch (i) {
                                case 0:
                                    Picasso
                                            .with(GalleryActivity.this)
                                            .load(Url)
                                            .into(img1);
                                    break;
                                case 1:
                                    Picasso
                                            .with(GalleryActivity.this)
                                            .load(Url)
                                            .into(img2);
                                    break;
                                case 2:
                                    Picasso
                                            .with(GalleryActivity.this)
                                            .load(Url)
                                            .into(img3);
                                    break;
                                case 3:
                                    Picasso
                                            .with(GalleryActivity.this)
                                            .load(Url)
                                            .into(img4);
                                    break;
                                case 4:
                                    Picasso
                                            .with(GalleryActivity.this)
                                            .load(Url)
                                            .into(img5);
                                    break;
                                case 5:
                                    Picasso
                                            .with(GalleryActivity.this)
                                            .load(Url)
                                            .into(img6);
                                    break;
                            }
                        }
                    } catch (Exception e) {
e.printStackTrace();
                    }
                }
            });
            uiHandler.sendMessage(msg);
        }
    }

    private void onClickRefresh() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {

                randomBnt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                                SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
                                String token = sharedPreferences.getString("token", " ");
                                try {
                                    String baseUrl = "http://106.14.1.150:8080/file/getGalleryFiles";
                                    Map<String, String> data = new HashMap<>();
                                    data.put("token", sharedPreferences.getString("token"," "));
                                    JSONObject jsonObject = HttpUtil.submitPostData(baseUrl, data);
                                    JSONArray jsonArray = jsonObject.getJSONArray("filenames");
                                    String downloadUrl = "http://106.14.1.150:8080/file/download/";
                                    int len = jsonArray.length();
                                    for (int i = 0; i < len; i++) {
                                        String filename = jsonArray.get(i).toString();
                                        String Url = downloadUrl + "?filename=" + filename + "&token=" + token;
                                        switch (i) {
                                            case 0:
                                                Picasso
                                                        .with(GalleryActivity.this)
                                                        .load(Url)
                                                        .into(img1);
                                                break;
                                            case 1:
                                                Picasso
                                                        .with(GalleryActivity.this)
                                                        .load(Url)
                                                        .into(img2);
                                                break;
                                            case 2:
                                                Picasso
                                                        .with(GalleryActivity.this)
                                                        .load(Url)
                                                        .into(img3);
                                                break;
                                            case 3:
                                                Picasso
                                                        .with(GalleryActivity.this)
                                                        .load(Url)
                                                        .into(img4);
                                                break;
                                            case 4:
                                                Picasso
                                                        .with(GalleryActivity.this)
                                                        .load(Url)
                                                        .into(img5);
                                                break;
                                            case 5:
                                                Picasso
                                                        .with(GalleryActivity.this)
                                                        .load(Url)
                                                        .into(img6);
                                                break;
                                        }
                                    }
                                } catch (Exception e) {
                                    Toast.makeText(GalleryActivity.this,
                                            e.toString(), Toast.LENGTH_LONG).show();
                                }
                    }
                });
//            }
//        }).start();

    }

    //返回键监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU && mdrawerlayout != null) {
            if (mdrawerlayout.isDrawerOpen(Gravity.END)) {
                mdrawerlayout.closeDrawer(Gravity.END);
            } else {
                mdrawerlayout.openDrawer(Gravity.END);
            }
            return true;
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mdrawerlayout.isDrawerOpen(Gravity.END)) {
                mdrawerlayout.closeDrawer(Gravity.END);
            } else {
                if ((System.currentTimeMillis() - DOUBLE_CLICK_TIME) > 2000) {
                    Toast.makeText(GalleryActivity.this, "One more click to exit", Toast.LENGTH_SHORT).show();
                    DOUBLE_CLICK_TIME = System.currentTimeMillis();
                } else {
                    Intent intent = new Intent(GalleryActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


  private void findViews() {
    toolbar = findViewById(R.id.toolbar);
    mdrawerlayout = findViewById(R.id.dl_left);
    lvLeftMenu = findViewById(R.id.lv_left_menu);
  }
}

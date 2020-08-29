package com.example.yoha;

import static android.view.View.GONE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

  SharedPreferences sharedPreferences;
  Button changepassword;
  Button logout;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    init();
    initListener();
  }

  private void initListener() {
    onClickChangePassword();
    onClickLogout();
  }

  private void onClickLogout() {
    logout.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        sharedPreferences.edit().putBoolean("isLogin", false).apply();
        sharedPreferences.edit().putString("token", "-1").apply();
        sharedPreferences.edit().putString("username", "-1").apply();
        AlertDialog alertDialog2 = new AlertDialog.Builder(SettingsActivity.this)
            .setMessage("Logout Success")
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                SettingsActivity.this.finish();
              }
            })
            .create();
        alertDialog2.show();

      }
    });
  }

  private void onClickChangePassword() {
    changepassword.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        if (sharedPreferences.getBoolean("isLogin", false)) {
          Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
          startActivity(intent);
        } else {
          AlertDialog alertDialog2 = new AlertDialog.Builder(SettingsActivity.this)
              .setMessage("Not logged in yet\nLogin Now?")
              .setIcon(R.mipmap.ic_launcher)
              .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
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
  }

  private void init() {
    sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
    changepassword = findViewById(R.id.settings_changepassword);
    logout = findViewById(R.id.settings_logout);

    if (!sharedPreferences.getBoolean("isLogin", false)) {
      logout.setVisibility(GONE);
    }
  }


}

package com.example.yoha;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.yoha.utils.HttpUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

  private transient EditText editUser;
  private transient EditText editPassword;
  private transient Button loginBnt;
  private transient Button registerBnt;

  private transient  SharedPreferences sharedPreferences;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_login);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        .detectDiskReads().detectDiskWrites().detectNetwork()
        .penaltyLog().build());
    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
        .detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath()
        .build());
    init();
    onChangeListener();
  }

  protected void init() {
    sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
    loginBnt = findViewById(R.id.loginBnt);
    registerBnt = findViewById(R.id.enterregisterBnt);
    editUser = findViewById(R.id.logineditUser);
    editPassword = findViewById(R.id.logineditPassword);
  }

  protected void onChangeListener() {
    onClickLogin();
    onClickRegister();
  }

  /**
   * The function is to enter the Register page.
   *
   * @author wzy
   */
  protected void onClickLogin() {
    loginBnt.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String name = editUser.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        requestLogin(name, password);
      }
    });
  }

  /**
   * The function is to enter the Register page.
   *
   * @author wzy
   */
  protected void onClickRegister() {
    registerBnt.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
      }
    });
  }

  /**
   * The function is to input username and password to login.
   *
   * @param name The username
   * @param password and password
   * @author wzy
   */
  protected void requestLogin(String name, String password) {
    try {
      String baseUrl = "http://106.14.1.150:8080/user/login";
      Map<String, String> data = new HashMap<>();
      data.put("name", name);
      data.put("password", password);
      final JSONObject jsonObject = HttpUtil.submitPostData(baseUrl, data);
      final String message = jsonObject.getString("message");
      if ("Success".equals(message)) {
        AlertDialog alertDialog2 = new AlertDialog.Builder(this)
            .setMessage("Login Success")
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                LoginActivity.this.finish();
              }
            })
            .create();
        alertDialog2.show();
        sharedPreferences.edit().putBoolean("isLogin", true).apply();
        sharedPreferences.edit().putString("token", jsonObject.getString("token")).apply();
        sharedPreferences.edit()
            .putString("username", jsonObject.getJSONObject("user").getString("name")).apply();
      } else {
        AlertDialog alertDialog2 = new AlertDialog.Builder(this)
            .setMessage("Login Failed")
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
              }
            })
            .create();
        alertDialog2.show();
      }
    } catch (Exception e) {
      Toast.makeText(LoginActivity.this,
          e.toString(), Toast.LENGTH_LONG).show();
    }
  }

}

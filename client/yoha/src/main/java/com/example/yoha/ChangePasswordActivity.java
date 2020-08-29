package com.example.yoha;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class ChangePasswordActivity extends AppCompatActivity {

  private EditText editUser;
  private EditText editPassword;
  private EditText editRepassword;
  private SharedPreferences sharedPreferences;
  private Button registerBnt;
  @SuppressLint("HandlerLeak")
  private Handler uiHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      if (msg.what == 1) {
        AlertDialog alertDialog2 = new AlertDialog.Builder(ChangePasswordActivity.this)
            .setMessage("Change Password Success")
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                ChangePasswordActivity.this.finish();
              }
            })
            .create();
        alertDialog2.show();
      } else {
        AlertDialog alertDialog2 = new AlertDialog.Builder(ChangePasswordActivity.this)
            .setMessage("Change Password Failed")
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
              }
            })
            .create();
        alertDialog2.show();
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_changepassword);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
    init();
    iniListener();

  }

  protected void init() {
    sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
    registerBnt = findViewById(R.id.registerBnt);
    editUser = findViewById(R.id.editUser);
    editPassword = findViewById(R.id.editPassword);
    editRepassword = findViewById(R.id.editRepassword);
  }

  private void iniListener() {
    onClickChange();
  }

  /**
   * The function helps the user to register his account and interact with the server, and gives the
   * validation of the code.
   */
  protected void onClickChange() {
    registerBnt.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String name = editUser.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String repassword = editRepassword.getText().toString().trim();
        if (!password.equals(repassword)) {
          Toast.makeText(ChangePasswordActivity.this,
              "Password not the same!", Toast.LENGTH_SHORT).show();
        } else {
          DownloadThread downloadThread = new DownloadThread();
          downloadThread.start();
        }
      }
    });
  }

  class DownloadThread extends Thread {

    @Override
    public void run() {
      Message msg = new Message();

      try {
        String baseUrl = "http://106.14.1.150:8080/user/updatePassword";
        String token = sharedPreferences.getString("token", "");
        String password = editPassword.getText().toString().trim();
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("password", password);
        final JSONObject jsonObject = HttpUtil.submitPostData(baseUrl, data);
        final String message = jsonObject.getString("message");
        if (message.equals("Success")) {
          msg.what = 1;
          uiHandler.sendMessage(msg);
        } else {
          msg.what = 2;
          uiHandler.sendMessage(msg);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }


    }
  }

}

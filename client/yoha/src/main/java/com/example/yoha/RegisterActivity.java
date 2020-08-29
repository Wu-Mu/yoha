package com.example.yoha;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

public class RegisterActivity extends AppCompatActivity {

  transient protected EditText editUser;
  transient protected EditText editPassword;
  transient protected EditText editRepassword;
  transient private Button registerBnt;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);
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
    onChangeListener();

  }

  protected void init() {
    registerBnt = findViewById(R.id.registerBnt);
    editUser = findViewById(R.id.editUser);
    editPassword = findViewById(R.id.editPassword);
    editRepassword = findViewById(R.id.editRepassword);
  }

  private void onChangeListener() {
    onClickRegister();
  }

  /**
   * The function helps the user to register his account and interact with the server, and gives the
   * validation of the code.
   *
   * @author wzy
   */
  protected void onClickRegister() {
    registerBnt.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String name = editUser.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String repassword = editRepassword.getText().toString().trim();
        if (!password.equals(repassword)) {
          Toast.makeText(RegisterActivity.this,
              "Password not the same!", Toast.LENGTH_SHORT).show();
        } else {
          requestRegister(name, password);
        }
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
  protected void requestRegister(String name, String password) {
    try {
      String baseUrl = "http://106.14.1.150:8080/user/register";
      Map<String, String> data = new HashMap<>();
      data.put("name", name);
      data.put("password", password);
      final JSONObject jsonObject = HttpUtil.submitPostData(baseUrl, data);
      final String message = jsonObject.getString("message");
      if (message.equals("Success")) {
        AlertDialog alertDialog2 = new AlertDialog.Builder(this)
            .setMessage("Register Success")
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                RegisterActivity.this.finish();
              }
            })
            .create();
        alertDialog2.show();
      } else {
        AlertDialog alertDialog2 = new AlertDialog.Builder(this)
            .setMessage("Register Failed")
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
              }
            })
            .create();
        alertDialog2.show();
      }
    } catch (Exception e) {
      Toast.makeText(RegisterActivity.this,
          e.toString(), Toast.LENGTH_LONG).show();
    }
  }

}

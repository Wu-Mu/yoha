package com.example.yoha.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpUtil {

  /**
   * 送Post请求到服务器 Param     :   params请求体内容，encode编码格式.
   */
  public static JSONObject submitPostData(String strUrlPath, Map<String, String> params)
      throws JSONException {
    JSONObject jsonObject = new JSONObject();
    byte[] data = getRequestData(params, "utf-8").toString().getBytes();
    try {

      URL url = new URL(strUrlPath);

      HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
      httpUrlConnection.setConnectTimeout(3000);     //设置连接超时时间
      httpUrlConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
      httpUrlConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
      httpUrlConnection.setRequestMethod("POST");     //设置以Post方式提交数据
      httpUrlConnection.setUseCaches(false);               //使用Post方式不能使用缓存
      httpUrlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      httpUrlConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
      OutputStream outputStream = httpUrlConnection.getOutputStream();
      outputStream.write(data);

      int response = httpUrlConnection.getResponseCode();            //获得服务器的响应码
      if (response == HttpURLConnection.HTTP_OK) {
        InputStream inptStream = httpUrlConnection.getInputStream();
        return new JSONObject(dealResponseResult(inptStream));             //处理服务器的响应结果
      }
    } catch (IOException e) {
      //e.printStackTrace();

      return jsonObject.put("error", e.getMessage());
    }
    return jsonObject.put("error", "Unknown Exception");
  }


  private static StringBuffer getRequestData(Map<String, String> params, String encode) {
    StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
    try {
      for (Map.Entry<String, String> entry : params.entrySet()) {
        stringBuffer.append(entry.getKey())
            .append("=")
            .append(URLEncoder.encode(entry.getValue(), encode))
            .append("&");
      }
      stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
    } catch (Exception e) {
      e.printStackTrace();
    }
    return stringBuffer;
  }

  private static String dealResponseResult(InputStream inputStream) {
    String resultData = null;      //存储处理结果
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    byte[] data = new byte[1024];
    int len = 0;
    try {
      while ((len = inputStream.read(data)) != -1) {
        byteArrayOutputStream.write(data, 0, len);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    resultData = new String(byteArrayOutputStream.toByteArray());
    return resultData;
  }


}
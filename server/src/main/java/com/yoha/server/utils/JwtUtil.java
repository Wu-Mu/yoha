package com.yoha.server.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

  static final String SECRET = "yoha";

  /**
   * get the token of the user by jwt.
   *
   * @param username the username
   * @return the token
   */
  public static String generateToken(String username) {

    try {
      Algorithm algorithm = Algorithm.HMAC256(SECRET);
      Map<String, Object> map = new HashMap<String, Object>();
      Date nowDate = new Date();
      Date expireDate = getAfterDate(nowDate, 0, 0, 0, 2, 0, 0);//2小时过期
      map.put("alg", "HS256");
      map.put("typ", "JWT");
      return JWT.create()
          /*设置头部信息 Header*/
          .withHeader(map)
          /*设置 载荷 Payload*/
          .withClaim("username", username)
          .withIssuer("LCH-YOHA-SERVER")//签名是有谁生成 例如 服务器
          .withAudience("APP")//签名的观众 也可以理解谁接受签名的
          .withIssuedAt(nowDate) //生成签名的时间
          .withExpiresAt(expireDate)//签名过期的时间
          /*签名 Signature */
          .sign(algorithm);
    } catch (JWTCreationException | UnsupportedEncodingException exception) {
      exception.printStackTrace();
    }
    return null;
  }

  /**
   * Validate token.
   *
   * @param token the validated token
   * @return true if auth
   */
  public static boolean validateToken(String token) {
    try {

      Algorithm algorithm = Algorithm.HMAC256(SECRET);
      JWTVerifier verifier = JWT.require(algorithm)
          .withIssuer("LCH-SERVER")
          .withAudience("APP")
          .build();

      DecodedJWT jwt = verifier.verify(token);

      if (jwt.getIssuer().equals("LCH-SERVER")) {
        return true;
      }

    } catch (JWTVerificationException | UnsupportedEncodingException exception) {
      return false;
    }

    return false;


  }


  /**
   * 返回一定时间后的日期.
   *
   * @param date 开始计时的时间
   * @param year 增加的年
   * @param month 增加的月
   * @param day 增加的日
   * @param hour 增加的小时
   * @param minute 增加的分钟
   * @param second 增加的秒
   */
  private static Date getAfterDate(Date date, int year, int month, int day, int hour, int minute,
      int second) {
    if (date == null) {
      date = new Date();
    }

    Calendar cal = new GregorianCalendar();

    cal.setTime(date);
    if (year != 0) {
      cal.add(Calendar.YEAR, year);
    }
    if (month != 0) {
      cal.add(Calendar.MONTH, month);
    }
    if (day != 0) {
      cal.add(Calendar.DATE, day);
    }
    if (hour != 0) {
      cal.add(Calendar.HOUR_OF_DAY, hour);
    }
    if (minute != 0) {
      cal.add(Calendar.MINUTE, minute);
    }
    if (second != 0) {
      cal.add(Calendar.SECOND, second);
    }
    return cal.getTime();
  }

  public static int getUserID(String token) {
    return Integer.parseInt(JWT.decode(token).getClaim("id").asString());
  }

  public static String getUserName(String token) {
    return JWT.decode(token).getClaim("name").asString();
  }
}
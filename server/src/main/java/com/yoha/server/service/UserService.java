package com.yoha.server.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.yoha.server.mapper.UserMapper;
import com.yoha.server.model.User;
import com.yoha.server.utils.JwtUtil;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private UserMapper userMapper;

  @Autowired
  public UserService(UserMapper userMapper) {
    this.userMapper = userMapper;
  }

  public UserMapper getUserMapper() {
    return userMapper;
  }

  public void setUserMapper(UserMapper up) {
    userMapper = up;
  }

  /**
   * Add user's information into database and encrypt the password.
   *
   * @param user The user need to register
   * @return com.yoha.server.model.User
   * @author Chenhao Li
   * @date 19-4-20 上午9:56
   **/
  public User add(User user) {
    String passwordHash = passwordToHash(user.getPassword());
    user.setPassword(passwordHash);
    userMapper.add(user);
    return findById(user.getId());
  }

  /**
   * Search the user by id from the database.
   *
   * @param id the id of the user in database
   * @return com.yoha.server.model.User
   * @author Chenhao Li
   * @date 19-4-20 上午9:58
   **/
  public User findById(int id) {
    User user = new User();
    user.setId(id);
    return userMapper.getUser(user);
  }

  /**
   * Search the user by name from the database.
   *
   * @param name the name of the user in database
   * @return com.yoha.server.model.User
   * @author Chenhao Li
   * @date 19-4-20 上午9:59
   **/
  public User findByName(String name) {
    User param = new User();
    param.setName(name);
    return userMapper.getUser(param);
  }

  /**
   * To update password  by token.
   *
   * @param token the client stored token
   * @param password the password
   * @return the user changed password stored in database
   * @author Chenhao Li
   */
  public User updatePassword(String token, String password) {

    User user = findById(JwtUtil.getUserID(token));
    String passwordHash = passwordToHash(password);
    user.setPassword(passwordHash);
    userMapper.updatePassword(user);
    return findByName(user.getName());
  }

  /**
   * compare the password between the input user and the user in database.
   *
   * @param user the input user
   * @param userInDataBase the user in database
   * @return boolean
   * @author Chenhao Li
   * @date 19-4-20 上午9:59
   **/
  public boolean comparePassword(User user, User userInDataBase) {
    return Objects.equals(passwordToHash(user.getPassword()),
        userInDataBase.getPassword());
  }

  /**
   * Encrypt the password using SHA-256 encryption algorithm.
   *
   * @param password the password to encrypted
   * @return encrypted password
   * @author Chenhao Li
   * @date 19-4-20 上午10:00
   **/

  private String passwordToHash(String password) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");

      digest.update(password.getBytes(Charset.defaultCharset()));
      byte[] src = digest.digest();
      StringBuilder stringBuilder = new StringBuilder();

      int i = 0;
      while (i < src.length) {
        byte b = src[i];
        String s = Integer.toHexString(b & 0xFF);
        int l = 2;
        if (s.length() < l) {
          stringBuilder.append('0');
        }
        stringBuilder.append(s);
        i++;
      }
      return stringBuilder.toString();
    } catch (NoSuchAlgorithmException ignore) {
      System.out.println("Please check the encryption algorithm");
    }
    return null;
  }

  /**
   * get the token of the input user.
   *
   * @param user the logined used
   * @return java.lang.String
   * @author Chenhao Li
   * @date 19-4-20 上午10:01
   **/
  public String getToken(User user) {
    try {
      return JWT.create()
          .withIssuer("LCH-SERVER")
          .withAudience("APP")
          .withClaim("name", user.getName())
          .withClaim("id", user.getId().toString())
          .sign(Algorithm.HMAC256("yoha"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }


}
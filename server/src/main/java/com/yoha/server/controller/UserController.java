package com.yoha.server.controller;

import static com.yoha.server.utils.JwtUtil.validateToken;

import com.alibaba.fastjson.JSONObject;
import com.yoha.server.model.User;
import com.yoha.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

  private UserService userService;


  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  public UserService getUserService() {
    return userService;
  }

  public void setUserService(UserService us) {
    userService = us;
  }

  /**
   * The api of the register a user.
   *
   * @param user the use need to be registered
   * @return java.lang.Object
   * @author Chenhao Li
   * @date 19-4-20 上午10:19
   **/
  @PostMapping("/register")
  public Object add(User user) {
    JSONObject jsonObject = new JSONObject();
    if (userService.findByName(user.getName()) != null) {

      jsonObject.put("message", "Duplicate username");
      return jsonObject;
    }
    User userInDataBase = userService.add(user);
    jsonObject.put("message", "Success");
    jsonObject.put("user", userInDataBase);
    return jsonObject;
  }

  /**
   * The api of the user login, which would compare the password and return the token of the user.
   *
   * @param user the user need to login
   * @return java.lang.Object
   * @author Chenhao Li
   * @date 19-4-20 上午9:54
   **/
  @PostMapping("/login")
  public Object login(User user) {
    User userInDataBase = userService.findByName(user.getName());
    JSONObject jsonObject = new JSONObject();
    if (userInDataBase == null) {
      jsonObject.put("message", "Nonexistent user");
    } else if (!userService.comparePassword(user, userInDataBase)) {
      jsonObject.put("message", "Incorrect password");
    } else {
      String token = userService.getToken(userInDataBase);
      jsonObject.put("message", "Success");
      jsonObject.put("token", token);
      jsonObject.put("user", userInDataBase);
    }
    return jsonObject;
  }

  /**
   * The api of the change password a user.
   *
   * @return java.lang.Object
   * @author Chenhao Li
   **/
  @PostMapping("/updatePassword")
  public Object updatePassword(@RequestParam("token") String token,
      @RequestParam("password") String password) {
    JSONObject jsonObject = new JSONObject();
    if (!validateToken(token)) {
      jsonObject.put("message", "Invalid token");
      return jsonObject;
    }

    User userInDataBase = userService.updatePassword(token, password);
    jsonObject.put("message", "Success");
    jsonObject.put("user", userInDataBase);
    return jsonObject;
  }


}
package com.yoha.server.mapper;

import com.yoha.server.model.User;

public interface UserMapper {

  void add(User user);

  User getUser(User user);

  void updatePassword(User user);
}
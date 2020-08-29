package com.yoha.server.model;

import java.sql.Timestamp;


public class File {

  private Integer id;
  private String name;
  private Integer ownerid;
  private Timestamp uploadtime;


  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getOwnerid() {
    return ownerid;
  }

  public void setOwnerid(Integer ownerid) {
    this.ownerid = ownerid;
  }


  /**
   * get Upload time form database.
   *
   * @return Timestamp format time
   */
  public Timestamp getUploadtime() {
    if (uploadtime == null) {
      return null;
    }
    return (Timestamp) uploadtime.clone();
  }

  /**
   * update upload time to database.
   * @param uploadtime set upload time and save to database
   */
  public void setUploadtime(Timestamp uploadtime) {
    if (uploadtime == null) {
      this.uploadtime = null;
    } else {
      this.uploadtime = (Timestamp) uploadtime.clone();
    }
  }
}

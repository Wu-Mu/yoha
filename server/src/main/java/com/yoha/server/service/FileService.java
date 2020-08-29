package com.yoha.server.service;

import com.alibaba.fastjson.JSONObject;
import com.yoha.server.mapper.FileMapper;
import com.yoha.server.model.File;
import com.yoha.server.utils.JwtUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

  private FileMapper fileMapper;

  @Autowired
  public FileService(FileMapper fileMapper) {
    this.fileMapper = fileMapper;
  }

  @Autowired
  private HttpServletRequest request;

  @Autowired
  private HttpServletResponse response;

  /**
   * The method to save a file in serer and relative information into databse.
   *
   * @param multipartFile the file need to upload
   * @param token the client stored token
   * @return the information in databse
   */
  public File upload(MultipartFile multipartFile, String token) throws IOException {

    int userid = JwtUtil.getUserID(token);
    String username = JwtUtil.getUserName(token);
    String origFileName = saveFile(multipartFile, username);
    File file = findFile(username + "_" + origFileName, userid);
    long time = new java.util.Date().getTime();
    if (file != null) {
      file.setUploadtime(new Timestamp(time + 28791806));
      fileMapper.updateFile(file);
    } else {
      file = new File();
      file.setName(username + "_" + origFileName);
      file.setOwnerid(userid);
      file.setUploadtime(new Timestamp(time + 28791806));
      fileMapper.addFile(file);
    }

    return findFileById(file.getId());
  }

  /**
   * The method to download a file from server.
   *
   * @param filename the filename need to be downloaded
   */
  public JSONObject download(String filename) {
    JSONObject jsonObject = new JSONObject();
    try (InputStream inputStream = new FileInputStream(new java.io.File("./images/" + filename));
        OutputStream outputStream = response.getOutputStream()) {
      response.setContentType("application/x-download");
      response.addHeader("Content-Disposition", "attachment;filename=" + filename);
      if (outputStream != null) {
        IOUtils.copy(inputStream, outputStream);
        outputStream.flush();
      }

    } catch (IOException e) {
      jsonObject.put("message", "Nonexistent file");
      return jsonObject;
    }
    return null;
  }


  /**
   * To get all the files user uploaded.
   *
   * @param token the client stored token
   * @return the user uploaded files in database format
   */
  public List<String> getUserFiles(String token) {

    File file = new File();
    file.setOwnerid(JwtUtil.getUserID(token));
    return fileMapper.getUserFiles(file);
  }


  /**
   * Get Gallery random files from database.
   *
   * @return Return the files name.
   */
  public List<String> getGalleryFiles() {
    int maxCount = findMaxID();
    List<String> result = new LinkedList<>();
    Set<Integer> filenames = new HashSet<>();
    while (filenames.size() != 6) {
      Random random = new Random();
      int rid = random.nextInt(maxCount) + 1;
      filenames.add(rid);
    }
    for (Integer ele : filenames) {
      File file = findFileById(ele);
      result.add(file.getName());
    }

    return result;
  }


  private Integer findMaxID() {
    return fileMapper.getMaxID();
  }

  private File findFileById(int id) {
    File file = new File();
    file.setId(id);
    return fileMapper.getFile(file);
  }

  private File findFile(String name, int ownerid) {
    File file = new File();
    file.setOwnerid(ownerid);
    file.setName(name);
    return fileMapper.getFile(file);
  }

  private String saveFile(MultipartFile multipartFile, String username) throws IOException {
    String origFileName = multipartFile.getOriginalFilename();
    String savePath = "./images/";
    multipartFile.transferTo(new java.io.File(savePath + username + "_" + origFileName));
    return origFileName;
  }


}

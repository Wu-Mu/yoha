package com.yoha.server.controller;

import static com.yoha.server.utils.JwtUtil.validateToken;

import com.alibaba.fastjson.JSONObject;
import com.yoha.server.service.FileService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {

  private FileService fileService;

  @Autowired
  public FileController(FileService fileService) {
    this.fileService = fileService;
  }

  /**
   * The api of he downloading files.
   *
   * @param filename the file's name
   * @return java.lang.Object
   */
  @GetMapping("/download")
  public Object download(@RequestParam("filename") String filename,
      @RequestParam("token") String token) {
    JSONObject jsonObject = new JSONObject();
    if (!validateToken(token)) {
      jsonObject.put("message", "Invalid token");
      return jsonObject;
    }

    return fileService.download(filename);
  }

  /**
   * The api of the uploading files.
   *
   * @param file the file to upload
   * @param token the token server sent to client
   * @return java.lang.Object
   * @author Chenhao Li
   */
  @PostMapping("/upload")
  public Object upload(@RequestParam("file") MultipartFile file,
      @RequestParam("token") String token)
      throws IllegalStateException, IOException {
    JSONObject jsonObject = new JSONObject();

    if (!validateToken(token)) {
      jsonObject.put("message", "Invalid token");

      return jsonObject;
    }

    if (file.isEmpty()) {
      jsonObject.put("message", "Empty file");

      return jsonObject;
    }

    jsonObject.put("message", "Success");
    jsonObject.put("file", fileService.upload(file, token));

    return jsonObject;
  }

  /**
   * The api of the getting all the file user uploaded.
   *
   * @param token the client stored token
   * @return java.lang.Object
   * @author Chenhao Li
   */
  @PostMapping("/getUserFiles")
  public Object getFiles(@RequestParam("token") String token) {
    JSONObject jsonObject = new JSONObject();

    if (!validateToken(token)) {
      jsonObject.put("message", "Invalid token");

      return jsonObject;
    }

    jsonObject.put("message", "Success");
    jsonObject.put("filenames", fileService.getUserFiles(token));

    return jsonObject;
  }

  /**
   * The api of the getting 8 files from server.
   *
   * @param token the client stored token
   * @author Chenhao Li
   */
  @PostMapping("getGalleryFiles")
  public Object getGallery(@RequestParam("token") String token) {
    JSONObject jsonObject = new JSONObject();

    if (!validateToken(token)) {
      jsonObject.put("message", "Invalid token");
      return jsonObject;
    }
    jsonObject.put("message", "Success");
    jsonObject.put("filenames", fileService.getGalleryFiles());
    return jsonObject;
  }
}



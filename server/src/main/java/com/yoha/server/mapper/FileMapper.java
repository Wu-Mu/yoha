package com.yoha.server.mapper;

import com.yoha.server.model.File;
import java.util.List;

public interface FileMapper {


  void addFile(File file);

  File getFile(File file);

  void updateFile(File file);

  List<String> getUserFiles(File file);

  List<String> getGalleryFiles(int startIndex, int endIndex);

  Integer getMaxID();
}
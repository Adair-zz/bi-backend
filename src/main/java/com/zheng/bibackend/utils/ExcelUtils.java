package com.zheng.bibackend.utils;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: Zheng Zhang
 * @Description Excel utils
 * @Created 08/16/2023 - 14:47
 */
@Slf4j
public class ExcelUtils {
  
  /**
   * Excel to csv.
   * @param multipartFile
   * @return
   */
  public static String excelToCsv(MultipartFile multipartFile) {
    List<Map<Integer, Integer>> list = null;
    try {
      list = EasyExcel.read(multipartFile.getInputStream())
          .excelType(ExcelTypeEnum.XLSX)
          .sheet()
          .headRowNumber(0)
          .doReadSync();
    } catch (IOException e) {
      log.error("Failed to convert excel to csv: ", e);
      e.printStackTrace();
    }
  
    // convert to csv file
    if (CollUtil.isEmpty(list)) {
      return "";
    }
    
    StringBuilder stringBuilder = new StringBuilder();
    // data header
    LinkedHashMap<Integer, String> headerMap = (LinkedHashMap) list.get(0);
    List<String> headerList = headerMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
    stringBuilder.append(StringUtils.join(headerList, ",")).append("\n");
    // data body
    for (int i = 1; i < list.size(); i++) {
    LinkedHashMap<Integer, String> dataBodyMap = (LinkedHashMap) list.get(i);
      List<String> dataBodyList = dataBodyMap.values().stream().filter(ObjectUtils::isNotEmpty).collect(Collectors.toList());
      stringBuilder.append(StringUtils.join(dataBodyList, ",")).append("\n");
    }
    return stringBuilder.toString();
  }
}
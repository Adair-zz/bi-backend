package com.zheng.bibackend.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.zheng.bibackend.manager.OpenAiManager;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 09/02/2023 - 18:20
 */
@SpringBootTest
class ExcelUtilsTest {
  
  @Resource
  private OpenAiManager openAiManager;
  
  /**
   * You may need slightly modify OpenAiManager to do this test.
   */
  @Test
  void utilsMemorySave() {
    List<Map<Integer, Integer>> list = null;
    list = EasyExcel.read("D:\\project\\bi-backend\\src\\main\\resources\\temp.xlsx")
        .excelType(ExcelTypeEnum.XLSX)
        .sheet()
        .headRowNumber(0)
        .doReadSync();
    String xlsxData = list.toString();
    
    String xlsxResult = openAiManager.genResultByOpenAi("temperature analysis", "line chart", xlsxData);
    System.out.println(xlsxResult);
    
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
    String csvData = stringBuilder.toString();


    String csvResult = openAiManager.genResultByOpenAi("temperature analysis", "line chart", csvData);
    System.out.println(csvResult);
  }
  
}
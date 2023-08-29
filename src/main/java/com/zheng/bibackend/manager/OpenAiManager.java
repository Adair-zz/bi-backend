package com.zheng.bibackend.manager;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 08/25/2023 - 22:40
 */
@Service
public class OpenAiManager {
  
//  public static void main(String[] args) {
//    String url = "https://api.openai.com/v1/completions";
//
//    Map<String, String> map = new HashMap<>();
//    map.put("model", "gpt-3.5-turbo");
//    map.put("message", "I will give you my analysis goal and data. Give me...");
//    map.put("My data:", "Header: date, user_number\n08/01/2023,10\n08/02/2023,20\n08/03/2023");
//    String jsonData = JSONUtil.toJsonStr(map);
//    String result = HttpRequest.post(url)
//        .header("Authorization", "Bearer your_key")
//        .body(jsonData)
//        .execute()
//        .body();
//
//    System.out.println(result);
//  }
  
  public String genResultByOpenAi(String jsonData) {
    String url = "https://api.openai.com/v1/completions";
    String response = HttpRequest.post(url)
        .header("Authorization", "Bearer your_key")
        .body(jsonData)
        .execute()
        .body();
    return response;
  }
}

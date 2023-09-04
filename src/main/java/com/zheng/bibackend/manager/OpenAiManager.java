package com.zheng.bibackend.manager;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import org.springframework.stereotype.Service;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 08/25/2023 - 22:40
 */
@Service
public class OpenAiManager {
  private final String aiUrl = "https://api.openai.com/v1/chat/completions";
  
  public String genResultByOpenAi(String goal, String chartType, String csvData) {
    JSONObject requestJson = new JSONObject();
    requestJson.putOpt("model", "gpt-3.5-turbo");

    
    JSONObject systemMessage = new JSONObject();
    systemMessage.putOpt("role", "system");
    systemMessage.putOpt("content", "You are a data analyst and front-end development expert, then I will provide you with content in the following fixed format: Analysis requirements: {requirement or goal of data analysis} Raw data: {raw data in csv format, with ',' as delimiter}");
    
    JSONObject assistantMessage = new JSONObject();
    assistantMessage.putOpt("role", "assistant");
    assistantMessage.putOpt("content", "Please generate content according to the following specified format based on these two parts. In addition, do not output any redundant beginning, end, and comments. Separate two parts by five '='. {This is the first part. The configuration object of the front-end Echarts V5, reasonably visualize the data, and do not generate any redundant content, such as comments. Only json data object. Do not assign the data object to a variable called 'option'. It should start with '{' and end with '}' } ===== {Clear data analysis conclusion, the more detailed the better, do not generate redundant notes}");
    
    JSONObject userMessage = new JSONObject();
    userMessage.putOpt("role", "user");
    String userContent = "Analysis requirement:" + goal + ".Please use " + chartType + ".Raw data:" + csvData;
    userMessage.putOpt("content", userContent);
    
    requestJson.putOpt("messages", new JSONObject[]{systemMessage, assistantMessage, userMessage});

    String aiResponse = HttpRequest.post(aiUrl)
        .header("Authorization", "Bearer your_key")
        .header("Content-Type", "application/json")
        .body(requestJson.toString())
        .execute()
        .body();
    
    JSONObject responseJson = new JSONObject(aiResponse);
    String aiResultContent = responseJson.getJSONArray("choices")
        .getJSONObject(0)
        .getJSONObject("message")
        .getStr("content");
    return aiResultContent;
  }
}

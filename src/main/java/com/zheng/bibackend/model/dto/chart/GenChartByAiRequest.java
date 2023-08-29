package com.zheng.bibackend.model.dto.chart;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Zheng Zhang
 * @Description
 * @Created 08/16/2023 - 12:54
 */
@Data
public class GenChartByAiRequest implements Serializable {
  
  /**
   * name
   */
  private String name;
  
  /**
   * goal
   */
  private String goal;
  
  /**
   * chart type
   */
  private String chartType;
  
  private static final long serialVersionUID = 1L;
}

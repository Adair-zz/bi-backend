package com.zheng.bibackend.model.dto.chart;

import lombok.Data;

import java.io.Serializable;


/**
 * Chart add request.
 *
 * @author <a href="https://github.com/Adair-zz">Zheng Zhang</a>
 */
@Data
public class ChartAddRequest implements Serializable {

    /**
     * chart name
     */
    private String name;
    
    /**
     * goal
     */
    private String goal;
    
    /**
     * chart raw data
     */
    private String chartData;
    
    /**
     * chart type
     */
    private String chartType;

    private static final long serialVersionUID = 1L;
}
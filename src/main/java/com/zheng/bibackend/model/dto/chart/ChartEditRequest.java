package com.zheng.bibackend.model.dto.chart;

import lombok.Data;

import java.io.Serializable;


/**
 * Chart edit request.
 *
 * @author <a href="https://github.com/Adair-zz">Zheng Zhang</a>
 */
@Data
public class ChartEditRequest implements Serializable {
    
    /**
     * id
     */
    private Long id;
    
    /**
     * goal
     */
    private String goal;
    
    /**
     * chart name
     */
    private String name;
    
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
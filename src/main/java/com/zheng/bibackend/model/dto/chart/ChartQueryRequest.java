package com.zheng.bibackend.model.dto.chart;


import com.zheng.bibackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


/**
 * Chart query request.
 *
 * @author <a href="https://github.com/Adair-zz">Zheng Zhang</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChartQueryRequest extends PageRequest implements Serializable {
    
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
     * chart type
     */
    private String chartType;
    
    /**
     * user id
     */
    private Long userId;
    
    private static final long serialVersionUID = 1L;
}
package com.zheng.bibackend.model.dto.chart;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Chart update request.
 *
 * @author <a href="https://github.com/Adair-zz">Zheng Zhang</a>
 */
@Data
public class ChartUpdateRequest implements Serializable {
    
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
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
    
    /**
     * generated chart
     */
    private String genChart;
    
    /**
     * generated result
     */
    private String genResult;
    
    /**
     * wait,running,succeed,failed
     */
    private String status;
    
    /**
     * executive message
     */
    private String execMessage;
    
    /**
     * user id
     */
    private Long userId;
    
    /**
     * create time
     */
    private Date createTime;
    
    /**
     * update time
     */
    private Date updateTime;
    
    /**
     * is delete
     */
    @TableLogic
    private Integer isDelete;

    private static final long serialVersionUID = 1L;
}
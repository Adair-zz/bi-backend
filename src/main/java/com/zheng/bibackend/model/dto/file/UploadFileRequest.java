package com.zheng.bibackend.model.dto.file;

import lombok.Data;

import java.io.Serializable;

/**
 * file upload request.
 *
 * @Author: Zheng Zhang
 * @Description
 * @Created 08/05/2023 - 19:21
 */
@Data
public class UploadFileRequest implements Serializable {

    /**
     * business.
     */
    private String biz;

    private static final long serialVersionUID = 1L;
}
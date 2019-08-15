package com.ekoplat.iot.model;

import lombok.Data;

/**
 * @author wuwudeqi
 * @version v1.0.0
 * @date 16:59 2019-07-31
 **/
@Data
public class UpdateInfo {
    public UpdateInfo() {
    }

    private String gwId;

    private Integer update_A;

    private String lockId;

    private Integer update_B;
}

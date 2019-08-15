package com.ekoplat.iot.dataobject;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author wuwudeqi
 * @version v1.0.0
 * @date 13:42 2019-07-31
 **/

@Entity(name = "gw_lock")
@Data
@DynamicUpdate
@DynamicInsert
public class GatewayAndLock {

    @Id
    @GeneratedValue
    private Integer id;

    private String gwId;

    private String ip;

    private Integer gwStatus;

    private String lockId;

    private Integer lockStatus;
}

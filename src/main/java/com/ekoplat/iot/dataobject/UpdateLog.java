package com.ekoplat.iot.dataobject;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author wuwudeqi
 * @version v1.0.0
 * @date 14:29 2019-08-22
 **/
@Data
@DynamicUpdate
@DynamicInsert
@Entity
public class UpdateLog {

    @Id
    @GeneratedValue
    private Integer id;

    private String deviceId;

    /** 主动 1.服务器版本高 2.服务器版本一样 3.服务器版本低
     *
     *  被动 0.升级包未选择 1.正常升级
     */
    private Integer isSuccess;

    private String typeName;

    private String updateType;

    private String oldVersion;

    private String newVersion;

    private Date startTime;

    private Date finishTime;

    private String useTime;
}

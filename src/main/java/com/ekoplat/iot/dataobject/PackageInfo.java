package com.ekoplat.iot.dataobject;

import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * @author wuwudeqi
 * @version v1.0.0
 * @date 14:33 2019-07-30
 **/
@Entity(name = "package")
@Data
@DynamicUpdate
public class PackageInfo {

    @Id
    @GeneratedValue
    private Integer id;

    private String typeName;

    private String typeNum;

    private String version;

    /** 创建时间. */
    private Date createTime;

    /** 更新时间. */
    private Date updateTime;

    private String updateType;
}

package com.ekoplat.iot.repository;

import com.ekoplat.iot.dataobject.UpdateLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author wuwudeqi
 * @version v1.0.0
 * @date 14:39 2019-08-22
 **/
public interface UpdateLogRepository extends JpaRepository<UpdateLog, Integer> {
}

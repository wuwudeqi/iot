package com.ekoplat.iot.repository;

import com.ekoplat.iot.dataobject.GatewayAndLock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author wuwudeqi
 * @version v1.0.0
 * @date 13:46 2019-07-31
 **/
public interface GatewayAndLockRepository extends JpaRepository<GatewayAndLock, Integer> {
    GatewayAndLock findBygwId(String gwId);
    GatewayAndLock findByip(String ip);
    GatewayAndLock findByLockId(String lockId);
    List<GatewayAndLock> findBygwStatus(Integer gwStatus);
}

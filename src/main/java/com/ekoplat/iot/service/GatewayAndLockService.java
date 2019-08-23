package com.ekoplat.iot.service;

import com.ekoplat.iot.dataobject.GatewayAndLock;

import java.util.List;

/**
 * @author wuwudeqi
 * @version v1.0.0
 * @date 13:47 2019-07-31
 **/
public interface GatewayAndLockService {
    void changeGwStatus(String gwId, String ip, int status);
    void bindLock(String gwId, String lockId);
    void unbindLock(String gwId, String lockId);
    GatewayAndLock save(GatewayAndLock gwLock);
    GatewayAndLock findByLockId(String lockId);
    GatewayAndLock findBygwId(String gwId);
    void changeAllGwStatus();
    List<GatewayAndLock> findAll();
}

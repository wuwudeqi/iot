package com.ekoplat.iot.service.impl;

import com.ekoplat.iot.dataobject.GatewayAndLock;
import com.ekoplat.iot.repository.GatewayAndLockRepository;
import com.ekoplat.iot.service.GatewayAndLockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wuwudeqi
 * @version v1.0.0
 * @date 13:47 2019-07-31
 **/
@Service
@Slf4j
public class GatewayAndLockServiceImpl implements GatewayAndLockService {

    @Autowired
    private GatewayAndLockRepository gatewayAndLockRepository;


    @Override
    public void changeGwStatus(String gwId, String ip, int status) {
        if (status == 0) {
            GatewayAndLock gwLock = gatewayAndLockRepository.findByip(ip);
            if (gwLock == null) {
                log.error("设备{}在数据库被删除, 无法下线, ip:{}", gwId, ip);
            }
            gwLock.setGwStatus(0);
            gwLock.setLockStatus(0);
            gatewayAndLockRepository.save(gwLock);
            log.info("【gw状态】gw下线，lock下线");
        } else {
            GatewayAndLock gwLock = gatewayAndLockRepository.findBygwId(gwId);
            if (gwLock == null) {
                    gwLock = new GatewayAndLock();
            }
            if(gwLock.getLockId()==null||"".equals(gwLock.getLockId())) {
                gwLock.setGwStatus(0);
            } else {
                gwLock.setLockStatus(1);
            }
            gwLock.setGwId(gwId);
            gwLock.setIp(ip);
            gwLock.setGwStatus(1);
            gatewayAndLockRepository.save(gwLock);
            log.info("【gw状态】gw与lock状态更新", gwId);
        }
    }

    @Override
    public void bindLock(String gwId, String lockId) {
        GatewayAndLock gwLock = gatewayAndLockRepository.findBygwId(gwId);
        gwLock.setLockId(lockId);
        gwLock.setLockStatus(1);
        gatewayAndLockRepository.save(gwLock);
        log.info("【绑定设备】绑定设备成功, lockId:{}", lockId);
    }

    @Override
    public void unbindLock(String gwId, String lockId) {
        GatewayAndLock gwLock = gatewayAndLockRepository.findBygwId(gwId);
        gwLock.setLockId("");
        gwLock.setLockStatus(0);
        gatewayAndLockRepository.save(gwLock);
        log.info("【绑定设备】解绑设备成功, lockId:{}", lockId);
    }

    @Override
    public GatewayAndLock save(GatewayAndLock gwLock) {
        return gatewayAndLockRepository.save(gwLock);
    }

    @Override
    public GatewayAndLock findByLockId(String lockId) {
        return gatewayAndLockRepository.findByLockId(lockId);
    }

    @Override
    public GatewayAndLock findBygwId(String gwId) {
        return gatewayAndLockRepository.findBygwId(gwId);
    }

    @Override
    public void changeAllGwStatus() {
        List<GatewayAndLock> allList = gatewayAndLockRepository.findBygwStatus(1);
        for (GatewayAndLock gl : allList) {
            gl.setGwStatus(0);
            if(!"".equals(gl.getLockId())) {
                gl.setLockStatus(0);
            }
        }
        gatewayAndLockRepository.saveAll(allList);
    }

    @Override
    public List<GatewayAndLock> findAll() {
        return  gatewayAndLockRepository.findAll();
    }

}

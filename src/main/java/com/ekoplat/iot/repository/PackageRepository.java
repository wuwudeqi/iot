package com.ekoplat.iot.repository;

import com.ekoplat.iot.dataobject.PackageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author wuwudeqi
 * @version v1.0.0
 * @date 14:39 2019-07-30
 **/
public interface PackageRepository extends JpaRepository<PackageInfo, Integer> {
    PackageInfo findFirstBytypeNumOrderByIdDesc(String typeNum);
    PackageInfo findFirstBytypeNameOrderByIdDesc(String typeName);
    @Transactional
    void deleteByTypeNameAndVersion(String typeName, String version);
}

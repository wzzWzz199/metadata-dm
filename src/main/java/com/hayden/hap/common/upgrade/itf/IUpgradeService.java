package com.hayden.hap.common.upgrade.itf;


import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.spring.service.IService;

@IService
public interface IUpgradeService {
    void listUpgrade() throws HDException;
}

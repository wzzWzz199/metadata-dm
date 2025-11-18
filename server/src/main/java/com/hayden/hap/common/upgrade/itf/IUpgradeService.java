package com.hayden.hap.common.upgrade.itf;


import com.hayden.hap.dbop.exception.HDException;


@IService
public interface IUpgradeService {
    void listUpgrade() throws HDException;
}

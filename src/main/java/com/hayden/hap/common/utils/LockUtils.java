package com.hayden.hap.common.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description:
 * @version: v3
 * @author: liyanzheng
 * @date: 2020/6/5 16:00
 */
public class LockUtils {
    private static ReentrantLock rootLock=new ReentrantLock(true);

    public static boolean lockroot() throws InterruptedException {
        return rootLock.tryLock(5, TimeUnit.SECONDS);
    }
}

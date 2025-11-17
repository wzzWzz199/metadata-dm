package com.hayden.hap.serial;

/**
 * Bridge mapper that reuses the common serialization rules while keeping the
 * legacy package name used throughout the upgrade services.
 */
public class VOObjectMapper extends com.hayden.hap.common.serial.VOObjectMapper {
    private static final long serialVersionUID = 1L;
}

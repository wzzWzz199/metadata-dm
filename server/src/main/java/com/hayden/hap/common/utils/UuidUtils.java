package com.hayden.hap.common.utils;

import java.util.UUID;

public class UuidUtils {
	public static String getUuid(){
		UUID uuid=UUID.randomUUID();
	    String str = uuid.toString(); 
	    String uuidStr=str.replace("-", "");
	    return uuidStr;
	}
}

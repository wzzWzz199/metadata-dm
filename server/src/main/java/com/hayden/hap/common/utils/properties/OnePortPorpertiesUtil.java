package com.hayden.hap.common.utils.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Component("onePortPorperties")
public class OnePortPorpertiesUtil {
	@Value("${port.isOne}")
	private Boolean isOnePort = false;

	public Boolean getIsOnePort() {
		return isOnePort;
	}

	public void setIsOnePort(Boolean isOnePort) {
		this.isOnePort = isOnePort;
	}
	
	
}

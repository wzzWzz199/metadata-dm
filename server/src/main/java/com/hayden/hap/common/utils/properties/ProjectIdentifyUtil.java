package com.hayden.hap.common.utils.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProjectIdentifyUtil {

    private static String projectIdentify;

	public static String getProjectIdentify() {
		return projectIdentify;
	}
	@Value("${projectIdentify:}")
	public void setProjectIdentify(String projectIdentify) {
		this.projectIdentify = projectIdentify;
	}

    
    
}

package com.hayden.hap.dbop.db.keyGen;

import com.hayden.hap.dbop.db.keyGen.itf.IKeyGeneratorService;
import com.hayden.hap.dbop.db.util.ObjectUtil;
import com.hayden.hap.common.spring.service.AppServiceHelper;
import org.apache.commons.lang3.StringUtils;


/** 
 * 
 * @ClassName: KeyGeneratorManager 
 * @Description: 
 * @author LUYANYING
 * @date 2015年5月26日 下午2:53:18 
 * @version V1.0   
 *
 */
public class KeyGeneratorManager {
	
	/**
	 * 
	 * @Title: generate 
	 * @Description: 初始化对应的主键生成服务以生成主键
	 * @param type
	 * @param gencode 序列号生成器编码 一般为表名
	 * @return
	 * @return String
	 * @throws
	 */
	public static String generate(String type, String gencode){
		IKeyGeneratorService keyGeneratorService = (IKeyGeneratorService) AppServiceHelper.findBean(type+"Service");
		if(StringUtils.isNotBlank(gencode))
			return keyGeneratorService.generate(gencode);
		return keyGeneratorService.generate();
	}
	
	/**
	 * 
	 * @Title: generate 
	 * @Description: 按默认gencode生成主键
	 * @return
	 * @return String
	 * @throws
	 */
	public static String generate() {
		return generate("serialGenerator", null);
	}
	/**
	 * 
	 * @Title: generate 
	 * @Description: 生成主键
	 * @param gencode 序列号生成器编码 一般为表名
	 * @return
	 * @return String
	 * @throws
	 */
	public static String generate(String gencode){
		return generate("serialGenerator", gencode);
	}
	
	/**
	 * 
	 * @Title: generate 
	 * @Description: 生成主键
	 * @param type
	 * @param gencode 序列号生成编码  gencode一般为表名
	 * @param batchSize 生成主键个数
	 * @return
	 * @return String[]
	 * @throws
	 */
	public static String[] generate(String type, String gencode, int batchSize){
		IKeyGeneratorService keyGeneratorService = (IKeyGeneratorService) AppServiceHelper.findBean(type+"Service");
		if(ObjectUtil.isNotNull(gencode))
			return keyGeneratorService.generate(gencode, batchSize);
		return keyGeneratorService.generate(batchSize);
	}
	/**
	 * 
	 * @Title: generate 
	 * @Description: 生成主键
	 * @param gencode 序列号生成编码  gencode一般为表名
	 * @param batchSize 生成主键个数
	 * @return
	 * @return String[]
	 * @throws
	 */
	public static String[] generate(String gencode, int batchSize){
		return generate("serialGenerator", gencode, batchSize);
	}
}

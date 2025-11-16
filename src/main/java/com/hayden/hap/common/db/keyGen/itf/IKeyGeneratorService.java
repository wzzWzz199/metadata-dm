package com.hayden.hap.common.db.keyGen.itf;

/** 
 * @ClassName: IKeyGeneratorService 
 * @Description: 
 * @author LUYANYING
 * @date 2015年5月26日 上午11:00:24 
 * @version V1.0   
 *  
 */
public interface IKeyGeneratorService {
	/**
	 * 
	 * @Title: generate 
	 * @Description: 生成主键
	 * @param gencode 序列号生成编码  gencode一般为表名
	 * @return
	 * @return String
	 * @throws
	 */
	public String generate(String gencode);
	
	/**
	 * 
	 * @Title: generate 
	 * @Description: 按默认gencode生成主键
	 * @return
	 * @return String
	 * @throws
	 */
	public String generate();
	
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
	public String[] generate(String gencode, int batchSize);
	
	/**
	 * 
	 * @Title: generate 
	 * @Description: 按默认gencode生成主键
	 * @param batchSize 生成主键个数
	 * @return
	 * @return String[]
	 * @throws
	 */
	public String[] generate(int batchSize);
}

package com.hayden.hap.dbop.db.keyGen.itf;



/** 
 * @ClassName: ISerialGeneratorService 
 * @Description: 
 * @author LUYANYING
 * @date 2015年6月2日 下午5:33:20 
 * @version V1.0   
 *  
 */
@IService("serialGeneratorService")
public interface ISerialGeneratorService extends IKeyGeneratorService {
	/**
	 * 
	 * @Title: loadFromDb 
	 * @Description: 从数据库加载序列生成器
	 * @param gencode
	 * @return void
	 * @throws
	 */
	public void loadFromDb_RequiresNew(String gencode);
	/**
	 * 
	 * @Title: loadFromDb 
	 * @Description: 从数据库加载序列生成器
	 * @param gencode
	 * @param batchSize 批量获取主键
	 * @return void
	 * @throws
	 */
	public void loadFromDb_RequiresNew(String gencode, int batchSize);
}

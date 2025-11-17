package com.hayden.hap.db.dataSource.itf;

import com.hayden.hap.dbop.exception.HDException;
import com.hayden.hap.common.db.tableDef.entity.TableDefVO;
import com.hayden.hap.common.spring.service.IService;

/** 
 * @ClassName: ISerialGeneratorService 
 * @Description: 
 * @author LUYANYING
 * @date 2015年6月2日 下午5:33:20 
 * @version V1.0   
 *  
 */
@IService("dataSourceGeneratorService")
public interface IDataSourceGeneratorService{

	String getPkColGencode(TableDefVO tableDefVO);

	String generate(String dataSourceId) throws HDException;

	String generate(String gencode, String dataSourceId) throws HDException;

	String[] generate(String gencode, String dataSourceId, int batchSize) throws HDException;

	void cleanCache(String dataSourceId);
}

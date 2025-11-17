package com.hayden.hap.dbop.db.temptable.service;

import com.hayden.hap.dbop.service.BaseServiceImpl;
import com.hayden.hap.dbop.db.temptable.dao.TempTableDaoImpl;
import com.hayden.hap.dbop.db.temptable.itf.ITempTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service("tempTableService")
public class TempTableServiceImpl extends BaseServiceImpl implements ITempTableService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private TempTableDaoImpl tempTableDao;
	
	@Override
	public <T> void createAndInsertData(String tableName, String[][] columns,
			Collection<T> list, String dataSourceId) {
		tempTableDao.createAndInsertData(tableName, columns, list, dataSourceId);
	}

	@Override
	public void dropTempTable(String tableName, String dataSourceId) {
		tempTableDao.dropTempTable(tableName, dataSourceId);
	}
	
}


delete from sy_table_def where table_code='mgr_upgrade_module' and tenantid=1;
delete from sy_table_column where table_code='mgr_upgrade_module';
delete from sy_serial_generator where gencode='mgr_upgrade_module';

INSERT INTO sy_table_def (tabledefid, table_code, modulecode, issqllog, table_desc, ddlsql, classname, created_by, created_dt, updated_by, updated_dt, ver, df, table_type, table_name, tenantid, table_delwhere, isenable) VALUES (20221, 'mgr_upgrade_module', 'mgr', 0, NULL, NULL, 'com.hayden.hap.upgrade.entity.UpgradeModuleVO', 1000, '2020-06-05 14:14:26', 1000, '2020-06-05 14:15:52', 1, 0, 1, '升级模块表', 1, NULL, 1);

INSERT INTO sy_table_column(tablecolumnid, table_code, coltype, ora_coltype, collen, colscale, colname, coldesc, ispk, coldefault, isnotnull, isautoinc, gencode, colorder, created_by, created_dt, updated_by, updated_dt, ver, df, colcode, tabledefid) VALUES (20222, 'mgr_upgrade_module', 'BIGINT', 'NUMBER', 19, 0, '主键', '主键', 1, NULL, 1, 1, 'mgr_upgrade_module', 1, 1000, '2020-06-05 14:14:26', 1000, '2020-06-05 14:14:26', 1, 0, 'id', 20221);
INSERT INTO sy_table_column(tablecolumnid, table_code, coltype, ora_coltype, collen, colscale, colname, coldesc, ispk, coldefault, isnotnull, isautoinc, gencode, colorder, created_by, created_dt, updated_by, updated_dt, ver, df, colcode, tabledefid) VALUES (20223, 'mgr_upgrade_module', 'VARCHAR', 'VARCHAR2', 50, 0, '模块编码', '模块编码', 0, NULL, 0, 0, NULL, 2, 1000, '2020-06-05 14:14:26', 1000, '2020-06-05 14:14:26', 1, 0, 'code', 20221);
INSERT INTO sy_table_column(tablecolumnid, table_code, coltype, ora_coltype, collen, colscale, colname, coldesc, ispk, coldefault, isnotnull, isautoinc, gencode, colorder, created_by, created_dt, updated_by, updated_dt, ver, df, colcode, tabledefid) VALUES (20224, 'mgr_upgrade_module', 'VARCHAR', 'VARCHAR2', 20, 0, '模块名称', '模块名称', 0, NULL, 0, 0, NULL, 3, 1000, '2020-06-05 14:14:26', 1000, '2020-06-05 14:14:26', 1, 0, 'name', 20221);
INSERT INTO sy_table_column(tablecolumnid, table_code, coltype, ora_coltype, collen, colscale, colname, coldesc, ispk, coldefault, isnotnull, isautoinc, gencode, colorder, created_by, created_dt, updated_by, updated_dt, ver, df, colcode, tabledefid) VALUES (20225, 'mgr_upgrade_module', 'VARCHAR', 'VARCHAR2', 100, 0, '当前版本', '当前版本', 0, NULL, 0, 0, NULL, 4, 1000, '2020-06-05 14:14:26', 1000, '2020-06-05 14:14:26', 1, 0, 'currentver', 20221);
INSERT INTO sy_table_column(tablecolumnid, table_code, coltype, ora_coltype, collen, colscale, colname, coldesc, ispk, coldefault, isnotnull, isautoinc, gencode, colorder, created_by, created_dt, updated_by, updated_dt, ver, df, colcode, tabledefid) VALUES (20226, 'mgr_upgrade_module', 'VARCHAR', 'VARCHAR2', 50, 0, '状态', '状态', 0, NULL, 0, 0, NULL, 5, 1000, '2020-06-05 14:14:26', 1000, '2020-06-05 14:14:26', 1, 0, 'status', 20221);
INSERT INTO sy_table_column(tablecolumnid, table_code, coltype, ora_coltype, collen, colscale, colname, coldesc, ispk, coldefault, isnotnull, isautoinc, gencode, colorder, created_by, created_dt, updated_by, updated_dt, ver, df, colcode, tabledefid) VALUES (20227, 'mgr_upgrade_module', 'VARCHAR', 'VARCHAR2', 10, 0, '进度', '进度', 0, NULL, 0, 0, NULL, 6, 1000, '2020-06-05 14:14:26', 1000, '2020-06-05 14:14:26', 1, 0, 'progress', 20221);
INSERT INTO sy_table_column(tablecolumnid, table_code, coltype, ora_coltype, collen, colscale, colname, coldesc, ispk, coldefault, isnotnull, isautoinc, gencode, colorder, created_by, created_dt, updated_by, updated_dt, ver, df, colcode, tabledefid) VALUES (20228, 'mgr_upgrade_module', 'VARCHAR', 'VARCHAR2', 20, 0, '最后升级人', '最后升级人', 0, NULL, 0, 0, NULL, 7, 1000, '2020-06-05 14:14:26', 1000, '2020-06-05 14:14:26', 1, 0, 'person', 20221);
INSERT INTO sy_table_column(tablecolumnid, table_code, coltype, ora_coltype, collen, colscale, colname, coldesc, ispk, coldefault, isnotnull, isautoinc, gencode, colorder, created_by, created_dt, updated_by, updated_dt, ver, df, colcode, tabledefid) VALUES (20229, 'mgr_upgrade_module', 'BIGINT', 'NUMBER', 19, 0, '升级开始时间', '升级开始时间', 0, NULL, 0, 0, NULL, 8, 1000, '2020-06-05 14:14:26', 1000, '2020-06-05 15:18:34', 1, 0, 'datetime', 20221);
INSERT INTO sy_table_column(tablecolumnid, table_code, coltype, ora_coltype, collen, colscale, colname, coldesc, ispk, coldefault, isnotnull, isautoinc, gencode, colorder, created_by, created_dt, updated_by, updated_dt, ver, df, colcode, tabledefid) VALUES (20230, 'mgr_upgrade_module', 'BIGINT', 'NUMBER', 19, 0, 'ts', 'ts', 0, NULL, 0, 0, NULL, 9, 1000, '2020-06-05 14:14:26', 1000, '2020-06-05 14:14:26', 1, 0, 'ts', 20221);
INSERT INTO sy_table_column(tablecolumnid, table_code, coltype, ora_coltype, collen, colscale, colname, coldesc, ispk, coldefault, isnotnull, isautoinc, gencode, colorder, created_by, created_dt, updated_by, updated_dt, ver, df, colcode, tabledefid) VALUES (20231, 'mgr_upgrade_module', 'INTEGER', 'INTEGER', 9, 0, '版本号', '乐观锁版本', 0, NULL, 0, 0, NULL, 20, 1000, '2020-06-05 14:14:34', 1000, '2020-06-05 14:14:34', 1, 0, 'ver', 20221);
INSERT INTO sy_table_column(tablecolumnid, table_code, coltype, ora_coltype, collen, colscale, colname, coldesc, ispk, coldefault, isnotnull, isautoinc, gencode, colorder, created_by, created_dt, updated_by, updated_dt, ver, df, colcode, tabledefid) VALUES (20232, 'mgr_upgrade_module', 'BIGINT', 'NUMBER', 19, 0, '创建人', '创建人', 0, '1000', 0, 0, NULL, 21, 1000, '2020-06-05 14:14:34', 1000, '2020-06-05 14:14:34', 1, 0, 'created_by', 20221);
INSERT INTO sy_table_column(tablecolumnid, table_code, coltype, ora_coltype, collen, colscale, colname, coldesc, ispk, coldefault, isnotnull, isautoinc, gencode, colorder, created_by, created_dt, updated_by, updated_dt, ver, df, colcode, tabledefid) VALUES (20233, 'mgr_upgrade_module', 'DATETIME', 'DATE', 0, 0, '创建时间', '创建时间', 0, NULL, 0, 0, NULL, 22, 1000, '2020-06-05 14:14:34', 1000, '2020-06-05 14:14:34', 1, 0, 'created_dt', 20221);
INSERT INTO sy_table_column(tablecolumnid, table_code, coltype, ora_coltype, collen, colscale, colname, coldesc, ispk, coldefault, isnotnull, isautoinc, gencode, colorder, created_by, created_dt, updated_by, updated_dt, ver, df, colcode, tabledefid) VALUES (20234, 'mgr_upgrade_module', 'BIGINT', 'NUMBER', 19, 0, '修改人', '修改人', 0, '1000', 0, 0, NULL, 23, 1000, '2020-06-05 14:14:34', 1000, '2020-06-05 14:14:34', 1, 0, 'updated_by', 20221);
INSERT INTO sy_table_column(tablecolumnid, table_code, coltype, ora_coltype, collen, colscale, colname, coldesc, ispk, coldefault, isnotnull, isautoinc, gencode, colorder, created_by, created_dt, updated_by, updated_dt, ver, df, colcode, tabledefid) VALUES (20235, 'mgr_upgrade_module', 'DATETIME', 'DATE', 0, 0, '修改时间', '修改时间', 0, NULL, 0, 0, NULL, 22, 1000, '2020-06-05 14:14:34', 1000, '2020-06-05 14:14:34', 1, 0, 'updated_dt', 20221);
INSERT INTO sy_table_column(tablecolumnid, table_code, coltype, ora_coltype, collen, colscale, colname, coldesc, ispk, coldefault, isnotnull, isautoinc, gencode, colorder, created_by, created_dt, updated_by, updated_dt, ver, df, colcode, tabledefid) VALUES (20236, 'mgr_upgrade_module', 'TINYINT', 'NUMBER', 1, 0, '逻辑删除', '逻辑删除', 0, '0', 0, 0, NULL, 25, 1000, '2020-06-05 14:14:34', 1000, '2020-06-05 14:14:34', 1, 0, 'df', 20221);
INSERT INTO sy_table_column(tablecolumnid, table_code, coltype, ora_coltype, collen, colscale, colname, coldesc, ispk, coldefault, isnotnull, isautoinc, gencode, colorder, created_by, created_dt, updated_by, updated_dt, ver, df, colcode, tabledefid) VALUES (20237, 'mgr_upgrade_module', 'BIGINT', 'NUMBER', 19, 0, '时间戳', NULL, 0, NULL, 0, 0, NULL, 26, 1000, '2020-06-05 17:06:39', 1000, '2020-06-05 17:07:00', 1, 0, 'lastfilets', 20221);
INSERT INTO sy_table_column(tablecolumnid, table_code, coltype, ora_coltype, collen, colscale, colname, coldesc, ispk, coldefault, isnotnull, isautoinc, gencode, colorder, created_by, created_dt, updated_by, updated_dt, ver, df, colcode, tabledefid) VALUES (20238, 'mgr_upgrade_module', 'VARCHAR', 'VARCHAR2', 100, 0, '当前产品版本', '当前产品版本', 0, NULL, 0, 0, NULL, 27, 1000, '2020-06-05 14:14:26', 1000, '2020-06-05 14:14:26', 1, 0, 'productver', 20221);
INSERT INTO sy_table_column(tablecolumnid, table_code, coltype, ora_coltype, collen, colscale, colname, coldesc, ispk, coldefault, isnotnull, isautoinc, gencode, colorder, created_by, created_dt, updated_by, updated_dt, ver, df, colcode, tabledefid) VALUES (20239, 'mgr_upgrade_module', 'BIGINT', 'NUMBER', 19, 0, '产品时间戳', '产品时间戳', 0, NULL, 0, 0, NULL, 28, 1000, '2020-06-05 14:14:26', 1000, '2020-06-05 14:14:26', 1, 0, 'prolastfilets', 20221);

INSERT INTO sy_serial_generator(serialgenid, gencode, gennext, gencache, genprefix, created_by, created_dt, updated_by, updated_dt, ver, df) VALUES (10200, 'mgr_upgrade_module', '1', 10, NULL, 1000, '2019-12-06 10:30:00', 1000, '2019-12-06 10:30:00', 1, 0);

CREATE TABLE mgr_upgrade_module(
 	id BIGINT(19) NOT NULL COMMENT '主键',
 	code VARCHAR(50) COMMENT '模块编码',
 	name VARCHAR(20) COMMENT '模块名称',
 	currentver VARCHAR(100) COMMENT '当前版本',
 	productver VARCHAR(100)  COMMENT '当前产品版本',
 	status VARCHAR(50) COMMENT '状态',
 	progress VARCHAR(10) COMMENT '进度',
 	person VARCHAR(20) COMMENT '最后升级人',
 	datetime BIGINT(19) COMMENT '升级开始时间',
 	ts BIGINT(19) COMMENT 'ts',
 	ver INTEGER(9) COMMENT '[版本号]乐观锁版本',
 	created_by BIGINT(19) DEFAULT '1000' COMMENT '创建人',
 	created_dt DATETIME COMMENT '创建时间',
 	updated_by BIGINT(19) DEFAULT '1000' COMMENT '修改人',
 	updated_dt DATETIME COMMENT '修改时间',
 	df TINYINT(1) DEFAULT '0' COMMENT '逻辑删除',
 	lastfilets BIGINT(19) COMMENT '最后一个升级的文件的时间戳',
 	prolastfilets BIGINT(19) COMMENT '产品补丁时间戳',
 	PRIMARY KEY (id) 
)



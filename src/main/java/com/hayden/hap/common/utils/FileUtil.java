package com.hayden.hap.common.utils;

import com.hayden.hap.common.common.exception.HDException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.*;


public class FileUtil {
	private static final String lineSeparator = System.getProperty("line.separator");
	private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
	
	/**
	 * 将contents内容写入filePath的文件中
	 * @param contents
	 * @param filePath
	 */
	public static void writeFile(String contents,String filePath) {
		OutputStreamWriter writerStream=null;
		BufferedWriter bw =null;
		try {
			File file = new File(filePath);
			writerStream = new OutputStreamWriter(new FileOutputStream(file,true),"UTF-8");
			bw = new BufferedWriter(writerStream);
			
			/*
			FileWriter fw = new FileWriter(filePath,true);//支持续写
			BufferedWriter bw = new BufferedWriter(fw);
			*/
			if(!contents.endsWith(";")){
				//contents += ";-- lineend";
				contents += ";";
			}
			bw.write(contents + lineSeparator);		   
		} catch (IOException e) {
		    logger.error("写文件出错",e);
		}
		finally{
			IOUtils.closeQuietly(bw);
			IOUtils.closeQuietly(writerStream);
		}
	}
	
	/**
	 * 将contents内容写入filePath的文件中
	 * @param contents
	 * @param filePath
	 */
	public static void writeFile(List<String>contents,String filePath) {
		OutputStreamWriter writerStream =null;
		BufferedWriter bw =null;
		try {
			File file = new File(filePath);
			writerStream = new OutputStreamWriter(new FileOutputStream(file,true),"UTF-8");
			bw = new BufferedWriter(writerStream);
			
			/*
			FileWriter fw = new FileWriter(filePath,true);//支持续写
			BufferedWriter bw = new BufferedWriter(fw);
			*/
			for(String str : contents){
				if(!str.endsWith(";")){
					//str += ";-- lineend";
					str += ";";
				}
				//str=str.replaceAll("\\\\", "\\\\\\\\");
				bw.write(str + lineSeparator);
			}		   
		} catch (IOException e) {
			logger.error("写文件出错",e);
		}finally{
			IOUtils.closeQuietly(bw);
			IOUtils.closeQuietly(writerStream);
		}
		
	}
	
	/**
	 * 将filePath路径的文件按行读到list中
	 * @param filePath
	 * @return
	 */
	public static List<String> readFile(String filePath){
		List<String> contents = new ArrayList<String>();
		BufferedReader bufferedreader=null;
		FileReader fr = null;
		try {
			fr = new FileReader(filePath);
			bufferedreader = new BufferedReader(fr);
			String sqlLine = "";
			String strLine = "";  
			while ((strLine = bufferedreader.readLine()) != null) {  
				if (0 != strLine.length()) {
					if(!strLine.endsWith(";")){
						sqlLine += strLine;
						strLine = "";
						continue;
					}else{
						sqlLine += strLine;
						contents.add(sqlLine.trim());
						sqlLine = "";
					}  
				}  
			}  
			return contents;
		} catch (IOException e) {
			e.printStackTrace();
		} 
		finally{
			IOUtils.closeQuietly(bufferedreader);
			IOUtils.closeQuietly(fr);
		}
		return null;
	}
	
	
	public static Map<String,List<String>> readFileToMap(String filePath) {
		Map<String,List<String>> sqlMap = new HashMap<String,List<String>>();
		//List<String> createList = new ArrayList<String>();
		//List<String> alterList = new ArrayList<String>();
		List<String> deleteList = new ArrayList<String>();
		List<String> updateList = new ArrayList<String>();
		List<String> insertList = new ArrayList<String>();
		FileReader fr = null;
		BufferedReader bufferedreader =null;
		try {
			fr = new FileReader(filePath);
			bufferedreader = new BufferedReader(fr);
			String sqlLine = "";
			String strLine = "";  
			while ((strLine = bufferedreader.readLine()) != null) {  
				if (0 != strLine.length()) {
					if(!strLine.endsWith(";")){
						sqlLine += strLine;
						strLine = "";
						continue;
					}else{
						sqlLine += strLine;
						sqlLine = sqlLine.trim();
						/*
						if(sqlLine.startsWith("create")){
							createList.add(sqlLine);
						}else if(sqlLine.startsWith("alter")){
							alterList.add(sqlLine);
						}*/
						if(sqlLine.startsWith("delete")){
							deleteList.add(sqlLine);
						}else if(sqlLine.startsWith("update")){
							updateList.add(sqlLine);
						}else if(sqlLine.startsWith("insert")){
							insertList.add(sqlLine);
						}
						//sqlList.add(sqlLine);
						sqlLine = "";
					}  
				}  
			}  
			/*
			sqlMap.put("create", createList);
			sqlMap.put("alter", alterList);
			*/
			sqlMap.put("delete", deleteList);
			sqlMap.put("update", updateList);
			sqlMap.put("insert", insertList);
			return sqlMap;
		} catch (IOException e) {		
			logger.error(e.getMessage());
			return null;
		}
		finally{
			IOUtils.closeQuietly(bufferedreader);
			IOUtils.closeQuietly(fr);
		}
	}
	
	
	
	/**
	 * 获取文件夹路径下的所有文件（按文件名排序）
	 * @param filePath
	 * @param fileType 文件后缀，不给的话获取所有文件，给定的话，获取指定后缀的文件
	 * @param sortType 排序类型：asc（null）-正序，desc-倒序
	 * @return
	 */
	public static List<File> getSortFiles(String filePath,final String fileType,final String sortType){
		//"D:\\myworkfiles\\20160428001-hap_sy-v1.0.1.001-海顿-patch\\xmlscript\\mysql"
		File file = new File(filePath);
        if (!file.exists()||file.isFile() || (0 == file.list().length)){
            return null;  
        }else{  
            File[] files = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
	            	if((StringUtils.isBlank(fileType)&&file.isFile())||(file.isFile() && file.getName().endsWith(fileType))){
	            		return true;
	            	}
					return false;
				}
			});
            List<File> listFiles=Arrays.asList(files);
            Collections.sort(listFiles, new Comparator<File>(){
                @Override
                public int compare(File obj1, File obj2){
                	int compareValue=obj1.getName().compareTo(obj2.getName());
                	compareValue="desc".equals(sortType) ? 0-compareValue : compareValue;
                	return compareValue;
                	/*
	                if(obj1.isDirectory() && obj2.isFile()){
	                	return 1;
	                }
	                else if(obj1.isFile() && obj2.isDirectory()){
	                    return -1;
	                }
	                else{
	                    return obj1.getName().compareTo(obj2.getName());
	                }*/
                }
            });
            return listFiles;
        }
	}
	
	/**
	 * 获取文件夹路径下的所有文件夹（按文件夹名排序）
	 * @param filePath
	 * @param sortType 排序类型：asc（null）-正序，desc-倒序
	 * @return
	 */
	public static List<File> getSortFolders(String filePath,final String sortType){
		//"D:\\myworkfiles"
		File file = new File(filePath);
        if (file.isFile() || file.list()==null|| (0 == file.list().length)){  
            return null;  
        }else{  
            File[] files = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					return file.isDirectory();
				}
			});             
            List<File> listFiles = Arrays.asList(files);                       
            Collections.sort(listFiles, new Comparator<File>(){
                @Override
                public int compare(File obj1, File obj2){
                	int compareValue=obj1.getName().compareTo(obj2.getName());
                	compareValue="desc".equals(sortType) ? 0-compareValue : compareValue;
                	return compareValue;
                }
            });
/*            //倒叙排
            if(sortType != null && "desc".equals(sortType)){
            	Collections.reverse(listFiles);
            }*/
            return listFiles;
        }
	}
	
	
	/**
	 * 移动目录（将oldPath目录（包含目录本身）完整移动到newPath目录下）
	 * @param oldPath
	 * @param newPath
	 * @throws Exception
	 */
	public static void moveFolder(String oldPath,String newPath){		
		boolean bool = false;
		File f1 = new File(oldPath);
		String f1Name = f1.getName();//源目录名
		String f2path = newPath;//目标路径
		String path = f2path + File.separator + f1Name;
		File f3 = new File(path);
		if(!f3.exists()){
			f3.mkdir();//先创建目录
			bool = true;
		}
		if(bool){
			copyDerectoryFun(f1,f3);
			delFolder(oldPath);
		}		
	}
	
	/**
	 * 移动目录（将f1目录（包含目录本身）完整移动到f2目录下）
	 * @param f1
	 * @param f2
	 * @throws Exception
	 */
	public static void moveFolder(File f1,File f2){		
		boolean bool = false;
		String f1Name = f1.getName();//源目录名
		String f2path = f2.getPath();//目标路径
		String path = f2path + File.separator + f1Name;
		File f3 = new File(path);
		if(!f3.exists()){
			f3.mkdir();//先创建目录
			bool = true;
		}
		if(bool){
			copyDerectoryFun(f1,f3);
			delFolder(f1.getPath());
		}		
	}
	
	
	/**
	 * 复制目录（将oldPath目录（包含目录本身）完整复制到newPath目录下）
	 * @param oldPath
	 * @param newPath
	 * @throws Exception
	 */
	public static void copyFolder(String oldPath,String newPath){
		File f1 = new File(oldPath);
		File f2 = new File(newPath);
		copyDerectory(f1,f2);
	}
	
	
	/**
	 * 复制目录（将f1目录（包含目录本身）完整复制到f2目录下）
	 * @param f1
	 * @param f2
	 * @throws Exception
	 */
	public static void copyDerectory(File f1, File f2){
		boolean bool = false;
		String f1Name = f1.getName();//源目录名
		String f2path = f2.getPath();//目标路径
		String path = f2path + File.separator + f1Name;
		File f3 = new File(path);
		if(!f3.exists()){
			f3.mkdir();//先创建目录
			bool = true;
		}
		if(bool){
			copyDerectoryFun(f1,f3);
		}
	}
		
	/**
	 * 复制目录执行方法
	 * @param f1
	 * @param f2
	 * @throws Exception
	 */
	public static void copyDerectoryFun(File f1, File f3){
		logger.info("###### 开始复制目录，源目录名："+f1.getName()+" ,目标目录名："+f3.getName());
		File[] files = f1.listFiles();//获得源目录下所有文件
		int total = files.length;//文件总数
		for(int currentfile=0;currentfile<total;currentfile++ ){
			if(!files[currentfile].isDirectory()){//如果不是文件夹,按文件复制
				String newTargetPath = f3.getPath() + File.separator + files[currentfile].getName();
				copyFile(files[currentfile], new File(newTargetPath));
			}else{//目录需要做递归处理
				copyDerectory(files[currentfile], f3);
			}
		}
		logger.info("###### 复制目录结束，原目录名："+f1.getName());
	}
	
	/**
	 * 复制文件(文件到文件)
	 * @param f1
	 * @param f2
	 * @throws Exception
	 */
	public static void copyFile(File f1, File f2){
		int length = 2097152;
		try{
			logger.info("###### 开始复制文件，原文件名："+f1.getName());
			FileInputStream in = new FileInputStream(f1);
			FileOutputStream out = new FileOutputStream(f2);
			byte[] streamByts = new byte[length];
			int count = 0;
			while ((count = in.read(streamByts)) != -1) {
				out.write(streamByts, 0, count);
				out.flush();
			}
			in.close();
			out.close();
		}catch(Exception e){
			logger.error("&&&&&&&& "+e.getMessage());
		}finally{
			logger.info("###### "+f1.getName()+" 文件复制结束");
		}

	}
	
	/**
	 *  删除文件目录
	 * @param folderPath
	 */
	public static void delFolder(String folderPath) {
		try {
			delFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			File myFilePath = new File(filePath);
			if(myFilePath.exists())
				myFilePath.delete(); // 删除空文件夹
		} catch (Exception e) {
			logger.error("&&&&&&&&&& " + e.getMessage());
		}
	}
	
	/**
	 *  删除文件夹下的所有文件
	 * @param path
	 */
	public static void delFile(String path) {
		File file = new File(path);
		if(file.exists()){
			if (file.isFile()) {
				try {
					FileUtils.forceDelete(file);
				} catch (IOException e) {
					logger.error("&&&&&&&& "+e.getMessage());
				}
			} else {
				String[] tempList = file.list();
				File temp = null;
				if (tempList != null) {
					for (int i = 0; i < tempList.length; i++) {
						if (path.endsWith(File.separator)) {
							temp = new File(path + tempList[i]);
						} else {
							temp = new File(path + File.separator + tempList[i]);
						}
						if (temp.isFile()) {
							temp.delete();
						}
						if (temp.isDirectory()) {
							// 先删除文件夹里面的文件
							delFile(path + File.separator + tempList[i]);
							// 再删除空文件夹
							delFolder(path + File.separator + tempList[i]);
						}
					}
				}
			}
		}
	}
	
	 
	/**
	 * 加锁文件
	 * @param fileName
	 * @throws HDException 
	 * @author wangyi
	 * @date 2017年8月23日
	 */
	@SuppressWarnings("resource")
	public static void lockFile(String fileName) throws HDException{
		RandomAccessFile rf = null;
		try {
			//获取文件对象
			try {
				rf = new RandomAccessFile(fileName, "rw");
			} catch (FileNotFoundException e) {
				logger.error("文件未找到！"+fileName);
				throw new HDException("文件未找到！"+fileName);
			} 
			//获取锁
	        FileChannel fcin=rf.getChannel();
	        FileLock flin=null;
	        while(true){
	            try {
	                flin = fcin.tryLock();
	                //判断是否为空，如果为空，表示没有获取到锁对象,等待1s
	                if(flin!=null)
	                	break;
	                else
	                	sleepWaiting(1000l);
	            } catch (IOException e) {
	            	logger.error("文件加锁失败！"+fileName);
					throw new HDException("文件加锁失败！"+fileName);
	            }  
	        }
		}catch (HDException e) {
			throw e;
		} 
	}
	/**
	 * 解锁文件 
	 * @param rf
	 * @throws HDException 
	 * @author wangyi
	 * @date 2017年8月23日
	 */
	public static void unLockFile(RandomAccessFile rf){
		IOUtils.closeQuietly(rf);
	}
	
	/**
	 * 获取最后一行数据，表示升级时升级到了哪个脚本，支持增量升级
	 * 参数传递RandomAccessFile，考虑获取最后行数据时，前提需要对该文件进行加锁
	 * @param rf
	 * @return
	 * @throws HDException 
	 * @author wangyi
	 * @date 2017年8月23日
	 */
	public static String getFileLastRow(RandomAccessFile rf) throws HDException{
		//记录最后一行数据
    	String lastRow = null;
        try {           
            //总数据
            long length = rf.length();
            //判断是否没有数据，为0时表示空文件
            if(length>0){
            	//起始位置
            	long startIndex = rf.getFilePointer();
            	//最后一个位置，倒序
            	long endIndex = startIndex + length - 1;
            	//调到最后一个位置
            	rf.seek(endIndex);
            	//当大于起始位置时，一直往回找。等于是只有1行数据，一直减到0，然后和start0值比较。
                while (endIndex >= startIndex) {
                	//读取的字节数据
                	int readByte = rf.read();
                	//当是换行符时
                    if (readByte == '\n') {
                    	//读取的行数据
                    	lastRow = rf.readLine();                
                        break;
                    }                     
                    if (endIndex == 0) {
                    	// 当文件指针退至文件开始处，输出第一行
                    	//这里需要跳转到0，然后再调用readline
                    	rf.seek(endIndex);
                    	lastRow = rf.readLine();
                        break;
                    }else{
                    	//减值
                    	endIndex--;
                    	rf.seek(endIndex);
                    }
                }
            }         
            if(StringUtils.isNotEmpty(lastRow)){
            	lastRow = new String(lastRow.getBytes("ISO-8859-1"), "UTF-8");
            }
            return lastRow;      
        } catch (FileNotFoundException e) {  
        	logger.error("获取最后行数据出错！",e);
        	throw new HDException(e);
        } catch (IOException e) {  
        	logger.error("获取最后行数据出错！",e);
        	throw new HDException(e);
        } 
	}
		
	/**
	 * 将参数内容，追加到文件的末尾
	 * @param fileName
	 * @param contents 
	 * @author wangyi
	 * @date 2017年8月22日
	 */
	public static void appendFileContents(RandomAccessFile rf, List<String> contents) throws HDException{
		//写文件数据
		try {
			//文件长度，字节数
            long fileLength = rf.length();
            //将写文件指针移到文件尾
            rf.seek(fileLength);
            for(String content:contents){
            	//添加换行符
            	content = content + "\n";
            	rf.write(content.getBytes("UTF-8"));
            }
        } catch (IOException e) {
        	logger.error("保存数据出错！",e);
        	throw new HDException(e);
        } 
		
	}
	
	/**
	 * 等待秒数
	 * @param millis 
	 * @author wangyi
	 * @date 2017年8月23日
	 */
	private static void sleepWaiting(long millis){
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			logger.error("等待"+millis+"秒时发生异常!",e);
		}
	}
}
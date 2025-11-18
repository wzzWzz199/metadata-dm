package com.hayden.hap.dbop.utils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


/**
 * 
 * 
 * @author haocs
 * @date 2019年10月24日
 */
public class ZipUtils {
	
	private static final Logger logger = LoggerFactory
			.getLogger(ZipUtils.class);

	private static final int BUFFER_SIZE = 2 * 1024;

	/**
	 * 压缩成ZIP 方法1
	 * 
	 * @param srcDir
	 *            压缩文件夹路径
	 * @param out
	 *            压缩文件输出流
	 * @param KeepDirStructure
	 *            是否保留原来的目录结构,true:保留目录结构;
	 *            false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
	 * @throws RuntimeException
	 *             压缩失败会抛出运行时异常
	 */
	public static void toZip(String srcDir, OutputStream out, boolean KeepDirStructure)

			throws RuntimeException {

		long start = System.currentTimeMillis();

		ZipOutputStream zos = null;

		try {

			zos = new ZipOutputStream(out);

			File sourceFile = new File(srcDir);

			compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);

			long end = System.currentTimeMillis();
			logger.info("压缩完成，耗时：" + (end - start) + " ms");

		} catch (Exception e) {

			throw new RuntimeException("zip error from ZipUtils", e);

		} finally {

			if (zos != null) {

				try {

					zos.close();

				} catch (IOException e) {

					e.printStackTrace();

				}

			}

		}
	}

	/**
	 * 压缩成ZIP 方法2
	 * 
	 * @param srcFiles
	 *            需要压缩的文件列表
	 * @param out
	 *            压缩文件输出流
	 * @throws RuntimeException
	 *             压缩失败会抛出运行时异常
	 */
	public static void toZip(List<File> srcFiles, OutputStream out) throws RuntimeException {
		
		if(out==null) {
			return;
		}
		long start = System.currentTimeMillis();

		ZipOutputStream zos = null;

		try {

			zos = new ZipOutputStream(out);

			for (File srcFile : srcFiles) {

				byte[] buf = new byte[BUFFER_SIZE];

				zos.putNextEntry(new ZipEntry(srcFile.getName()));

				int len;

				FileInputStream in = new FileInputStream(srcFile);

				while ((len = in.read(buf)) != -1) {

					zos.write(buf, 0, len);

				}

				zos.closeEntry();

				in.close();

			}
			System.out.println("压缩完成，耗时：" + (System.currentTimeMillis() - start) + " ms");

		} catch (Exception e) {

			throw new RuntimeException("zip error from ZipUtils", e);

		} finally {

			if (zos != null) {

				try {

					zos.close();

				} catch (IOException e) {

					e.printStackTrace();

				}

			}

		}

	}


	/**
	 * 压缩成ZIP 方法3, 根据得到的多个InputStream 压缩至zip
	 * @param fileIns       被压缩文件 名称及输入流映射
	 * @param out       输出流
	 * @return: void
	 * @Author: suntaiming
	 * @Date: 2021/3/8 10:58
	 */
	public static void toZip(Map<String, InputStream> fileIns, OutputStream out) throws RuntimeException {
		if(out==null) {
			return;
		}
		long start = System.currentTimeMillis();

		ZipOutputStream zos = null;

		try {

			zos = new ZipOutputStream(out);

			for (String srcFileName : fileIns.keySet()) {
				byte[] buf = new byte[BUFFER_SIZE];

				zos.putNextEntry(new ZipEntry(srcFileName));

				int len;

				InputStream in = fileIns.get(srcFileName);

				while ((len = in.read(buf)) != -1) {

					zos.write(buf, 0, len);

				}

				zos.closeEntry();

				in.close();

			}
			System.out.println("压缩完成，耗时：" + (System.currentTimeMillis() - start) + " ms");

		} catch (Exception e) {

			throw new RuntimeException("zip error from ZipUtils", e);

		} finally {

			if (zos != null) {

				try {

					zos.close();

				} catch (IOException e) {

					e.printStackTrace();

				}

			}

		}
	}

	/**
	 * 递归压缩方法
	 * 
	 * @param sourceFile
	 *            源文件
	 * @param zos
	 *            zip输出流
	 * @param name
	 *            压缩后的名称
	 * @param KeepDirStructure
	 *            是否保留原来的目录结构,true:保留目录结构;
	 *            false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
	 * @throws Exception
	 */
	private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean KeepDirStructure)
			throws Exception {
		byte[] buf = new byte[BUFFER_SIZE];
		if (sourceFile.isFile()) {
			// 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
			zos.putNextEntry(new ZipEntry(name));
			// copy文件到zip输出流中
			int len;
			FileInputStream in = new FileInputStream(sourceFile);
			while ((len = in.read(buf)) != -1) {
				zos.write(buf, 0, len);
			}
			// Complete the entry
			zos.closeEntry();
			in.close();
		} else {
			File[] listFiles = sourceFile.listFiles();
			if (listFiles == null || listFiles.length == 0) {
				// 需要保留原来的文件结构时,需要对空文件夹进行处理
				if (KeepDirStructure) {
					// 空文件夹的处理
					zos.putNextEntry(new ZipEntry(name + "/"));
					// 没有文件，不需要文件的copy
					zos.closeEntry();
				}
			} else {
				for (File file : listFiles) {
					// 判断是否需要保留原来的文件结构
					if (KeepDirStructure) {
						// 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
						// 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
						compress(file, zos, name + "/" + file.getName(), KeepDirStructure);
					} else {
						compress(file, zos, file.getName(), KeepDirStructure);
					}

				}

			}

		}

	}
    public static void unzipFile2TempPath(File file,String unZipPath) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        Enumeration entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = (ZipEntry) entries.nextElement();
            InputStream inputStreamTemp = zipFile.getInputStream(zipEntry);

            String filename = zipEntry.getName();
            String target = unZipPath + File.separator + filename;
            File tmp = new File(target);

            FileUtils.deleteDirectory(tmp);

            if(!tmp.exists()){
                if(filename.lastIndexOf("/")==filename.length()-1){
                    tmp.mkdirs();
                }else{
                    FileUtils.copyInputStreamToFile(inputStreamTemp, tmp);
                }
            }
        }
    }

    public static void unzipStream2TempPath(InputStream inputStream,String tmpFile,String unZipPath) throws IOException {
        File file = new File(tmpFile);
        FileUtils.copyInputStreamToFile(inputStream, file);
        unzipFile2TempPath(file,unZipPath);
        file.delete();
    }

	public static void main(String[] args) throws Exception {
		/** 测试压缩方法1 */
//		FileOutputStream fos1 = new FileOutputStream(new File("c:/mytest01.zip"));
//		ZipUtils.toZip("D:/log", fos1, true);

		/** 测试压缩方法2 */
		List<File> fileList = new ArrayList<>();
		fileList.add(new File("D:\\mnt\\patch\\3.0\\20190125-25-hap_trn-001-V3.0.5-haochengshuai\\xmlscript\\mysql\\trn.xml"));
		fileList.add(new File("D:\\mnt\\patch\\3.0\\20190125-25-hap_trn-001-V3.0.5-haochengshuai\\补丁说明.txt"));
		fileList.add(new File("D:\\mnt\\doclinks\\hap_files\\TRN_TRAIN_RECORD\\signin_attach\\1\\2019\\10\\kgjBs0BUgLaBW_9xwSNNGazLPKdh.png"));
		FileOutputStream fos2 = new FileOutputStream(new File("D:/mytest02.zip"));
		ZipUtils.toZip(fileList, fos2);
	}
}

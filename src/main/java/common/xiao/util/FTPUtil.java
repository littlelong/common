package common.xiao.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;

import com.alibaba.fastjson.JSONObject;

/**
 * ftp Util类
 * 
 * @author
 */
public class FTPUtil {

	private static Log LOG = LogFactory.getLog(FTPUtil.class);

	private FTPClient ftpClient;
	
	private String ip = ""; //ftp服务器ip
    private String username = ""; //ftp服务器账号
	private String password = "";//ftp服务器密码
    private int port = -1;

	private final int DEFAULT_TIMEOUT = 10 * 1000;
	private final int DATA_TIMEOUT = 10 * 60 * 1000;
	private final int SO_TIMEOUT = 10 * 60 * 1000;

	private String defaultPath = "";
	// ftp下载文件保存地址
	
	public FTPUtil(String ip, int port, String username, String password) {
        this.ip = ip;
        this.username = username;
        this.password = password;
        this.port = port;
        ftpClient = new FTPClient();
    }

	public FTPClient getFtpClient() {
		return ftpClient;
	}

	public void setFtpClient(FTPClient ftpClient) {
		this.ftpClient = ftpClient;
	}

	public FTPUtil() {
	}

	/**
     * 连接ftp服务器
     *TaskUtils
     * @throws IOException
     */
    public boolean connect() {
        try {
            ftpClient.connect(this.ip, this.port);

            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
            	ftpClient.disconnect();
            	throw new Exception("Not connect to :" + ip);
            }

            boolean loginTag = ftpClient.login(this.username, this.password);

            if(loginTag == false){
            	throw new Exception("Invalid userName/userPass!");
            }
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);// 用2进制上传、下载
            ftpClient.enterLocalPassiveMode();
            if(defaultPath.equals(""))
            	this.defaultPath = ftpClient.printWorkingDirectory(); //得到开始目录的绝对路径
            return true;

        } catch (Exception e) {
            return false;
        }
    }
    
    public byte[] getByte(String directory, String fileName) {
    	try {
    		
    		if (directory != null && !"".equals(directory))
            {
                ftpClient.changeWorkingDirectory(directory);
            }
    		InputStream retrieveFileStream = ftpClient.retrieveFileStream(fileName);
			byte[] fileData = IOUtils.toByteArray(retrieveFileStream);
			retrieveFileStream.close();
			ftpClient.completePendingCommand();
            return fileData;
		} catch (Exception e) {
		}
		return null;
	}
    
	/**
	 * 登录ftp服务器
	 * 
	 * @param host
	 *            服务器地址
	 * @param port
	 *            服务器端口
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 */
	public boolean ftpLogin(String host, int port, String username, String password) {
		ftpClient = new FTPClient();
		try {
			ftpClient.setDefaultTimeout(DEFAULT_TIMEOUT);
			ftpClient.setDataTimeout(DATA_TIMEOUT);
			ftpClient.connect(host, port);
			ftpClient.setSoTimeout(SO_TIMEOUT);
			ftpClient.enterLocalPassiveMode();

			int reply = ftpClient.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftpClient.disconnect();
				return false;
			}
			if (username != null) {
				if (!ftpClient.login(username, password)) {
					ftpClient.disconnect();
					return false;
				}
			}
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 关闭ftp连接
	 */
	public void close() {

		try {
			if (ftpClient != null) {
				ftpClient.logout();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ftpClient != null && ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	/**
	 * 上传文件
	 * 
	 * @param localFilePath
	 *            本地文件路径及名称
	 * @param remoteFileName
	 *            FTP 服务器文件名称
	 * @return
	 */
	public boolean uploadFile(String localFilePath, String remoteFileName) {
		BufferedInputStream inStream = null;
		boolean success = false;
		try {
			inStream = new BufferedInputStream(new FileInputStream(localFilePath));
			success = this.ftpClient.storeFile(remoteFileName, inStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return success;
	}

	/**
	 * 上传中文文件
	 * 
	 * @param localFilePath
	 * @param remoteFileName
	 * @return
	 * @throws Exception
	 * @throws IOException
	 */
	public boolean uploadFileByCN(String localFilePath, String remoteFileName) throws Exception {

		boolean flag = false;
		OutputStream out = null;
		RandomAccessFile raf = null;
		try {
			String uploadFileName = new String(remoteFileName.getBytes("utf-8"), "ISO-8859-1");
			// 每次删除上次的文件
			boolean falg = ftpClient.deleteFile(uploadFileName);
			LOG.info("清除文件[" + falg + "]：" + remoteFileName);

			raf = new RandomAccessFile(new File(localFilePath), "r");
			out = ftpClient.appendFileStream(uploadFileName);
			if (out == null) {
				throw new Exception("错误的远程上传路径：" + remoteFileName);
			} else {
				byte[] bytes = new byte[1024];
				int c;
				while ((c = raf.read(bytes)) != -1) {
					out.write(bytes, 0, c);
				}
				out.flush();
				raf.close();
			}
			out.close();
			flag = ftpClient.completePendingCommand();

		} catch (Exception e) {
			e.printStackTrace();
			LOG.info(e.getMessage());
			throw new Exception(e.getMessage());
		} finally {
			try {
				if (out != null)
					out.close();
				if (raf != null)
					raf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}

	public InputStream getFile(String fileName) throws IOException {
		return ftpClient.retrieveFileStream(fileName);
	}

	public void complete() {
		try {
			ftpClient.completePendingCommand();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param localFilePath
	 *            本地文件名及路径
	 * @param remoteFileName
	 *            远程文件名称
	 * @return
	 */
	public boolean downloadFile(String remoteFileName, String localFilePath) {
		BufferedOutputStream outStream = null;
		boolean success = false;
		try {
			FTPFile[] ftpFiles = ftpClient.listFiles(remoteFileName);
			if (ftpFiles != null && ftpFiles.length > 0) {
				outStream = new BufferedOutputStream(new FileOutputStream(localFilePath));
				success = this.ftpClient.retrieveFile(remoteFileName, outStream);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return success;
	}

	/**
	 * 功能说明：通过递归实现ftp目录文件与本地文件同步更新
	 * 
	 * @param ftpfilepath
	 *            当前ftp目录
	 * @param localpath
	 *            当前本地目录
	 */
	public void ftpDownFiles(String ftpfilepath, String localpath) {

		try {

			FTPFile[] ff = ftpClient.listFiles(ftpfilepath);
			// 得到当前ftp目录下的文件列表

			if (ff != null) {
				for (int i = 0; i < ff.length; i++) {
					// System.out.println(ff[i].getName());
					String localfilepath = localpath + ff[i].getName();
					File localFile = new File(localfilepath);
					// 根据ftp文件生成相应本地文件
					Date fflastModifiedDate = ff[i].getTimestamp().getTime();
					// 获取ftp文件最后修改时间
					Date localLastModifiedDate = new Date(localFile.lastModified());
					// 获取本地文件的最后修改时间
					int result = localLastModifiedDate.compareTo(fflastModifiedDate);
					// result=0，两文件最后修改时间相同；result<0，本地文件的最后修改时间早于ftp文件最后修改时间；result>0，则相反
					if (ff[i].isDirectory()) {
						// 如果是目录
						localFile.mkdir();
						// 如果本地文件夹不存在就创建
						String ftpfp = ftpfilepath + ff[i].getName() + "/";
						// 转到ftp文件夹目录下
						String localfp = localfilepath + "/";
						// 转到本地文件夹目录下
						this.ftpDownFiles(ftpfp, localfp);
						// 递归调用

					}
					if (ff[i].isFile()) {
						// 如果是文件
						File lFile = new File(localpath);
						lFile.mkdir();
						// 如果文件所在的文件夹不存在就创建
						if (!lFile.exists()) {
							return;
						}
						if (ff[i].getSize() != localFile.length() || result < 0) {
							// 如果ftp文件和本地文件大小不一样或者本地文件不存在或者ftp文件有更新，就进行创建、覆盖
							String filepath = ftpfilepath + ff[i].getName();
							// 目标ftp文件下载路径
							FileOutputStream fos = new FileOutputStream(localFile);
							try {
								ftpClient.retrieveFile(new String(filepath.getBytes("UTF-8"), "ISO-8859-1"), fos);
								// 从FTP服务器上取回一个文件
							} catch (Exception e) {
								e.printStackTrace();
							}
							fos.flush();
							// 将缓冲区中的数据全部写出
							fos.close();
							// 关闭流
						} else {
							// System.out.println("两个文件相同！");
						}
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	/**
	 * 列出指定目录下的文件和目录
	 * 
	 * @param pathname
	 *            目录名称
	 * @return
	 */
	public List<String> listRemoteFiles(String pathname) {
		final List<String> files = new ArrayList<String>();
		try {
			String[] fileArray = ftpClient.listNames(pathname);
			if (fileArray != null && fileArray.length != 0) {
				for (int i = 0; i < fileArray.length; i++) {
					files.add(fileArray[i]);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return files;
	}

	/**
	 * 删除一个FTP服务器上的目录（如果为空）
	 * 
	 * @param pathname
	 *            目录路径
	 * @return
	 */
	public boolean removeDirectory(String pathname) {
		boolean b = false;
		try {
			b = ftpClient.removeDirectory(pathname);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	/**
	 * 删除一个FTP服务器上的目录
	 * 
	 * @param path
	 *            目录路径
	 * @param isAll
	 *            是否删除所有子目录和文件,如果有
	 * @return
	 * @throws IOException
	 */
	public boolean removeDirectory(String path, boolean isAll) {
		try {
			if (!isAll) {
				return removeDirectory(path);
			}
			// 遍历子目录和文件
			FTPFile[] ftpFileArr = ftpClient.listFiles(path);
			if (ftpFileArr == null || ftpFileArr.length == 0) {
				return removeDirectory(path);
			}

			for (int i = 0; i < ftpFileArr.length; i++) {
				FTPFile ftpFile = ftpFileArr[i];
				String name = ftpFile.getName();
				if (ftpFile.isDirectory()) {
					removeDirectory(path + "/" + name, true);
				} else if (ftpFile.isFile()) {
					deleteFile(path + "/" + name);
				} else if (ftpFile.isSymbolicLink()) {

				} else if (ftpFile.isUnknown()) {

				}
			}
			return removeDirectory(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean deleteAllFile(String path) {
		try {
			// 遍历子目录和文件
			FTPFile[] ftpFileArr = ftpClient.listFiles(path);
			if (ftpFileArr != null) {
				for (int i = 0; i < ftpFileArr.length; i++) {
					FTPFile ftpFile = ftpFileArr[i];
					String name = ftpFile.getName();
					if (ftpFile.isFile()) {
						deleteFile(path + "/" + name);
					}
				}
				return true;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 删除文件
	 * 
	 * @param pathName
	 *            文件名
	 * @return 删除结果,是否成功.
	 * @throws IOException
	 */
	public boolean deleteFile(String pathName) {
		boolean b = false;
		try {
			b = ftpClient.deleteFile(pathName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	/**
	 * 返回到上一层目录
	 * 
	 * @return
	 */
	public boolean changeToParentDirectory() {
		boolean b = false;
		try {
			b = ftpClient.changeToParentDirectory();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	/**
	 * 设置传输文件的类型[文本文件或者二进制文件]
	 * 
	 * @param fileType
	 * @return fileType--BINARY_FILE_TYPE、ASCII_FILE_TYPE
	 */
	public boolean setFileType(int fileType) {
		boolean b = false;
		try {
			b = ftpClient.setFileType(fileType);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	/**
	 * 下载文件
	 * 
	 * @param localFilePath
	 *            本地文件名及路径
	 * @param remoteFileName
	 *            远程文件名称
	 * @return
	 */
	public boolean downloadFileSingle(String remoteFileName, String localFilePath) {
		BufferedOutputStream outStream = null;
		boolean success = false;
		try {
			outStream = new BufferedOutputStream(new FileOutputStream(localFilePath));
			success = this.ftpClient.retrieveFile(remoteFileName, outStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (!success) {
			File file = new File(localFilePath);
			if (file.exists()) {
				file.delete();
			}
		}

		return success;
	}
	
	
	/**
	 * 下载文件,采用多线程写文件的方式
	 * 
	 * @param localFilePath
	 *            本地文件名及路径
	 * @param remoteFileName
	 *            远程文件名称
	 * @return
	 */
	public boolean downloadFileMultithread(ExecutorService pool,String remoteFileName, String localFilePath) {
		OutputStream outStream = null;
		InputStream is = null;
		boolean success = false;
		try {
//			is = this.ftpClient.retrieveFileStream(remoteFileName);
			outStream = new FileOutputStream(localFilePath);
//			byte[] bts = new byte[1024];
//			int len = 0;
//			while(is != null && (len = is.read(bts)) != -1){
//				outStream.write(bts , 0 ,len);
//			}
//			outStream.flush();
			ftpClient.retrieveFile(remoteFileName, outStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outStream != null) {
				try {
					outStream.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		/*if (!success) {
			File file = new File(localFilePath);
			if (file.exists()) {
				file.delete();
			}
		}
*/
		return success;
	}

	/**
	 * 重命名文件
	 * 
	 * @param oldFileName
	 *            --原文件名
	 * @param newFileName
	 *            --新文件名
	 * @return 如果改变成功返true否则返回false
	 */
	public boolean renameFile(String oldFileName, String newFileName) {
		boolean b = false;
		try {
			b = ftpClient.rename(oldFileName, newFileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}

	/**
     * 取得当前目录下所有文件列表
     *
     * @param path
     * @return
     */
	public FTPFile[] fileNameList(final String regex) {
		FTPFile[] files;
		try {
			String workPath = ftpClient.printWorkingDirectory();
			ftpClient.getReplyCode();
			if(regex == null){
				files = ftpClient.listFiles();
			}else{
				files = ftpClient.listFiles(workPath, new FTPFileFilter() {
					public boolean accept(FTPFile ftpFile) {
						if (ftpFile.isDirectory())
							return false;
						return ftpFile.getName().matches(regex);
					}
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		if (null == files || files.length == 0) {
			return null;
		}

//		List<FTPFile> listFiles = new ArrayList<FTPFile>();
//		for (int i = 0; i < files.length; i++) {
//			if (files[i].isFile()) {
//				listFiles.add(files[i]);
//			}
//		}
		return files;
	}
	
	/**
	 * 读取json文件，返回json串
	 * 
	 * @param fileName
	 * @return
	 */
	public static String readJsonFile(InputStream file) {
		String jsonStr = "";
		try {
			Reader reader = new InputStreamReader(file, "gbk");
			int ch = 0;
			StringBuffer sb = new StringBuffer();
			while ((ch = reader.read()) != -1) {
				sb.append((char) ch);
			}
			reader.close();
			// complete();
			jsonStr = sb.toString();
			return jsonStr;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

    /**
     * 改变ftp工作目录
     */
    public boolean toReadPath(String sourcePath){
    	try {
//    		ftpClient.changeWorkingDirectory("/");
    		if("/".equals(sourcePath) || "".equals(sourcePath)){
    			if(!ftpClient.changeWorkingDirectory(defaultPath)){
    				LOG.warn("Could not get into defaultPath " + defaultPath);
    				return false;
    			}
    		}else{
    			return ftpClient.changeWorkingDirectory(defaultPath + sourcePath);
    		}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return false;
    }

	public static void main(String[] args) throws IOException {
		// FTPUtil ftpUtil = new FTPUtil("10.148.16.51", 21, "user5", "G00gle.com");
		// ftpUtil.connect();
		// InputStream retrieveFileStream2 =
		// ftpUtil.getFile("440000-20191230-Disaster-hh-1.json");

		String readJsonFile2 = readJsonFile(
				new FileInputStream(new File("C:\\Users\\86188\\Desktop\\440000-20200219-Disaster-hh-1.json")));
		JSONObject parseObject = JSONObject.parseObject(readJsonFile2);
		String string = parseObject.get("pgjson").toString();
		String object = JSONObject.parseObject(string).get("features").toString();
		String geometry = JSONObject.parseArray(object).get(0).toString();
		String coordinates = JSONObject.parseObject(geometry).get("geometry").toString();
		String convert2Wkt = CoordinateUtil.convert2Wkt("MULTIPOLYGON", JSONObject.parseObject(coordinates).get("coordinates").toString());
		System.out.println(convert2Wkt);
		// System.out.println(object);
	}
	
}

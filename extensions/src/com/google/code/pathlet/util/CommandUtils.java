package com.google.code.pathlet.util;

import java.io.IOException;

import com.google.code.pathlet.util.impl.CommandUtilsHelper;




/**
 * 本地命令行执行工具,用于执行如DOS command和Unix(Linux) shell命令
 * 
 * @author Charlie Zhange
 *
 */
public class CommandUtils {
		
	private static CommandUtilsHelper helper = new CommandUtilsHelper(); 
	
	public static OSType getOSType() throws IOException {
		return helper.getOSType();
	}
	
	/**
	 * 直接执行一个命令行
	 * 如果发生错误自动记录到日志中
	 * 
	 * @param commandLine
	 * @return the exit value of the process. By convention, 
     *             <code>0</code> indicates normal termination. <br/>
     *             More information please reference: Process.waitFor() return value. <br/>
	 */
	public static int execute(String commandLine, String outputCharset) throws IOException, InterruptedException {
		return helper.execute(commandLine, outputCharset);
	}
	
	/**
	 * 
	 * 方法功能：得到上次执行命令后的 system out输出. <BR>
	 * @return
	 * @see com.greatwe.dim.util.CommandUtils
	 * @since
	 */
	public static String getLastOut() {
		return helper.getLastOut();
	}

	/**
	 * 
	 * 方法功能：得到上次执行命令后的 system err输出. <BR>
	 * @return
	 * @see com.greatwe.dim.util.CommandUtils
	 * @since
	 */
	public static String getLastErr() {
		return helper.getLastErr();
	}
	
}



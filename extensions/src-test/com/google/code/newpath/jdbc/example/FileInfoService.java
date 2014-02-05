package com.google.code.newpath.jdbc.example;

import java.io.IOException;

import com.google.code.newpath.jdbc.example.vo.FileInfoVO;

public interface FileInfoService {
	

	FileInfoVO createFileInfo(String fileName, String mediaType, long fileSize) throws IOException;

	FileInfoVO getFileInfo(long fileId);
	
	void updateFileInfo(FileInfoVO fileInfo);
	
	void deleteFileInfo(long fileId);
	

}

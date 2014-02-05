package com.google.code.newpath.jdbc.example.impl;

import java.io.IOException;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.transaction.annotation.Transactional;

import com.google.code.newpath.jdbc.example.FileInfoService;
import com.google.code.newpath.jdbc.example.vo.FileInfoVO;
import com.google.code.pathlet.jdbc.EntityInsertDef;
import com.google.code.pathlet.jdbc.EntityInsertDef.Key;
import com.google.code.pathlet.jdbc.EntityRowMapper;
import com.google.code.pathlet.jdbc.EntityUpdateDef;
import com.google.code.pathlet.jdbc.ExtJdbcTemplate;

/**
 * 
 * File Service
 * 
 * @author Zhang Chen Long
 * 
 */
@Transactional
public class FileInfoServiceImpl implements FileInfoService {

	private ExtJdbcTemplate jdbcTemplate;

	public void setDataSource(DataSource dataSource) throws IOException {
		this.jdbcTemplate = new ExtJdbcTemplate(dataSource);

		// For oracle sequence key generation
		// Key[] primaryKey = {new Key("fileId", "SEQ_FILE_INFO.NEXTVAL")};

		// For MySQL Auto Increment key generation
		Key[] primaryKey = { new Key("fileId", null) };

		jdbcTemplate.registerInsertEntity(new EntityInsertDef("T_FILE_INFO",
				FileInfoVO.class, "T_FILE_INFO", primaryKey));

		jdbcTemplate.registerUpdateEntity(new EntityUpdateDef("T_FILE_INFO",
				FileInfoVO.class, "T_FILE_INFO", new String[] {"fileId"}));
	}



	public FileInfoVO createFileInfo(String fileName, String contentType,
			long fileSize)  {
		FileInfoVO info = new FileInfoVO();
		info.setFileName(fileName);
		info.setContentType(contentType);
		info.setFileSize(fileSize);
		info.setCreationTime(new Date());

		jdbcTemplate.insertEntity("T_FILE_INFO", info);

		return info;
	}

	public void updateFileInfo(FileInfoVO fileInfo)  {
		jdbcTemplate.updateEntity("T_FILE_INFO", fileInfo);
	}


	@Transactional(readOnly = true)
	public FileInfoVO getFileInfo(long fileId) {
		return (FileInfoVO) jdbcTemplate.queryForObject("select * from T_FILE_INFO where FILE_ID=?",
				new Object[] { fileId }, new EntityRowMapper(FileInfoVO.class,
						null));
	}

	public void deleteFileInfo(long fileId) {
		this.jdbcTemplate.update("delete from T_FILE_INFO where FILE_ID=?", fileId);
	}

}
package com.google.code.pathlet.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.Assert;

public class ScrollableRowMapperResultSetExtractor<T> implements ResultSetExtractor<List<T>> {

	private final RowMapper<T> rowMapper;

	private final int startIndex;
	
	private final int fetchSize;

	/**
	 * 
	 * Create a new RowMapperResultSetExtractor.
	 * 
	 * @param startIndex zero base index number to indicate the start row number
	 * @param fetchSize max data row size to be fetched
	 * @param rowMapper the RowMapper which creates an object for each row
	 */
	public ScrollableRowMapperResultSetExtractor(int startIndex, int fetchSize, RowMapper<T> rowMapper) {
		Assert.notNull(rowMapper, "RowMapper is required");
		this.startIndex = startIndex;
		this.fetchSize = fetchSize;
		this.rowMapper = rowMapper;
	}
	

	public List<T> extractData(ResultSet rs) throws SQLException {
		List<T> results = new ArrayList<T>(fetchSize);
		
        boolean hasNext = rs.absolute(startIndex + 1);
        rs.setFetchSize(fetchSize);
		
		int rowNum = 0;
		while (hasNext && rowNum < fetchSize) {
			results.add(this.rowMapper.mapRow(rs, rowNum));
			rowNum++;
			hasNext = rs.next();
		}
		
		return results;
	}
	
}

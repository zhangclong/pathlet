package com.wanda.ccs.sqlasm.impl;

import java.util.Collection;

import com.wanda.ccs.sqlasm.ClauseParagraph;
import com.wanda.ccs.sqlasm.ClauseResult;



public class DefaultClauseParagraph implements ClauseParagraph {
	
	private String id;
	
	private String prefix;
	
	private String suffix;
	
	private String separator; 
	
	public DefaultClauseParagraph(String id, String prefix, String suffix,
			String separator) {
		this.id = id;
		this.prefix = prefix;
		this.suffix = suffix;
		this.separator = separator;
	}
	
	public String getId() {
		return id;
	}

	public void compose(StringBuilder buf, Collection<ClauseResult> clauseResults) {
		buf.append(prefix);
		
		int index = 0;
		for(ClauseResult cr : clauseResults) {
			if(index != 0) { buf.append(separator); }
			buf.append(cr.getComposedClause());
			index ++;
		}
		
		buf.append(suffix);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultClauseParagraph other = (DefaultClauseParagraph) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}

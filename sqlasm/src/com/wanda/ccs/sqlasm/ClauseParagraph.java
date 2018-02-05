package com.wanda.ccs.sqlasm;

import java.util.Collection;

public interface ClauseParagraph {

	String getId();

	void compose(StringBuilder buf, Collection<ClauseResult> clauseResults);

}
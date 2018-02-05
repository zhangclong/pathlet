package com.wanda.ccs.sqlasm;

import com.wanda.ccs.sqlasm.impl.ContainsValueCond;
import com.wanda.ccs.sqlasm.impl.DefaultClauseParagraph;
import com.wanda.ccs.sqlasm.impl.DefaultCriteriaParser;
import com.wanda.ccs.sqlasm.impl.EqualsValueCond;
import com.wanda.ccs.sqlasm.impl.NotEmptyCond;


public class CriteriaParserBuilder {
	
	public static ClauseParagraph[] QUERY_PARAGRAPHS = {
		new DefaultClauseParagraph("select",  " select ",   " \n",  ","),
		new DefaultClauseParagraph("from",    " from \n",   " \n",  ",\n"),
		new DefaultClauseParagraph("where",   " where \n",  " \n",  " and \n"),
		new DefaultClauseParagraph("having",  " having ",   " \n",  " and \n"),
		new DefaultClauseParagraph("groupby", " group by ", " \n",  ","),
		new DefaultClauseParagraph("orderby", " order by ", " \n",  ""),
	};
	
	public static ClauseParagraph[] SELECT_PARAGRAPHS = {
		new DefaultClauseParagraph("select",  " select ",   " \n", ","),
		new DefaultClauseParagraph("from",    " from \n",   " \n", ",\n"),
		new DefaultClauseParagraph("where",   " where \n",  " \n", " and \n"),
		new DefaultClauseParagraph("orderby", " order by ", " \n", ""),
	};
	
	public static ClauseParagraph[] INSERT_PARAGRAPHS = {
		new DefaultClauseParagraph("insert",  "insert into ",   " ",  ""),
		new DefaultClauseParagraph("columns",  "(",   ")",  ","),
		new DefaultClauseParagraph("values",    " values(",     ") ",  ","),
	};
	
	public static ClauseParagraph[] UPDATE_PARAGRAPHS = {
		new DefaultClauseParagraph("update", "update ", " ", ""),
		new DefaultClauseParagraph("set",    "set ",    " ", ","),
		new DefaultClauseParagraph("where",  "where ",  " ", "and"),
	};
	
	
	public static ClauseParagraph[] QUERY_PARAGRAPHS_SEGMENT = {
		new DefaultClauseParagraph("select",  " select ",   " \n",  ","),
		new DefaultClauseParagraph("from",    " from \n",   " \n",  ",\n"),
		new DefaultClauseParagraph("where",   " where \n",  " \n",  " and \n"),
//		new DefaultClauseParagraph("notexistsTransAll",   "  and not exists ( select transSalesAllNot.Member_Key from CCS_NRPT2.T_F_CON_MEMBER_CINEMA transSalesAllNot where member.MEMBER_KEY=transSalesAllNot.MEMBER_KEY and  \n",  " ) \n",  " and \n"),
		new DefaultClauseParagraph("notexistsTransAllFrom",   "  and not exists ( select transSalesAllNot.Member_Key from  \n",  "  \n",  " , \n"),
		new DefaultClauseParagraph("notexistsTransAllWhere", " where  \n", " ) \n", " and \n"),
//		new DefaultClauseParagraph("notexistsTrans",   " and not exists ( select transSalesNot.Member_Key from CCS_NRPT2.T_F_CON_MEMBER_TICKET transSalesNot where member.MEMBER_KEY=transSalesNot.MEMBER_KEY and  \n",  " ) \n",  " and \n"),
		new DefaultClauseParagraph("notexistsTransFrom",   " and not exists ( select transSalesNot.Member_Key from  \n",  " \n",  " , \n"),
		new DefaultClauseParagraph("notexistsTransWhere", " where  \n", " ) \n", " and \n"),
//		new DefaultClauseParagraph("notexistsCon",   "  and not exists ( select consaleNot.Member_Key from CCS_NRPT2.T_F_CON_MEMBER_SALE consaleNot where member.MEMBER_KEY=consaleNot.MEMBER_KEY and \n",  " ) \n",  " and \n"),
		new DefaultClauseParagraph("notexistsConFrom",   " and not exists ( select consaleNot.Member_Key from  \n",  " \n",  " , \n"),
		new DefaultClauseParagraph("notexistsConWhere", " where  \n", " ) \n", " and \n"),
		new DefaultClauseParagraph("having",  " having ",   " \n",  " and \n"),
		new DefaultClauseParagraph("groupby", " group by ", " \n",  ","),
		new DefaultClauseParagraph("orderby", " order by ", " \n",  ""),
	};
	public static CriteriaParser newParser(ClauseParagraph[] paragraphs) {
		return new DefaultCriteriaParser(paragraphs);
	}
	
	public static EqualsValueCond equalsValue(String criterionId, Object value) {
		return new EqualsValueCond(criterionId, value);
	}
	
	public static NotEmptyCond notEmpty(String criterionId) {
		return new NotEmptyCond(criterionId);
	}
	
	public static ContainsValueCond containsValue(String criterionId, Object value) {
		return new ContainsValueCond(criterionId, value);
	}
}

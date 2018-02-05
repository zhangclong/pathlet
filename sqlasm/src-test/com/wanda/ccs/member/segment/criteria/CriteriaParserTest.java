package com.wanda.ccs.member.segment.criteria;


import static com.wanda.ccs.sqlasm.CriteriaParserBuilder.QUERY_PARAGRAPHS;
import static com.wanda.ccs.sqlasm.CriteriaParserBuilder.newParser;
import static com.wanda.ccs.sqlasm.CriteriaParserBuilder.notEmpty;
import static com.wanda.ccs.sqlasm.expression.ExpressionClauseBuilder.newExpression;
import static com.wanda.ccs.sqlasm.expression.ExpressionClauseBuilder.newPlain;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.wanda.ccs.sqlasm.Clause;
import com.wanda.ccs.sqlasm.CriteriaParser;
import com.wanda.ccs.sqlasm.CriteriaResult;
import com.wanda.ccs.sqlasm.DataType;
import com.wanda.ccs.sqlasm.expression.ExpressionCriterion;
import com.wanda.ccs.sqlasm.expression.JsonCriteriaHelper;
import com.wanda.ccs.sqlasm.util.ClassPathResource;
import com.wanda.ccs.sqlasm.util.ClassUtils;
import com.wanda.ccs.sqlasm.util.IOUtils;

public class CriteriaParserTest {
	
	static CriteriaParser parser;
	
	static {		
		CinemaCompositeParser cinemaCom = new CinemaCompositeParser();

		Clause consale = newPlain().in("from").output("CCS_NRPT2.T_F_CON_SALE consale")
			.depends(newPlain().in("where").output("member.MEMBER_KEY=consale.MEMBER_KEY"));

		Clause consale_date = newPlain().in("from").output("CCS_NRPT2.T_D_CON_DATE consale_date")
			.depends(newPlain().in("where").output("consale_date.DATE_KEY=consale.BOOK_DATE_KEY"))
			.depends(consale);
		
		Clause consale_hour = newPlain().in("from").output("CCS_NRPT2.T_D_CON_HOUR consale_hour")
			.depends(newPlain().in("where").output("consale_hour.HOUR_KEY=consale.BOOK_HOUR_KEY"))
			.depends(consale);
		
		Clause consale_cinema = newPlain().in("from").output("CCS_NRPT2.T_D_CON_CINEMA consale_cinema")
			.depends(newPlain().in("where").output("consale_cinema.CINEMA_KEY = consale.CINEMA_KEY"))
			.depends(consale);
		
		Clause consale_cate = newPlain().in("from").output("CCS_NRPT2.T_D_CON_CS_CLASS consale_cate")
			.depends(newPlain().in("where").output("consale.SALE_CLASS_KEY = consale_cate.SALE_CLASS_KEY"))
			.depends(consale);
		
		Clause group_by = newPlain().in("groupby").output("member.MEMBER_KEY");

		
		parser = newParser(QUERY_PARAGRAPHS)
			.add(newPlain().in("select").output("count(distinct member.MEMBER_KEY)"))
			.add(newPlain().in("from").output("CCS_NRPT2.T_D_CON_MEMBER member"))
			
			.add(notEmpty("conSaleDate"), newExpression().in("where").output("consale.BOOK_DATE_KEY", DataType.DATE)
					.depends(consale))
			.add(notEmpty("conSaleHoliday"), newPlain().in("where").output("consale_date.HOLIDAY_ID is not null")
					.depends(consale_date))	
			.add(notEmpty("conSaleHourPeriod"), newExpression().in("where").output("consale_hour.TIME_DIVIDING_ID", DataType.STRING)
					.depends(consale_hour))	
			.add(notEmpty("conSaleHour"), newExpression().in("where").output("consale.BOOK_HOUR_KEY", DataType.STRING)
					.depends(consale))
			.add(notEmpty("conSaleCinema"), newExpression().in("where").output("consale_cinema.INNER_CODE", DataType.STRING, cinemaCom)
					.depends(consale_cinema))
			.add(notEmpty("conSaleAmount"), newExpression().in("having").output("sum(consale.BK_SALE_AMOUNT) - sum(consale.RE_SALE_AMOUNT)", DataType.DOUBLE)
					.depends(consale).depends(group_by))
			.add(notEmpty("conSaleConsumeTime"), newExpression().in("having").output("count(distinct consale.Bk_CS_ORDER_CODE)", DataType.INTEGER)
					.depends(consale).depends(group_by))
			.add(notEmpty("conSaleCategory"), newExpression().in("where").output("consale_cate.THIRD_CLASS_ID", DataType.LONG)
					.depends(consale_cate));
	}
	
	
	
	@Test
	public void test() throws Exception {
		
		
		
		List<ExpressionCriterion> criteria = JsonCriteriaHelper.parse(getThisPackageText("CriteriaParserTest.js"));
		
		CriteriaResult result = parser.parse(criteria);
		
		System.out.println("===Query SQL===");
		System.out.println(result.getParameterizeText());
		System.out.println("===Parameters size===");
		System.out.println(result.getParameters().size());
		
		assertEquals(result.getParameters().size(), 21);
		//CriteriaParserTestResult.txt must save as UNIX text format(No Windows).
		String resultTxt = getThisPackageText("CriteriaParserTestResult.txt");
		assertEquals(result.getParameterizeText(), resultTxt);
		
		//File resultFile = getThisPackageFile("CriteriaParserTestResult.txt");
		//FileUtils.writeStringToFile(resultFile, result.getParameterizeText());
	}
	
	protected String getThisPackageText(String fileName) throws IOException  {
		File file = getThisPackageFile(fileName);
		return IOUtils.toString(new FileInputStream(file), "UTF-8");
	}
	
	protected File getThisPackageFile(String fileName) throws IOException  {
		ClassLoader cl = this.getClass().getClassLoader();
		String packagePath = ClassUtils.getPackageName(this.getClass()).replace('.', '/');
		return (new ClassPathResource(packagePath + "/" + fileName, cl)).getFile();
	}
	
}

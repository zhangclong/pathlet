package com.google.code.pathlet.ognl;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

public class OgnlExpression
{

    private Object expression;

    public OgnlExpression( String expressionString )
        throws OgnlException
    {
        super();
        expression = Ognl.parseExpression( expressionString );
    }

    public Object getExpression()
    {
        return expression;
    }

    public Object getValue( OgnlContext context, Object rootObject )
        throws OgnlException
    {
    	return Ognl.getValue( getExpression(), context, rootObject );
    }

    public void setValue( OgnlContext context, Object rootObject, Object value )
        throws OgnlException
    {
        Ognl.setValue(getExpression(), context, rootObject, value);
    }

}
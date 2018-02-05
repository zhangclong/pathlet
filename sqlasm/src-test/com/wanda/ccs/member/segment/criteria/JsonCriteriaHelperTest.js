[ {
	"inputId" : "user",
	"label" : "选择用户",
	"operator" : "in",
	"value" : {
		"selTarget" : true,
		"criteria" : [ {
			"inputId" : "username",
			"label" : "用户名",
			"operator" : "eq",
			"value" : "",
			"valueLabel" : ""
		},
		{
			"inputId" : "email",
			"label" : "电子邮件",
			"operator" : "eq",
			"value" : "",
			"valueLabel" : ""
		}, 
		{
			"inputId" : "gender",
			"label" : "性别",
			"operator" : "in",
			"value" : [],
			"valueLabel" : []
		} 
		],
		"selections" : {
			"value" : [ "user8", "user9" ],
			"valueLabel" : [ "用户8", "用户9" ]
		}
	}
}, 
{
	"inputId" : "noDisturb",
	"label" : "是否希望被联络",
	"operator" : "in",
	"value" : [ "1", "2" ]
}, 
{
	"inputId" : "conAmount",
	"label" : "卖品消费金额",
	"operator" : "eq",
	"value" : "100"
},
{
	"inputId" : "registerDtime",
	"label" : "入会时间",
	"operator" : "between",
	"value" : [ "2010-01-01 00:00:00", "2013-01-01 00:00:00" ]
} ]
package com.wanda.ccs.sqlasm.expression;

import java.util.ArrayList;
import java.util.List;


public class CompositeValue {
	
	private boolean selTarget;
	
	private List<ExpressionCriterion> criteria;
	
	private Selections selections;
	

	/*
	compositeCinema: {
		selTarget: false,  //是否选择明细目标
		criteria: [
			{inputId:"innerName", label:"影城内部名称", operator:"like", value:"北京", valueLabel:"北京"}
	    ],
	    selections: {
		    value:[],
		    valueLabel:[]
	    }
	},*/
	
	public CompositeValue() {
		super();
		this.selTarget = true;
		this.criteria = new ArrayList<ExpressionCriterion>(0);//give an empty criteria
		this.selections = new Selections();
	}

	
	public CompositeValue(List<String> selectionValue, List<String> selectionValueLabel) {
		super();
		this.selTarget = true;
		this.criteria = new ArrayList<ExpressionCriterion>(0);//give an empty criteria
		this.selections = new Selections(selectionValue, selectionValueLabel);
	}
	
	public CompositeValue(boolean selTarget, List<ExpressionCriterion> criteria, List<String> selectionValue, List<String> selectionValueLabel) {
		super();
		this.selTarget = selTarget;
		this.criteria = criteria;
		this.selections = new Selections(selectionValue, selectionValueLabel);
	}

	public boolean isSelTarget() {
		return selTarget;
	}



	public void setSelTarget(boolean selTarget) {
		this.selTarget = selTarget;
	}



	public List<ExpressionCriterion> getCriteria() {
		return criteria;
	}



	public void setCriteria(List<ExpressionCriterion> criteria) {
		this.criteria = criteria;
	}



	public Selections getSelections() {
		return selections;
	}



	public void setSelections(Selections selections) {
		this.selections = selections;
	}


	public static class Selections {
		
		private List<String> value;
		
		private List<String> valueLabel;
		
		public Selections() {
			this.value = new ArrayList<String>();
			this.valueLabel = new ArrayList<String>();
		}
		
		public Selections(List<String> value, List<String> valueLabel) {
			super();
			this.value = value;
			this.valueLabel = valueLabel;
		}

		public List<String> getValue() {
			return value;
		}

		public void setValue(List<String> value) {
			this.value = value;
		}

		public List<String> getValueLabel() {
			return valueLabel;
		}

		public void setValueLabel(List<String> valueLabel) {
			this.valueLabel = valueLabel;
		}

	}
	

}

package com.ainq.caliphr.hqmf.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

public @Data class FromClause {
	
	private final String alias;
	private final String tableName;
	private List<Join> joins = new ArrayList<Join>();
	
	public static @Data class Join {
		private final String alias;
		private final String tableName;
		private final String onClause;
	}

}

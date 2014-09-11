package com.group1.englishchichewatranslator;

public class HistoryObject extends Bilingual {
	String time;
	public HistoryObject(String source, String target,String time) {
		super(source, target);
		this.source = source;
		this.target = target;
		this.time = time;
	}

}

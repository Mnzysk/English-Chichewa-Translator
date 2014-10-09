package com.group1.englishchichewatranslator;

public class Sentence {
	private String text;
	private String endOfSentPunc;
	
	public Sentence(String text, String string) {
		this.text = text;
		this.endOfSentPunc= string;
	}
	public String returnsentences(){
		return this.text+this.endOfSentPunc;
	}
	public String returnText(){
		return this.text;
	}
	public String returnPunctuation(){
		return this.endOfSentPunc;
	}
}

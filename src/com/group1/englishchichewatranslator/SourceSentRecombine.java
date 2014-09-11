package com.group1.englishchichewatranslator;

public class SourceSentRecombine {
	private String sentence;
	
	SourceSentRecombine(){
		this.sentence = "";
	}
	public void addtext(String text){
		this.sentence = (text +" " +this.sentence).trim();
	}
	public String toString(){
		return this.sentence;
	}
	public void reset(){
		this.sentence ="";
	}
}

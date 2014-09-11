package com.group1.englishchichewatranslator;

public class Solution {
	private String result;
	
	public Solution() {
		this.result = "";
	}
	public void addResult(String text){
		this.result = (this.result +" "+text).trim();
	}
	public String toString(){
		return this.result;
	}
	public void reset(){
		this.result ="";
	}
}

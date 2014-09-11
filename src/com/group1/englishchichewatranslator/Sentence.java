package com.group1.englishchichewatranslator;

public class Sentence {
	private String text;
	private char endOfSentPunc;
	
	public Sentence(String text, char puntuation) {
		this.text = text;
		this.endOfSentPunc= puntuation;
	}
}

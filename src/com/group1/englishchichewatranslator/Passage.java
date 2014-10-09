package com.group1.englishchichewatranslator;

import java.util.ArrayList;
import java.util.Scanner;

import android.util.Log;

public class Passage {
	
	private String passage;
	public Passage(String passage) {
		this.passage = passage;
	}
	
	public ArrayList<Sentence> getSentences(){
		Scanner psg = new Scanner(this.passage);
		ArrayList<Sentence> sentences = new ArrayList<Sentence>();
		String line = "";
		while (psg.hasNext()) {
			String word = psg.next();
			if(word.contains(".")||word.contains(",")||word.contains(";")||word.contains(":")||word.contains("?")){
				if(word.contains(".")){
					line =line+" "+word.replace(".", "");
					Sentence curSent = new Sentence(line, ".");
					Log.d("Sentence", curSent.returnsentences());
					sentences.add(curSent);
					line = "";
				}
				if(word.contains(",")){
					line =line+" "+word.replace(",", "");
					Sentence curSent = new Sentence(line, ",");
					Log.d("Sentence", curSent.returnsentences());
					sentences.add(curSent);
					line = "";
				}
				if(word.contains(";")){
					line =line+" "+word.replace(";", "");
					Sentence curSent = new Sentence(line, ";");
					Log.d("Sentence", curSent.returnsentences());
					sentences.add(curSent);
					line = "";
				}
				if(word.contains(":")){
					line =line+" "+word.replace(":", "");
					Sentence curSent = new Sentence(line, ":");
					Log.d("Sentence", curSent.returnsentences());
					sentences.add(curSent);
					line = "";
				}
				if(word.contains("?")){
					line =line+" "+word.replace("?", "");
					Sentence curSent = new Sentence(line, "?");
					Log.d("Sentence", curSent.returnsentences());
					sentences.add(curSent);
					line = "";
				}
				
				}
				else{
					line = line +" " + word;
				}
			
		}
		psg.close();
		return sentences;
	}

}

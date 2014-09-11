package com.group1.englishchichewatranslator;

public class Hyponthesis extends Bilingual {

		private float prbTra;
		private float prbLan = 1;
		private float prbText;
		public Hyponthesis(String source, String target, float prbTra) {
			super(source, target);
			this.source = source;
			this.target = target;
			this.prbTra = prbTra;

		}
		public void setlanModel(float prbLan) 
		{
			this. prbLan=  prbLan;
		}
		
		public void setprbText(){
			this.prbText = this.prbLan * this.prbTra;
		}
		public float getprbText(){
			return this.prbLan * this.prbTra;
		}
		public String getTarget(){
			return target;
		}
		
}

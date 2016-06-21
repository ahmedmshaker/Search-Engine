package com.search.ir;

public class Row {
	
	
	private String word;
	private String idAndPosition;
	private String frequency;
	
	public Row (String word , String positionAndId , String frequency){
		this.word = word ;
		idAndPosition = positionAndId;
		this.frequency = frequency;
	}

	String getFrequency() {
		return frequency;
	}

	String getWord() {
		return word;
	}

	String getIdAndPosition() {
		return idAndPosition;
	}



	

	
	
	

}

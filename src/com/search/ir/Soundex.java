package com.search.ir;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

public class Soundex {

	Bigarm bi;
	public Soundex(){
		bi = new Bigarm();
	}
	public void saveSoundex() {
		ArrayList<String> soundex = new ArrayList<>();
		ArrayList<String> originalWords = DBConnection
				.getAllWordsFromOriginal();
		for (String word : originalWords) {
			String allWords = "";
			word = Normalizer.normalize(word, Normalizer.Form.NFD);
			String code = soundex(word);
			if (!soundex.contains(code)) {
				for (String term : originalWords) {
					if (code.equals(soundex(term))) {
						soundex.add(code);
						allWords += term + ",";
					}
				}
				if(!allWords.equals("")){
					boolean bool=DBConnection.insertIntoSoundexIndex(code, allWords);
					if(bool)
						System.out.println(code+"  saved");
				}
			}
		}
	}

	public void cuttingQuery(String query) {
		StringTokenizer toke = new StringTokenizer(query, " ");
		HashMap<String, ArrayList<String>> map= new HashMap<>();
		//int size = toke.countTokens();
		while (toke.hasMoreElements()) {
			String word = toke.nextToken();
			String code = soundex(word);
			String allWords = "";
			allWords = DBConnection.getWordsFromSoundex(code);
			map.put(word, cuttingAllWords(allWords));
		}
		HashMap<String, Set<String>> afterEditDistance = bi.calculateEditDistance(map);
		List<List<String>> toBecombined = new ArrayList<List<String>>();
		for (String word : afterEditDistance.keySet()) {
			System.out.println(word);
			List<String> list = new ArrayList<String>();
			list.addAll(afterEditDistance.get(word));
			toBecombined.add(list);
		}
		List<String> finalresult =bi.findCombination(toBecombined);
		for (int i = 0; i < 200; i++) {
			System.out.println(finalresult.get(i));
		}

		System.out.println(map.toString());
			
	}
	
	public ArrayList<String> cuttingAllWords(String allWords){
		ArrayList<String> ret =new ArrayList<>();
		allWords = allWords.substring(0, allWords.length() - 1);
		StringTokenizer toke = new StringTokenizer(allWords, ",");
		while (toke.hasMoreElements()) {
			String word = toke.nextToken();
			ret.add(word);
		}
		return ret;
	}
	
	public String soundex(String s) {
		char[] x = s.toUpperCase().toCharArray();
		char firstLetter = x[0];

		// convert letters to numeric code
		for (int i = 0; i < x.length; i++) {
			switch (x[i]) {

			case 'B':
			case 'F':
			case 'P':
			case 'V':
				x[i] = '1';
				break;

			case 'C':
			case 'G':
			case 'J':
			case 'K':
			case 'Q':
			case 'S':
			case 'X':
			case 'Z':
				x[i] = '2';
				break;

			case 'D':
			case 'T':
				x[i] = '3';
				break;

			case 'L':
				x[i] = '4';
				break;

			case 'M':
			case 'N':
				x[i] = '5';
				break;

			case 'R':
				x[i] = '6';
				break;

			default:
				x[i] = '0';
				break;
			}
		}

		// remove duplicates
		String output = "" + firstLetter;
		for (int i = 1; i < x.length; i++)
			if (x[i] != x[i - 1] && x[i] != '0')
				output += x[i];

		// pad with 0's or truncate
		output = output + "0000";
		return output.substring(0, 4);
	}
}

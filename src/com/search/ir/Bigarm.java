package com.search.ir;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import com.mysql.jdbc.StringUtils;

public class Bigarm {

	public Bigarm() {
	}

	ArrayList<String> savedSub = new ArrayList<>();
	HashMap<String, ArrayList<String>> map = new HashMap<>();

	public void saveBigarm() {
		ArrayList<String> allOriginals = DBConnection.getAllWordsFromOriginal();
		System.out.println("\\\\\\\\\\\\\\\\\\\\\\\\ " + allOriginals.size());
		int count = 0;
		for (String word : allOriginals) {
			count++;
			if (count == allOriginals.size() - 2)
				System.out.println("///////////////// finish");
			word = Normalizer.normalize(word, Normalizer.Form.NFD);
			word = "$" + word + "$";
			for (int i = 0; i < word.length() - 1; i++) {
				String subString = word.substring(i, i + 2);
				// System.out.println("////////////////"+subString);
				String allWordsToSave = "";
				if (!savedSub.contains(subString)) {
					savedSub.add(subString);
					for (String element : allOriginals) {
						if (subString.charAt(0) == '$') {
							if (element.startsWith(subString.charAt(1) + "")) {
								allWordsToSave = allWordsToSave + element + ",";
							}
						} else if (subString.charAt(1) == '$') {
							if (element.endsWith(subString.charAt(0) + "")) {
								allWordsToSave = allWordsToSave + element + ",";
							}
						} else {
							if (element.contains(subString)) {
								allWordsToSave = allWordsToSave + element + ",";
							}
						}

					}
					// save into db
					boolean bool = DBConnection.insertIntoBigramIndex(
							subString, allWordsToSave);
					if (bool) {
						System.out.println(subString + " saved");
					}

				}

			}
		}

	}

	public void cuttingQuery(String query) {
		StringTokenizer toke = new StringTokenizer(query, " ");
		int size = toke.countTokens();
		while (toke.hasMoreElements()) {
			String word = toke.nextToken();
			word = "$" + word + "$";
			String allWords = "";
			for (int i = 0; i < word.length() - 1; i++) {
				String sub = word.substring(i, i + 2);
				allWords += DBConnection.getWordsFromBigram(sub);
			}
			word = word.substring(1,word.length()-1);
			map.put(word,calculateJaccardCoefficient(word, allWords));
		}

		
		HashMap<String, Set<String>> afterEdit =calculateEditDistance(map);
		List<List<String>> toBecombined = new ArrayList<List<String>>();
		for (String word : afterEdit.keySet()) {
			List<String> list = new ArrayList<String>();
			list.addAll(afterEdit.get(word));
			toBecombined.add(list);
		}
		List<String> finalresult =findCombination(toBecombined);
		for (int i = 0; i < 200; i++) {
			System.out.println(finalresult.get(i));
		}

	}

	private ArrayList<String> calculateJaccardCoefficient(String word,
			String allWords) {
		ArrayList<String> afterJaccard =new ArrayList<>();
		allWords = allWords.substring(0, allWords.length() - 1);
		StringTokenizer toke = new StringTokenizer(allWords, ",");
		//System.out.println("2bl"+toke.countTokens());
		while (toke.hasMoreElements()) {
			String temp = toke.nextToken();
			int common = 0;
			//System.out.println(temp);
			for (int i = 0; i < temp.length() - 1; i++) {
				String subString = temp.substring(i, i + 2);
				if (word.contains(subString)) {
					common++;
				}
			}
			
			//System.out.println(temp + "/////////"+common);
			double res = (double)2*common/(temp.length()-1 + word.length()-1);
			//System.out.println("//////"+res);
			if(res >= 0.75)
				afterJaccard.add(temp);

		}
		//System.out.println("b3d"+afterJaccard.size());

		return afterJaccard;
	}
	
	
	public  HashMap<String,Set<String>> calculateEditDistance(HashMap<String,ArrayList<String>> map) {
		MinimumEditDistance edit= new MinimumEditDistance();
		HashMap<String,Set<String>> ret = new HashMap<>();
		
		for (String word : map.keySet()) {
			HashMap<String,Integer> subRet=new HashMap<>();
			for (int i = 0; i < map.get(word).size(); i++) {
				int distance = edit.getDistance(word, map.get(word).get(i));
				subRet.put(map.get(word).get(i), distance);
			}
			
			ret.put(word,SteamerSearch.sortByValues(subRet,2).keySet());
		}
		return ret;
	}
	public List<String> findCombination(List<List<String>> totalList)
	{
	    List<String> result = new ArrayList<String>(totalList.get(0));

	    for(int index = 1; index < totalList.size() ; index++)
	    {
	        result = combineTwoLists(result, totalList.get(index));
	    }

	    return result;
	}

	private List<String> combineTwoLists(List<String> list1, List<String> list2)
	{
	    List<String> result = new ArrayList<String>();
	    StringBuilder sb = new StringBuilder();
	    for(String s1 : list1)
	    {
	        for(String s2: list2)
	        {
	            sb.setLength(0);
	            sb.append(s1).append(' ').append(s2);
	            result.add(sb.toString());
	        }
	    }
	    return result;
	}
	
}

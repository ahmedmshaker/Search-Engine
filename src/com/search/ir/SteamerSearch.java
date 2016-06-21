package com.search.ir;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;

public class SteamerSearch {

	String text;
	Stemmer steamer;
	ArrayList<Row> rows;
	boolean exact = false;
	String mTest;

	public SteamerSearch(String text) throws SQLException {
		this.text = text;
		mTest = text;
		steamer = new Stemmer();
		rows = new ArrayList<>();
		findSteam();
	}

	public void findSteam() throws SQLException {
		exact = ifExact(text);
		if (exact) {
			text = text.replaceAll("\"", "");
		}
		System.out.println(text);
		StringTokenizer token = new StringTokenizer(text, " ");
		String word, steam;
		while (token.hasMoreElements()) {
			word = token.nextToken();
			steam = steamer.stem(word);
			System.out.println(steam);
			Row res = DBConnection.retriveRowFromSteamer(steam);
			if (res != null) {
				rows.add(res);
			}
		}
	}

	public HashMap<Integer, Integer> findDocs() {
		HashMap<Integer, Integer> docIdFrequncy;
		HashMap<String, HashMap<Integer, Integer>> WordDocIdFrequncy = new HashMap<String, HashMap<Integer, Integer>>();

		for (Row element : rows) {
			String freq = element.getFrequency();
			String[] arr = freq.split(",");
			docIdFrequncy = new HashMap<Integer, Integer>();
			for (String item : arr) {
				String[] arr2 = item.split(":");
				docIdFrequncy.put(Integer.parseInt(arr2[0]),
						Integer.parseInt(arr2[1]));
			}
			WordDocIdFrequncy.put(element.getWord(), docIdFrequncy);
		}
		Set<Integer> intersectDocs = WordDocIdFrequncy.get(
				WordDocIdFrequncy.entrySet().iterator().next().getKey())
				.keySet();

		for (String word : WordDocIdFrequncy.keySet()) {
			intersectDocs.retainAll(WordDocIdFrequncy.get(word).keySet());
		}

		docIdFrequncy = new HashMap<>();
		for (Integer docId : intersectDocs) {
			int sum = 0;
			for (String word : WordDocIdFrequncy.keySet()) {
				sum += WordDocIdFrequncy.get(word).get(docId);

			}
			docIdFrequncy.put(docId, sum);
		}

		return sortByValues(docIdFrequncy, 1);

	}

	public ArrayList<String> search() throws CorruptIndexException, IOException, ParseException {
		ArrayList<String> urls = null;
		if (exact) {
			urls = com.test.ahmed.SteamerSearch.exactSearch(mTest);
		} else {
			HashMap<Integer, Integer> docAndId = findDocs();
			urls = DBConnection.getUrl(docAndId.keySet());
		}

		return urls;
	}

	@SuppressWarnings({ "unchecked", "unchecked", "unchecked", "unchecked" })
	public static HashMap sortByValues(HashMap map, final int flag) {
		List list = new LinkedList(map.entrySet());
		// Defined Custom Comparator here
		Collections.sort(list, new Comparator() {

			public int compare(Object o1, Object o2) {
				if (flag == 1) {
					return ((Comparable) ((Map.Entry) (o2)).getValue())
							.compareTo(((Map.Entry) (o1)).getValue());
				} else {
					return ((Comparable) ((Map.Entry) (o1)).getValue())
							.compareTo(((Map.Entry) (o2)).getValue());
				}
			}
		});

		// Here I am copying the sorted list in HashMap
		// using LinkedHashMap to preserve the insertion order
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}

	private boolean ifExact(String query) {
		if (query.charAt(0) == '"' && query.charAt(query.length() - 1) == '"')
			return true;
		return false;
	}

	public HashMap<String, HashMap<Integer, Set<String>>> findIdsAndPosition(
			ArrayList<Row> allrows) {

		HashMap<String, HashMap<Integer, Set<String>>> ret = new HashMap<>();

		for (Row row : allrows) {
			String word = row.getWord();
			String idPositions = row.getIdAndPosition();
			idPositions = idPositions.substring(1, idPositions.length() - 1);
			String[] pairs = idPositions.split("]");
			HashMap<Integer, Set<String>> ids = null;
			System.out.println(word + "\\\\\\" + idPositions);
			ids = new HashMap<Integer, Set<String>>();
			for (String element : pairs) {
				// System.out.println(element);
				String[] entry = element.split("=");
				// if (entry.length >= 2) {
				entry[1] = entry[1].replaceAll(" ", "");
				entry[1] = entry[1].replace("[", "");
				entry[1] = entry[1].replace("]", "");
				// entry[0] = entry[0].substring(2,entry[0].length());
				entry[0] = entry[0].replace(",", "");
				entry[0] = entry[0].replace(" ", "");
				String positions = entry[1];
				// System.out.println(entry[0] + "\\\\\\\\" + positions)
				Set<String> s = new HashSet<String>();
				s.addAll(Arrays.asList(positions.split(",")));
				ids.put(Integer.parseInt(entry[0]), s);
				// }
			}
			ret.put(word, ids);

		}

		return ret;
	}

	public Set<Integer> exactSearch() {
		HashMap<String, HashMap<Integer, Set<String>>> idAndPosition;
		idAndPosition = findIdsAndPosition(rows);
		Set<Integer> ret = new HashSet<Integer>();
		Set<Integer> intersectDocs = idAndPosition.get(
				idAndPosition.entrySet().iterator().next().getKey()).keySet();

		for (String word : idAndPosition.keySet()) {
			intersectDocs.retainAll(idAndPosition.get(word).keySet());
		}
		for (Integer docId : intersectDocs) {
			int count =0 ;
			Set<String> intersectPosition = new HashSet<String>();
			for (String word : idAndPosition.keySet()) {
				Set<String> ss = new HashSet<String>();
				if(count ==0){
					intersectPosition=idAndPosition.get(word).get(docId);
				}
				else{
					Set<String> s=idAndPosition.get(word).get(docId);
					for (String item : s) {
						//System.out.println(item);
						ss.add(Integer.parseInt(item)-count +"");
						//System.out.println(Integer.parseInt(item)-count +"");
					}
					System.out.println(ss);
					System.out.println(intersectPosition);
					intersectPosition.retainAll(ss);
					System.out.println(intersectPosition.toString());
				}
				
				
				
				count++;
			}
			if(intersectPosition.size() >0)
				ret.add(docId);
		}
		System.out.println(ret.toString());
		return ret ;
	}

}

/*
 * System.out.println(intersectDocs.toString()); ArrayList<ArrayList<String>>
 * positions; ArrayList<String> temp = new ArrayList<String>();
 * ArrayList<String> temp1 = new ArrayList<String>(); Set<Integer> docs = new
 * HashSet<>();
 * 
 * // mn awl hena shof l temp w temp1 byfady wla 3lashan bya5od kol l //
 * intersect for (Integer docId : intersectDocs) { boolean flag=false; positions
 * = new ArrayList<>(); for (String word : idAndPosition.keySet()) {
 * positions.add(idAndPosition.get(word).get(docId)); }
 * System.out.println(positions.toString()); temp1.addAll(positions.get(0)); for
 * (int i = 1; i < positions.size(); i++) { temp.clear(); temp.addAll(temp1);
 * temp1.clear(); // System.out.println(temp1); for (String id :
 * positions.get(i)) { int idNext = Integer.parseInt(id) - 1; if
 * (temp.contains(idNext + "")){ temp1.add(id); flag=true; } } if(flag=false)
 * break; } if (temp1.size() > 0) { docs.add(docId); } } //
 * System.out.println(docs.toString()); return docs;
 */

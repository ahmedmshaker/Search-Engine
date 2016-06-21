package com.test.ahmed;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SteamerSearch {

	public static ArrayList<String> exactSearch(String querystring) throws CorruptIndexException, IOException, ParseException {
		 File INDEX_DIRECTORY = new File(
				"F:\\programs\\IndexDirectory");
			ArrayList<String> ret = new ArrayList();


		Directory directory = FSDirectory.getDirectory(INDEX_DIRECTORY);
		IndexReader indexReader = IndexReader.open(directory);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);

		Analyzer analyzer = new StandardAnalyzer();
		QueryParser queryParser = new QueryParser("content", analyzer);
		Query query = queryParser.parse(querystring);
		TopDocs hits = indexSearcher.search(query,null,20);
		System.out.println("Number of hits: " + hits.totalHits);

		ScoreDoc[] it = hits.scoreDocs;
		for (int i = 0; i < it.length; i++) {

			
			Document document = indexSearcher.doc(it[i].doc);
			String path = document.get("url");
			ret.add(path);
			//System.out.println("Hit: " + path);
		}
		return ret;
	}
}

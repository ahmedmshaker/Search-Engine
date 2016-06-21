package com.search.ir;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Set;

public class DBConnection {

	@SuppressWarnings("finally")
	public static Connection createConnection() throws Exception {
		Connection con = null;
		try {
			Class.forName(Constants.dbClass);
			con = DriverManager.getConnection(Constants.dbUrl,
					Constants.dbUser, Constants.dbPwd);
		} catch (Exception e) {
			throw e;
		} finally {
			return con;
		}
	}

	public static Row retriveRowFromSteamer(String steamWord)
			throws SQLException {
		Connection con = null;
		try {
			con = createConnection();
			Statement statment = con.createStatement(
					java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);

			String query = "select * from steamer where word like '"
					+ steamWord + "'";
			ResultSet result = statment.executeQuery(query);
			if (result.next())
				if (!result.equals(null))
					return new Row(result.getString("word"),
							result.getString("id_and_position"),
							result.getString("frequency"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public static ArrayList<String> getUrl(Set<Integer> docId) {
		Connection con = null;
		ArrayList<String> urls = new ArrayList<String>();
		try {
			con = createConnection();
			Statement statment = con.createStatement(
					java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);

			for (Integer id : docId) {
				String query = "select * from task_crawler where id=" + id;
				ResultSet result = statment.executeQuery(query);
				if (result.next())
					if (!result.equals(null))
						urls.add(result.getString("url"));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());

		}

		return urls;
	}

	public static ArrayList<String> getAllWordsFromOriginal() {
		ArrayList<String> allWords = new ArrayList<String>();
		Connection dbConn = null;
		Statement stmt;

		try {
			dbConn = DBConnection.createConnection();
			stmt = dbConn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(Integer.MAX_VALUE);

			String sql = "select word from origenalword";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				System.out.println("retrieving");
				allWords.add(rs.getString("word"));
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return allWords;

	}

	public static boolean insertIntoBigramIndex(String subword, String allWords) {

		boolean insertStatus = false;
		Connection dbConn = null;
		try {
			dbConn = DBConnection.createConnection();

			String query = "INSERT into bigram_index(bi_word,words) values( ?,?)";
			PreparedStatement statment = dbConn.prepareStatement(query);
			statment.setString(1, subword);
			statment.setString(2, allWords);

			// System.out.println(query);
			int records = statment.executeUpdate();
			if (records > 0) {
				insertStatus = true;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block

			System.out.println("   can't save  " + subword);
		}
		return insertStatus;

	}

	public static String getWordsFromBigram(String sub) {
		String words = "";
		Connection dbConn = null;
		Statement stmt;

		try {
			dbConn = DBConnection.createConnection();
			stmt = dbConn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(Integer.MAX_VALUE);

			String sql = "select words from bigram_index where bi_word like\""+sub+"\"";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				words = rs.getString("words");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return words;
	}
	
	public static boolean insertIntoSoundexIndex(String code, String allWords) {

		boolean insertStatus = false;
		Connection dbConn = null;
		try {
			dbConn = DBConnection.createConnection();

			String query = "INSERT into soundex_index(soundex_code,words) values( ?,?)";
			PreparedStatement statment = dbConn.prepareStatement(query);
			statment.setString(1, code);
			statment.setString(2, allWords);

			// System.out.println(query);
			int records = statment.executeUpdate();
			if (records > 0) {
				insertStatus = true;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block

			System.out.println("   can't save  " + code);
		}
		return insertStatus;

	}
	
	public static String getWordsFromSoundex(String code) {
		String words = "";
		Connection dbConn = null;
		Statement stmt;

		try {
			dbConn = DBConnection.createConnection();
			stmt = dbConn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
					java.sql.ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(Integer.MAX_VALUE);

			String sql = "select words from soundex_index where soundex_code like\""+code+"\"";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				words = rs.getString("words");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return words;
	}
}

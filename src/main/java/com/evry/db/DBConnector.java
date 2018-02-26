package com.evry.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import com.evry.api.Aisle;
import com.evry.api.Vision;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBConnector {
	Connection conn;
	private static DBConnector instance;

	private DBConnector(String connectionUrl, String userName, String password) throws SQLException, NamingException {
		// try {
		Context ctx = new InitialContext();
		DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/sqlite");
		conn = ds.getConnection();
		/*
		 * Class.forName("org.sqlite.JDBC").newInstance(); conn =
		 * DriverManager.getConnection(
		 * "jdbc:sqlite:D:\\Projects\\MediaStreamer\\sqlite\\sqlite-tools-win32-x86-3210000\\VISION"
		 * );
		 */
		/*
		 * } catch (InstantiationException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); } catch (IllegalAccessException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); } catch
		 * (ClassNotFoundException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } // conn =
		 * DriverManager.getConnection(connectionUrl, userName, // password);
		 */ }

	public static DBConnector getInstance() {
		try {
			if (instance == null) {
				// To-Do : Change the DB Connector signature.

				instance = new DBConnector("test", "", "");
			}

		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instance;
	}

	public ArrayList<Aisle> getAllAisle() {
		try {
			Statement stmt = conn.createStatement();
			String query = "SELECT * FROM aisle";
			if (stmt.execute(query)) {
				ResultSet result = stmt.getResultSet();
				ArrayList<Aisle> aisleArray = new ArrayList<>();
				while (result.next()) {
					Aisle obj = new Aisle();
					obj.setId(result.getInt("id"));
					obj.setUrl(result.getString("aisleURL"));
					obj.setThreashold(result.getInt("threashold"));
					obj.setAlertTimeout(result.getInt("alertTimeout"));
					obj.setCallabckURL(result.getString("callbackURL"));
					obj.setDetails(result.getString("details"));
					obj.setApplicationId(result.getInt("applicationId"));
					obj.setTenantId(result.getInt("tenantId"));
					obj.setLastQueriedTimeStamp(-1);
					System.out.println("Aisle details are " + obj);
					aisleArray.add(obj);
				}
				return aisleArray;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public int addVisionResult(String aisleName, Timestamp date, String fileName, String results, int aisleNumber) {
		try {
			String query = "INSERT INTO vision2(aisleID, datetime, imageresult, aisleref) VALUES (?, ?, ?, ?)";
			PreparedStatement pStatment = conn.prepareStatement(query);
			pStatment.setString(1, aisleName);
			pStatment.setTimestamp(2, date);
			pStatment.setString(3, results);
			pStatment.setInt(4, aisleNumber);
			pStatment.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	public ArrayList<Vision> getAllVisionResultsForAisle(int aisleId) {
		try {
			String query = "SELECT * FROM vision2 WHERE aisleref=?";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setInt(1, aisleId);
			ResultSet result = statement.executeQuery();
			ArrayList<Vision> vision = new ArrayList<Vision>();

			while (result.next()) {
				Vision vis = new Vision();
				vis.setAisleID(result.getString("aisleID"));
				vis.setDateTime(result.getTimestamp("datetime"));
				vis.setImageResults(result.getString("imageresult"));
				vis.setId(result.getInt("id"));
				vision.add(vis);
			}
			return vision;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public ArrayList<Vision> getAllVisionResultsForAisleWithinDate(int aisleId, Timestamp fromDate, Timestamp toDate) {
		try {
			if(fromDate ==  null || toDate == null) {
				return getAllVisionResultsForAisle(aisleId);
			}
			String query = "select * from vision2 where aisleref=? AND datetime>? AND datetime<?";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setInt(1, aisleId);
			statement.setTimestamp(2, fromDate);
			statement.setTimestamp(3, toDate);
			ResultSet result = statement.executeQuery();
			ArrayList<Vision> array = new ArrayList<Vision>();

			while (result.next()) {
				Vision vision = new Vision();
				vision.setAisleID(result.getString("aisleID"));
				vision.setDateTime(result.getTimestamp("datetime"));
				vision.setImageResults(result.getString("imageresult"));
				vision.setId(result.getInt("id"));
				array.add(vision);
			}
			return array;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public ArrayList<Vision> getAllVisionResults() {
		try {
			String query = "select * from vision2";
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery(query);
			ArrayList<Vision> array = new ArrayList<Vision>();

			while (result.next()) {
				Vision vision = new Vision();
				vision.setAisleID(result.getString("aisleID"));
				vision.setDateTime(result.getTimestamp("datetime"));
				vision.setImageResults(result.getString("imageresult"));
				vision.setId(result.getInt("aisleref"));
				System.out.println(vision);
				array.add(vision);
			}
			return array;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public int addAisle(String url, String aisleDetails, String callbackURL, int alertTimeout, int threashold,
			int applicationId, int tenantId) {
		try {
			String query = "INSERT INTO aisle(aisleURL, details, callbackURL, alertTimeout, threashold, applicationId, tenantId) VALUES(?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pStatment = conn.prepareStatement(query);
			pStatment.setString(1, url);
			pStatment.setString(2, aisleDetails);
			pStatment.setString(3, callbackURL);
			pStatment.setInt(4, alertTimeout);
			pStatment.setInt(5, threashold);
			pStatment.setInt(6, applicationId);
			pStatment.setInt(7, tenantId);
			pStatment.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	public void removeAisle(int aisleId) {
		try {
			String query = "DELETE * from aisle where aisleid=?";
			PreparedStatement statement = conn.prepareStatement(query);
			statement.setInt(1, aisleId);
			statement.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			DBConnector db = new DBConnector(
					"jdbc:sqlite:D:\\Projects\\MediaStreamer\\sqlite\\sqlite-tools-win32-x86-3210000\\VISION", "", "");
			// db.addAisle("rtsp://10.10.10.10/live.sdp", "Frozen Items", "some
			// callback", 20, 10, 2, 2);
			// db.getAllVisionResults();

			db.addVisionResult("rtsp://131.0.0.1/live.sdp", new Timestamp(System.currentTimeMillis()), "image_7", "crowd 5",
					1);
			db.addVisionResult("rtsp://121.0.0.1/live.sdp", new Timestamp(System.currentTimeMillis()), "image 9", "crowd 3",
					1);
			ArrayList<Vision> vsn = db.getAllVisionResults();
			System.out.println("Resultss are ");
			for (int i = 0; i < vsn.size(); i++) {
				Vision vision = vsn.get(i);
				System.out.println(vision.toString());
			}
			// db.addAisle("rtsp://127.0.0.1/live.sdp", "Counter", callbackURL,
			// alertTimeout, threashold, applicationId, tenantId)

			db.getAllAisle();
		} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
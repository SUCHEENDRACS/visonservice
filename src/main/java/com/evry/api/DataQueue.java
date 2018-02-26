package com.evry.api;

import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.NamingException;

import com.evry.db.DBConnector;

public class DataQueue implements Runnable {
	private ArrayList<Aisle> aisles;
	private boolean firstTime;
	
	
	
	
	public DataQueue() {
		// TODO Auto-generated constructor stub
		aisles = DBConnector.getInstance().getAllAisle();
		
	}

	@Override
	public void run() {
		try {
		
		if (firstTime == false) {
			System.out.println("scheduler called for the first time");
			firstTime = true;
		} else {
			for (Aisle obj : aisles) {
				DataAccessor.getInstance().addDeviceDataToWorkerQueue(obj);
			}
		}
			
	} catch (SQLException | NamingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	}

}

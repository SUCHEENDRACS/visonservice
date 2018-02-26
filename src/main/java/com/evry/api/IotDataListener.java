package com.evry.api;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class IotDataListener implements ServletContextListener {
	private ScheduledExecutorService scheduler;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		scheduler.shutdownNow();
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// Load the configuration related to Iot, database, mongodb etc.
		//try {
			initializePropertyFile();
			scheduler = new ScheduledThreadPoolExecutor(1);
			//scheduler.scheduleAtFixedRate(new DataAccessor(Util.getConfigParam("url"), null), 0, 5, TimeUnit.MINUTES);
			scheduler.scheduleAtFixedRate(new DataQueue(), 0, 1, TimeUnit.MINUTES);
		/*} catch (SQLException | NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
	}

	private static void insertConfigParams(NodeList nodeList) {
		for (int count = 0; count < nodeList.getLength(); count++) {
			Node tempNode = nodeList.item(count);
			if (tempNode.getNodeType() == Node.ELEMENT_NODE) {
				if (tempNode.getChildNodes().getLength() == 1) {
					Util.addConfigParam(tempNode.getNodeName(), tempNode.getTextContent().trim());
				}
				if (tempNode.hasChildNodes()) {
					insertConfigParams(tempNode.getChildNodes());
				}
			}
		}
	}

	private void initializePropertyFile() {
		try {

			String propertiesFileName = "vision-service.xml";
			if (System.getProperty("SwiftPropertyFileName") != null) {
				propertiesFileName = System.getProperty("SwiftPropertyFileName");
			}

			if (System.getProperty("catalina.home") == null) {
				System.out.println("Failed to read property -> catalina.home");
				return;
			}

			String path = "file:///" + System.getProperty("catalina.home") + "/conf/" + propertiesFileName;
			System.out.println("Reading configuration file from Path -> " + path);

			try {
				URL url = new URL(path);
				if (new File(url.toURI()).exists()) {
					DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Document doc = dBuilder.parse(url.openStream());
					if (doc.hasChildNodes()) {
						insertConfigParams(doc.getChildNodes());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

package com.evry.api;

import java.sql.Timestamp;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.jboss.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.evry.db.DBConnector;
//import com.evry.db.FileReaderMongoDB;
import com.evry.vision.IBMVisionImpl;

public class CoreWorkerManager {
	// Log Object
	private static final Logger log = Logger.getLogger(CoreWorkerManager.class);

	private static final BlockingDeque<DeviceData> queue = new LinkedBlockingDeque<DeviceData>();
	private static final AtomicBoolean work = new AtomicBoolean(false);
	private static final AtomicBoolean queueAllowed = new AtomicBoolean(false);
	private static final CoreWorkerManager dispatcher = new CoreWorkerManager();

	private static AtomicReference<CoreWorkerThread> wsMessageSender = new AtomicReference<CoreWorkerThread>();

	private CoreWorkerManager() {
		if (wsMessageSender == null) {
			wsMessageSender = new AtomicReference<CoreWorkerThread>();
		}

		startWorkerThread();
	}

	/**
	 * Returns the Singleton object for the Core Manager
	 * 
	 * @return - Returns singleton object of CoreWorkerManager
	 */
	public static CoreWorkerManager getInstance() {
		return dispatcher;
	}

	/**
	 * Clears the pending CoreMessages from Worker Queue
	 */
	public void clearMessageQueue() {
		log.infof("CoreWorkerManager is about to clear queue [Size : {%s}, QueueAllowed : {%s}] ", queue.size(),
				queueAllowed.get());
		queueAllowed.getAndSet(false);
		queue.clear();
		log.infof("CoreWorkerManager cleared queue [Size : {%s}, QueueAllowed : {%s}] ", queue.size(),
				queueAllowed.get());
	}

	/**
	 * Starts the Core Manager's Worker Thread
	 */
	public synchronized void startWorkerThread() {
		if (work.compareAndSet(false, true)) {
			queueAllowed.getAndSet(true);
			// Check if previous sender exists
			CoreWorkerThread old = wsMessageSender.get();
			if (old != null) {
				old.interrupt();
			}

			CoreWorkerThread newSender = new CoreWorkerThread();
			boolean success = wsMessageSender.compareAndSet(old, newSender);
			// If success then only start the new thread.
			if (success) {
				newSender.start();
			}

			log.infof(
					"CoreWorkerManager {New Started : [%s], Running : [%s], QueueAllowed : [%s]} initialized with thread ConcurrencyRequestDispatcher [%s]",
					success, work.get(), queueAllowed.get(), wsMessageSender.get());
		} else {
			log.error("Failed to start CoreWorkerManager", new RuntimeException(
					"Trying to create another Concurrency sender thread when already one is active"));
		}
	}

	/**
	 * Stops the Core Manager's Worker Thread
	 */
	public synchronized void stopWorkerThread() {
		log.info("Stopping CoreWorkerManager from sending concurrency requests to monitor server ");
		work.getAndSet(false);
		CoreWorkerThread thread = wsMessageSender.get();
		if (thread != null) {
			thread.interrupt();
		}
		boolean success = wsMessageSender.compareAndSet(thread, null);
		log.infof(
				"Stopped CoreWorkerManager[Running : {%s}, QueueAllowed : {%s}, Stopped : {%s} ] from sending concurrency requests to monitor server ",
				work.get(), queueAllowed.get(), success);
	}

	/**
	 * Adds the given Message to the thread queue.
	 * 
	 * @param coreMessage
	 *            - Message received from Call Layer Object.
	 * @return - Returns TRUE if added successfully, FALSE otherwise.
	 */
	public boolean addMessageRequest(DeviceData obj) {
		if (obj == null) {
			log.warn("WS Request is null, failed to send to MS", new RuntimeException("WS Request is null"));
			return false;
		}

		if (!work.get()) {
			log.debug("Failed to send WS Request as thread has stopped");
			return false;
		}

		boolean success = queue.add(obj);
		return success;
	}

	private static class CoreWorkerThread extends Thread {
		private static Logger log = Logger.getLogger(CoreWorkerThread.class);

		public CoreWorkerThread() {
			super("CoreWorkerThread");
			log.info("CoreWorkerThread initialized");
		}

		public void run() {
			DeviceData obj;
			log.info("CoreWorkerThread thread is started..." + work.get());
			while (work.get()) {
				try {
					if (queue.size() == 0) {
						obj = queue.poll(5, TimeUnit.SECONDS);
					} else {
						obj = queue.poll();
					}
					if (obj != null) {
						IBMVisionImpl impl = new IBMVisionImpl(obj);
						Thread current = new Thread(impl);
						current.start();
						/*
						 * JSONArray array = obj.getDeviceData(); JSONObject
						 * object; int i = 0; while((object =
						 * array.getJSONObject(i)) != null) {
						 * System.out.println(
						 * "Details of the json are deviceid:" +
						 * object.getString("deviceid") + " File name is " +
						 * object.getString("fileName") + " Time is " +
						 * object.getString("time")); i++; String result =
						 * FetchAndRunVisionAlgorithm(object); if (result !=
						 * null) {
						 * DBConnector.getInstance().addVisionResult(obj.
						 * getDeviceId(), getDate(object.getString("time")),
						 * object.getString("fileName"), result, 1); } }
						 * System.out.println("Out of the while loop");
						 */
					}
				} catch (Exception err) {
					log.warn("Exception while processing CoreWorkerThread object.", err);
				}
			}
		}

	}

}

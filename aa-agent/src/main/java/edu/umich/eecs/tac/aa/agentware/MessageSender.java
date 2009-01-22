package edu.umich.eecs.tac.aa.agentware;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.botbox.util.ArrayQueue;
import se.sics.tasim.aw.Message;

/**
 */
public class MessageSender extends Thread {

	private static final Logger log = Logger.getLogger(MessageSender.class
			.getName());

	private final ServerConnection connection;
	private ArrayQueue messageQueue = new ArrayQueue();
	private boolean isClosed = false;

	public MessageSender(ServerConnection connection, String name) {
		super(name);
		this.connection = connection;
		start();
	}

	public boolean isClosed() {
		return isClosed;
	}

	public synchronized void close() {
		if (!isClosed) {
			this.isClosed = true;
			messageQueue.clear();
			messageQueue.add(null);
			notify();
		}
	}

	public synchronized boolean addMessage(Message message) {
		if (isClosed) {
			return false;
		}
		messageQueue.add(message);
		notify();
		return true;
	}

	private synchronized Message nextMessage() {
		while (messageQueue.size() == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
		return (Message) messageQueue.remove(0);
	}

	// -------------------------------------------------------------------
	// Message sending handling
	// -------------------------------------------------------------------

	public void run() {
		do {
			Message msg = null;
			try {
				msg = nextMessage();
				if (msg != null) {
					connection.deliverMessage(msg);
				}

			} catch (ThreadDeath e) {
				log.log(Level.SEVERE, "message thread died", e);
				throw e;

			} catch (Throwable e) {
				log.log(Level.SEVERE, "could not handle message " + msg, e);
			}
		} while (!isClosed);
	}

} // MessageSender


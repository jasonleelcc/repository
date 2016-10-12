package iwin.jms.server;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author leotu@nec.com.tw
 */
public class JmsServiceLisenter {

	protected static final Logger log = LoggerFactory.getLogger(JmsServiceLisenter.class);

	// XXX: Collections.synchronizedList(new ArrayList<Connection>());
	// protected static List<Connection> connections = new Vector<Connection>();
	protected static List<Repository> connections = new CopyOnWriteArrayList<Repository>();

	public Repository bindListener(String queueName, MessageListener messageListener) throws Exception {
		InitialContext ic = new InitialContext();
		ConnectionFactory cf = (ConnectionFactory) ic.lookup("/ConnectionFactory");
		// log.debug("ConnectionFactory=" + cf);
		Connection connection = cf.createConnection();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue queue = (Queue) ic.lookup(queueName);
		MessageConsumer consumer = session.createConsumer(queue);
		consumer.setMessageListener(messageListener); // consumer.receive()
		//
		Repository repo = new Repository();
		repo.queueName = queueName;
		repo.connection = connection;
		repo.session = session;
		log.debug("repo=" + repo);
		connections.add(repo);
		//
		connection.start();
		return repo;
	}

	public static void stop(Repository repo) {
		log.debug("repo=" + repo);
		try {
			connections.remove(repo);
			repo.close();
		} catch (Exception e) {
			log.warn("repo=" + repo + ", " + e);
		}
	}

	public static void stop() {
		log.debug("connections.size=" + connections.size());
		for (Repository repo : connections.toArray(new Repository[0])) {
			stop(repo);
		}
		if (!connections.isEmpty()) {
			log.warn("(!connections.isEmpty()), connections.size=" + connections.size());
		}
	}

	public class Repository {
		protected String queueName;
		protected Connection connection;
		protected Session session;

		public void close() {
			try {
				if (session != null) {
					session.close();
				}
			} catch (Exception e) {
				log.warn(e.toString());
			} finally {
				session = null;
			}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				log.warn(e.toString());
			} finally {
				connection = null;
			}
		}

		@Override
		public String toString() {
			return "Repository [queueName=" + queueName + ", connection=" + connection + ", session=" + session + "]";
		}

	}
}

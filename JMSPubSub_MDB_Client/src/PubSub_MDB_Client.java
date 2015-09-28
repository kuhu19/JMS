import java.io.IOException;

import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/PubSub_MDB_Client")
public class PubSub_MDB_Client extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		System.out.println("--Entering doGet() method--");
		try {
			// Obtain a JNDI connection
			Context initialContext = new InitialContext();
			System.out.println("Initial Context created..");

			// Lookup a JMS Connection factory
			TopicConnectionFactory topicConnectionFactory = (TopicConnectionFactory) initialContext
					.lookup("jms/GFConnectionFactory");
			System.out.println("Lookup of Connection Factory complete..");

			// Looking up a JMS Topic
			Topic topic = (Topic) initialContext.lookup("topic/PJMDBTopic");
			System.out.println("Lookup of Topic done...");

			// Create a JMS connection
			TopicConnection topicConnection = topicConnectionFactory
					.createTopicConnection();
			System.out.println("Connection available...");

			// Creating Session object
			TopicSession topicSession = topicConnection.createTopicSession(
					false, Session.AUTO_ACKNOWLEDGE);
			System.out.println("Session is ready...");

			// Creating Sender
			TopicPublisher publisher = topicSession.createPublisher(topic);
			System.out.println("Sender is ready to send...");

			// Sending Message
			TextMessage msg = topicSession
					.createTextMessage("Hello from Pub Sub MDB Web Client/Servlet");
			publisher.publish(msg);
			System.out.println("Message sent..");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

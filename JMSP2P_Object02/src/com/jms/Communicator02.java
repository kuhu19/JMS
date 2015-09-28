package com.jms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Communicator02 implements MessageListener {
	public static final String QUEUE01 = "queue/Channel01";
	public static final String QUEUE02 = "queue/Channel02";
	
	@Override
	public void onMessage(Message message) {
		ObjectMessage objectMessage = (ObjectMessage) message;
		try{
			P2PApp app02 = (P2PApp) objectMessage.getObject();
			System.out.println("Sender: " + app02.getName());
			System.out.println("Message: " + app02.getMessage());
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws JMSException, NamingException, IOException {
		System.out.println("----Entering Communicator02----");

		// Obtain a JNDI connection
		Context initialContext = Communicator02.getInitialContext();
		System.out.println("Initial Context created..");

		// Lookup a JMS Connection factory
		QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) initialContext
				.lookup("jms/GFQueueConnectionFactory");
		System.out.println("Lookup of Connection Factory complete..");

		// Looking up a JMS Queue
		Queue queue01 = (Queue) initialContext.lookup(Communicator02.QUEUE01);
		Queue queue02 = (Queue) initialContext.lookup(Communicator02.QUEUE02);
		System.out.println("Lookup of Queue done...");

		// Create a JMS connection
		QueueConnection queueConnection = queueConnectionFactory
				.createQueueConnection();
		System.out.println("Connection available...");

		Communicator02 comm01 = new Communicator02();
		comm01.receiveMessage(queueConnection, queue01);
		comm01.sendMessage(queueConnection, queue02);

	}
	public void sendMessage(QueueConnection qConn, Queue q) throws JMSException, IOException {
		QueueSession queueSession = qConn.createQueueSession(false,
				Session.AUTO_ACKNOWLEDGE);
		System.out.println("Sending Session is ready...");
		QueueSender sender = queueSession.createSender(q);
		qConn.start();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Please enter username: ");
		String uname = br.readLine();
		while (true) {
			String message = br.readLine();
			if (message.equalsIgnoreCase("exit")) {
				qConn.close();
			} else {
				ObjectMessage objectMessage = queueSession
						.createObjectMessage();
				objectMessage.setObject(new P2PApp(uname, message));
				sender.send(objectMessage);
			}
		}

	}

	public void receiveMessage(QueueConnection qConn, Queue q) throws JMSException {
		// Creating Session object
		QueueSession queueSession = qConn.createQueueSession(false,
				Session.AUTO_ACKNOWLEDGE);
		System.out.println("Receving Session is ready...");
		QueueReceiver receiver = queueSession.createReceiver(q);
		receiver.setMessageListener(this);
	}

	
	// Specifying the JNDI properties specific to vendor
	public static Context getInitialContext() throws NamingException{
		Properties props = new Properties();
		props.setProperty("java.naming.factory.initial","com.sun.enterprise.naming.SerialInitContextFactory");
		props.setProperty("java.naming.factory.url.pkgs","com.sun.enterprise.naming");
		props.setProperty("java.naming.provider.url","http://localhost:4848");
		Context context = new InitialContext(props);
		return context;
	}
}

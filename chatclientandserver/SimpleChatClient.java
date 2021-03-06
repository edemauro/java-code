package NetworkingExample;

import java.io.*;
import java.net.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class SimpleChatClient {
	JTextArea incoming;
	JTextField outgoing;
	BufferedReader reader;
	PrintWriter writer;
	Socket sock;
	
	public void go(){
		// create GUI
		JFrame frame = new JFrame("Chat Client");
		JPanel mainPanel = new JPanel();
		incoming = new JTextArea(15,50);
		incoming.setLineWrap(true);
		incoming.setWrapStyleWord(true);
		incoming.setEditable(false);
		JScrollPane qScroller = new JScrollPane(incoming);
		qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		outgoing = new JTextField(20);
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(new SendButtonListener());
		mainPanel.add(qScroller);
		mainPanel.add(outgoing);
		mainPanel.add(sendButton);
		setUpNetworking();
		
		// Create thread to read incoming messages
		// start new thread
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
		
		// display GUI
		frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
		frame.setSize(600,500);
		frame.setVisible(true);
	} // close go method
	
	// connect to server
	// obtain input and output streams
	private void setUpNetworking(){
		try{
			sock = new Socket("127.0.0.1", 5000);
			InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
			reader = new BufferedReader(streamReader);
			writer = new PrintWriter(sock.getOutputStream());
			System.out.println("networking established.");
		} catch (IOException e){
			e.printStackTrace();
		}
	} // close setUpNetworking method
	
	// wait for user to click send button to send message to server
	// obtain message text from outgoing textfield
	public class SendButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent ev) {
			try{
				writer.println(outgoing.getText());
				writer.flush();
			} catch(Exception e) {
				e.printStackTrace();
			}
			outgoing.setText("");
			outgoing.requestFocus();
		}	
	} // close SendButtonListener inner class
	
	// read message from input stream
	// add message to incoming textarea
	public class IncomingReader implements Runnable{
		public void run(){
			String message;
			
			try{
				while((message = reader.readLine()) != null){
					System.out.println("read " + message);
					incoming.append(message + "\n");
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		} // close run
	} // close IncomingReader inner class
	
	public static void main(String[] args){
		SimpleChatClient client = new SimpleChatClient();
		client.go();
	} // close main
	
}// close SimpleChatClient class

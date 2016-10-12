package iwin.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ResourceBundle;
/*
 * @author TaiOne International Ltd. 
 * �q�l���Ħ�A���x�e�m�q�T�Ҳ�
 */
public class ClientAgent2 {
	private java.net.InetAddress address = null;
	private int port = 0;
	private java.net.Socket connection = null;
	private java.io.BufferedReader is = null;
	//private java.io.BufferedInputStream is = null;
	private java.io.PrintWriter os = null;
	private boolean log = false;
	private static java.lang.String logInName;
	private static java.lang.String logOutName;

	public ClientAgent2() {
		super();
		try {
			ResourceBundle msg = ResourceBundle.getBundle("ClientAgent");
			log = Boolean.valueOf(msg.getString("frontLog")).booleanValue();
			logInName = msg.getString("frontLogInName");
			logOutName = msg.getString("frontLogOutName");
		}catch(Exception e){
			log = false;
		}
	}
	/**	 
	 * ��w��s�u���D��IP �� Port
	 * @param  add java.net.InetAddress�G�s�u���D��IP
	 * @param    p java.lang.Integer�G�s�u���D��Port
	 */
	public ClientAgent2(InetAddress add, int port) {
		super();
		this.address = add;
		this.port = port;
		try {
			//ResourceBundle msg = ResourceBundle.getBundle("ClientAgent");
			//log = Boolean.valueOf(msg.getString("frontLog")).booleanValue();
			//logInName = msg.getString("frontLogInName");
			//logOutName = msg.getString("frontLogOutName");
		}catch(Exception e){
			log = false;
		}
	}
	
	/**	 
	 * �P�D���s�u
	 */
	public void connect() throws java.io.IOException {
		connection = new Socket(address, port);
		os =
			new PrintWriter(
				new OutputStreamWriter(connection.getOutputStream()),
				true);
		is = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		//is = new BufferedInputStream(connection.getInputStream());
	}
	/**	 
	 * �P�D�����_�s�u
	 */

	public void disconnect() throws java.io.IOException {
		is.close();
		os.close();
		connection.close();
		connection = null;
		is = null;
		os = null;
	}
	/**
	 * ���ԥD���^��
	 * @return java.lang.String
	 * @exception java.io.IOException
	 */

	public String retrieve() throws java.io.IOException {
		//byte[] ba = new byte[4];
		//int len = is.read(ba, 0, 4);
		//int readLen = Integer.parseInt(new String(ba));
		//byte[] baRead = new byte[readLen];
		//len = is.read(baRead, 0, readLen);
		//String xmlString = new String(baRead);
		String xmlString = is.readLine();
		if(log){
			//EasyLog.toLog(logInName, xmlString);
		}
		return xmlString;
	}

	/**
	 * �ǰeData���D��
	 * @return java.lang.String
	 * @exception java.io.IOException
	 */
	public void send(String xmlString) throws java.io.IOException {
		os.println(xmlString);
		if(log){
			//EasyLog.toLog(logOutName, xmlString);
		}
		
	}
	/**
	 * ��w��s�u���D��IP �� Port �ǰe Data�A�����ԥD���^�Ǹ��
	 * @param  add java.net.InetAddress�G�s�u���D��IP
	 * @param    p java.lang.Integer�G�s�u���D��Port
	 * @param data java.lang.Integer�G�ǵ��D����Data�D��
	 * @exception java.io.IOException
	 */
	public void sendOnce(InetAddress add, int p, String data)
		throws java.io.IOException {
		address = add;
		port = p;
		connect();
		send(data);
		disconnect();
	}
	/**
	 * ��w��s�u���D��IP �� Port �ǰe Data�A�õ��ԥD���^�Ǹ��
	 * @return java.lang.String
	 * @param  add java.net.InetAddress�G�s�u���D��IP
	 * @param    p java.lang.Integer�G�s�u���D��Port
	 * @param data java.lang.Integer�G�ǵ��D����Data
	 * @exception java.io.IOException
	 */
	public String sendMsg(InetAddress add, int p, String data)
		throws java.io.IOException {
		address = add;
		port = p;
		connect();
		send(data);
		String msg = "";
		msg = retrieve();
		disconnect();
		return msg;
	}

/**
 * Insert the method's description here.
 * Creation date: (2004/12/1 �W�� 10:54:31)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	
	//String tim = "<?xml version=\"1.0\" encoding=\"Big5\"?><RqXMLData>	<Header>		<ClientDtTm pic=\"14\">20041215110547</ClientDtTm>		<FrnName pic=\"3\">NP</FrnName>		<FrnMsgID pic=\"60\">050271859232</FrnMsgID>		<SvcType pic=\"12\">BalInqRq</SvcType>		<Encoding pic=\"16\">Big5</Encoding>		<Language pic=\"10\">zh_TW</Language>	</Header>	<AuthData>		<CustPermId pic=\"32\">A123640067</CustPermId>		<CustLoginId pic=\"32\">eric</CustLoginId>		<CustLoginType pic=\"1\">4</CustLoginType>		<CustPswd pic=\"16\"></CustPswd>		<SignonRole pic=\"8\">Agent</SignonRole>	</AuthData>	<Text>		<AcctId pic=\"14\">985004777776</AcctId>	</Text></RqXMLData>";
	String tim = "http://testsite.moneydj.com/w/ubot.djxml?A=1";
	//String tim = "time";
	String tom = null;
	//String server = "192.168.0.127";
		try{
			//ClientAgent client = new ClientAgent(InetAddress.getByName(server), 6002);
			//client.connect();

//			for(int i=0; i<10; i++){
				ClientAgent2 client = new ClientAgent2(InetAddress.getLocalHost(), 6001);
				client.connect();
				System.out.println( "Send:"+tim);
				client.send(tim + "\n");
//				Thread.sleep(5000);
				tom = client.retrieve();
				System.out.println( "Receive:" + tom);
				client.disconnect();
//			}
			//client.disconnect();
			/*
			Thread.sleep(5000);
			for(int i=0; i<100; i++){
				System.out.println(i + "Client"+tim);
				client.send(tim + "\n");
				tom = client.retrieve();
				System.out.println(i + "Client" + tom);
				//client.disconnect();
			}
			*/
		}catch(Exception e){System.out.println(e);}
}	
}
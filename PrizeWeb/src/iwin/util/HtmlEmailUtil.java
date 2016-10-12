package iwin.util;

import iwin.log.DiagnoseLogFactory;

import java.io.*;
import java.util.Map;

import javax.mail.internet.MimeUtility;
import javax.swing.JOptionPane;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.mail.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

/**
 * @update 20130704 ynag
 */
public class HtmlEmailUtil {
	protected static final Logger log = LoggerFactory.getLogger(HtmlEmailUtil.class);
	protected static final Logger toEai = DiagnoseLogFactory.getToEaiLogger();
	
	public static String MAIL_SMTP_HOST = "10.10.2.142";
	public static String MAIL_SMTP_USER = "";
	public static String MAIL_SMTP_PASSWORD = "";

    
	public static void sendMail(Map map){
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", MAIL_SMTP_HOST);
		

		Session session = Session.getDefaultInstance(properties);
		
		try {

	        StringBuilder sb = new StringBuilder(1024 * 1024);
	        
	        
	        sb.append("姓名：").append(map.get("cName")).append("<br>");
	        sb.append("性別：").append(map.get("sex")).append("<br>");
	        sb.append("年齡：").append(map.get("age")).append("<br>");
	        sb.append("聯絡電話：").append(map.get("tel")).append("<br>");
	        sb.append("電子信箱：").append(map.get("email")).append("<br>");
	        sb.append("聯絡地址：").append(map.get("address")).append("<br>");
	        sb.append("客戶留言：<br>").append(map.get("message"));
	        
	        
			HtmlEmail email = new HtmlEmail();
			
	        email.setHostName(MAIL_SMTP_HOST);
	       	        
	        email.setFrom("mbadmin@ubot.com.tw");
	        email.setCharset("UTF-8");
	        email.setSubject("線上留言板");

		        
	        email.setContent(sb.toString(), "text/html");
        
	        if ("信用卡服務".equals(map.get("service").toString())){
	            email.addTo("cardcsr@cservice.ubot.com.tw");   
	            sb.append("cardcsr@cservice.ubot.com.tw <br>");
	            sb.append(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")).append("<br>");
				toEai.trace(sb.toString()); // TOOD
	        }
	        else {
	            email.addTo("bankmail@cservice.ubot.com.tw");    
	            sb.append("bankmail@cservice.ubot.com.tw <br>");
	            sb.append(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS")).append("<br>");
				toEai.trace(sb.toString()); // TOOD
	        }
	       
	        email.send();
    
		} catch (Exception e){

			log.warn(e.toString());
		} 
        
	}


}

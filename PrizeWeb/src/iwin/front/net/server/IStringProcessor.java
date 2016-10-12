package iwin.front.net.server;

/**
 * 
 * @author leotu@nec.com.tw
 */
public interface IStringProcessor {

	/**
	 * Socket data been processed here !
	 * 
	 * @param input
	 * @return output
	 */
	public String handleRequest(String input);
}

package iwin.exception;

/**
 * taidi (20120702 已上正式)
 * @author frances.cheng
 * 若要變更這個產生的類別註解的範本，請移至
 * 視窗 > 喜好設定 > Java > 程式碼產生 > 程式碼和註解
 * @update 20120907 taidi
 */
public class HexaException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	private String function;
	private Exception e;

	public HexaException() {
		super();
	}
	
	public HexaException(Exception e) {
		super(e);
		this.e = e;
	}

	public void setFunction(String functionName){
		this.function = functionName;
	}
	
	public void setException(Exception e){
		this.e = e;
	}
	
	public String getFunction(){
		return "Error Occurred in [" + this.function + "]";
	}
	
	public Exception getException(){
		return this.e;
	}
	
	public String getError(){
		String retStr = "{";
		retStr = retStr + (this.function == null ? "": (this.getFunction() + "\n"));
		retStr = retStr + this.getException()+"}";
		return retStr;
	}
}

package id2212.homework3.jdbc.exception;

@SuppressWarnings("serial")
public class RejectedException extends Exception {

	public RejectedException(){
	}
	
	public RejectedException(String msg){
		super(msg);
	}
}

package id2212.homework3.jdbc.exception;

public class DBException extends Exception {
	public DBException(){
		super("Could not create database connection. Either unknown dbms or datasource");
	}
	public DBException(String msg){
		super(msg);
	}
}

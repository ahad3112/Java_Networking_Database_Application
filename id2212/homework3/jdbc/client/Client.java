package id2212.homework3.jdbc.client;

import id2212.homework3.jdbc.bankServer.Account;
import id2212.homework3.jdbc.bankServer.Bank;
import id2212.homework3.jdbc.marketServer.MarketInterface;

/*
 * this is the main class for the client
 * @author Muhammed Ahad
 * e-mail: ahad3112@yahoo.com
 */


public class Client {
	public static final int REGISTRY_PORT_NUMBER = 1099;
	public static final String HOST = "localhost";
	
	public static final String DEFAULE_BANK_NAME = "Nordea";
	public static final String DEFAULE_MARKET_NAME = "MarketPlace";
	public static final String USAGE = "";

	Account account;
	public static Bank bankObject;
	public static MarketInterface market;
	
	public static String bankName;
	public static String marketName;
	private String clientName;
	
	// Statis Userinterface object;
	public static UserInterface gui;
	
	// main method
	public static void main(String[] args){
		System.out.println("****************************************");
		System.out.println("*             Client                   *");
		System.out.println("****************************************");
		// get the bank name and the market name
		bankName = null;
		marketName = null;
		if(args.length>1){
			bankName = args[0];
			marketName = args[1];
		}else{
			bankName = DEFAULE_BANK_NAME;
			marketName = DEFAULE_MARKET_NAME;
		}
		
		gui = new UserInterface();
		gui.runGUI();
	}
	
}

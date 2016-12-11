package id2212.homework3.jdbc.marketServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import id2212.homework3.jdbc.bankServer.Bank;
import id2212.homework3.jdbc.client.Client;

/*
 * this is the MarketPlace Server
 */

public class MarketPlace {
	public static final int REGISTRY_PORT_NUMBER = 1099;
	public static final String HOST = "localhost";
	
	public static final String DEFAULT_MARKET_NAME = "MarketPlace";
	public static final String USAGE = "";
	
	public static final String DEFAULE_BANK_NAME = "Nordea";
	
	public static final String bankName = DEFAULE_BANK_NAME;
	public static Bank bankObject;
	
	// variables related to database dribers(DBMS) datasource
	public static final String dbms = "mysql";
	public static final String datasource = "jdbc:mysql://localhost:3306/mysql";
	
	
	// Market place need access to the bank as well
	public MarketPlace(String marketName){
		try{
			try{
				LocateRegistry.getRegistry(REGISTRY_PORT_NUMBER).list();
			}catch(RemoteException rex){
				LocateRegistry.createRegistry(REGISTRY_PORT_NUMBER);
			}
			Naming.rebind("rmi://"+HOST+":"+REGISTRY_PORT_NUMBER+"/"+marketName, new MarketImplementation(marketName,dbms,datasource));
			System.out.println("Marketplace Server has bound MarketPlace object to the naming service...");
			
		}catch(RemoteException rex){
			rex.printStackTrace();
		}catch(MalformedURLException urlex){
			urlex.printStackTrace();
		}
	}
	
	// main method 
	public static void main(String[] args){
		System.out.println("****************************************");
		System.out.println("*             MarketPlace              *");
		System.out.println("****************************************");
		String marketName = null;
		if(args.length>0){
			marketName = args[0];
		} else{
			marketName = DEFAULT_MARKET_NAME;
		}
		
		new MarketPlace(marketName);
		// get the bankObject
		getBankObject();
	}
	
	// this methos get the bankObject
	public static void getBankObject(){
		try{
			bankObject = (Bank) Naming.lookup("rmi://"+Client.HOST+":"+REGISTRY_PORT_NUMBER+"/"+bankName);
		}catch(Exception e){
			System.out.println("Runtime failed: "+e.getMessage());
			System.exit(1);
		}
		System.out.println("MarketPlace Server Connected to Bank: "+bankName);
	}
	
}

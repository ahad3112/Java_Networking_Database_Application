package id2212.homework3.jdbc.bankServer;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class BankServer {
	public static final int REGISTRY_PORT_NUMBER = 1099;
	public static final String HOST = "localhost";
	
	public static final String DEFAULT_BANK_NAME = "Nordea";
	public static final String USAGE = "";
	
	public BankServer(String bankName){
		try{
			try{
				LocateRegistry.getRegistry(REGISTRY_PORT_NUMBER).list();
			}catch(RemoteException rex){
				LocateRegistry.createRegistry(REGISTRY_PORT_NUMBER);
			}
			Naming.rebind("rmi://"+HOST+":"+REGISTRY_PORT_NUMBER+"/"+bankName, new BankImplementation(bankName));
			
		}catch(RemoteException rex){
			rex.printStackTrace();
		}catch(MalformedURLException urlex){
			urlex.printStackTrace();
		}
	}
	
	// main method 
	public static void main(String[] args){
		System.out.println("****************************************");
		System.out.println("*                Bank                  *");
		System.out.println("****************************************");
		String bankName = null;
		if(args.length>0){
			bankName = args[0];
		} else{
			bankName = DEFAULT_BANK_NAME;
		}
		
		new BankServer(bankName);
	}
}

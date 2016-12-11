package id2212.homework3.jdbc.bankServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import id2212.homework3.jdbc.exception.RejectedException;

@SuppressWarnings("serial")
public class BankImplementation extends UnicastRemoteObject implements Bank{
	private String bankName;
	private Map<String,Account> accounts = new HashMap<String,Account>();

	protected BankImplementation(String bankName) throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		this.bankName = bankName;
		System.out.println(bankName+" Bank has been created and Ready to roll on.");
	}

	@Override
	public synchronized Account newAccount(String name) throws RemoteException, RejectedException {
		// TODO Auto-generated method stub
		AccountImplementation account = (AccountImplementation) accounts.get(name);
		if(account != null){
			// the given name already got a account
			System.out.println("Account [ "+name+" ] already exists!!!");
			throw new RejectedException("Rejected by Bank: "+bankName+"\nAccount for: "+name+" already exists.");
		}
		account = new AccountImplementation(name);
		accounts.put(name, account);
		System.out.println("New account has been created: ");
		System.out.println("Bank: "+bankName+"\n"+account.accountDetails());
		return account;
	}

	@Override
	public synchronized Account getAccount(String name) throws RemoteException {
		// TODO Auto-generated method stub
		return accounts.get(name);
	}

	@Override
	public synchronized boolean deleteAccount(String name) throws RemoteException {
		// TODO Auto-generated method stub
		if(!hasAccount(name)){
			return false;
		}
		accounts.remove(name);
		System.out.println("Bank: "+bankName+" :Account has been deleted for "+name);
		return true;
	}

	
	@Override
	public synchronized String[] listAccounts() throws RemoteException {
		// TODO Auto-generated method stub
		return accounts.keySet().toArray(new String[1]);
	}
	
	@Override
	public boolean hasAccount(String name) throws RemoteException {
		// TODO Auto-generated method stub
		if(accounts.get(name) == null){
			return false;
		} else{
			return true;
		}
	}

}

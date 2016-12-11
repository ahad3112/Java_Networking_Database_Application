package id2212.homework3.jdbc.bankServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import id2212.homework3.jdbc.exception.RejectedException;

@SuppressWarnings("serial")
public class AccountImplementation extends UnicastRemoteObject implements Account{
	private String name;
	private float balance = 500.0f; // initial ammount for each acount holder
	protected AccountImplementation(String name) throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		this.name = name;
	}
	@Override
	public synchronized float getBalance() throws RemoteException {
		// TODO Auto-generated method stub
		return balance;
	}
	@Override
	public synchronized String withdraw(float value) throws RemoteException, RejectedException {
		// TODO Auto-generated method stub
		String message = null;
		if(value<0){
			message = "Rejected: Account "+name+" : Illegal value :"+value;
			throw new RejectedException(message);
		} else if((balance-value)<0){
			message = "Rejected: Account "+name+" : Negative balance on withdraw :"+(balance - value);
			throw new RejectedException(message);
		}
		balance -= value;
		message = "Transaction. Account: "+name+" : Withdraw: Sek "+value+" : balance: "+balance;
		System.out.println(message);
		return message;
	}
	@Override
	public synchronized String deposit(float value) throws RemoteException, RejectedException {
		// TODO Auto-generated method stub
		String message = null;
		if(value<0){
			message = "Rejected: Account "+name+" : Illegal value :"+value;
			throw new RejectedException("Rejected: Account "+name+" : Illegal value :"+value);
		}
		balance += value;
		message = "Transaction. Account: "+name+" : Deposit: Sek "+value+" : balance: "+balance;
		System.out.println(message);
		return message;
	}

	// to string method
	public synchronized String accountDetails(){
		return String.format("Account Holder: %s\nCurrent Balance: Sek %f", name,balance);
	}
	@Override
	public String getHolderName() throws RemoteException {
		// TODO Auto-generated method stub
		return name;
	}
}

package id2212.homework3.jdbc.bankServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

import id2212.homework3.jdbc.exception.RejectedException;

/*
 * this is the remote interface for the Account
 */

public interface Account extends Remote{
	public float getBalance() throws RemoteException;
	public String getHolderName() throws RemoteException;
	public String withdraw(float value) throws RemoteException, RejectedException;
	public String deposit(float value) throws RemoteException, RejectedException;
	public String accountDetails() throws RemoteException;
}

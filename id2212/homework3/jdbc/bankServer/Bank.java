package id2212.homework3.jdbc.bankServer;

/*
 * this is the remote interface for the Bank
 */

import java.rmi.Remote;
import java.rmi.RemoteException;

import id2212.homework3.jdbc.exception.RejectedException;

public interface Bank extends Remote{
	public Account newAccount(String name) throws RemoteException, RejectedException;
	public Account getAccount(String name) throws RemoteException;
	public boolean deleteAccount(String name) throws RemoteException;
	public boolean hasAccount(String name) throws RemoteException;
	public String[] listAccounts() throws RemoteException;
}

package id2212.homework3.jdbc.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * this is the interface for the Client
 */
public interface ClientInterface extends Remote{
	public String getID() throws RemoteException;
	public String getPassword() throws RemoteException;
	public void reveiveMsgFromMarket(String msg) throws RemoteException;
	public void reveiveMsgFromBank(String msg) throws RemoteException;
	public String updateMsgFromMarket() throws RemoteException;
	public String updateMsgFromBank() throws RemoteException;
}

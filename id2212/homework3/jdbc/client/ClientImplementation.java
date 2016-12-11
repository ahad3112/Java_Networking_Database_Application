package id2212.homework3.jdbc.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

@SuppressWarnings("serial")
public class ClientImplementation extends UnicastRemoteObject implements ClientInterface{
	private String clientName;
	private String password;
	public String messageFromMarket = "";
	public String messageFromBank = "";
	
	public ClientImplementation(String clientName) throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		this.clientName = clientName;
		System.out.println("Client: "+this.clientName+" has ben created.");
	}

	// this method reset the password of the client
	public void setPassword(String password){
		this.password = password;
	}
	
	@Override
	public synchronized String getID() throws RemoteException {
		// TODO Auto-generated method stub
		return clientName;
	}

	@Override
	public String getPassword() throws RemoteException {
		// TODO Auto-generated method stub
		return password;
	}
	@Override
	public synchronized void reveiveMsgFromMarket(String msg) throws RemoteException {
		// TODO Auto-generated method stub
		messageFromMarket = msg+"\n"+messageFromMarket;
		System.out.println("New message from Market: "+msg);
	}


	@Override
	public synchronized void reveiveMsgFromBank(String msg) throws RemoteException {
		// TODO Auto-generated method stub
		messageFromBank = msg+"\n"+messageFromBank;
		System.out.println("New message from Bank: "+msg);
	}
	
	// to string method
	public synchronized String toString(){
		return String.format("Client: ", clientName);
	}

	@Override
	public synchronized String updateMsgFromMarket() throws RemoteException {
		// TODO Auto-generated method stub
		return messageFromMarket;
	}

	@Override
	public synchronized String updateMsgFromBank() throws RemoteException {
		// TODO Auto-generated method stub
		return messageFromBank;
	}


}

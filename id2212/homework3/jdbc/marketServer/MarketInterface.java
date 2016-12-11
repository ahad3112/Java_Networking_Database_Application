package id2212.homework3.jdbc.marketServer;

import java.rmi.Remote;
import java.rmi.RemoteException;
/*
 * this is the interface for the market
 */

import id2212.homework3.jdbc.client.ClientInterface;
import id2212.homework3.jdbc.exception.RejectedException;

public interface MarketInterface extends Remote{
	public void registerClient(ClientInterface client) throws RemoteException, RejectedException;
	public boolean unregisterClient(ClientInterface client) throws RemoteException, RejectedException;
	public boolean isRegisteredInCache(ClientInterface client) throws RemoteException;
	public void loginClient(ClientInterface client) throws RemoteException, RejectedException;
	public void logoutClient(ClientInterface client) throws RemoteException, RejectedException;
	public String checkClientActivity(ClientInterface client) throws RemoteException, RejectedException;
	public String toSell(ClientInterface client,String productName,float price, int productAmount) throws RemoteException;
	public String toBuy(ClientInterface client, String productID, int amount) throws RemoteException;
	public String[] getProductsList() throws RemoteException;
	public String toWish(ClientInterface client,String productName,float price,int productAmount) throws RemoteException;
	public String[] getWishList() throws RemoteException;
	public ProductInterface getProduct(String productID) throws RemoteException;
	public ClientInterface getClient(String clientName) throws RemoteException;
}

package id2212.homework3.jdbc.marketServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ProductInterface extends Remote{
	public String getClientName() throws RemoteException;
	public String getProductName() throws RemoteException;
	public float getProductPrice() throws RemoteException;
	public void updateProductAmount(int amount) throws RemoteException;
	public int getProductAmount() throws RemoteException;
	public String getProductID() throws RemoteException;

}

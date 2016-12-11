package id2212.homework3.jdbc.marketServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

@SuppressWarnings("serial")
public class Product extends UnicastRemoteObject implements ProductInterface{
	private String clientName;
	private String productName;
	private float price;
	private int productAmount;
	// A static variable to control the ID of a item
	private static int idCounter = 	0;
	private String productID;
	protected Product(String clientName,String productName, float price, int productAmount,String sellOrWish) throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		this.clientName = clientName;
		this.productName = productName;
		this.price = price;
		this.productAmount = productAmount;
		// product unique ID
		//productID = "PID"+idCounter;
		// unique id
		productID = sellOrWish+createUniqueID();
		idCounter++;
	}

	protected Product(String productID,String clientName,String productName, float price, int productAmount) throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		this.productID = productID;
		this.clientName = clientName;
		this.productName = productName;
		this.price = price;
		this.productAmount = productAmount;
	}
	
	@Override
	public synchronized String getProductName() throws RemoteException {
		// TODO Auto-generated method stub
		return productName;
	}

	@Override
	public synchronized float getProductPrice() throws RemoteException {
		// TODO Auto-generated method stub
		return price;
	}
	@Override
	public synchronized int getProductAmount() throws RemoteException {
		// TODO Auto-generated method stub
		return productAmount;
	}
	@Override
	public synchronized String getClientName() throws RemoteException {
		// TODO Auto-generated method stub
		return clientName;
	}

	@Override
	public synchronized String getProductID() throws RemoteException {
		// TODO Auto-generated method stub
		return productID;
	}

	@Override
	public void updateProductAmount(int amount) throws RemoteException {
		// TODO Auto-generated method stub
		this.productAmount -= amount;
	}

	// this method create a random product id
	private String createUniqueID(){
		String s = "";
        double d;
        for (int i = 1; i <= 16; i++) {
            d = Math.random() * 10;
            s = s + ((int)d);
            if (i % 4 == 0 && i != 16) {
                s = s + "-";
            }
        }
        return s;
	}
}

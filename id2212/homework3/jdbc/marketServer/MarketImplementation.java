package id2212.homework3.jdbc.marketServer;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import id2212.homework3.jdbc.client.ClientInterface;
import id2212.homework3.jdbc.exception.DBException;
import id2212.homework3.jdbc.exception.RejectedException;

@SuppressWarnings("serial")
public class MarketImplementation extends UnicastRemoteObject implements MarketInterface{
	private String marketName;
	// list of client
	private Map<String,ClientInterface> clientsList = new HashMap<String,ClientInterface>();
	
	// Item list should not use hashmap as hashmap does not make duplicate key 
	// list of Item
	private Map<String,ProductInterface> productsList = new HashMap<String,ProductInterface>();
	// list of wish
	private Map<String,ProductInterface> wishList = new HashMap<String,ProductInterface>();
	
	// Market database. Database object is transient. (Not sure) It is not included when serialized MarketPlace is sent to Client
	private transient MarketDatabase marketDBO;
	public MarketImplementation(String marketName, String dbms, String datasource) throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
		this.marketName = marketName;
		System.out.println(this.marketName+" has been Initialized...");
		// create Market database
		marketDBO = new MarketDatabase(dbms,datasource);
		
	}

	@Override
	public void loginClient(ClientInterface client) throws RemoteException, RejectedException {
		// TODO Auto-generated method stub
		try {
			if(marketDBO.getUserDatabase().passwordVerified(client)){
				// Password varified. add the client to the online client list
				clientsList.put(client.getID(), client);
				System.out.println("Client: "+client.getID()+" added to the server client cache.");
			} else {
				throw new RejectedException("Wrong password.");
			}
		} catch (DBException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage()+" Problem in loginClient in MarketImplementation...");
			throw new RejectedException(e.getMessage());
		}
		
	}
	
	@Override
	public void logoutClient(ClientInterface client) throws RemoteException, RejectedException {
		// TODO Auto-generated method stub
		if(!isRegisteredInCache(client)){
			throw new RejectedException("Why user offline when he/she is logged in?????? Check loginClient() method in MarketImplementation.");
		}
		// don't need to remove from the clients list....use database in buy then remove .Comment this ****
		clientsList.remove(client.getID());
		
		
		System.out.println("Client: "+client.getID()+" logged out from the Market.");
		
	}
	
	@Override
	public synchronized void registerClient(ClientInterface client) throws RemoteException, RejectedException {
		// TODO Auto-generated method stub
		// if client is registered in the database, put the client to the clientsList and throws DBExeption
		try {
			if(clientsList.get(client.getID()) != null){
				System.out.println(client.getID()+" already registered!!!");
				throw new RejectedException("Rejected: "+client.getID()+" is already registered");
			}
			
			if(marketDBO.getUserDatabase().isRegisteredInDatabase(client.getID())){
				//clientsList.put(client.getID(), client);
				System.out.println(client.getID()+" already registered!!!");
				throw new RejectedException("A user already registered in database with name "+client.getID()+". Added to the Online user list.");
			} 
			
			clientsList.put(client.getID(), client);
			// add data of the client to the database
			marketDBO.getUserDatabase().registerInDatabase(client);
			// printing the all registered users
			marketDBO.getUserDatabase().selectAll();
			System.out.println("Registered new client: "+client.getID());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	
	// this method check whether a client is already registered  in the cache
	public boolean isRegisteredInCache(ClientInterface client) throws RemoteException{
		if(clientsList.get(client.getID()) != null){
			return true;
		} else {
			return false;
		}
	}

	@Override
	public synchronized boolean unregisterClient(ClientInterface client) throws RemoteException, RejectedException {
		// TODO Auto-generated method stub
		if(!isRegisteredInCache(client)){
			throw new RejectedException("No user to be unregister");
		}
		clientsList.remove(client.getID());
		// remove from database as well
		try {
			marketDBO.getUserDatabase().deleteUserFromDatabase(client.getID());
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.getMessage();
		}
		System.out.println("Unregistered client: "+client.getID()+" from the Market.");
		return true;
	}

	@Override
	public synchronized String toSell(ClientInterface client, String productName, float price,int productAmount) throws RemoteException {
		// TODO Auto-generated method stub
		String sellStatus = null;
		if(isRegisteredInCache(client)){
			// last argument says this product is for sell, used it product unique ID
			Product newProduct = new Product(client.getID(),productName,price,productAmount,"s");
			productsList.put(newProduct.getProductID(), newProduct);
			// adding product to the database
			if(marketDBO.getProductDatabase() == null){
				System.out.println("Product database is nulll");
			}
			
			try {
				marketDBO.getProductDatabase().registerProductInDatabase(newProduct);
			} catch (DBException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
			
			sellStatus = "Product added: Seller: "+newProduct.getClientName()+", ProductID: "+newProduct.getProductID()+", Product name: "
					+newProduct.getProductName()+", Price: "+newProduct.getProductPrice()+" Amount: "+newProduct.getProductAmount();
			
			// checking with the wish list and send a call-back if any match found
			checkWishListAndCallBack(newProduct);
		} else{
			sellStatus = "Product cann't be added!!! Seller: "+client.getID()+" is not registered to the market Place. Register first!!!";
		}
		System.out.println(sellStatus);
		return sellStatus;
	}
	// this method check the current product with the wish list
	private synchronized void checkWishListAndCallBack(Product currentP) throws RemoteException{
		ProductInterface[] wishProduct = wishList.values().toArray(new ProductInterface[1]);
		
		// we do compare if wishlist got at least one product. every wisher will get a call back
		// if condition make sure that wishProduct got at least one product as it's length will be minimum 1
		if(wishProduct[0] != null){
			for(ProductInterface p: wishProduct){
				try {
					if(currentP.getProductName().equals(p.getProductName()) && currentP.getProductPrice() <= p.getProductPrice()){
						// a match found. call back wisher
						String wisher = p.getClientName();
						String messageToWisher = "Your wish is available: "+currentP.getProductID()+" "+currentP.getProductName()+" "+currentP.getProductPrice();
						clientsList.get(wisher).reveiveMsgFromMarket(messageToWisher);
						
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public synchronized String toBuy(ClientInterface client, String productID, int amount) throws RemoteException {
		// TODO Auto-generated method stub
		String buyStatus = null;
		
		// need to check whether both seller and buyer both have accounts
		if(isRegisteredInCache(client) && MarketPlace.bankObject.hasAccount(client.getID())){
			// **** OPTION 1 ***: 	refresh productList cache before checinkg whether client's chosen product is available
			// updateProductsList();
			// can buy only if client is registered to the market
			Product currentP = (Product)productsList.get(productID);
			
			// OPTION 2: 			// checking in the database as well.  comment updatep roductsList
			if(currentP == null){
				// check in the database
				try {
					currentP = marketDBO.getProductDatabase().getProductFromDatabase(productID);
					if(currentP == null){
						// productList included all product to sell in the database.
						buyStatus = "Rejected: This product is not available.";
						return buyStatus;
					}
				} catch (DBException e) {
					// TODO Auto-generated catch block
					buyStatus = e.getMessage();
				}
			}
			
			
			if(amount <= 0){
				buyStatus = "Rejected: Choose at least 1 item.";
				return buyStatus;
			}
			
			if(currentP.getProductAmount()<amount){
				buyStatus = "Rejected: There is only "+currentP.getProductAmount()+" "+currentP.getProductName()+" available";
				return buyStatus;
			}
			
			
			String buyer = client.getID();
			String seller = currentP.getClientName();
			float price = currentP.getProductPrice();
			
			if(MarketPlace.bankObject.hasAccount(seller)){
				// remove the product from the products list
				if(seller.equals(buyer)){
					buyStatus = "Can not buy this product!!! Buyer and seller are same person: "+seller;
				} else{
					
					try {
						/****************************************************************/
						// sending message can be done in the bank as well.
						// Next time this can be edited...... nothing important.**********
						/****************************************************************/
						
						String messageFromBank = null;
						messageFromBank = MarketPlace.bankObject.getAccount(buyer).withdraw(price*amount);
						clientsList.get(buyer).reveiveMsgFromBank(messageFromBank); // call-back
						// send message to both seller and buyer from market. Call-back
						clientsList.get(buyer).reveiveMsgFromMarket("Your bought the product "+currentP.getProductName()+".");
						
						messageFromBank = MarketPlace.bankObject.getAccount(seller).deposit(price*amount);
						if(clientsList.get(seller) != null){
							clientsList.get(seller).reveiveMsgFromBank(messageFromBank); // call-back
							clientsList.get(seller).reveiveMsgFromMarket("Your product "+currentP.getProductName()+" has been sold.");
							buyStatus = "Sold: "+amount+" "+currentP.getProductName()+" to "+buyer+". Seller "+seller;
						} else {
							// create the seller add it to the clientList and send message
							buyStatus = "Seller is offline. Could not send message. No database for messages.\n"+"Sold: "+amount+" "+currentP.getProductName()+" to "+buyer+". Seller "+seller;
						}
						

						// remove the sold item
						// if no items available after selling, remove the product else decrease by amount
						if((currentP.getProductAmount()-amount) == 0){
							productsList.remove(productID);
							// remove from database
							try {
								marketDBO.getProductDatabase().deleteProductFromDatabase(productID);
								// remove product if bought item was wish. Need another database for wishlist
								
							} catch (DBException e) {
								// TODO Auto-generated catch block
								System.out.println(e.getMessage());
							}
							
						} else if((currentP.getProductAmount()-amount) > 0){
							// reduce amount in the producs list
							currentP.updateProductAmount(amount);
							productsList.remove(productID);
							productsList.put(currentP.getProductID(),currentP);
							// update in database
							try {
								marketDBO.getProductDatabase().updateProductInDatabase(productID, amount);
								// remove product if bought item was wish
							} catch (DBException e) {
								// TODO Auto-generated catch block
								System.out.println(e.getMessage());
							}
							
						} else{
							buyStatus = "Rejected Negative amount after sell.Rejected: There is only "+currentP.getProductAmount()+" "+currentP.getProductName()+" available. Check method toBuy().";
							return buyStatus;
						}
						
						// update activity of user
						try {
							marketDBO.getUserDatabase().updateUserActivity(buyer, 0, amount);
							marketDBO.getUserDatabase().updateUserActivity(seller, amount, 0);
						} catch (DBException e) {
							// TODO Auto-generated catch block
							System.out.println(e.getMessage());
							e.getMessage();
						}
						
					} catch (RejectedException e) {
						buyStatus = "Rejected!!! Seller: "+buyer+" does not have enough money";
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			} else{
				buyStatus = "Rejected!!! Seller: "+seller+" does not have a bank account.";
			}
			
		} else{
			buyStatus = client.getID()+" can not buy product: "+" Need to be registered to the Market place as well as a Bank account.";
		}
		
		System.out.println(buyStatus);
		return buyStatus;
	}
	
	// this is not method from interface
	public synchronized void updateProductsList() throws RemoteException {
		// TODO Auto-generated method stub
		// Get the list of all available products in the database. If it is not in the productlist, add it to the list
		ResultSet result = marketDBO.getProductDatabase().getProductsListFromDatabase();
		// result table: column 1= PID, Column 2 = product name, column 3 = price, column 4 = amount, column 5 = client/seller
		try{
			for(int i=1; result.next(); i++){
				String productID = result.getString(1);
				if(productID.startsWith("s") && productsList.get(productID) == null){
					// product is not in the productlist cache. add it to the list
					String productName = result.getString(2);
					Float productPrice = result.getFloat(3);
					int productAmount = result.getInt(4);
					String productClient = result.getString(5);
					productsList.put(productID, new Product(productID,productClient,productName,productPrice,productAmount));
				} 
			}
			result.close();
		} catch(SQLException sqlex){
			System.out.println("No product in the database. "+sqlex.getMessage());
		}
	}

	@Override
	public synchronized String toWish(ClientInterface client, String productName, float price,int productAmount) throws RemoteException {
		// TODO Auto-generated method stub
		String wishStatus = null;
		if(isRegisteredInCache(client)){
			
			// need to check in the data base whether the wish product is available
			if(marketDBO.getProductDatabase().getSpecificProductUsingName(productName, price)){
				// send message to wisher from market
				wishStatus = "Your wish product : "+productName+" is already available in the market. Not added to the wish list.";
				client.reveiveMsgFromMarket("Your wish product : "+productName+" is already available in the market.");
			} else{
				// last argument says the product is a wish
				Product newProduct = new Product(client.getID(),productName,price,productAmount,"w");
				// add to the server cache
				wishList.put(newProduct.getProductID(), newProduct);
				wishStatus = "Product added: Wish for: "+newProduct.getClientName()+", ProductID: "+newProduct.getProductID()+", Product name: "
						+newProduct.getProductName()+", Price: "+newProduct.getProductPrice();
			}
			
			// add to the data base for wish list product
//			try {
//				marketDBO.getProductDatabase().registerProductInDatabase(newProduct);
//			} catch (DBException e) {
//				// TODO Auto-generated catch block
//				System.out.println(e.getMessage());
//			}
			
		} else{
			wishStatus = "Product cann't be added: Client: "+client.getID()+"is not registered to the market Place. Register first!!!";
		}
		System.out.println(wishStatus);
		return wishStatus;
	}

	@Override
	public synchronized String[] getWishList() throws RemoteException {
		// TODO Auto-generated method stub
		return wishList.keySet().toArray(new String[1]);
	}

	@Override
	public synchronized ProductInterface getProduct(String productID) throws RemoteException {
		// TODO Auto-generated method stub
		// update products first
		updateProductsList();
		return productsList.get(productID);
	}

	@Override
	public String[] getProductsList() throws RemoteException{
		// update products list first
		updateProductsList();
		return productsList.keySet().toArray(new String[1]);
	}
	
	@Override
	public ClientInterface getClient(String clientName) throws RemoteException {
		// TODO Auto-generated method stub
		return clientsList.get(clientName);
	}

	@Override
	public String checkClientActivity(ClientInterface client) throws RemoteException, RejectedException {
		// TODO Auto-generated method stub
		String activity = null;
		if(!isRegisteredInCache(client)){
			throw new RejectedException("No user to show activities.");
		}
		try {
			activity = marketDBO.getUserDatabase().getUserActivity(client.getID());
		} catch (DBException e) {
			// TODO Auto-generated catch block
			activity = e.getMessage();
		}
		
		return activity;
	}
}

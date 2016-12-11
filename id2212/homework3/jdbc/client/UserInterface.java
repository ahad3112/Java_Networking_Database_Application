package id2212.homework3.jdbc.client;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import id2212.homework3.jdbc.bankServer.Bank;
import id2212.homework3.jdbc.exception.RejectedException;
import id2212.homework3.jdbc.marketServer.MarketInterface;
import id2212.homework3.jdbc.marketServer.ProductInterface;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/*
 * public void add(Node child,
                int columnIndex,
                int rowIndex,
                int colspan,
                int rowspan)
 */
public class UserInterface extends Application{
	// Remote object
	public Bank bankObject;
	public MarketInterface market;
	// cLIENT OBJECT
	public ClientImplementation clientObject;
	
	// this is the client name
	public String clientName;
	public String userPassword = "";
	public int passwordLength = 8;
	// Stage of the GUI
	private Stage stage;

	// variables related to gui
	Label user;
	TextField userName;
	Label password;
	Label status;
	PasswordField passwordField;
	Button logIn_logOut;
	
	Button newAccount;
	Button deleteAccount;
	Button accountDetails;
	TextArea bankStatus;
	
	Button register_unregister;
	TextArea marketStatus;
	Button sellProduct;
	TextField putSellProduct;
	Button wishProduct;
	TextField putWishProduct;
	Button buyProduct;
	TextField amount;
	ComboBox<String> availableProducts;
	
	Button inspectMarket;
	Button userActivity;;
	
	// message from market and bank
	Button bankReply;
	TextArea messageFromBank;
	Button marketReply;
	TextArea messageFromMarket;
	
	// css for button style
	String cssButton = "-fx-background-insets: 0,1,2,3;-fx-background-radius: 3,2,2,2;-fx-padding: 12 30 12 30;-fx-text-fill: white;"
			+ "-fx-font-size: 12px;-fx-background-color:#000000,linear-gradient(#7ebcea, #2f4b8f),linear-gradient(#426ab7, #263e75),linear-gradient(#395cab, #223768);";
	
	
	// constructor
	public UserInterface(){
		// get the bank and market server
		getBankObject();
		getMarketPlace();
	}
	// create the GUI
	public void createGUI(){
		// creating and setting main GridPane object
		GridPane mainGrid = createGridPane(new Label());
		
		GridPane initialGrid = createGridPane(new Label("Initialize"));
		initialGrid.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderWidths.DEFAULT)));
		
		user = new Label("username:");
		user.setStyle("-fx-text-fill: green; -fx-font-size: 16;");
		userName = new TextField();
		userName.setStyle("-fx-text-fill: green; -fx-font-size: 16;");
		
		password = new Label("password:");
		password.setStyle("-fx-text-fill: green; -fx-font-size: 16;");
		passwordField = new PasswordField();
		passwordField.setStyle("-fx-text-fill: green; -fx-font-size: 16;");
		status = new Label("Choose a name and password");
		
		logIn_logOut = new Button("login");
		logIn_logOut.setStyle(cssButton);

		initialGrid.add(user, 1, 0);
		initialGrid.add(userName, 2, 0);
		initialGrid.add(password,3, 0);
		initialGrid.add(passwordField, 4, 0);
		initialGrid.add(status, 2, 1);
		
		
		
		// Bank grid
		GridPane bankGrid = createGridPane(new Label("Bank"));
		bankGrid.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderWidths.DEFAULT)));
		bankGrid.setMaxWidth(150);
		Label sectionBank = new Label("BANK");
		sectionBank.setStyle("-fx-text-fill: green; -fx-font-size: 20;");
		newAccount = new Button("NewAccount");
		newAccount.setStyle(cssButton);
		//newAccount.setDisable(true);
		deleteAccount = new Button("DelAccount");
		//deleteAccount.setDisable(true);
		deleteAccount.setStyle(cssButton);
		accountDetails = new Button("AccountDetails");
		//accountDetails.setDisable(true);
		accountDetails.setStyle(cssButton);
		bankStatus = new TextArea("");
		bankStatus.setEditable(false);
		
		bankGrid.add(sectionBank,0,0);
		bankGrid.add(newAccount,0,1);
		bankGrid.add(deleteAccount,0,2);
		bankGrid.add(accountDetails,0,3);
		bankGrid.add(bankStatus,0,4);
		
		GridPane marketGrid = createGridPane(new Label("Market"));
		marketGrid.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderWidths.DEFAULT)));
		Label sectionMarket = new Label("MARKET");
		sectionMarket.setStyle("-fx-text-fill: green; -fx-font-size: 20;");
		register_unregister = new Button("Register");
		//register_unregister.setDisable(true);
		register_unregister.setStyle(cssButton);
		marketStatus = new TextArea("");
		marketStatus.setEditable(false);
		marketStatus.setPrefWidth(30);
		sellProduct = new Button("Sell");
		sellProduct.setPrefWidth(100);
		sellProduct.setStyle(cssButton);
		putSellProduct = new TextField("item price amount");
		putSellProduct.setPrefWidth(50);
		wishProduct = new Button("Wish");
		wishProduct.setPrefWidth(100);
		wishProduct.setStyle(cssButton);
		putWishProduct = new TextField("item price amount");
		putWishProduct.setPrefWidth(50);
		buyProduct = new Button("Buy");
		amount = new TextField();
		amount.setPrefWidth(20);
		buyProduct.setStyle(cssButton);
		availableProducts = new ComboBox<String>();
		availableProducts.setPrefWidth(350);
		availableProducts.setPromptText("Available Products");
		
		inspectMarket = new Button("InspectMarket");
		inspectMarket.setStyle(cssButton);
		userActivity = new Button("Activity");
		userActivity.setStyle(cssButton);
		
		marketGrid.add(sectionMarket, 0, 0);
		marketGrid.add(register_unregister, 0, 1);
		marketGrid.add(putSellProduct, 0, 2);
		marketGrid.add(sellProduct, 1, 2);
		marketGrid.add(putWishProduct, 0, 3);
		marketGrid.add(wishProduct, 1, 3);
		marketGrid.add(inspectMarket,0,4);
		marketGrid.add(availableProducts, 1, 4);
		marketGrid.add(amount, 2, 4);
		marketGrid.add(userActivity,0,6);
		marketGrid.add(marketStatus,1,6);
		marketGrid.add(buyProduct, 2, 6);
		
		
		GridPane messageGrid = createGridPane(new Label("Message"));
		messageGrid.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderWidths.DEFAULT)));
		
		
		Label sectionInbox = new Label("INBOX");
		sectionInbox.setStyle("-fx-text-fill: green; -fx-font-size: 20;");
		bankReply = new Button("FromBank");
		bankReply.setStyle(cssButton);
		messageFromBank = new TextArea();
		messageFromBank.setMaxWidth(400);
		marketReply = new Button("FromMarket");
		marketReply.setStyle(cssButton);
		messageFromMarket = new TextArea("");
		messageFromMarket.setMaxWidth(400);
		
		messageGrid.add(sectionInbox, 0, 0);
		messageGrid.add(bankReply, 0, 1);
		messageGrid.add(messageFromBank, 1, 1);
		messageGrid.add(marketReply, 0, 2);
		messageGrid.add(messageFromMarket, 1, 2);
		
		
		// set GUI to the defaul settings
		defaultGUI();
		
		// adding everything to the main grid
		mainGrid.add(initialGrid, 0, 0);
		mainGrid.add(logIn_logOut, 1, 0);
		mainGrid.add(marketGrid, 0, 1);
		mainGrid.add(bankGrid, 1, 1);
		mainGrid.add(messageGrid, 0, 2);
		
		// adding action HANDLER
		Handler handler = new Handler();
		userName.setOnAction(handler);
		newAccount.setOnAction(handler);
		accountDetails.setOnAction(handler);
		putSellProduct.setOnAction(handler);
		sellProduct.setOnAction(handler);
		putWishProduct.setOnAction(handler);
		wishProduct.setOnAction(handler);
		availableProducts.setOnAction(handler);
		buyProduct.setOnAction(handler);
		deleteAccount.setOnAction(handler);
		register_unregister.setOnAction(handler);
		inspectMarket.setOnAction(handler);
		bankReply.setOnAction(handler);
		marketReply.setOnAction(handler);
		logIn_logOut.setOnAction(handler);
		passwordField.setOnAction(handler);
		userActivity.setOnAction(handler);
		
		// Creating scene
		Scene scene = new Scene(mainGrid,800,800);
		stage.setScene(scene);
		stage.setTitle("Client");
		stage.setResizable(true);
		
		stage.show();
	}
	
	// this method create a GridPane object with a specific peoperties
	private GridPane createGridPane(Label title){
		GridPane grid = new GridPane();
        title.getStyleClass().add("bordered-titled-title");
       //grid.setAlignment(title, Pos.TOP_CENTER);
        grid.setAlignment(Pos.CENTER);
        //grid.setAlignment(Pos.TOP_LEFT);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(5,5,5,5));
		return grid;
	}
	
	// action event handler class
	private class Handler implements EventHandler<ActionEvent>{
		private String productName;
		private float productPrice;
		private int productAmount;

		@Override
		public void handle(ActionEvent event) {
			// get the user name
			if(event.getSource() == userName){
				// initialize client, Bank and Market
				clientName = userName.getText();
				// Get the client
				try{
					clientObject = new ClientImplementation(clientName);
				} catch(RemoteException rex){
					System.out.println("Runtime failed. "+rex.getMessage());
				}
				
				// Show the current status of the client in the bank and in the market
				bankStatus.setText("Client's Bank Details:\nNothing to show yet.");
				marketStatus.setText("Client's Market Details:\nNoting to show yet.\n"+marketStatus.getText());
			}
			
			if(event.getSource() == passwordField){
				userPassword = passwordField.getText();
				if(clientObject != null){
					clientObject.setPassword(userPassword);
					register_unregister.setDisable(false);
					logIn_logOut.setDisable(false);
				} else{
					status.setText("choose a user name");
				}	
			}
			
			// if both user name and password given, try to login or register
			if(event.getSource() == logIn_logOut){
				if(logIn_logOut.getText().equals("login")){
					// send data to server to login if username and password requirements are fullfilled
					if(clientObject != null){
						try {
							market.loginClient(clientObject);
							register_unregister.setText("Unregister");
							logIn_logOut.setText("logout");
							userName.setDisable(true);
							passwordField.setDisable(true);
							marketReply.setDisable(false);
							putSellProduct.setDisable(false);
							putWishProduct.setDisable(false);
							inspectMarket.setDisable(false);
							userActivity.setDisable(false);
							status.setText("logged in.");
							marketStatus.setText("Client's Market Status:\nLogged in.\n"+marketStatus.getText());
						} catch (RemoteException | RejectedException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							userName.setDisable(false);
							passwordField.setDisable(false);
							status.setText(e.getMessage());
							//status.setText("Wrong user name/password or not registered.");
							marketStatus.setText("Wrong username/password or unregistered."+"\nTry again."+"\n"+marketStatus.getText());
						}
					}

				} else{
					// client is not null ----should be guarrented
					try {
						market.logoutClient(clientObject);
						status.setText("Choose a name and password");
						marketStatus.setText("Client's Market Status:\nLogged out.\n"+marketStatus.getText());
						//logIn_logOut.setText("login");
						// need to reset GUI
						defaultGUI();
					} catch (RemoteException | RejectedException e) {
						// TODO Auto-generated catch block
						status.setText(e.getMessage());
						marketStatus.setText(e.getMessage()+"\n"+marketStatus.getText());
						//e.printStackTrace();
					}
				}
			}
			
			// get a new account
			if(event.getSource() == newAccount){
				if(clientObject != null){
					try {
						bankObject.newAccount(clientObject.getID());
						bankReply.setDisable(false);
						bankStatus.setText(clientObject.getID()+" got a Bank Acc.");
						//marketStatus.setText("You got Bank account\n"+marketStatus.getText());
						// listAccounts return an array of key string of accounts holder
						// we can use this key to get accesto any one's acccont.
						// looks like any one can update other's account.
						// fix it*************
						//System.out.println(bankObject.getAccount(bankObject.listAccounts()[bankObject.listAccounts().length-1]).accountDetails());
					} catch (RemoteException | RejectedException e) {
						bankStatus.setText(e.getMessage()+"\nTry with different name.");
						bankReply.setDisable(false);
						//bankStatus.setText(clientObject.getID()+" already got a Bank account.\nTry with different name.");
						userName.setDisable(false);
						//e.printStackTrace();
					}
				} else{
					bankStatus.setText("Client's Bank Status:\nNo client to register.Create a client first.");
				}
			}
			
			if(event.getSource() == accountDetails){
				// get details if there an account exists
				if(clientObject != null){
					try {
						if(bankObject.hasAccount(clientObject.getID())){
							try {
								bankStatus.setText("Client's Bank Details:\nBank: "+Client.bankName+"\nAcc Holder: "+clientObject.getID()
								+"\nBalance: "+bankObject.getAccount(clientObject.getID()).getBalance()+"SEK");
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else{
							bankStatus.setText("No account exists for "+clientName);
						}
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else{
					bankStatus.setText("No client has been chosen.");
				}
			}
			
			// delete a account
			if(event.getSource() == deleteAccount){
				boolean isDeleted = false; 
				if(clientObject != null){
					try {
						isDeleted = bankObject.deleteAccount(clientObject.getID());
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				if(isDeleted){
					bankReply.setDisable(true);
					bankStatus.setText("Client's Bank Details:\nAccount has been deleted.");
				} else{
					bankStatus.setText("Client's Bank Details:\nNo Account to be deleted.");
				}
			}
			
			// register to market
			if(event.getSource() == register_unregister){
				if(register_unregister.getText().equals("Register")){
					// requirements fullfilled. Try to register. if a client with the same name exists
					// return a exception and ask for another input. 
					if(clientObject !=null){
						try {
							if(clientObject.getPassword().length()>=passwordLength){
								try {
									market.registerClient(clientObject);
									register_unregister.setText("Unregister");
									logIn_logOut.setText("logout");
									userName.setDisable(true);
									passwordField.setDisable(true);
									marketReply.setDisable(false);
									putSellProduct.setDisable(false);
									putWishProduct.setDisable(false);
									inspectMarket.setDisable(false);
									userActivity.setDisable(false);
									status.setText("Registered and logged in.");
									marketStatus.setText("Client's Market Status:\nRegistration Status: Registered.\n"+marketStatus.getText());
								} catch (RemoteException | RejectedException e) {
									// TODO Auto-generated catch block
									userName.setDisable(true);
									passwordField.setDisable(true);
									status.setText("You were Registered before.");
									marketStatus.setText(e.getMessage()+"\nTry with different name"+"\n"+marketStatus.getText());
								}
							} else{
								marketStatus.setText("Client's Market Status:\nChoose password with at least"+ passwordLength +"characters.\n"+marketStatus.getText());
							}
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else{
						marketStatus.setText("Client's Market Status:\nNo client has been chosen to register.\n"+marketStatus.getText());
					}
				} else{
					// client is not null-- guarrented...else something wrong
					try {
						market.unregisterClient(clientObject);
						status.setText("Choose a name and password");
						marketStatus.setText("Client's Market Status:\nClient removed from Market.\n"+marketStatus.getText());
						//logIn_logOut.setText("login");
						// need to reset GUI
						defaultGUI();
					} catch (RemoteException | RejectedException e) {
						// TODO Auto-generated catch block
						status.setText(e.getMessage());
						marketStatus.setText(e.getMessage()+"\n"+marketStatus.getText());
					}
				}
			}
			
			// sale/wish/buy only if a client is registered to the market
			
			if(event.getSource() == putSellProduct || event.getSource() == putWishProduct){
				StringTokenizer token;
				if(event.getSource() == putSellProduct){
					token = new StringTokenizer(putSellProduct.getText());
				} else{
					token = new StringTokenizer(putWishProduct.getText());
				} 
				
				if(token.countTokens() == 3){
					productName = token.nextToken();
					try{
						productPrice = Float.parseFloat(token.nextToken());
						productAmount = Integer.parseInt(token.nextToken());
						if(event.getSource() == putSellProduct){
							sellProduct.setDisable(false);
						} else{
							wishProduct.setDisable(false);
						}
					} catch(NumberFormatException nfex){
						marketStatus.setText("Wrong input. Format: product price amount.\n"+marketStatus.getText());
					}
					
				} else{
					if(event.getSource() == putSellProduct){
						sellProduct.setDisable(true);
					} else{
						wishProduct.setDisable(true);
					}
					marketStatus.setText("Wrong input. Format: product price amount.\n"+marketStatus.getText());
				}
			}
			
			// post product
			if(event.getSource() == sellProduct){
				try {
					String sellStatus = market.toSell(clientObject, productName,productPrice,productAmount);
					marketStatus.setText(sellStatus+"\n"+marketStatus.getText());
					sellProduct.setDisable(true);
					putSellProduct.setDisable(false);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(event.getSource() == wishProduct){
				try {
					String wishStatus = market.toWish(clientObject, productName, productPrice,productAmount);
					marketStatus.setText(wishStatus+"\n"+marketStatus.getText());
					wishProduct.setDisable(true);
					putWishProduct.setDisable(false);
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(event.getSource() == buyProduct){
				// first get the product ID to buy
				StringTokenizer itemToBuy = new StringTokenizer(availableProducts.getValue());
				//marketStatus.setText("Chosen: "+itemToBuy+"\n"+marketStatus.getText());
				if(itemToBuy.hasMoreTokens()){
					String itemID = itemToBuy.nextToken();
					System.out.println("chosen: "+itemID);
					if(itemID.equals("Available")){
						marketStatus.setText("Please Select a Product to buy and then hit buy.\n"+marketStatus.getText());
					} else{
						// got the ID tell market to buy the item
						marketStatus.setText("Product "+itemID+" has been chosen to buy.\n"+marketStatus.getText());
						String buyStatus = null;
						try {
							try{
								int amt = Integer.parseInt(amount.getText());
								System.out.println("amount chosen: "+amt);
								buyStatus = market.toBuy(clientObject, itemID,amt);
								// update combobox. Update bank details. message from bank and market********
									
								marketStatus.setText(buyStatus+"\n"+marketStatus.getText());
							} catch(NumberFormatException nxe){
								
								marketStatus.setText(nxe.getMessage()+"Choose an integer>0 for amount"+"\n"+marketStatus.getText());
								
							}
							
						} catch (NumberFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} else{
					marketStatus.setText("Please Select a Product to buy and then hit buy.\n"+marketStatus.getText());
				}
			}
			
				
			// inspect market
			if(event.getSource() == inspectMarket){
				availableProducts.setDisable(false);
				amount.setDisable(false);
				try {
					availableProducts.getItems().removeAll(availableProducts.getItems());
					//availableProducts.getItems().add("ID Product Price Seller");
					String[] productsID = market.getProductsList();
					// Checking current product list. If no items available, productsID is of length 1 will null. Need to adjust it.
					//System.out.println("Length: "+productsID[0]);
					if(productsID[0] != null){
						// visible only if there is at least one item available
						buyProduct.setDisable(false);
						for(String id: productsID){
							ProductInterface currentProduct = market.getProduct(id);
							System.out.println("Product ID: "+currentProduct.getProductID()+", Product Name: "+currentProduct.getProductName()+" Amount: "+currentProduct.getProductAmount()
							+", Price: "+currentProduct.getProductPrice()+", Seller: "+currentProduct.getClientName());
							// updating the comboLists
							String currentProductDetails = currentProduct.getProductID()+" "+currentProduct.getProductName()+" "+" Amount: "+currentProduct.getProductAmount()
									+" Price: "+currentProduct.getProductPrice()+" Sek"+" Seller: "+currentProduct.getClientName();
							
							// update productList
							availableProducts.getItems().add(currentProductDetails);
						}
					} else{
						buyProduct.setDisable(true);
						marketStatus.setText("No Product is available in the market.\n"+marketStatus.getText());
					}
					
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			
			if(event.getSource() == userActivity){
				if(clientObject != null){
					try {
						String activity = market.checkClientActivity(clientObject);
						marketStatus.setText(activity+"\n"+marketStatus.getText());
					} catch (RemoteException | RejectedException e) {
						// TODO Auto-generated catch block
						marketStatus.setText(e.getMessage()+"\n"+marketStatus.getText());
						//e.printStackTrace();
					}
				}
			}
			
			if(event.getSource() == bankReply){
				//messageFromBank.setText(clientObject.messageFromBank);
				if(clientObject != null){
					try {
						ClientInterface currentClient = market.getClient(clientObject.getID());
						if(currentClient != null){
							messageFromBank.setText(currentClient.updateMsgFromBank());
						}
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			if(event.getSource() == marketReply){
				//messageFromMarket.setText(clientObject.messageFromMarket);
				if(clientObject != null){
					try {
						ClientInterface currentClient = market.getClient(clientObject.getID());
						if(currentClient != null){
							messageFromMarket.setText(currentClient.updateMsgFromMarket());
						}
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}
	}


	public void runGUI() {
		launch();
	}
	// abstract method for the Application
	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		// TODO Auto-generated method stub
		this.createGUI();
	}
	
	// get the bank object
	private void getBankObject(){
		try{
			bankObject = (Bank) Naming.lookup("rmi://"+Client.HOST+":"+Client.REGISTRY_PORT_NUMBER+"/"+Client.bankName);
		}catch(Exception e){
			System.out.println("Runtime failed: "+e.getMessage());
			System.exit(1);
		}
		System.out.println("Connected to Bank: "+Client.bankName);
	}
	
	// get the MarketPlace
	private void getMarketPlace(){
		try{
			market = (MarketInterface) Naming.lookup("rmi://"+Client.HOST+":"+Client.REGISTRY_PORT_NUMBER+"/"+Client.marketName);
		}catch(Exception e){
			System.out.println("Runtime failed: "+e.getMessage());
			System.exit(1);
		}
		System.out.println("Open marketplace: "+Client.marketName);
	}
	
	// this is the default setting of the GUI
	private void defaultGUI(){
		// this is the client name
		clientName = null;
		userPassword = null;
		clientObject = null;
		
		userName.setDisable(false);
		userName.setText("");
		passwordField.setDisable(false);
		passwordField.setText("");
		logIn_logOut.setDisable(true);
		logIn_logOut.setText("login");
		
		newAccount.setDisable(false);
		deleteAccount.setDisable(false);
		accountDetails.setDisable(false);
		bankStatus.setEditable(false);
		
		register_unregister.setDisable(true);
		register_unregister.setText("Register");
		
		marketStatus.setEditable(false);
		sellProduct.setDisable(true);
		putSellProduct.setDisable(true);
		putSellProduct.setText(("item price amount"));
	    wishProduct.setDisable(true);
		putWishProduct.setDisable(true);
		putWishProduct.setText("item price anmout");
		buyProduct.setDisable(true);
		
		amount.setDisable(true);
		amount.setText("amount");
		
		availableProducts.setDisable(true);
		availableProducts.setPromptText("Available Products");
		
		inspectMarket.setDisable(true);
		userActivity.setDisable(true);
		
		// message from market and bank
		bankReply.setDisable(true);
		messageFromBank.setEditable(false);
		marketReply.setDisable(true);
		messageFromMarket.setEditable(false);
	}
}



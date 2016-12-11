package id2212.homework3.jdbc.marketServer;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import id2212.homework3.jdbc.exception.DBException;

public class ProductsDatabase {
	private static final String TABLE_NAME = "MARKET_AVAILABLE_PRODUCTS";
	private static final String ID_COLUMN_NAME ="ID";
	private static final String NAME_COLUMN_NAME ="Item";
	private static final String PRICE_COLUMN_NAME = "Price";
	private static final String AMOUNT_COLUMN_NAME = "Amount";
	private static final String CLIENT_COLUMN_NAME ="Client";
	
	// Statement 
	private static Statement statement;
	// some prepared statement for manilupating the table
	private PreparedStatement insertItemStatement;
	private PreparedStatement deleteItemStatement;
	private PreparedStatement updateItemStatement;
	private PreparedStatement selectItemStatement;
	private PreparedStatement specificItemStatement;
	
	// this is the connection object for the userdatabase
	private static Connection connection;
	
	// constructor for ProductDatabase
	public ProductsDatabase(String dbms, String datasource) throws DBException{
		try{
			connection = MarketDatabase.getConnection(dbms, datasource);
			System.out.println("Connected to database "+connection);
			statement = connection.createStatement();
			// create table
			createTable();
			System.out.println();
			System.out.println("Table "+TABLE_NAME+" has been (re)created.");
			// create prepared statements
			prepareStatement();
		}catch(SQLException | ClassNotFoundException e){
			e.getMessage();
		}
	}
	
	//// this method prepared statements
	private void prepareStatement() throws SQLException{
		insertItemStatement = connection.prepareStatement("INSERT INTO "+TABLE_NAME+" VALUES(?,?,?,?,?)");
		deleteItemStatement = connection.prepareStatement("DELETE FROM "+TABLE_NAME+" WHERE "+ID_COLUMN_NAME+"=?");
		selectItemStatement = connection.prepareStatement("SELECT * FROM "+TABLE_NAME+" WHERE "+ID_COLUMN_NAME+"=?");
		updateItemStatement = connection.prepareStatement("UPDATE "+TABLE_NAME+" SET "+AMOUNT_COLUMN_NAME+"="+AMOUNT_COLUMN_NAME+"-?"
															+" WHERE "+ID_COLUMN_NAME+"=?");
		
		specificItemStatement = connection.prepareStatement("SELECT * FROM "+TABLE_NAME+" WHERE "+NAME_COLUMN_NAME+"=?");
	}
	
	// this is a method to create a table providing a connection and table name
	private void createTable() throws SQLException{
		boolean exist = false;
		ResultSet result = connection.getMetaData().getTables(null, null,TABLE_NAME,null);
		if(result.next()){
			// column 3 is the table name
			System.out.println(result.getString(3)+" table already exits. Going to delete the table and add a new one for the current Application.");
			exist = true;
			//dropTable(result.getString(3));
		}
		result.close();
		// now create a new table if it is not exist
		if(!exist){
			statement.executeUpdate("CREATE TABLE "+TABLE_NAME
					+"("+ID_COLUMN_NAME +" VARCHAR(32) PRIMARY KEY,"
					+NAME_COLUMN_NAME +" VARCHAR(32),"
					+PRICE_COLUMN_NAME+" FLOAT,"
					+AMOUNT_COLUMN_NAME+" INTEGER,"
					+CLIENT_COLUMN_NAME+" VARCHAR(32)"+")");
		}
	}
	
	
	private void dropTable(String table) throws SQLException{
		int NoOfAffectedRows = statement.executeUpdate("DROP TABLE "+ table);
		System.out.println();
		System.out.println(table+" dropped: "+NoOfAffectedRows+"row(s) affected.");
	}
	
	// this method insert a new product to the database
	public synchronized void registerProductInDatabase(ProductInterface product) throws DBException{
		try {
			insertItemStatement.setString(1, product.getProductID());
			insertItemStatement.setString(2, product.getProductName());
			insertItemStatement.setFloat(3, product.getProductPrice());
			insertItemStatement.setInt(4, product.getProductAmount());
			insertItemStatement.setString(5, product.getClientName());
			int NoOfAffectedRows = insertItemStatement.executeUpdate();
			if(NoOfAffectedRows != 1){
				throw new DBException("Could not register new product to the database.");
			}
			System.out.println("Data inserted in "+NoOfAffectedRows+" row(s)");
			System.out.println("Product: "+product.getProductID()+" "+product.getProductName()+" has been added to the database.");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// this method delete product from product database
	public synchronized void deleteProductFromDatabase(String productID) throws DBException{
		try{
			deleteItemStatement.setString(1, productID);
			int NoOfAffectedRows = deleteItemStatement.executeUpdate();
			if(NoOfAffectedRows != 1){
				throw new DBException("Could not delete product from usersdatabase.");
			}
		}catch(SQLException e){
			System.out.println(e.getMessage()+" Problem deleting product from product database.");
		}
	}
	
	// this method update productsin the database
	public synchronized void updateProductInDatabase(String productID, int itemsold) throws DBException{
		try{
			updateItemStatement.setInt(1, itemsold);
			updateItemStatement.setString(2, productID);
			int NoOfAffectedRows = updateItemStatement.executeUpdate();
			if(NoOfAffectedRows != 1){
				throw new DBException("Could not update product in usersdatabase.");
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
	}
	// this method select a item from database using product name
	public synchronized boolean getSpecificProductUsingName(String productName, float price){
		boolean matchFound = false;
		try{
			specificItemStatement.setString(1, productName);
			ResultSet result = specificItemStatement.executeQuery();
			try{
				for(int i=1;result.next();i++){
					if(result.getFloat(PRICE_COLUMN_NAME)<=price){
						matchFound = true;
						break;
					}
				}
				result.close();
			}catch(SQLException e){
				System.out.println(e.getMessage()+" no product found in the database using name.");
			}
			
		} catch(SQLException e){
			e.printStackTrace();
		}
		return matchFound;
	}
	
	// this method return the total list of the products list
	public synchronized ResultSet getProductsListFromDatabase(){
		ResultSet result = null;
		try {
			result = statement.executeQuery("SELECT * FROM "+TABLE_NAME);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	// this method sent a particular product using product id
	public synchronized Product getProductFromDatabase(String productID) throws DBException{
		Product product = null;
		try {
			selectItemStatement.setString(1, productID);
			ResultSet result  =  selectItemStatement.executeQuery();
			try{
				result.next();
				String productName = result.getString(2);
				Float productPrice = result.getFloat(3);
				int productAmount = result.getInt(4);
				String productClient = result.getString(5);
				product = new Product(productID,productClient,productName,productPrice,productAmount);
				
				result.last();
				if(result.getRow()>1){
					throw new DBException("Duplicate product (ID) entry in the database. Check Product Database.");
				}
				
				result.close();
			}catch(SQLException | RemoteException e){
				System.out.println(e.getMessage()+" . No item found using the ID.");
				return null;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return product;
	}
	
}

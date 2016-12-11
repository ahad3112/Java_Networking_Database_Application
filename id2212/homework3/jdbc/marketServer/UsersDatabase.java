package id2212.homework3.jdbc.marketServer;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import id2212.homework3.jdbc.client.ClientInterface;
import id2212.homework3.jdbc.exception.DBException;


/*
 * this class creates users database and manage the database
 * @param dbms		database manage system vendor. EX: DERBY, MYSQL
 * @datasource		database name in the form of url
 */
public class UsersDatabase {
	private static final String TABLE_NAME = "MARKET_USERS";
	private static final String NAME_COLUMN_NAME ="user";
	private static final String PASSWORD_COLUMN_NAME = "password";
	private static final String NO_ITEMS_SOLD_NAME = "itemssold";
	private static final String NO_ITEMS_BOUGHT_NAME = "itemsbought";
	
	// Statement 
	private static Statement statement;
	// some prepared statement for manilupating the table
	private PreparedStatement insertUserStatement;
	private PreparedStatement deleteUserStatement;
	private PreparedStatement updateUserStatement;
	private PreparedStatement selectUserStatement;
	
	// this is the connection object for the userdatabase
	private static Connection connection;

	// constructor for the Users database
	public UsersDatabase(String dbms, String datasource) throws DBException{
		System.out.println("Creating database for the users.");
		try{
			// create a connection to the database driver
			connection = MarketDatabase.getConnection(dbms,datasource);
			System.out.println("Connected to database "+connection);
			statement = connection.createStatement();
			// create table
			createTable();
			System.out.println();
			System.out.println("Table "+TABLE_NAME+" has been (re)created.");
			// create prepared statements
			prepareStatement();
			
			/*
			// checking tables
			insert("Ahad", "dadadada",4, 10);
			insert("Israt", "dadadadisrata",40, 100);
			// print the entries of the table
			selectAll();
			*/
		} catch(SQLException | ClassNotFoundException e){	
			throw new DBException("Couldnot connect to datasource."+e.getMessage());
		}
		
	}
	
	
	
	// this is a method to create a table providing a connection and table name
	private void createTable() throws SQLException{
		// column 3 is the table name
		boolean exist = false;
		ResultSet result = connection.getMetaData().getTables(null, null,TABLE_NAME,null);
		if(result.next()){
			System.out.println(result.getString(3)+" table already exits. Going to delete the table and add a new one for the current Application.");
			exist = true;
			//dropTable(result.getString(3));
		}
		result.close();
		// now create a new table 
		if(!exist){
			statement.executeUpdate("CREATE TABLE "+TABLE_NAME
					+"("+NAME_COLUMN_NAME +" VARCHAR(32) PRIMARY KEY,"
					+PASSWORD_COLUMN_NAME+" VARCHAR(32),"
					+NO_ITEMS_SOLD_NAME+" INTEGER,"
					+NO_ITEMS_BOUGHT_NAME+" INTEGER"+")");
		}
	}

	// this method prepared statements
	private void prepareStatement() throws SQLException{
		insertUserStatement = connection.prepareStatement("INSERT INTO "
															+TABLE_NAME+" VALUES(?,?,?,?)");
		deleteUserStatement = connection.prepareStatement("DELETE FROM "+TABLE_NAME+" WHERE "+NAME_COLUMN_NAME+"=?");
		selectUserStatement = connection.prepareStatement("SELECT * FROM "+TABLE_NAME+" WHERE "+NAME_COLUMN_NAME+"=?");
		updateUserStatement = connection.prepareStatement("UPDATE "+TABLE_NAME+" SET "+NO_ITEMS_SOLD_NAME+"="+NO_ITEMS_SOLD_NAME+"+?,"
															+NO_ITEMS_BOUGHT_NAME+"="+NO_ITEMS_BOUGHT_NAME+"+? WHERE "+NAME_COLUMN_NAME+"=?");
		// update later..... for user activity
	}
	
	private void dropTable(String table) throws SQLException{
		int NoOfAffectedRows = statement.executeUpdate("DROP TABLE "+ table);
		System.out.println();
		System.out.println(table+" dropped: "+NoOfAffectedRows+"row(s) affected.");
	}
	// this method insert new user to the table...REmove this one later. this was for checking purpose only
	private synchronized void insert(String name, String password, int sold, int bought) throws SQLException{
		insertUserStatement.setString(1, name);
		insertUserStatement.setString(2, password);
		insertUserStatement.setInt(3, sold);
		insertUserStatement.setInt(4, bought);
		int NoOfAffectedRows = insertUserStatement.executeUpdate();
		System.out.println();
		System.out.println("Data inserted in "+NoOfAffectedRows+" row(s)"+" in table "+TABLE_NAME);
	}
	
	// this method print all data entry in the table 
	public void selectAll() throws SQLException{
		ResultSet result = statement.executeQuery("SELECT * FROM "+TABLE_NAME);
		System.out.println();
		System.out.println("XXXXXXXXXXXXXXXXX Selecting data from table XXXXXXXXXXXXXXX");
		System.out.println("XXXXXXXXXXX Query returned from the results XXXXXXXXXXXXXXX");
		for(int i=1; result.next(); i++){
			System.out.println("Row"+i+"-"
			+result.getString(NAME_COLUMN_NAME)
			+"\t"+result.getString(PASSWORD_COLUMN_NAME)
			+"\t"+result.getInt(NO_ITEMS_SOLD_NAME)
			+"\t"+result.getInt(NO_ITEMS_BOUGHT_NAME));
		}
		result.close();
	}
	// methods related to the SQL query
	// this method check whether a user exist provided the user name
	public synchronized boolean isRegisteredInDatabase(String name) throws SQLException{
		boolean registered = false;
		selectUserStatement.setString(1,name);
		// we perform a query with the name. if at least one rows effected ... registered
		ResultSet result = selectUserStatement.executeQuery();
		result.last();
		int noRows = result.getRow();
		if(noRows == 1){
			// at least one match found. Though there should be maximum one user with one name
			registered = true;
		} else if(noRows > 1){
			System.out.println("More than one user already registered with name "+name+". Check registerClient() method in MarketImplementation. Exiting...");
			System.exit(1);
		}
		result.close();
		
		return registered;
	}
	
	// this method added a new user to the database
	public synchronized void registerInDatabase(ClientInterface client){
		try {
			insertUserStatement.setString(1, client.getID());
			insertUserStatement.setString(2, client.getPassword());
			// initially sold items and bought items are 0 in number
			insertUserStatement.setInt(3, 0);
			insertUserStatement.setInt(4, 0);
			int NoOfAffectedRows = insertUserStatement.executeUpdate();
			System.out.println("Data inserted in "+NoOfAffectedRows+" row(s)");
			System.out.println("Client: "+client.getID()+" has been added to the database.");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// this method check whether the username is consistent with the password
	public synchronized boolean passwordVerified(ClientInterface client) throws DBException{
		boolean verify = false;
		try {
			selectUserStatement.setString(1,client.getID());
			ResultSet result = selectUserStatement.executeQuery();
			// Should find only one row. other wise something went wrong
			// need to check empty result some how  ****************
			try{
				result.next();
				if(result.getString(PASSWORD_COLUMN_NAME).equals(client.getPassword())){
					verify = true;
				}
				// checking whether there has been more than one user with a same name
				result.last();
				if(result.getRow()>1){
					System.out.println("More than one user already registered with name "+client.getID()+". Check registerClient() method in MarketImplementation. Exiting...");
					throw new DBException("More than one user already registered with name");
				}
				result.close();
			} catch(SQLException e){
				System.out.println(e.getMessage()+" No match found in the database. Register first.");
				throw new DBException("User not registered.");
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return verify;
	}
	
	// this method return the activity of the client
	public synchronized String getUserActivity(String name) throws DBException{
		String activity = null;
		try{
			selectUserStatement.setString(1,name);
			ResultSet result = selectUserStatement.executeQuery();
			try{
				result.next();
				int itemsSold = result.getInt(NO_ITEMS_SOLD_NAME);
				int itemsBought = result.getInt(NO_ITEMS_BOUGHT_NAME);
				activity = "****************\n"+" "+name+" activities \n"+"****************\n"+"# Items Sold :\t"+itemsSold+"\n# Items Bought :\t"+itemsBought;
				
				result.last();
				if(result.getRow()>1){
					System.out.println("More than one user already registered with name "+name+". Check registerClient() method in MarketImplementation. Exiting...");
					throw new DBException("More than one user already registered with name "+name);
				}
				result.close();
				
			}catch(SQLException e){
				System.out.println(e.getMessage()+" no user available in database");
			}
			
		} catch(SQLException e){
			System.out.print(e.getMessage()+" Exception in getUserActivity");
		}
		
		return activity;
	}
	
	
//	// this method get a offline user from the database 
//	public synchronized ClientInterface getOfflineClient(String name){
//		ClientInterface client = null;
//		try{
//			selectUserStatement.setString(1, name);
//			ResultSet result = selectUserStatement.executeQuery();
//			try{
//				result.next();
//				try {
//					client = (ClientInterface) new ClientImplementation(result.getString(1));
//					
//				} catch (RemoteException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			} catch(SQLException e){
//				System.out.println(e.getMessage()+" No match found in the database. Register first.");
//			}
//			
//			
//		}catch(SQLException e){
//			System.out.println(e.getMessage());
//		}
//		return client;
//	}
	// delete a user from the database
	public synchronized void deleteUserFromDatabase(String name) throws DBException{
		try{
			deleteUserStatement.setString(1, name);
			int NoOfAffectedRows = deleteUserStatement.executeUpdate();
			System.out.println("Data deleted from "+NoOfAffectedRows+" row(s)");
			if(NoOfAffectedRows != 1){
				throw new DBException("Could not delete the user from database");
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	// this method update user activity
	public synchronized void updateUserActivity(String name, int itemsold, int itembought) throws DBException{
		try{
			updateUserStatement.setInt(1, itemsold);
			updateUserStatement.setInt(2, itembought);
			updateUserStatement.setString(3, name);
			int NoOfAffectedRows = updateUserStatement.executeUpdate();
			if(NoOfAffectedRows != 1){
				throw new DBException("Could not update user activity in usersdatabase.");
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
	}
}

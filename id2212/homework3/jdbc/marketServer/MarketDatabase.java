package id2212.homework3.jdbc.marketServer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import id2212.homework3.jdbc.exception.DBException;

/*
 * this method create two database for the market. One for users and other for items
 * @param dbms		database manage system vendor. EX: DERBY, MYSQL
 * @datasource		database name in the form of url 
 */
public class MarketDatabase {
	// a users database
	private UsersDatabase usersDBO;
	// available products database
	private ProductsDatabase productsDBO;
	// constructor
	public MarketDatabase(String dbms, String datasource){
		System.out.println("Market database is creating....");
		// users databases
		try {
			productsDBO = new ProductsDatabase(dbms,datasource);
			System.out.println("Products databases has been created.");
			usersDBO = new UsersDatabase(dbms,datasource);
			System.out.println("Users databases has been created.");
		} catch (DBException e) {
			// TODO Auto-generated catch block
			e.getMessage();
		}
	}
	
	// this is a static method to return a connection object to the database driver  providing dbms and datasource
	public static Connection getConnection(String dbms, String datasource) throws ClassNotFoundException, SQLException, DBException{
		if(dbms.equalsIgnoreCase("derby")){
			Class.forName("org.apache.derby.jdbc.ClientXADataSource");
			return  DriverManager.getConnection(datasource+";create=true");
		} else if(dbms.equalsIgnoreCase("mysql")){
			Class.forName("com.mysql.jdbc.Driver");
			Properties properties = new Properties();
			properties.setProperty("user", "root");
			properties.setProperty("password", "");
			properties.setProperty("useSSL", "false");
			properties.setProperty("autoReconnect", "true");
			System.out.println("Creating connection to database.");
			return  DriverManager.getConnection(datasource,properties);
			
		} else{
			throw new DBException("Unable to create database. Unknown dbms");
		}
	}
	
	// getter method
	public UsersDatabase getUserDatabase(){
		return usersDBO;
	}
	
	// getter method
	public ProductsDatabase getProductDatabase(){
		return productsDBO;
	}
}

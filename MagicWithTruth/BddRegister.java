package MagicWithTruth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

public class BddRegister {
	
	private Statement st=null;				
	private Connection connexion = null;
	
	public BddRegister(){
		String url = "jdbc:mysql://192.168.1.30:3306/magic";
	    String user = "root";
	    String passwd = "magicpswd";
	    new BddRegister(url, user,  passwd);
	}
	public BddRegister(String url,String user, String passwd){
		
	    try {	
	    	Class.forName( "com.mysql.jdbc.Driver" );
	    	connexion = DriverManager.getConnection(url, user, passwd);
		    st = (Statement) connexion.createStatement();	     

	    } catch ( SQLException e ) {		    	
	    	e.printStackTrace();	
	        /* Gérer les éventuelles erreurs ici */		    
	    } catch ( ClassNotFoundException e ) {
	    	e.printStackTrace();	
	    	System.out.println("Driver non valide");	
	    }
	    catch (Exception e) {
	      e.printStackTrace();		      
	    } 
	}
	
	public void waitt(){
		ResultSet resultat;
		try {
			resultat = st.executeQuery( "SELECT *  FROM TimeRegister;" );
			 while ( resultat.next() ) {
			    	for(int i=1; i<5;i++)
			    		System.out.print(resultat.getString(i)+" ");
			    	System.out.println("");
			        }
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	public void SQLinsert(String sql){
		try {
			st.executeUpdate(sql);
		} catch (SQLException e) {			
			e.printStackTrace();
		}
	}
	
	public ResultSet SQLselect(String sql){
		ResultSet resultat=null;
		try {
			resultat = st.executeQuery(sql);
		} catch (SQLException e) {			
			e.printStackTrace();
		}
		return resultat;		
	}
	
	public void finalize(){
		System.out.println("destruction");
		if ( connexion != null ){
			try {
				connexion.close();
				st.close();
			} catch (SQLException e) {			
				e.printStackTrace();
			}
		}
	}
	
}

package test_mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

// télécharger : https://dev.mysql.com/downloads/connector/j/
// l'ajouter dans le repertoire
// l'ajouter au projet
// 

public class Bdd {
	private Statement st=null;				
	private Connection connexion = null;
	
	private String url = "jdbc:mysql://192.168.118.40:3306/test_sql"; 
	private String user = "root";
	private String passwd = "magicpswd";
	
	public Bdd(){
		
	}
	public Bdd(String url,String user, String passwd){
		this.url=url;
		this.user=user;
		this.passwd=passwd;
	}
	
	// la connexion à la BDD a chaque fois n'est pas propre 
	// mais sinon ca ne marchais pas donc a voir pour améliorer cette partie 
	
	/* Permet de lancer une requete SQL du type SELECT  */
	public ResultSet SQLselect(String sql){
		ResultSet resultat = null;
		try {	
	    	Class.forName( "com.mysql.jdbc.Driver" );
	    	connexion = DriverManager.getConnection(url, user, passwd);
	    	st =  connexion.createStatement();	
		    if (st==null){
		    	System.out.println("impossibde créer un statement");
		    }
		    
			try {
				resultat = st.executeQuery(sql);
			} catch (SQLException e) {			
				e.printStackTrace();
			}catch (Exception e) {
			      e.printStackTrace();		      
			}					    
	    } catch ( SQLException e ) {		    	
	    	e.printStackTrace();	
	    	sqlError(e);
	        /* Gérer les éventuelles erreurs ici */		    
	    } catch ( ClassNotFoundException e ) {
	    	e.printStackTrace();	
	    	System.out.println("Driver non valide");	
	    }
	    catch (Exception e) {
	      e.printStackTrace();		      
	    } 		
							
		return resultat;		
	}
	
	/* Permet de lancer une requete SQL du type INSERT  */
	public void SQLinsert(String sql){
		try {	
	    	Class.forName( "com.mysql.jdbc.Driver" );
	    	connexion = DriverManager.getConnection(url, user, passwd);
	    	st =  connexion.createStatement();	
		    if (st==null){
		    	System.out.println("impossibde créer un statement");
		    }
		    
			try {
				st.executeUpdate(sql);
			} catch (SQLException e) {			
				e.printStackTrace();
			}catch (Exception e) {
			      e.printStackTrace();		      
			}					    
	    } catch ( SQLException e ) {		    	
	    	e.printStackTrace();	
	    	sqlError(e);
	        /* Gérer les éventuelles erreurs ici */		    
	    } catch ( ClassNotFoundException e ) {
	    	e.printStackTrace();	
	    	System.out.println("Driver non valide");	
	    }
	    catch (Exception e) {
	      e.printStackTrace();		      
	    }
	}
	
	public void sqlError(SQLException e){
		System.out.println("SQLException: " + e.getMessage());
        System.out.println("SQLState: " + e.getSQLState());
        System.out.println("VendorError: " + e.getErrorCode());
	}
}

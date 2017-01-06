package MagicWithTruth;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TimeRegister extends BddRegister {
	
	private String table;
	private long debut;
	private int idAnalyse=0;
	
	public TimeRegister(String url,String user, String passwd,int idAnalyse, String table){
		super(url, user, passwd);
		this.table=table;
		this.debut = System.currentTimeMillis();
		this.idAnalyse=idAnalyse;
	}
	
	public TimeRegister(String url,String user, String passwd,String table){
		super(url, user, passwd);
		this.table=table;
		this.debut = System.currentTimeMillis();
		
		ResultSet resultat = this.SQLselect("SELECT MAX(idAnalyse) FROM "+table);		
		
		try {
			while ( resultat.next() )
				this.idAnalyse=resultat.getInt(1);
			this.idAnalyse++;
		} catch (SQLException e) {			
			e.printStackTrace();
		}
	}
	
	public void balise(String Name){		
		long time=System.currentTimeMillis()-debut;
		String sql="INSERT INTO "+this.table+"(idAnalyse, marqueur, time) VALUE("+this.idAnalyse+",'"+Name+"',"+time+");";
		this.SQLinsert(sql);
	}
}

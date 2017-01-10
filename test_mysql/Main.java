package test_mysql;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Main {
	// chemin vers la banque d'image
	static String path="C:\\Users\\edouard\\Google Drive\\PFE_carte\\image_edition\\edition\\icon\\";
	
	// chemin vers l'image inconnu
	static String pathImageTest="C:\\Users\\edouard\\Google Drive\\PFE_carte\\corpus_edition\\khans of tarkir\\2.png";
	
	public static void main(String[] args) throws SQLException {
		System.out.println("start");
		
		// chargement de l'image inconnu
		BufferedImage imageInconnu=null;
		try {		
			imageInconnu = ImageIO.read(new File(pathImageTest));
		} catch (IOException e) {
			System.out.println("file not found");
			e.printStackTrace();
			return ;
		}
		
		// création d'un interface pour afficher les images
		Interface ihm= new Interface("");		
		ihm.addPicture("Image inconnu", imageInconnu);
		
		Bdd  bdd=new Bdd();
		//bdd.SQLinsert("INSERT INTO Edition (Nom, Logo, Image, Parution, IdBloc) VALUES ('moi', 'jhjhjh', 'kjkj','0000-00-00',1)");
		
		// lance la ruequete
		ResultSet resultat = bdd.SQLselect("SELECT Nom, Image FROM Edition;");
		String NomTest="khans of tarkir";
		String ImageTest="";
		
		// analyse la requete
		// cette partie fonctionne pour un résultat avec 1 ligne comme avec plusieurs lignes
		while ( resultat.next() ) {		    
			String Nom = resultat.getString( "Nom" );
			String Image = resultat.getString( "Image" );
		    System.out.println(Nom+" -> "+Image);	
		    if(Nom.equals(NomTest)){
		    	ImageTest=Image;
		    }
		}
		
		// charge l'image car on a le chemin avec la requete SQL
		BufferedImage imageTest=null;
		try {		
			imageTest = ImageIO.read(new File(path+ImageTest));
		} catch (IOException e) {
			System.out.println("file not found");
			e.printStackTrace();
			return ;
		}		
		
		// ajoute l'image à l'interface
		ihm.addPicture("Image test : "+NomTest, imageTest);
		System.out.println("fin");
	}

}

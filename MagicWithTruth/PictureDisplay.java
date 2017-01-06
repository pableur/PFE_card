package MagicWithTruth;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class PictureDisplay extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public PictureDisplay(String title, BufferedImage image){
		//Définit un titre pour notre fenêtre
		this.setTitle(title);
		this.setSize(image.getHeight(), image.getWidth() );
		System.out.print("size : "+image.getHeight()+", "+image.getWidth()+"\n");
		this.setResizable(false);
		//Nous demandons maintenant à notre objet de se positionner au centre
		this.setLocationRelativeTo(null);
		//Termine le processus lorsqu'on clique sur la croix rouge
		this.setDefaultCloseOperation();

		getContentPane().add(new JLabel(new ImageIcon(image)));
							
		this.setVisible(true);
		pack();
	}
	public void setDefaultCloseOperation() {
		this.dispose();		
	}
}

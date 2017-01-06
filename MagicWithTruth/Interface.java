package MagicWithTruth;

import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Interface extends JFrame{
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel pan = new JPanel();

	  public Interface(String name){
		pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));
	    this.setTitle(name);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setLocationRelativeTo(null);

	    this.setContentPane(pan);
	    
	    this.setVisible(true);
	  }   
	  
	public void addPicture(String title, BufferedImage image){
			JPanel panImage = new JPanel();
			panImage.setLayout(new BoxLayout(panImage, BoxLayout.PAGE_AXIS));
			panImage.add(new JLabel(title));
			panImage.add(new JLabel(new ImageIcon(image)));		

			pan.add(panImage);

			this.setVisible(true);
			pack();
		}
	
}
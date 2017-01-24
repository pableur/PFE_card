
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Interface extends JFrame{
	 

	private static final long serialVersionUID = 1L;
	private JPanel pan = new JPanel();
	
	public Interface(String name){
		this(name, 1);
	}

	public Interface(String name, int col){
		GridLayout experimentLayout = new GridLayout(0,col);
		pan.setLayout (experimentLayout);
	    this.setTitle(name);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setLocationRelativeTo(null);
	    this.setContentPane(pan);
	    this.setVisible(true);
	  }   
	  
	public void addPicture(String title, BufferedImage image){
		this.addPicture(title,image,image.getWidth(),image.getHeight());
		}
	
	 public void addPicture(String title, BufferedImage image, int sizeY){
		 int width = image.getWidth();
		 int height = image.getHeight();
		 int sizeX=(width*sizeY)/height;
		 this.addPicture(title,image,sizeX,sizeY);
	  }
	 
	public void addPicture(String title, BufferedImage image, int sizeX, int sizeY){
			JPanel panImage = new JPanel();
			panImage.setLayout(new BoxLayout(panImage, BoxLayout.PAGE_AXIS));
			panImage.add(new JLabel(title));
			ImageIcon imageIcon = new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(sizeX, sizeY, Image.SCALE_DEFAULT));
			panImage.add(new JLabel(imageIcon));		
			pan.add(panImage);
			this.setVisible(true);
			pack();
		}
}
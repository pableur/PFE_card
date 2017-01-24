import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main {

	private static String path="E:\\programmation\\PFE_card\\image_test\\full\\";
	
	public static void main(String[] args) {
		BufferedImage image=null;
		
		try {
			//image = ImageIO.read(new File(path+"14795757758411616188350.jpg"));
			//image = ImageIO.read(new File(path+"1470763282590-689674451.jpg"));
			image = ImageIO.read(new File(path+"1479575553894955631010.jpg"));
			
		} catch (IOException e) {
			System.out.println("file not found");
			e.printStackTrace();
			return ;
		}
		Locate locate = new Locate(image);
		locate.card();
		
		Interface ihm= new Interface("detection des zones",2);
		ihm.addPicture("Image d'origine", image,300);
		ihm.addPicture("Image carte", locate.getImageCard(),300);
		save(locate.getImageCard(),"card.png");
		System.out.println("fin");

	}
	private static void save(BufferedImage image,String file){
		try {
			ImageIO.write(image, "png", new File(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

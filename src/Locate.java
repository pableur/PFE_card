import java.awt.image.BufferedImage;

public class Locate {

	private BufferedImage imageOrigin= null;
	private BufferedImage imageCard  = null;
	
	public Locate(BufferedImage image){
		this.imageOrigin=image;
	}
	
	public void card(){
		Interface ihm= new Interface("Localisation",2);
		
		// affin d'accélérer les traitements on réduit l'image à environs 300px
		int coeff = imageOrigin.getHeight()/ 300; // coeff de réduction entier
		//System.out.println(coeff);
		BufferedImage littelOrigin = Picture.resize(imageOrigin,imageOrigin.getWidth()/coeff,imageOrigin.getHeight()/coeff);
		GreyImage littelGrey = new GreyImage(littelOrigin);
		ihm.addPicture("Image gris", littelGrey.display());
		
		float[][] matrice ={				
				{5,5,5},
				{5,10,5},
				{5,5,5}
		};
		for(int i=0; i<5; i++)
		littelGrey.convolution(matrice);

		// dérive
		float[][] matrice3 ={		
				{-2,-2,-2,-2,-2},
				{-2,-2,-2,-2,-2},
				{-2,-2,0,-2,-2},
				{-2,-2,-2,-2,-2},
				{-2,-2,-2,-2,-2}
		};
		for(int i=0; i<10; i++)
		littelGrey.convolution(matrice3);
		
		float[][] matrice2 ={	
				{0,-1,0},
				{-1,5,-1},
				{0,-1,0}
		};

		littelGrey.convolution(matrice2);
		
		littelGrey.invertColor();
		ihm.addPicture("dérivé", littelGrey.display());
		
		
		/*
		int border[] = Picture.borderCard(littelOrigin);
		imageCard = Picture.selectZone(imageOrigin,border[0]*coeff,border[2]*coeff,border[1]*coeff,border[3]*coeff);
		if(imageCard.getWidth()>imageCard.getHeight()){
			imageCard=Picture.rotate90(imageCard);
		}
		*/
		imageCard = littelGrey.display();
	}
	
	public BufferedImage getImageCard(){ return imageCard;}
}

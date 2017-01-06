package MagicWithTruth;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

	public class Draw {
		public static BufferedImage lineHoriz(BufferedImage image,int posY, int posX[] , int rgb){
			BufferedImage imageReturn = copy(image);
			if(posX[1]>imageReturn.getWidth())
				posX[1]=imageReturn.getWidth();
			
			for(int x=posX[0];x<posX[1];x++){
				imageReturn.setRGB(x, posY, rgb);
			}
			return imageReturn;
		}
		public static BufferedImage lineVertical(BufferedImage image,int posX, int posY[], int rgb){

			if(posY[1]>image.getHeight())
				posY[1]=image.getHeight();
			
			for(int y=posY[0];y<posY[1];y++){
				image.setRGB(posX, y, rgb);
			}
			return image;
		}
		
		public static BufferedImage rect(BufferedImage image,int point1[],int point2[],int rgb){
			BufferedImage imageReturn = copy(image);
			int temp[]={point1[0],point2[0]};
			imageReturn=lineHoriz(image,point1[1],temp,rgb);
			imageReturn=lineHoriz(image,point2[1],temp,rgb);
			
			temp[0]=point1[1];
			temp[1]=point2[1];
			//image=lineVertical(image,point1[0],temp,rgb);
			//image=lineVertical(image,point2[0],temp,rgb);
			
			return imageReturn;
		}
		public static BufferedImage copy(BufferedImage bImage) {
		    int w = bImage.getWidth(null);
		    int h = bImage.getHeight(null);    
		    BufferedImage bImage2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		    Graphics2D g2 = bImage2.createGraphics();
		    g2.drawImage(bImage, 0, 0, null);
		    return bImage2;
		  }
}

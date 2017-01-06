package MagicWithTruth;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.ArrayList;

public class Picture {
	static BufferedImage selectZone(BufferedImage image, int X1, int Y1, int X2, int Y2){
		BufferedImage imageResize= new BufferedImage(X2-X1, Y2-Y1, 1);
		for(int x=0; x<X2-X1;x++){
			for(int y=0; y<Y2-Y1;y++){
				int rgb = image.getRGB(x+X1, y+Y1);
				imageResize.setRGB(x, y, rgb);
			}
		}
		return imageResize;
	}
	static GreyImage selectZone(GreyImage image, int X1, int Y1, int X2, int Y2){
		GreyImage imageResize= new GreyImage(X2-X1, Y2-Y1, 1);
		for(int x=0; x<X2-X1;x++){
			for(int y=0; y<Y2-Y1;y++){
				int rgb = image.getColor(x+X1, y+Y1);
				imageResize.setColor(x, y, rgb);
			}
		}
		return imageResize;
	}
	static int[] borderCard(BufferedImage image){
		int list[] ={0,0,0,0};
		for(int x=0; x<image.getWidth();x++){
			int intColor=image.getRGB(x, image.getHeight()/2);
			 Color c = new Color(intColor, true);
			 if(c.getRed()<70 && c.getGreen()<70 && c.getBlue()<70 ){
				 list[1]=x;
			 }
		}
		for(int x=image.getWidth()-1;x>=0;x--){
			int intColor=image.getRGB(x, image.getHeight()/2);
			 Color c = new Color(intColor, true);
			 if(c.getRed()<70 && c.getGreen()<70 && c.getBlue()<70 ){
				 list[0]=x;
			 }
		}
		for(int y=0; y<image.getHeight();y++){
			int intColor=image.getRGB(image.getWidth()/2, y);
			 Color c = new Color(intColor, true);
			 if(c.getRed()<70 && c.getGreen()<70 && c.getBlue()<70 ){
				 list[3]=y;
			 }
		}
		for(int y=image.getHeight()-1;y>=0;y--){
			int intColor=image.getRGB(image.getWidth()/2, y);
			 Color c = new Color(intColor, true);
			 if(c.getRed()<70 && c.getGreen()<70 && c.getBlue()<70 ){
				 list[2]=y;
			 }
		}
		return list;
	}
	static BufferedImage resize(BufferedImage truth, int width, int height){
		Image tmp = truth.getScaledInstance(width, height, BufferedImage.SCALE_FAST);
		BufferedImage buffered = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
		buffered.getGraphics().drawImage(tmp, 0, 0, null);
		return buffered;
		//return (BufferedImage) truth.getScaledInstance(width, height, Image.SCALE_SMOOTH) ;
	}
	static BufferedImage masque(BufferedImage image,BufferedImage masque){
		BufferedImage imageReturn = copy(image);
		if(image.getHeight()!=masque.getHeight() || image.getWidth() != masque.getWidth()){
			masque=resize(masque,image.getWidth(),image.getHeight());
		}
		for(int x=0; x<image.getWidth();x++){
			for(int y=0; y<image.getHeight();y++){
				if(masque.getRGB(x, y)!=0xFFFFFFFF){
					imageReturn.setRGB(x, y, 0xFF000000);
				}
			}
		}
		return imageReturn;
	}
		 
	 public static ArrayList<BufferedImage> detecZone(BufferedImage image,BufferedImage masque){
		 BufferedImage imageTemp=masque(image,masque);
		 int color=0xFF000000;
		 ArrayList<BufferedImage> zone=new ArrayList<BufferedImage>();
		 zone=detecZoneY(imageTemp,color);
		 
		 return zone;
	 } 
	 public static ArrayList<BufferedImage> detecZoneY(BufferedImage image,int color){
		 ArrayList<BufferedImage> zone = new ArrayList<BufferedImage>();
		 boolean startZone=false;
		 int ligne=0;
		 for(int y=0; y<image.getHeight();y++){
			 int temp=0;
			 for(int x=0; x<image.getWidth();x++){
				 if(image.getRGB(x, y)!=color){
					temp=temp+1;
				 }
			 }
			 if(startZone==false && temp>0){				 
				 ligne=y;
				 startZone=true;
			 }
			 if(startZone==true && temp==0){
				 zone.add(Picture.selectZone(image, 0, ligne, image.getWidth(),y));				 
				 startZone=false;
			 }	
		 }
		 ArrayList<BufferedImage> zoneReturn = new ArrayList<BufferedImage>();
		 ArrayList<BufferedImage> buffer = new ArrayList<BufferedImage>();
		 for(int i=0;i<zone.size();i++){
			 buffer=detecZoneX(zone.get(i),color);
			 for(int j=0;j<buffer.size();j++)
				 zoneReturn.add(buffer.get(j));
		 }
		 if(zoneReturn.size()==0){
			 zoneReturn.add(image);
		 }
		 return zoneReturn;
	 }
	 public static ArrayList<BufferedImage> detecZoneX(BufferedImage image,int color){
		 ArrayList<BufferedImage> zone = new ArrayList<BufferedImage>();
		 boolean startZone=false;
		 int colonne=0;
		 for(int x=0; x<image.getWidth();x++){
			 int temp=0;
			 for(int y=0; y<image.getHeight();y++){
				 if(image.getRGB(x, y)!=color){
					temp=temp+1;
				 }
			 }
			 if(startZone==false && temp>0){
				 colonne=x;
				 startZone=true;
			 }
			 if(startZone==true && temp==0){
				 zone.add(Picture.selectZone(image, colonne, 0, x,image.getHeight()));
				 startZone=false;
			 }	
		 }
		 return zone;
	 }
	 public static ArrayList<GreyImage> detecZoneX(GreyImage image,int color){
		 ArrayList<GreyImage> zone = new ArrayList<GreyImage>();
		 boolean startZone=false;
		 int colonne=0;
		 for(int x=0; x<image.getWidth();x++){
			 int temp=0;
			 for(int y=0; y<image.getHeight();y++){
				 if(image.getColor(x, y)!=color){
					temp=temp+1;
				 }
			 }
			 if(startZone==false && temp>0){
				 colonne=x;
				 startZone=true;
			 }
			 if(startZone==true && temp==0){
				 zone.add(Picture.selectZone(image, colonne, 0, x,image.getHeight()));
				 startZone=false;
			 }	
		 }
		 return zone;
	 }
	 
	 public static BufferedImage grey(BufferedImage image){
		 ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		 BufferedImage imageReturn = copy(image);// new BufferedImage(200, 200,BufferedImage.TYPE_BYTE_INDEXED);
		 imageReturn = op.filter(imageReturn, null);
		 
		 return imageReturn;
	 }
	 public static BufferedImage binarisation(BufferedImage image){
		 int seuil =autoSeuillageMax( histogramme(image));
		 System.out.println("binarasation seuil : "+ seuil);
		 return binarisation(image,seuil);
	 }
	 public static BufferedImage binarisation(BufferedImage image, int seuil){
		 BufferedImage imageReturn = copy(image);
		 for(int x=0;x<image.getWidth();x++){
			 for(int y=0;y<image.getHeight();y++){
				 Color c = new Color(image.getRGB(x, y), true);				 
				 if(c.getRed()<seuil)
					 imageReturn.setRGB(x, y, 0xFF000000);
				 else
				 	imageReturn.setRGB(x, y, 0xFFFFFFFF);
			 }
		 }
		 return imageReturn;
	 }
	 public static BufferedImage binarisationZone(BufferedImage image,int tailleFiltre){
		 BufferedImage imageReturn=copy(image);
		 int demiTailleFiltre=tailleFiltre/2;
		 for(int x=demiTailleFiltre;x<image.getWidth()-demiTailleFiltre;x++){
			 for(int y=demiTailleFiltre;y<image.getHeight()-demiTailleFiltre;y++){
				 int temp=0;
				 for(int xFiltre=0;xFiltre<tailleFiltre;xFiltre++){
					 for(int yFiltre=0;yFiltre<tailleFiltre;yFiltre++){
						 Color c = new Color(image.getRGB(x+xFiltre-demiTailleFiltre, y+yFiltre-demiTailleFiltre));
						 temp=temp+c.getRed();
					 }
				 }
				 temp=temp/(tailleFiltre*tailleFiltre);
				 Color c=new Color(temp,temp,temp,0xFF);
				 imageReturn.setRGB(x, y, c.getRGB());
			 }
		 }
		 
		 return imageReturn;
	 }
	 public static int autoSeuillageMoyenne(int histo[]){
		 int seuil=0;
		 int nbPoint=0;
		 for(int i=0;i<256;i++)
			 nbPoint=nbPoint+histo[i];
		 
		 int temp=0;
		 for(int i=0;i<256;i++){
			 temp=temp+histo[i];
			 if(temp>=(nbPoint/2)){
				 seuil=i;
				 break;
			 }
		 }
		 
		 return seuil;
	 }
	 public static int autoSeuillageMax(int histo[]){
		 int seuil=0;
		 int nbPointMax=0;
		 for(int i=0;i<256;i++){
			 if(nbPointMax<histo[i]){
				 nbPointMax=histo[i];
			 }
		 }

		 for(int i=0;i<256;i++){
			 if(histo[i]>nbPointMax*0.11){
				 seuil=i;
				 break;
		 	}
		 }
		 
		 return seuil;
	 }
	 
	 
	 public static BufferedImage copy(BufferedImage bImage) {
		    int w = bImage.getWidth(null);
		    int h = bImage.getHeight(null);    
		    BufferedImage bImage2 = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		    Graphics2D g2 = bImage2.createGraphics();
		    g2.drawImage(bImage, 0, 0, null);
		    return bImage2;
		  }
	 
	 public static BufferedImage contrast(BufferedImage image) {
		 BufferedImage imageReturn=histoEgal(image,histogramme(image),10);
		 
		 /*
		int factor = (259 * (contrast + 255)) / (255 * (259 - contrast));
					
		for(int x=0;x<image.getWidth();x++){
			 for(int y=0;y<image.getHeight();y++){
				 Color color = new Color(image.getRGB(x, y),true);
				 int newColor = factor * (color.getBlue() - 128) + 128;
				 Color c = new Color(newColor, newColor, newColor, 255);
				 imageReturn.setRGB(x, y, c.getRGB());
			 }
		}*/
		 return imageReturn;
	 }
	 public static int[] histogramme(BufferedImage image){
		 int tabReturn[] = new int[256];
		 for(int x=0;x<image.getWidth();x++){
			 for(int y=0;y<image.getHeight();y++){
				 Color color = new Color(image.getRGB(x, y), true);
				 tabReturn[color.getBlue()]=tabReturn[color.getBlue()]+1;
			 }
		 }		 
		 return tabReturn;
		 
	 }
	 public static BufferedImage histoEgal(BufferedImage image, int [] histo, int seuil){
		 BufferedImage imageReturn = copy(image);
		 
		 int startHisto=0, endHisto=256;
		 
		 for(int i=0; i<256; i++){
			 if(histo[i]<seuil){
				 histo[i]=0;
			 }
		 }
		 for(int i=0;i<256; i++){
			 if(histo[i]>0){
				 startHisto=i;
				 break;
			 }
		 }
		 for(int i=255;i>=0; i--){
			 if(histo[i]>0){
				 endHisto=i;
				 break;
			 }
		 }
		 
		 double coeff=256.0/(endHisto-startHisto);
		 for(int x=0;x<image.getWidth();x++){
			 for(int y=0;y<image.getHeight();y++){
				 Color color = new Color(image.getRGB(x, y), true);
				 if(color.getBlue()<startHisto){
					 imageReturn.setRGB(x, y, 0xFF000000);
				 }
				 int temp=color.getGreen()-startHisto;
				 temp=(int) (temp*coeff);
				 if(temp>255){
					 temp=255;
				 }
				 if(temp<0){
					 temp=0;
				 }
				 Color c=new Color(temp,temp,temp,255);
				 imageReturn.setRGB(x, y, c.getRGB());
			 }
		 }
			 
		 return imageReturn;
	 }
	 
	 public static BufferedImage filtrageNonDestructif(BufferedImage image, int seuil){
		 BufferedImage imageReturn = copy(image);
		 for(int x=0;x<image.getWidth();x++){
			 for(int y=0;y<image.getHeight();y++){
				 int color=image.getRGB(x, y);
				 Color c = new Color(color, true);
 
				 if(c.getBlue()>seuil){
					 imageReturn.setRGB(x, y, color);
				 }
				 else{
					 imageReturn.setRGB(x, y, 0xFF000000);
				 }
			 }
		 }
		 return imageReturn;
	 }
	 
	 public static void histoDisplay(BufferedImage image){
		 int histo[]=histogramme(image);
		 for(int i=0;i<256;i++){
			 System.out.print(histo[i]+", ");
		 }
		 System.out.println("");
	 }
	 
	 public static BufferedImage moyenneur (BufferedImage image, int tailleFiltre){
		 BufferedImage imageReturn=copy(image);
		 int demiTailleFiltre=(int)tailleFiltre/2;
		 for(int x=demiTailleFiltre;x<image.getWidth()-demiTailleFiltre;x++){
			 for(int y=demiTailleFiltre;y<image.getHeight()-demiTailleFiltre;y++){
				 int tempRed=0,tempGreen=0,tempBlue=0;
				 for(int xFiltre=0;xFiltre<tailleFiltre;xFiltre++){
					 for(int yFiltre=0;yFiltre<tailleFiltre;yFiltre++){
						 Color c = new Color(image.getRGB(x+xFiltre-demiTailleFiltre, y+yFiltre-demiTailleFiltre));
						 tempRed=tempRed+c.getRed();
						 tempGreen=tempGreen+c.getGreen();
						 tempBlue=tempBlue+c.getBlue();
					 }
				 }
				 tempRed=tempRed/(tailleFiltre*tailleFiltre);
				 tempGreen=tempGreen/(tailleFiltre*tailleFiltre);
				 tempBlue=tempBlue/(tailleFiltre*tailleFiltre);
				 
				 if(tempRed>255)
					 tempRed=255;
				 if(tempGreen>255)
					 tempGreen=255;
				 if(tempBlue>255)
					 tempBlue=255;

				 Color col = new Color(tempRed,tempGreen,tempBlue,0xFF);
				 int rgb= col.getRGB() ;
				 imageReturn.setRGB(x, y, rgb);
			 }
		 }
		 return imageReturn;
	 }
	 public static BufferedImage filtreLigne (BufferedImage image,int tailleFiltre){
		 BufferedImage imageReturn=copy(image);

		 int demiTailleFiltre=(int)tailleFiltre/2;
		 for(int x=demiTailleFiltre;x<image.getWidth()-demiTailleFiltre;x++){
			 for(int y=demiTailleFiltre;y<image.getHeight()-demiTailleFiltre;y++){
				 int maxRed=0,maxGreen=0,maxBlue=0;
				 for(int xFiltre=0;xFiltre<tailleFiltre;xFiltre++){
						 Color c = new Color(image.getRGB(x+xFiltre-demiTailleFiltre, y));
						 if(maxRed<c.getRed())
							 maxRed=c.getRed();
						 if(maxGreen<c.getGreen())
							 maxGreen=c.getGreen();
						 if(maxBlue<c.getBlue())
							 maxBlue=c.getBlue();			 
				 }
								 
				 Color col = new Color(maxRed,maxGreen,maxBlue,0xFF);
				 int rgb= col.getRGB() ;
				 imageReturn.setRGB(x, y, rgb);
			 }
		 }
		 return imageReturn;
	 }
	 public static BufferedImage filtreColonne (BufferedImage image,int tailleFiltre){
		 BufferedImage imageReturn=copy(image);

		 int demiTailleFiltre=(int)tailleFiltre/2;
		 for(int x=demiTailleFiltre;x<image.getWidth()-demiTailleFiltre;x++){
			 for(int y=demiTailleFiltre;y<image.getHeight()-demiTailleFiltre;y++){
				 int maxRed=0,maxGreen=0,maxBlue=0;
				 for(int yFiltre=0;yFiltre<tailleFiltre;yFiltre++){
						 Color c = new Color(image.getRGB(x, y+yFiltre-demiTailleFiltre));
						 if(maxRed<c.getRed())
							 maxRed=c.getRed();
						 if(maxGreen<c.getGreen())
							 maxGreen=c.getGreen();
						 if(maxBlue<c.getBlue())
							 maxBlue=c.getBlue();			 
				 }
								 
				 Color col = new Color(maxRed,maxGreen,maxBlue,0xFF);
				 int rgb= col.getRGB() ;
				 imageReturn.setRGB(x, y, rgb);
			 }
		 }
		 return imageReturn;
	 }

	 public static BufferedImage contourGrey(BufferedImage image){
		 
		 BufferedImage imageReturn = copy(image);//new BufferedImage(image.getWidth()-1, image.getHeight()-1, 1);;
			for(int x=1; x<image.getWidth()-1;x++){
				for(int y=1; y<image.getHeight()-1;y++){
					Color current = new Color(image.getRGB(x, y),true);
					
					Color temp;
					temp=new Color(image.getRGB(x-1, y),true);					
					int red = Math.abs(current.getRed()-temp.getRed());
					temp=new Color(image.getRGB(x+1, y),true);
					red = red+Math.abs(current.getRed()-temp.getRed());
					temp=new Color(image.getRGB(x, y-1),true);
					red = red+Math.abs(current.getRed()-temp.getRed());
					temp=new Color(image.getRGB(x, y+1),true);
					red = red+Math.abs(current.getRed()-temp.getRed());
					red=red/4;
					
					Color c = new Color(red,red,red,0xFF);
					
					imageReturn.setRGB(x, y, c.getRGB());
				}
			}
			return imageReturn;		
	 }
	 public static BufferedImage invertGrey(BufferedImage image){
		 BufferedImage imageReturn=copy(image);
		 for(int x=0;x<image.getWidth();x++){
			 for(int y=0;y<image.getHeight();y++){
				 Color c1 = new Color(image.getRGB(x, y));
				 int temp=255-c1.getBlue();
				 Color c2 =new Color(temp,temp,temp,0xFF);
				 imageReturn.setRGB(x, y, c2.getRGB());
			 }
		 }
		 return imageReturn;
	 }
	 public static BufferedImage sum(BufferedImage image1,BufferedImage image2){
		 BufferedImage imageRetun = copy(image1);
		 for(int x=0;x<image1.getWidth();x++){
			 for(int y=0;y<image1.getHeight();y++){
				 Color c1 = new Color(image1.getRGB(x, y));
				 Color c2 = new Color(image2.getRGB(x, y));
				 int temp =(c1.getBlue()+c2.getBlue());
				 temp=temp/2;
				 if(temp>255){
					 temp=255;
				 }
				 if(temp<0){
					 temp=0;
				 }
				 Color c3 =new Color(temp,temp,temp,0xFF);
				 imageRetun.setRGB(x, y, c3.getRGB());
			 }
		 }
		 return imageRetun;
	 }
	 
}

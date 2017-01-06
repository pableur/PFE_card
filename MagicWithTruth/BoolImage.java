package MagicWithTruth;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class BoolImage {

	private ArrayList<Boolean>imageGris=new ArrayList<Boolean>();
	private int height=0;
	private int width=0;
	
	private static  boolean blanc = false;
	private static  boolean noir = true;
	
	public BoolImage(){	}
	public BoolImage(int x, int y){ creat(x,x,true); }
	public BoolImage(int x,int y, Boolean color){ creat(x,x,color);}
	public BoolImage(BoolImage image){copy(image);}
	public BoolImage(GreyImage image){creat(image.getWidth(),image.getHeight(),true);copy(image,128);}
	public BoolImage(GreyImage image, int seuil){creat(image.getWidth(),image.getHeight(),true);copy(image,seuil);}	
	
	public int getHeight(){ return this.height; }
	public int getWidth() { return this.width;  }
	public Boolean getColor (int x, int y){return this.imageGris.get(this.toIndex(x, y));}
	public void setColor(int x, int y, Boolean grey){this.imageGris.set(this.toIndex(x, y), grey);}	
	
	public BufferedImage display(){
		BufferedImage imageReturn = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for(int x=0;x<this.getWidth();x++){
			for(int y=0;y<this.getHeight();y++){
				boolean color=this.getColor(x, y);
				int colorReturn=255;
				if(color)
					colorReturn=0;
					
				Color c=new Color(colorReturn,colorReturn,colorReturn,0xFF);
				imageReturn.setRGB(x, y, c.getRGB());
			}
		}
		return imageReturn;
	}
	public void copy(BoolImage image){
		creat(image.getWidth(),image.getHeight(),true);
		for(int x=0; x<image.getWidth();x++){
			for(int y=0; y<image.getHeight(); y++){
				this.setColor(x, y,image.getColor(x, y));
			}
		}
	}
	public void copy(BufferedImage image,int seuil){this.copy(new GreyImage(image), seuil);}
	public void copy(GreyImage image, int seuil){
		for(int y=0;y<image. getHeight();y++){
			for(int x=0; x<image.getWidth();x++){
				if(image.getColor(x, y)>seuil)
					this.setColor(x,y,false);
				else
					this.setColor(x,y,true);
			}
		}	
	}
	
	private void creat(int xSize,int ySize, Boolean color){
		for(int i=0;i<xSize*ySize;i++){
			this.imageGris.add(color);
		}		
		this.width=xSize;
		this.height=ySize;
	}
	private int toIndex(int x, int y){
		return y*this.width+x;
	}

	public void erosion(int xMatrice, int yMatrice){
		BoolImage temp = new BoolImage(this);
		int yOffset=(yMatrice-1)/2;
		int xOffset=(xMatrice-1)/2;
		for(int y=yOffset;y<(height-yOffset);y++){
			for(int x=xOffset;x<(width-xOffset);x++){
				
				for(int yTest=(y-yOffset);yTest<(y+yOffset);yTest++){
					for(int xTest=(x-xOffset);xTest<(x+xOffset);xTest++){
						if(yTest!=y && xTest!=x){
							if(temp.getColor(xTest, yTest)==blanc){
								this.setColor(x, y, blanc);
							}
						}
					}
				}
				
			}
		}
	}
	
	public void dilatation(int xMatrice, int yMatrice){
		BoolImage temp = new BoolImage(this);
		int yOffset=(yMatrice-1)/2;
		int xOffset=(xMatrice-1)/2;
		for(int y=yOffset;y<(height-yOffset);y++){
			for(int x=xOffset;x<(width-xOffset);x++){
				for(int yTest=(y-yOffset);yTest<(y+yOffset);yTest++){
					for(int xTest=(x-xOffset);xTest<(x+xOffset);xTest++){
						if(yTest!=y && xTest!=x){
							if(temp.getColor(xTest, yTest)==noir){
								this.setColor(x, y, noir);
							}
						}
					}
				}
			}
		}
	}
	
}

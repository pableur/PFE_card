package MagicWithTruth;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GreyImage {
	//private ArrayList<ArrayList<Integer>> imageGris=new ArrayList<ArrayList<Integer>>();
	private ArrayList<Integer>imageGris=new ArrayList<Integer>();
	private int height=0;
	private int width=0;
	
	public GreyImage(){	}
	public GreyImage(int x, int y){ creat(x,x,255); }
	public GreyImage(int x,int y, int color){ creat(x,x,color);}
	public GreyImage(BufferedImage image){
		creat(image.getWidth(),image.getHeight(),255);		
		copy(image);		
	}
	
	public int getHeight(){ return this.height; }
	public int getWidth(){  return this.width; }
	public int getColor (int x, int y){
		//ArrayList<Integer>temp=this.imageGris.get(y);		
		return this.imageGris.get(this.toIndex(x, y));
	}
	/*
	public void setColor(int x, int y, int grey){
		if(x==3 && y==0)
			System.out.println(this.image);
		//System.out.println("x : "+x+" y : "+y);
		//ArrayList<Integer> element=this.image.get(y);
		
		//System.out.println(element);
		//element.set(x, grey);
		//System.out.println(element);
		//ArrayList<Integer> test = new ArrayList<Integer>();
		this.image.get(y).set(x, grey);
		//this.image.set(y,test);
		if(x==3 && y==0)
			System.out.println(this.image);
	}	*/
	public void setColor(int x, int y, int grey){
		this.imageGris.set(this.toIndex(x, y), grey);
	}	
	public BufferedImage display(){
		BufferedImage imageReturn = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for(int x=0;x<this.getWidth();x++){
			for(int y=0;y<this.getHeight();y++){
				int color=this.getColor(x, y);
				Color c=new Color(color,color,color,0xFF);
				imageReturn.setRGB(x, y, c.getRGB());
			}
		}
		return imageReturn;
	}
	public void copy(BufferedImage image){
		for(int y=0;y<image. getHeight();y++){
			for(int x=0; x<image.getWidth();x++){
				Color c = new Color(image.getRGB(x, y));
				int rgb=c.getBlue()+c.getRed()+c.getGreen();
				rgb=rgb/3;//255*y/image.getHeight();
				this.setColor(x,y,rgb);
			}
		}		
	}
	public void contrast(){
		int yMatrice=3;
		int xMatrice=3;
		int yOffset=(yMatrice-1)/2;
		int xOffset=(xMatrice-1)/2;
		for(int y=yOffset;y<(height-yOffset);y++){
			for(int x=xOffset;x<(width-xOffset);x++){
				int temp=0;
				for(int yTest=(y-yOffset);yTest<(y+yOffset);yTest++){
					for(int xTest=(x-xOffset);xTest<(x+xOffset);xTest++){
						temp=temp+this.getColor(xTest, yTest);
					}
				}
				temp=temp/(yMatrice*xMatrice);
				if(temp>255)
					temp=255;
				this.setColor(x, y, temp);
			}
		}
	}
	private void creat(int xSize,int ySize, int color){
		/*
		ArrayList<Integer> temp= new ArrayList<Integer>();
		for(int x=0;x<xSize;x++){
			temp.add(color);
		}
		for(int y=0;y<ySize;y++){
			this.imageGris.add(temp);
		}
		*/
		for(int i=0;i<xSize*ySize;i++){
			this.imageGris.add(color);
		}
		
		this.width=xSize;
		this.height=ySize;
	}	
	private int toIndex(int x, int y){
		return y*this.width+x;
	}
}

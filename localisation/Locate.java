import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Locate {

	private BufferedImage imageOrigin= null;
	private BufferedImage imageCard  = null;
	private BufferedImage imageNom = null;
	private BufferedImage imageType = null;
	private BufferedImage imageCout = null;
	private BufferedImage imageEdition = null;
	private BufferedImage imageContour = null;
			
	private boolean debugCard = false;
	private boolean debugCardLambert = true;
	private boolean debugPart = false;
	private boolean debugEdition = true;
	
	
	public Locate(BufferedImage image){
		this.imageOrigin=image;
	}
	
	// détection de la carte grace à la méthode de Mr Lambert
	public void cardLambert(){
		// afin d'accélérer les traitements on réduit l'image à environs 300px
		
		int coeff = 1;//imageOrigin.getHeight()/ 300; // coeff de réduction entier
		BufferedImage littelOrigin = Picture.resize(imageOrigin,imageOrigin.getWidth()/coeff,imageOrigin.getHeight()/coeff);
		GreyImage littelGrey = new GreyImage(littelOrigin);
		
		Interface ihm=null;
		if(debugCardLambert){
			ihm= new Interface("Détection de contour",3);
			ihm.addPicture("  Image gris", littelGrey.display(),300);
		}
		
		// detéction des bors
		littelGrey = this.lambert(littelGrey, 12, 40, 50);
		littelGrey.invertColor();	
		
		// prend seulement les fortes variations
		BinImage bin = new BinImage(littelGrey,200);
		
		imageContour=littelGrey.display();
		
		
		
		// détection des lignes
		// isole 4 parties correspondant au 4 bors
		
		
		int[] vertical = bin.verticalSum();	
		vertical = Filtre.abs(Filtre.derivative(vertical));
		
		int[] horizontal = bin.horizontalSum();
		horizontal = Filtre.abs(Filtre.derivative(horizontal));
		
		int border[] = new int[4];
		border[0]=0;
		border[1]=0;
		border[2]=littelGrey.getWidth();
		border[3]=littelGrey.getHeight();
		
		
		/***********************************************
		 
		détecte les bors de l'image grace aux projections
		avec un seuillage ) 10% de la valeur max
		
		***********************************************/
		
		int seuilLargeur = (int) (Collections.max(Filtre.IntToArray(vertical))*0.01);
		// largeure
		for(int i=0; i<vertical.length; i++){if(vertical[i]>seuilLargeur){border[0]=i;break;}}		
		for(int i=(int) (vertical.length-10); i>0; i--){if(vertical[i]>seuilLargeur){border[2]=i; break;}}
		// hauteur
		for(int i=0; i<horizontal.length; i++){	 if(horizontal[i]>0){ border[1]=i; break;}}
		for(int i= horizontal.length-10; i>0; i--){if(horizontal[i]>0){border[3]=i; break;}}
		
		// détection de ligne dans le bors supérieur
		
		BinImage supBorder = bin; //new BinImage(Picture.selectZone(bin.display(), border[0],border[1],border[2],(int)(border[1]+(border[3]-border[1])*0.05)));
		save(supBorder.display(),"blabla.png");
		Hough hough = new Hough(supBorder.getWidth() ,supBorder.getHeight());
		
		for(int x=0; x<supBorder.getWidth(); x++){
			for(int y=0; y<0; y++){
				if(supBorder.getBin(x, y)==true) hough.vote(x, y);
			}
		}
		
		double[] rhotheta = hough.winner();
		double rho = rhotheta[0];
		double theta = rhotheta[1];
		 
		double[] ab = hough.rhotheta_to_ab(rho, theta);;
		double estimated_a = ab[0];
		double estimated_b = ab[1];
	
		System.out.println("droite estimé --> "+estimated_a+","+estimated_b);
		bin.ligne((int)ab[0],(int)ab[1],0);
		save(bin.display(),"ligne.png");
		if(debugCardLambert){
			ihm.addPicture("  Filtre de Lambert", littelGrey.display(),300);
			ihm.addPicture("  Binariasation Filtre de Lambert", bin.display(),300);
		}
		
		// détection de l'angle
		
		int y1 = (int) (bin.getHeight()*0.5);
		
		int x1 = 0;
		int x2 = 0;
		
		int y2= bin.getHeight()/2-y1/2;
		for(int x=1; x<bin.getWidth(); x++){
			if(bin.getColor(x, y2)!=bin.getColor(x-1, y2)) break;
			x1++;
		}
		
		y2= bin.getHeight()/2+y1/2;
		for(int x=1; x<bin.getWidth(); x++){
			if(bin.getColor(x, y2)!=bin.getColor(x-1, y2)) break;
			x2++;
		}
			
		int delta= Math.abs(x2-x1);
		double H = Math.sqrt(Math.pow(y1, 2)+Math.pow((delta), 2) );
		double angle=  Math.asin(delta/H);
		if(x2-x1<0) angle=angle*-1;		
		//System.out.println("angle "+angle*57.3);
		
		AffineTransform tx = new AffineTransform();
		
		//AffineTransform.rotate(
		//			the angle of rotation measured in radians,
		//			the X coordinate of the rotation anchor point, 
		//			the Y coordinate of the rotation anchor point);
		
	    tx.rotate(angle, imageOrigin.getWidth() / 2, imageOrigin.getHeight() / 2);
	    
	    AffineTransformOp op = new AffineTransformOp(tx,AffineTransformOp.TYPE_BILINEAR);
	    imageOrigin = op.filter(imageOrigin, null);
	    bin.invertColor();
	    bin = new BinImage( op.filter(bin.display(), null));
	    bin.invertColor();
		
		vertical = bin.verticalSum();	
		vertical = Filtre.abs(Filtre.derivative(vertical));
		
		horizontal = bin.horizontalSum();
		horizontal = Filtre.abs(Filtre.derivative(horizontal));
		if(debugCardLambert){
			ihm.addHistogramme(" vertical sum Lambert",	vertical);
			ihm.addHistogramme(" horizontal sum Lambert", horizontal);
		}

		
		border[0]=0;
		border[1]=0;
		border[2]=littelGrey.getWidth();
		border[3]=littelGrey.getHeight();
		
		
		/***********************************************
		 
		détecte les bors de l'image grace aux projections
		avec un seuillage ) 10% de la valeur max
		
		***********************************************/
		
		seuilLargeur = (int) (Collections.max(Filtre.IntToArray(vertical))*0.01);
		// largeure
		for(int i=0; i<vertical.length; i++){			
			if(vertical[i]>seuilLargeur){
				border[0]=i;
				break;
			}
			
		}
		
		for(int i=(int) (vertical.length-10); i>0; i--){
			if(vertical[i]>seuilLargeur){
				border[2]=i;
				break;
			}
		}
		
		// hauteur
		for(int i=0; i<horizontal.length; i++){			
			if(horizontal[i]>0){
				border[1]=i;
				break;
			}
			
		}
		
		for(int i= horizontal.length-10; i>0; i--){
			if(horizontal[i]>0){
				border[3]=i;
				break;
			}
		}	
		
		if(debugCardLambert){
			bin.ligneVertical(border[0],0);
			bin.ligneVertical(border[2],0);		
			bin.ligneHorizontal(border[1], 0);
			bin.ligneHorizontal(border[3], 0);
			ihm.addPicture("  ", bin.display(),300);
		}
		
		imageCard = Picture.selectZone(imageOrigin,border[0]*coeff,border[1]*coeff,border[2]*coeff,border[3]*coeff);
				
		// tourne l'image si elle est plus large que haute
		if(imageCard.getWidth()>imageCard.getHeight()){
			imageCard=Picture.rotate90(imageCard);
		}		
	}
	
	//localise la carte dans l'image
	public void card(){
		// afin d'accélérer les traitements on réduit l'image à environs 300px
		int coeff = imageOrigin.getHeight()/ 300; // coeff de réduction entier
		BufferedImage littelOrigin = Picture.resize(imageOrigin,imageOrigin.getWidth()/coeff,imageOrigin.getHeight()/coeff);
		GreyImage littelGrey = new GreyImage(littelOrigin);
		
		Interface ihm=null;
		if(debugCard){
			ihm= new Interface("Détection de contour",3);
			ihm.addPicture("  Image gris", littelGrey.display());
		}
		
		// detéction des bors
		littelGrey = sobel(littelGrey);
		littelGrey.invertColor();		
		// prend seulement les fortes variations
		BinImage bin = new BinImage(littelGrey,10);
		
		if(debugCard){
			ihm.addPicture("  Filtre de Sobel", littelGrey.display());
			ihm.addPicture("  Binariasation Filtre de Sobel", bin.display());
		}
		
		int[] vertical = bin.verticalSum();	
		vertical = Filtre.abs(Filtre.derivative(vertical));
		
		int[] horizontal = bin.horizontalSum();
		horizontal = Filtre.abs(Filtre.derivative(horizontal));
		if(debugCard){
			ihm.addHistogramme(" vertical sum Sobel",	vertical);
			ihm.addHistogramme(" horizontal sum Sobel", horizontal);
		}

		int border[] = new int[4];
		border[0]=0;
		border[1]=0;
		border[2]=littelGrey.getWidth();
		border[3]=littelGrey.getHeight();
		
		
		/***********************************************
		 
		détecte les bors de l'image grace aux projections
		avec un seuillage ) 10% de la valeur max
		
		***********************************************/
		
		int seuilLargeur = (int) (Collections.max(Filtre.IntToArray(vertical))*0.1);
		// largeure
		for(int i=0; i<vertical.length; i++){			
			if(vertical[i]>seuilLargeur){
				border[0]=i+5;
				break;
			}
			
		}
		
		for(int i=(int) (vertical.length-10); i>0; i--){
			if(vertical[i]>seuilLargeur){
				border[2]=i-5;
				break;
			}
		}
		
		// hauteur
		for(int i=0; i<horizontal.length; i++){			
			if(horizontal[i]>0){
				border[1]=i+5;
				break;
			}
			
		}
		
		for(int i= horizontal.length-10; i>0; i--){
			if(horizontal[i]>0){
				border[3]=i-5;
				break;
			}
		}
		
		if(debugCard){
			bin.ligneVertical(border[0],0);
			bin.ligneVertical(border[2],0);		
			bin.ligneHorizontal(border[1], 0);
			bin.ligneHorizontal(border[3], 0);
			ihm.addPicture("  ", bin.display());
		}
		
		imageCard = Picture.selectZone(imageOrigin,border[0]*coeff,border[1]*coeff,border[2]*coeff,border[3]*coeff);
				
		// tourne l'image si elle est plus large que haute
		if(imageCard.getWidth()>imageCard.getHeight()){
			imageCard=Picture.rotate90(imageCard);
		}
		
	}
	
	
	public void masque(){
		int offsetName =  (5*imageCard.getHeight())/88;
		int offsetType =  (50*imageCard.getHeight())/88;
		int hauteur =  (4*imageCard.getHeight())/88;
		int marge = (int) ((5*imageCard.getWidth())/63);
		int edition = (int) ((5*imageCard.getWidth())/63);
	
		int border[] = new int[4];	
		
		border[0]=marge;
		border[1]=offsetType;
		border[2]=imageCard.getWidth()-marge-edition;
		border[3]=offsetType+hauteur;
		
		this.imageType =Picture.selectZone(imageCard,border[0],border[1],border[2],border[3]);
		
		border[0]=imageCard.getWidth()-marge-edition;
		border[2]=border[0]+edition;
		
		this.imageEdition =Picture.selectZone(imageCard,border[0],border[1],border[2],border[3]);
		
		border[0]=marge;
		border[1]=offsetName;
		border[2]=imageCard.getWidth()-marge*3;
		border[3]=offsetName+hauteur;
		this.imageNom =Picture.selectZone(imageCard,border[0],border[1],border[2],border[3]);
		
		
		GreyImage tempGrey = new GreyImage(this.imageNom);
		//tempGrey.egalisation();
		this.imageNom=tempGrey.display() ;
		
		GreyImage tempType = new GreyImage(this.imageType);
		//tempType.egalisation();
		this.imageType=tempType.display() ;

	}
	public void part(){
		int offsetName =  (5*imageCard.getHeight())/88;
		int offsetType =  (50*imageCard.getHeight())/88;
		int hauteur =  (4*imageCard.getHeight())/88;
				
		/*
		System.out.println(offsetName);
		System.out.println(offsetType);
		System.out.println(hauteur);
		*/
		
		GreyImage name = new GreyImage( Picture.selectZone(imageCard,0,(int)offsetName,imageCard.getWidth(),(int)offsetName+hauteur));
		GreyImage type = new GreyImage( Picture.selectZone(imageCard,0,offsetType,imageCard.getWidth(),offsetType+hauteur));
		
		name.egalisation();
		float[][] matrice={				
				{1/3f},
				{1/3f},
				{1/3f}
		};
		name.convolution(matrice);
		
		name=this.sobel(name);
		int[] signalName = name.verticalSum();
		
		signalName=Filtre.dillatation(signalName, 10);
		
		signalName=Filtre.moyenneur(signalName, 10);
		float moyenne = (float) (Filtre.min(signalName));
				
		for(int i=0; i<signalName.length; i++){
			if(signalName[i]>moyenne) signalName[i]=100;
			else signalName[i]=0;
		}
		
		signalName=Filtre.abs(Filtre.derivative(signalName));
		int[] raie =Filtre.raie(signalName);
			
		int border[] = new int[4];
		int index = 0;	
		
		if(raie.length==2){
			border[0]=0;
			border[1]=raie[0];
			border[2]=raie[1];
			border[3]=name.getWidth();
		}
		else{
			if(raie.length<4) return;
			for(int i=0; i<4; i++){
				if(raie[i]!=0){
					border[index]=raie[i];
					index++;
				}
			}		
		}
		
		this.imageNom =Picture.selectZone(imageCard,border[0],(int)offsetName,border[1],(int)offsetName+hauteur);
		//GreyImage tempGrey = new GreyImage(this.imageNom);
		//tempGrey.egalisation();
		//this.imageNom=tempGrey.display() ;
		this.imageCout =Picture.selectZone(imageCard,border[2],(int)offsetName,border[3],(int)offsetName+hauteur);
		
		
		/*
		 * Détection du type
		 */
		type.egalisation();		
		type.convolution(matrice);
		
		type=this.sobel(type);
		
		int[] signalType = type.verticalSum();
		
		signalType=Filtre.dillatation(signalType, 10);
		
		signalType=Filtre.moyenneur(signalType, 10);
		moyenne = (float) (Filtre.min(signalType));
				
		for(int i=0; i<signalType.length; i++){
			if(signalType[i]>moyenne) signalType[i]=100;
			else signalType[i]=0;
		}
		
		signalType=Filtre.abs(Filtre.derivative(signalType));
		raie =Filtre.raie(signalType);
					
		index = 0;	
		
		if(raie.length==1){
			border[0]=0;
			border[1]=raie[0];
			border[2]=raie[0];
			border[3]=name.getWidth();
		}
		 else if(raie.length==2){
			border[0]=0;
			border[1]=raie[0];
			border[2]=raie[1];
			border[3]=name.getWidth();
		}
		else{
			if(raie.length<4) return;
			for(int i=0; i<4; i++){
				if(raie[i]!=0){
					border[index]=raie[i];
					index++;
				}
			}		
		}
		
		this.imageType =Picture.selectZone(imageCard,border[0],(int)offsetType,border[1],(int)offsetType+hauteur);
		//tempGrey = new GreyImage(this.imageType);
		//tempGrey.egalisation();
		//this.imageType=tempGrey.display() ;
		this.imageEdition =Picture.selectZone(imageCard,border[2],(int)offsetType,border[3],(int)offsetType+hauteur);
		
		/**********************
		 Affichage
		 **********************/
		if(debugPart){
			Interface ihm=null;
			ihm= new Interface("Détection des parties",1);
			ihm.addPicture("  Image gris nom", name);		
			ihm.addHistogramme("",signalName);
			ihm.addPicture("  Image gris type", type);		
			ihm.addHistogramme("",signalType);
			//ihm.addHistogramme("", type.verticalSum());
		}
	}
	
	public void part3(){
		// afin d'accélérer les traitements on réduit l'image à environs 300px
		int coeff = imageCard.getHeight()/ 300; // coeff de réduction entier
		BufferedImage littelOrigin = Picture.resize(imageCard,imageCard.getWidth()/coeff,imageCard.getHeight()/coeff);
		GreyImage littelGrey = new GreyImage(littelOrigin);
		
		GreyImage sobel = new GreyImage(sobel(littelGrey).display());
		sobel.invertColor();
		
		float[][] matrice={				
				{1/3f,1/3f,1/3f}
		};
		sobel.convolution(matrice);
		
		/**********************
		 détection de lignes
		 **********************/
		BinImage bin = new BinImage(sobel,175);
		
		
		BinImage erosion = new BinImage(bin);
		float[][] matriceErosionCol={
				{1},
				{1},
				{1}
				//{1}
		};
		for(int i=0; i<5; i++)
			erosion.erosion(matriceErosionCol);
		float[][] matriceErosionLigne={
				{1,1,1}
		};
		for(int i=0; i<1; i++)
			erosion.erosion(matriceErosionLigne);
		
		int[] horizontal = erosion.horizontalSum();
		for(int i=0; i<5;i++)
		horizontal = Filtre.moyenneur(horizontal, 10);
		int[] ligne = Filtre.raie(horizontal);
		
		for(int i=0; i<ligne.length;i++)
			System.out.println(ligne[i]);
		
		/**********************
			Affichage
		**********************/
		if(debugPart){
			Interface ihm=null;
			ihm= new Interface("Détection des parties",3);
			ihm.addPicture("  Image gris", littelGrey.display());
			ihm.addPicture("  Image sobel", sobel.display());
			ihm.addPicture("  Image bin", bin.display());
			ihm.addPicture("  Image bin", erosion.display());
			ihm.addHistogramme("", horizontal);
		}
	}
	
	public void part2(){
		Interface ihm=null;
		
		// afin d'accélérer les traitements on réduit l'image à environs 300px
		int coeff = imageCard.getHeight()/ 300; // coeff de réduction entier
		BufferedImage littelOrigin = Picture.resize(imageCard,imageCard.getWidth()/coeff,imageCard.getHeight()/coeff);
		GreyImage littelGrey = new GreyImage(littelOrigin);
		
		GreyImage sobel = new GreyImage(sobel(littelGrey).display());
		sobel.invertColor();
		
		float[][] matrice={
				{1/9f,1/9f,1/9f},
				{1/9f,1/9f,1/9f},
				{1/9f,1/9f,1/9f}
		};
		sobel.convolution(matrice);
		
		BinImage bin = new BinImage(sobel,175);
		
		
		BinImage erosion = new BinImage(bin);
		float[][] matriceErosionCol={
				{1},
				{1},
				{1}
				//{1}
		};
		for(int i=0; i<5; i++)
			erosion.erosion(matriceErosionCol);
		float[][] matriceErosionLigne={
				{1,1,1}
		};
		for(int i=0; i<1; i++)
			erosion.erosion(matriceErosionLigne);
		
		
		int[] horizontal = erosion.horizontalSum();
		//for(int i=0; i<5;i++)
		//horizontal = Filtre.moyenneur(horizontal, 10);
		
		/*
		for(int i=0; i<horizontal.length;i++)
			System.out.println(horizontal[i]);
			*/

		int[] ligne = Filtre.raie(horizontal);
		
		for(int i=0; i<ligne.length;i++){
			//System.out.println("ligne : "+ligne[i]);
			sobel.ligneHorizontal(ligne[i], 0);
		}
		
		// détections des parties
		ArrayList<BufferedImage> listPart = new ArrayList<BufferedImage>();
		for(int i=0; i<ligne.length-1;i++){
			int border[] = new int[4];
			border[0]=0;
			border[1]=ligne[i];
			border[2]=littelGrey.getWidth();
			border[3]=ligne[i+1];
			BufferedImage temp = Picture.selectZone(imageCard,border[0]*coeff,border[1]*coeff,border[2]*coeff,border[3]*coeff);
			
			// détermine le taux d'information dans l'image			
			if(temp.getHeight()< (temp.getWidth()*0.2) && temp.getHeight() > (temp.getWidth()*0.02)){	
				listPart.add(temp);
			}			
		}
		
		ArrayList<Integer> remove = new ArrayList<Integer>();
		for(int index=0; index<listPart.size();index++){
			GreyImage tempSobel = sobel(new GreyImage(listPart.get(index)));
			int[] sum = tempSobel.verticalSum();
			sum = Filtre.moyenneur(sum, (int) (sum.length*0.01));
			sum = Filtre.abs(Filtre.derivative(sum));
			System.out.println(Filtre.moyenne(sum)/tempSobel.getWidth()*100);
			
			// si le taux d'information dans l'image est plus faible que 10%
			if((Filtre.moyenne(sum)/tempSobel.getWidth()*100)<7)
				remove.add(index);
		}
		
		// supprime les images non importante
		for(int index = remove.size()-1; index>=0; index--) listPart.remove(index);
		
		// limitte aux 2 premières images
		for(int index = listPart.size()-1; index>=0; index--){
			if(listPart.size()<=2) break;
			listPart.remove(index);
		}
		
		/*******************************************/
		// localise le nom et le cout
		/*******************************************/
		BufferedImage img = listPart.get(0);
		
		GreyImage greyImg = new GreyImage (img);
		
		//for(int i=0; i<5; i++)
		//greyImg.convolution(matrice);
		
		int[] signal = sobel(greyImg).verticalSum();
		
		//signal = Filtre.moyenneur(signal, 20);
		signal = Filtre.abs(Filtre.derivative(signal));
		//for(int i=0; i<10; i++)
		//signal = Filtre.moyenneur(signal, 10);
		signal=Filtre.dillatation(signal, 20);
		System.out.println("moyenne "+Filtre.moyenne(signal));
		
		
		/*
		for(int i =0; i<signal.length; i++){
			if(signal[i]>50)
				signal[i] =100;
			else
				signal[i]=0;
		}
		signal=Filtre.abs(Filtre.derivative(signal));
		*/
		Interface ihmPart2= new Interface("Détection des parties images",1);
		ihmPart2.addPicture("", sobel(greyImg));
		ihmPart2.addHistogramme("blabla", signal);
		
		int border[] = new int[4];
		border[0]=50;
		border[1]=550;
		border[2]=850;
		border[3]=1000;
		imageNom = Picture.selectZone(img,border[0],0,border[1],img.getHeight());
		//imageCout = Picture.selectZone(img,border[2],0,border[3],img.getHeight());
			
		
		/*******************************************/
		// localise le type et l'édition
		/*******************************************/
		/*
		img = listPart.get(1);

		signal = sobel(new GreyImage (img)).verticalSum();
		signal = Filtre.moyenneur(signal, 20);
		signal = Filtre.abs(Filtre.derivative(signal));
		signal = Filtre.moyenneur(signal, 20);
		System.out.println("moyenne "+Filtre.moyenne(signal));
		
		// binarise le signal afin de déterminer des troncons
		for(int i =0; i<signal.length; i++){
			if(signal[i]>50)
				signal[i] =100;
			else
				signal[i]=0;
		}
		signal=Filtre.abs(Filtre.derivative(signal));
		ArrayList<Integer> raie = Filtre.raie(Filtre.IntToArray(signal));
		
		//ajoute un pic à la fin de l'image
		raie.add(img.getWidth());
		
		// selectionne les zones d'informations dans les troncons 
		for(int i=0; i<4; i++)
			border[i]=0;
		
		int seuilInfo = 20;
		for(int i =0; i<raie.size(); i++){
			if(raie.get(i)>img.getWidth()) break;
			GreyImage tempImg =new GreyImage(Picture.selectZone(img,border[0],0,raie.get(i),img.getHeight()));
			System.out.println("info "+tempImg.info());
			ihmPart2.addPicture("blabla", sobel(tempImg).display());
			
			if(border[2]==0){
				// si le bloc est petit mais contient de l'information on regarde si il peut être regroupé avec le suivant
				if(tempImg.getWidth()<1.5*img.getHeight() && tempImg.info()>seuilInfo && i<raie.size()-1){
					GreyImage tempImg2 =new GreyImage(Picture.selectZone(img,raie.get(i),0,raie.get(i+1),img.getHeight()));
					if (tempImg2.info()<seuilInfo){
						border[0]=raie.get(i);
					}
				}
				else if(tempImg.info()>seuilInfo){
					border[1]=raie.get(i);
				}
				else if(border[1]!=0 && tempImg.info()<seuilInfo){
					border[2]=raie.get(i);
				}else{
					border[0]=raie.get(i);	
				}
					
			}else{
				if(tempImg.getWidth()<1.5*img.getHeight() && tempImg.info()>seuilInfo && i<raie.size()-1){
					GreyImage tempImg2 =new GreyImage(Picture.selectZone(img,raie.get(i),0,raie.get(i+1),img.getHeight()));
					if (tempImg2.info()<seuilInfo){
						border[2]=raie.get(i);
					}
				}
				else if(tempImg.info()>seuilInfo){
					border[3]=raie.get(i);
				}else{
					border[2]=raie.get(i);	
				}
			}	
		}
		
		
		imageType = Picture.selectZone(img,border[0],0,border[1],img.getHeight());
		imageEdition = Picture.selectZone(img,border[2],0,border[3],img.getHeight());
		*/
		
		if(debugPart){
			ihm= new Interface("Détection des parties",3);
			ihm.addPicture("  Image gris", littelGrey.display());
			ihm.addPicture("  Sobel", sobel.display());
			ihm.addPicture("  bin", bin.display());
			ihm.addHistogramme(" horizontal sum Sobel", horizontal);
			//ihm.addHistogramme(" horizontal sum Sobel", sobel.histogramme());
			
			ihm.addPicture("  erosion", erosion.display());
			
			Interface ihmPart= new Interface("Détection des parties images",1);
			Interface ihmHisto= new Interface("Détection des parties histogrammes",1);
			
			for(int i=0; i<listPart.size();i++){
				GreyImage tempSobel = sobel(new GreyImage (listPart.get(i)));
				int[] sum = tempSobel.verticalSum();
				sum = Filtre.moyenneur(sum, 20);
				sum = Filtre.abs(Filtre.derivative(sum));
				sum = Filtre.moyenneur(sum, 20);
				ihmPart.addPicture("  part",listPart.get(i));
				ihmHisto.addHistogramme(" ",sum);				
				save(listPart.get(i),i+".png");
			}
		}
	}

	public void editionResize(){
		// on sait les dimensions du logo de l'édition
		this.imageEdition = Picture.selectZone(this.imageEdition,
				(int)(this.imageEdition.getWidth()-this.imageEdition.getHeight()*1.35)-1,
				0,
				this.imageEdition.getWidth(),
				this.imageEdition.getHeight()
				);
		
		//this.imageEdition  =  scale(this.imageEdition,10);
		
		/*
		GreyImage edition = new GreyImage(this.imageEdition);
		GreyImage sobelHorizontal = sobelHorizontal(edition);
		int[] signalHorizontal = sobelHorizontal.horizontalSum();
		signalHorizontal=Filtre.derivative(Filtre.dillatation(signalHorizontal, 5));
		signalHorizontal=Filtre.abs(Filtre.moyenneur(signalHorizontal,3));
		
		int offsetX=0;
		int max =0;
		for(int i=10; i<signalHorizontal.length; i++){
			if(signalHorizontal[i]>max){
				max=signalHorizontal[i];
				offsetX = i;
			}
		}
		
		GreyImage sobelVertical = sobelVertical(edition);
		int[] signalVertical = sobelVertical.verticalSum();
		signalVertical=Filtre.derivative(Filtre.dillatation(signalVertical, 5));
		signalVertical=Filtre.abs(Filtre.moyenneur(signalVertical,3));
		
		int offsetY=0;
		max =0;
		for(int i=10; i<signalVertical.length; i++){
			if(signalVertical[i]>max){
				max=signalVertical[i];
				offsetY = i;
			}
		}

		
		//System.out.println(Filtre.moyenne(signalHorizontal));
		System.out.println(this.imageEdition.getWidth()+" : "+this.imageEdition.getHeight());
		System.out.println(offsetY+" : "+offsetX);
		
		this.imageEdition = Picture.selectZone(this.imageEdition,
				offsetY,
				0,
				this.imageEdition.getWidth(),
				offsetX
				);
		this.imageEdition = scale(this.imageEdition,10);
		*/
		/***********************
		 Affichage
		 **********************/
		/*
		if(debugEdition){
			Interface ihm=null;
			ihm= new Interface("localisation de l'Edition",1);
			ihm.addPicture("  Edition ", this.imageEdition);	
			ihm.addPicture("  sobelHorizontal", sobelHorizontal);		
			ihm.addHistogramme("",signalHorizontal);
			ihm.addPicture("  sobelVertical", sobelVertical);		
			ihm.addHistogramme("",signalVertical);
			//ihm.addHistogramme("", type.verticalSum());
		}
		*/
	}
	
	public GreyImage sobel(BinImage image){return image.toGrey();}
	public GreyImage sobel(GreyImage image){
		// D'apres : https://fr.wikipedia.org/wiki/Filtre_de_Sobel
		
		GreyImage Gx = new GreyImage(image.display());
		GreyImage Gy = new GreyImage(image.display());
		GreyImage G  = new GreyImage(image.display());
		// filtre de Sobel
		float[][] matriceHorizontale ={				
				{-1,0,1},
				{-2,0,2},
				{-1,0,1}
			};
		
				
		float[][] matriceVertical ={				
				{-1,-2,-1},
				{ 0, 0, 0},
				{ 1, 2, 1}
			};
		
		Gx.convolution(matriceHorizontale,0.25f);
		Gy.convolution(matriceVertical,0.25f);
		
		for(int x=0; x<image.getWidth();x++){
			for(int y=0; y<image.getHeight() ;y++){
				double temp =  Math.sqrt(Math.pow(Gx.getColor(x, y),2)+Math.pow(Gy.getColor(x, y),2));
				G.setColor(x, y, (int)temp);
			}
		}
		return G;
	}
	public GreyImage sobelVertical(GreyImage image){
		// D'apres : https://fr.wikipedia.org/wiki/Filtre_de_Sobel
		
		GreyImage Gx = new GreyImage(image.display());

		// filtre de Sobel
		float[][] matriceHorizontale ={				
				{-1,0,1},
				{-2,0,2},
				{-1,0,1}
			};
		
		Gx.convolution(matriceHorizontale,0.25f);

		return Gx;
	}
	public GreyImage sobelHorizontal (GreyImage image){
		// D'apres : https://fr.wikipedia.org/wiki/Filtre_de_Sobel
		
		GreyImage Gx = new GreyImage(image.display());

		// filtre de Sobel
		float[][] matriceVertical ={				
				{-1,-2,-1},
				{ 0, 0, 0},
				{ 1, 2, 1}
			};
		
		Gx.convolution(matriceVertical,0.25f);

		return Gx;
	}
	
	public static BufferedImage scale(BufferedImage bi, double scaleValue) {
        AffineTransform tx = new AffineTransform();
        tx.scale(scaleValue, scaleValue);
        AffineTransformOp op = new AffineTransformOp(tx,
                AffineTransformOp.TYPE_BILINEAR);
        BufferedImage biNew = new BufferedImage( (int) (bi.getWidth() * scaleValue),
                (int) (bi.getHeight() * scaleValue),
                bi.getType());
        return op.filter(bi, biNew);
	}
		
	public GreyImage prewitt(GreyImage image){
		// D'apres : https://fr.wikipedia.org/wiki/Filtre_de_Sobel
		
		GreyImage Gx = new GreyImage(image.display());
		GreyImage Gy = new GreyImage(image.display());
		GreyImage G  = new GreyImage(image.display());
		// filtre de Sobel
		float[][] matriceHorizontale ={				
				{-1,0,1},
				{-1,0,1},
				{-1,0,1}
			};
		
				
		float[][] matriceVertical ={				
				{-1,-1,-1},
				{ 0, 0, 0},
				{ 1, 1, 1}
			};
		
		Gx.convolution(matriceHorizontale,0.25f);
		Gy.convolution(matriceVertical,0.25f);
		
		for(int x=0; x<image.getWidth();x++){
			for(int y=0; y<image.getHeight() ;y++){
				double temp =  Math.sqrt(Math.pow(Gx.getColor(x, y),2)+Math.pow(Gy.getColor(x, y),2));
				G.setColor(x, y, (int)temp);
			}
		}
		return G;
	}
	
	public GreyImage ligne(BinImage image){return image.toGrey();}
	public GreyImage ligne(GreyImage image){
		// D'apres : https://fr.wikipedia.org/wiki/Filtre_de_Sobel
		
		GreyImage Gx = new GreyImage(image.display());
		GreyImage Gy = new GreyImage(image.display());
		GreyImage G  = new GreyImage(image.display());

		float[][] matriceHorizontale ={				
				{-1,0,1},
				{-1,0,1},
				{-1,0,1}
			};
						
		float[][] matriceVertical ={				
				{-1,-1,-1},
				{ 0, 0, 0},
				{ 1, 1, 1}
			};
		
		Gx.convolution(matriceHorizontale,0.25f);
		Gy.convolution(matriceVertical,0.25f);
		
		for(int x=0; x<image.getWidth();x++){
			for(int y=0; y<image.getHeight() ;y++){
				double temp =  Math.sqrt(Math.pow(Gx.getColor(x, y),2)+Math.pow(Gy.getColor(x, y),2));
				G.setColor(x, y, (int)temp);
			}
		}
		return G;
	}
	
	private GreyImage lambert(GreyImage image, int M, int N, int delta){
		int X = M/3;
		GreyImage returnImage =  new GreyImage(image.getWidth(), image.getHeight());
		
		for(int x=0; x<image.getWidth(); x++){
			for(int y=0; y<image.getHeight(); y++){
				int grey = 0;
				int z1=0,z3;
				//System.out.println(x+" "+y);
				// bords vertical	
							
				z1 = this.moyenne(image,x-((int)(M/2)),y-N/2,X,N);
				z3 = this.moyenne(image,x+((int)(M/2)),y-N/2,X,N);
				//System.out.println("Z1 "+z1+" Z3 "+z3);
				if(z1>(z3+delta)){
					grey+=150;
				}
				if(z3>(z1+delta)){
					grey+=150;
				}
				
				// bords horizontal
				z1 = this.moyenne(image,x-((int)(N/2)),y-M/2,N,X);
				z3 = this.moyenne(image,x-((int)(N/2)),y+M/2,N,X);
				
				if(z1>(z3+delta)){
					grey+=150;
				}
				if(z3>(z1+delta)){
					grey+=150;
				}				
				
				returnImage.setColor(x, y, grey);
			}
		}
		
		return returnImage;
	}
	private int moyenne(GreyImage image, int x0, int y0, int width, int height){
		long value = 0;
		//System.out.println("X0 "+x0+" | Y0 "+y0+" | width "+width+" | height "+height);
		for(int x=x0; x<x0+width; x++){
			for(int y=y0; y<y0+height; y++){
				int tempX=x;
				int tempY=y;						
				
				//effet mirroir
				if(x<0){ tempX=Math.abs(x);}
				else if (x>=image.getWidth()){tempX = 2*image.getWidth()-x-1;}
				
				if(y<0) {tempY = Math.abs(y);}
				else if (y>=image.getHeight()){tempY = 2*image.getHeight()-y-1;}
				value=value+image.getColor(tempX, tempY);
				//System.out.println(image.getColor(tempX, tempY));
			}
		}		
		// retourne la moyenne
		return (int) (value/(width*height));
	}
	
	
	public BufferedImage getImageCard(){ return imageCard;}
	
	private static void save(BufferedImage image,String file){
		try {
			ImageIO.write(image, "png", new File(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage getImageOrigin() 	{return imageOrigin;}
	public BufferedImage getImageNom()		{return imageNom;	}
	public BufferedImage getImageType() 	{return imageType;}
	public BufferedImage getImageCout() 	{return imageCout;}
	public BufferedImage getImageEdition() 	{return imageEdition;}
	public BufferedImage getImageContour() 	{return imageContour;}
}

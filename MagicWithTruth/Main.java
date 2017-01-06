package MagicWithTruth;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Main {
	// args[0] input file
	// args[2] ouput dir 
	public static void main(String[] args) {
		
		boolean affichageIHM=true;
		boolean affichageReconnaissance=true;
		boolean affichageImageAvantTraitement=true;
		boolean affichageTextZone=true;
		boolean affichageHistogramme=true;
		boolean detectionOCR=true;
		BufferedImage monImage=null;
		BufferedImage truth;
		String outputPath="C:\\Users\\adrien\\workspace\\MagicWithTruth\\";		
		String repCourant="";
		String OS = System.getProperty("os.name").toLowerCase();
		boolean internet=false;
		
		TimeRegister register=null;
		
		// Interprétteur de commande
		for(int numArgs=0;numArgs<args.length;numArgs++) {
			String argument=args[numArgs];			
			
			if(argument.substring(0,1).equalsIgnoreCase("-")){
				switch(argument.substring(1).toLowerCase()){
					case "help": ; 
					case "h": System.out.println(help());break;
					case "image": ;
					case "i":
						try {
							monImage = ImageIO.read(new File(args[++numArgs]));
						} catch (IOException e) {						
							e.printStackTrace();
						}break;
					case "output":;
					case "o":outputPath=args[++numArgs];break;
					case "visuel":;
					case "v":affichageIHM=true;break;
					case "bdd":;case "b":;break;
					case "a":;case "affichage": 
						switch(args[++numArgs].toLowerCase()){
							case "ihm": affichageIHM=true;break;
							case "reconnaissance": affichageReconnaissance=true;break;
							case "picture": affichageImageAvantTraitement=true;break;
							case "text": affichageTextZone=true;break;
							case "histo": affichageHistogramme=true;break;
						}break;				
				}
			}
		}
		
		if(internet){
			register = new TimeRegister("jdbc:mysql://localhost:3306/magic", "root", "magicpswd", "TimeRegister");
		}
		
		if(monImage==null){
			try {
				
				//monImage = ImageIO.read(new File("images/photo_1.jpg"));
				monImage = ImageIO.read(new File("images/photo_2.jpg"));
				//monImage = ImageIO.read(new File("images/photo_3.jpg"));
				//monImage = ImageIO.read(new File("images/serra.jpg"));	
				//monImage = ImageIO.read(new File("images/tezzeret.jpg"));	
				//monImage = ImageIO.read(new File("images/balefire_dragon.jpg"));			
				//monImage = ImageIO.read(new File("images/hundred.jpg"));
			} catch (IOException e) {
				System.out.println("file not found");
				e.printStackTrace();
				return ;
			}
		}

		if(OS.indexOf("win")>=0){
			repCourant = new java.io.File(new java.io.File("").getAbsolutePath()).toString() ;			
		}else if(OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0) {
			repCourant = "/var/www/html/MagicWithTruth";			
		}else{
			System.out.println("impossile de dettecter l'OS");
		}
		System.out.println(repCourant);

		try {		
			truth = ImageIO.read(new File(repCourant+"/images/verite.png"));
		} catch (IOException e) {
			System.out.print("file not found");
			e.printStackTrace();
			return ;
		}
		if(internet){
			register.balise("start traitement");
		}
		int border[] = Picture.borderCard(monImage);
		BufferedImage imageResize = Picture.selectZone(monImage,border[0],border[2],border[1],border[3]);
		truth=Picture.resize(truth,imageResize.getWidth(),imageResize.getHeight());
		
		BufferedImage imageZone=Picture.masque(imageResize,truth);
		
		if(affichageIHM){
			Interface ihm= new Interface("detection des zones");
			ihm.addPicture("Image d'origine", imageResize);
			ihm.addPicture("Verite", truth);
			ihm.addPicture("image + verite", imageZone);
		}
		
		ArrayList<BufferedImage> imageZones=Picture.detecZone(imageResize,truth);
		
		if(imageZones.size()!=6){
			System.out.println("les "+imageZones.size()+" zones on été trouvé");	
			if(affichageTextZone){
				Interface textZone= new Interface("zones trouvé");
				for(int i=0;i<imageZones.size();i++){
					textZone.addPicture("", imageZones.get(i));
				}
			}
			return;
		}
		else{
			System.out.println("les 6 zones on ete trouve");
			String titre[]={"name","cost","type","extension","description","value"};
			if(affichageTextZone){
				Interface textZone= new Interface("zones trouvé");
				for(int i=0;i<imageZones.size();i++){
					textZone.addPicture("", imageZones.get(i));
				}
			}		
			for(int i=0;i<imageZones.size();i++){
				save(imageZones.get(i),outputPath+titre[i]+"BRUT.png");
			}
						
			Interface reconnaissance = null;
			Interface imageAvantTraitement = null;
			if(affichageImageAvantTraitement){
				imageAvantTraitement= new Interface("imageAvantTraitement");
			}
			if(affichageReconnaissance){
				reconnaissance= new Interface("Reconnaissance");
			}
		
			// traitement des images
			if(internet){
				register.balise("traitement image");
			}
			int i=0;
			
			do{				
				
				// nuance de gris				
				BufferedImage imageValue = Picture.grey(imageZones.get(i));								
				imageValue=Picture.contrast(imageValue);
				GreyImage greyImage=new GreyImage(imageValue);
								
				//greyImage.contrast();
				
				Histogramme histo = new Histogramme(titre[i],greyImage);
							
				if(affichageHistogramme){					
					histo.display();
				}
								
				BoolImage imageBinarise= new BoolImage(greyImage,histo.seuilBinarisation());
				if(affichageImageAvantTraitement){
					imageAvantTraitement.addPicture(titre[i], imageBinarise.display());
				}
				
				int tailleMatrice=3;
				int nombreDePasse=2;
				
				
				for(int j=0;j<nombreDePasse;j++)
					imageBinarise.dilatation(tailleMatrice, tailleMatrice);
				for(int j=0;j<nombreDePasse*2;j++)
					imageBinarise.erosion(tailleMatrice, tailleMatrice);
				for(int j=0;j<nombreDePasse;j++)
					imageBinarise.dilatation(tailleMatrice, tailleMatrice);
				if(affichageReconnaissance){
					reconnaissance.addPicture(titre[i], imageBinarise.display());
				}		
				save(imageBinarise.display(),outputPath+titre[i]+".png");
				if(internet){
					register.balise("end "+titre[i]);
				}
				if(i==0){
					i=2;
				}else{
					i=6;
				}
			}while(i<6);
			
			String OcrType="OCR désactivé";
			String OcrName=OcrType;
			
			if (detectionOCR){
				if(internet){
					register.balise("start OCR");
				}
				OcrType=new OCR(outputPath+"name.png",outputPath).getText();
				if(internet){
					register.balise("end OCR name");
				}
				OcrName=new OCR(outputPath+"type.png",outputPath).getText();
				if(internet){
					register.balise("end OCR type");
				}
				//OcrType=ocr("type.png");
				//OcrName=ocr("name.png");
				if(OcrType.length()==0)
					OcrType="aucun type detecté";
				if(OcrName.length()==0)
					OcrName="aucun nom detecté";
			}
			
			System.out.println("type : "+ OcrType);
			System.out.println("name : "  + OcrName);
			
			String adressedufichier = outputPath+"outputFile.txt";
			try
			{
				FileWriter fw = new FileWriter(adressedufichier, false);
				BufferedWriter output = new BufferedWriter(fw);
				output.write(OcrType+"\n");
				output.write(OcrName+"\n");
				output.flush();
				output.close();
				System.out.println("recorded");
			}
			catch(IOException ioe){
				System.out.println("Erreur : ");
				System.out.println(ioe.toString() );
				ioe.printStackTrace();				
				}
		}
		if(internet){
			register.balise("fin");
		}
		System.out.println("fin");
	}
	
	private static void save(BufferedImage image,String file){
		try {
			ImageIO.write(image, "png", new File(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String help(){
		String returnValue="Help function\nparametre\n";
		returnValue=returnValue+"-i or -image	: input image\n";
		returnValue=returnValue+"-o or -output	: dossier de sortie\n";
		returnValue=returnValue+"-b or -bdd		: base de donnee pour enregistrer les marqeurs de temps\n";
		returnValue=returnValue+"-a or -affichage	: ihm/reconnaissance/picture/text/histo\n";
		return returnValue;
	}
}	
package MagicWithTruth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class OCR {
	private String text="";
	public OCR(String file, String ouptputPath){
		
		String path=ouptputPath;//"C:\\Users\\adrien\\workspace\\MagicWithTruth\\";
		ouptputPath=path+"output";
				
		Charset charset = Charset.forName("UTF-8");
		String OS = System.getProperty("os.name").toLowerCase();
		
		if(OS.indexOf("win")>=0){
			String tesseractPATH="Tesseract-OCR\\tesseract";
			String cmd = "cmd /c "+tesseractPATH+" "+file+" "+ouptputPath;
			executeBathCommand(cmd); 
		}else if(OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0) {
			String cmd = "tesseract "+file+" "+ouptputPath;
			System.out.println(cmd);
			executeShellCommand(cmd);
		}else{
			System.out.println("impossile de dettecter l'OS");
			text="";
		}
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(ouptputPath+".txt"), charset)) {
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		    	text=text+line;
		        System.out.println(line);
		    }
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
	}
	
	public String getText(){
		return text;
	}
	
	private String executeShellCommand(String command) {
		StringBuffer output = new StringBuffer();
		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = 
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

                        String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();
	}
	
	private void executeBathCommand(String command){
		try {
			Runtime r = Runtime.getRuntime();
            Process p = r.exec(command);
            p.waitFor();//si l'application doit attendre a ce que ce process fini   
		}catch(Exception e) {
			System.out.println("erreur d'execution " + command + e.toString());
        }
	}
}

package MagicWithTruth;

import java.util.ArrayList;

public class Filtre {
	public int [] moyenneur(int [] signal, int taille){return arrayToInt(moyenneur(IntToArray(signal),taille));}
	public ArrayList<Integer> moyenneur(ArrayList<Integer> signal, int tailleFiltre){
		ArrayList<Integer> returnSignal = new ArrayList<Integer>();
		int temp=0;
		int taille=tailleFiltre;
		for(int i=0;i<signal.size(); i++ ){
			temp=0;
			taille=tailleFiltre;
			if(i>(signal.size()-tailleFiltre-1)){
				taille=(signal.size()-i);
				if(taille<0){
					taille=0;
				}
			}
			if(taille>0){
				for(int j=0;j<taille;j++){
					temp=temp+signal.get(i+j);
				}			
				temp=temp/taille;
			}
			returnSignal.add(temp);
		}		
		return returnSignal;
	}
	
	public int [] derivative(int [] signal){return arrayToInt(derivative(IntToArray(signal)));}
	public ArrayList<Integer> derivative(ArrayList<Integer> signal){
		ArrayList<Integer> returnSignal = new ArrayList<Integer>();
		int temp=signal.get(0)-signal.get(1);
		returnSignal.add(temp);
		for(int i=1;i<signal.size()-1;i++){
			temp=(signal.get(i-1)-signal.get(i+1))/2;
			returnSignal.add(temp);
		}
		temp=signal.get(signal.size()-2)-signal.get(signal.size()-1);
		returnSignal.add(temp);
		return returnSignal;
	}
	
	public int [] arrayToInt(ArrayList<Integer> list){
		int[] ret = new int[list.size()];
		  for(int i = 0;i < ret.length;i++)
		    ret[i] = list.get(i);
		  return ret;
	}
	public ArrayList<Integer> IntToArray(int [] list){
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for(int i = 0;i < list.length ;i++)
			ret.add(list[i]);
		return ret;
	}
}

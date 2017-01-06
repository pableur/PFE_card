package MagicWithTruth;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;


public class Histogramme extends ApplicationFrame {
//	public class Histogramme {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title;
	private GreyImage image;
	private int histo[];

	Histogramme(String title,GreyImage image) {
		super(title);
		this.title=title;
		this.image=image;
		histo=makeHisto(this.image);
	}
	
	public void display(){

	    //final XYSeriesCollection data = new XYSeriesCollection(makeSeries(title,makeHisto(image)));
		final XYSeriesCollection data = new XYSeriesCollection(makeSeries(title,this.histo));
	    final JFreeChart chart = ChartFactory.createXYLineChart(
	        title,
	        "X", 
	        "Y", 
	        data,
	        PlotOrientation.VERTICAL,
	        true,
	        true,
	        false
	    );

	    final ChartPanel chartPanel = new ChartPanel(chart);
	    chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
	    setContentPane(chartPanel);
	    
		this.pack();
		this.setVisible(true);
	}
		
	public int seuilBinarisation(){
		int tab[] = this.histo;
		 for(int i=0;i<10;i++)
			 tab = new Filtre().moyenneur(tab, 5);
		 
		 tab = new Filtre().derivative(tab);
		 Boolean picDetected=false;
		 for(int i=tab.length-1;i>=0;i--){
			 if(Math.abs(tab[i])>5 && picDetected==false){
				 picDetected=true;				 
			 }
			 if(picDetected && Math.abs(tab[i])==0){
				 System.out.println(i);
				 return i;
			 }
		 }
		 return 0;
	}
	public int get(int indice){
		return histo[indice];
	}
	private XYSeries makeSeries(String title, int[] histo){
		XYSeries series = new XYSeries(title);
		
		for(int i=0;i<256;i++){
			series.add(i, histo[i]);
		}
		
		return series;
	}
	
	private int[] makeHisto(GreyImage image){
		int tabReturn[] = new int[256];
		 for(int x=0;x<image.getWidth();x++){
			 for(int y=0;y<image.getHeight();y++){
				 tabReturn[image.getColor(x, y)]=tabReturn[image.getColor(x, y)]+1;
			 }
		 }		 
		 return tabReturn;
	}
	@SuppressWarnings("unused")
	private int[] makeHistoFiltre(GreyImage image){
		int tabReturn[] = new int[256];
		 for(int x=0;x<image.getWidth();x++){
			 for(int y=0;y<image.getHeight();y++){
				 tabReturn[image.getColor(x, y)]++;
			 }
		 }
		 for(int i=0;i<5;i++)
			 tabReturn = new Filtre().moyenneur(tabReturn, 5);
		 //tabReturn = new Filtre().moyenneur(tabReturn, 5);
		 //tabReturn = new Filtre().moyenneur(tabReturn, 5);
		 tabReturn = new Filtre().derivative(tabReturn);		 
		 return tabReturn;
	}
	
}

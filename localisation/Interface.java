
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;


public class Interface extends JFrame{
	 

	private static final long serialVersionUID = 1L;
	private JPanel pan = new JPanel();
	
	public Interface(String name){
		this(name, 1);
	}

	public Interface(String name, int col){
		GridLayout experimentLayout = new GridLayout(0,col);
		pan.setLayout (experimentLayout);
	    this.setTitle(name);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    this.setLocationRelativeTo(null);
	    this.setContentPane(pan);
	    this.setVisible(true);
	  }   
	  
	public void addPicture(String title, BufferedImage image){
		this.addPicture(title,image,image.getWidth(),image.getHeight());
		}
	
	 public void addPicture(String title, BufferedImage image, int sizeY){
		 int width = image.getWidth();
		 int height = image.getHeight();
		 int sizeX=(width*sizeY)/height;
		 this.addPicture(title,image,sizeX,sizeY);
	  }
	 
	public void addPicture(String title, BufferedImage image, int sizeX, int sizeY){
		JPanel panImage = new JPanel();
		panImage.setLayout(new BoxLayout(panImage, BoxLayout.PAGE_AXIS));
		panImage.add(new JLabel(title));
		ImageIcon imageIcon = new ImageIcon(new ImageIcon(image).getImage().getScaledInstance(sizeX, sizeY, Image.SCALE_DEFAULT));
		panImage.add(new JLabel(imageIcon));		
		pan.add(panImage);
		this.setVisible(true);
		pack();
		this.setLocationRelativeTo(null);
		}
	
	public void addHistogramme(String title, int histo[]){
		int sizeX = histo.length;
		int sizeY = max(histo) -min(histo) ;
		this.addHistogramme(title, histo, sizeX,  sizeY);
	}
	
	
	public void addHistogramme(String title, int histo[], int sizeX, int sizeY){
		
		final XYSeriesCollection data = new XYSeriesCollection(makeSeries(title,histo));
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
	    chartPanel.setPreferredSize(new java.awt.Dimension(sizeX, sizeY));
		
	    JPanel panImage = new JPanel();	
		pan.add(chartPanel);
		this.setVisible(true);
		pack();
		this.setLocationRelativeTo(null);
	}
	
	private XYSeries makeSeries(String title, int[] histo){
		XYSeries series = new XYSeries(title);
		
		for(int i=0;i<histo.length;i++){
			series.add(i, histo[i]);
		}
		
		return series;
	}
	
	private int max(int list[]){
		int max=list[0];
		for (int i=1; i<list.length; i++){
			if(max<list[i])max=list[i];
		}
		return max;	
	}
	
	private int min(int list[]){
		int min=list[0];
		for (int i=1; i<list.length; i++){
			if(min>list[i])min=list[i];
		}
		return min;	
	}
}

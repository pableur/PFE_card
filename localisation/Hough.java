 
/**
 * Hough Transform
 * 
 * @author Xavier Philippeau
 *
 */
public class Hough {
 
	// image size
	private int Width,Height;
 
	// max Rho walue (= length of the diagonal)
	private double maxRho;
 
	// size of the accumulators array
	private int maxIndexTheta,maxIndexRho;
 
	// accumulators array
	int[][] acc;
 
	/**
         * Constructor
         * 
         * @param width,height Size of the image
         */
	public Hough(int width,int height) {
		this.Width=width;
		this.Height=height;
 
		this.maxRho = Math.sqrt( width*width + height*height );
		this.maxIndexTheta=360; // precision : 1 degree by cell
		this.maxIndexRho=(int)(1+this.maxRho); // precision : 1 pixel by cell
		this.acc = new int[maxIndexTheta][maxIndexRho];
	}
 
	/**
         * pixel vote
         * 
         * @param x,y coordinates of the pixel
         */
	public void vote(int x,int y) {
		// use origin = center of the image
		x-=Width/2;	y-=Height/2;
 
		// for each theta value
		for(int indexTheta=0; indexTheta<maxIndexTheta; indexTheta+=1) {
			double theta = ((double)indexTheta/maxIndexTheta)*Math.PI;
 
			// compute corresponding rho value
			double rho = x*Math.cos(theta) + y*Math.sin(theta);
 
			// rho -> index
			int indexRho   = (int) (0.5 + (rho/this.maxRho + 0.5)*this.maxIndexRho );
 
			// increment accumulator
			acc[indexTheta][indexRho]++;
		}
	}
 
	/**
         * retrieve winner
         * 
         * @return array={rho,theta}  
         */
	public double[] winner() {
		// parsing the accumulators for max accumulator
		double max=0, winrho=0, wintheta=0;
		for(int r=0;r<maxIndexRho;r++) {
			for(int t=0;t<maxIndexTheta;t++) {
				if (acc[t][r]<max) continue;
				max=acc[t][r];
				winrho=r;
				wintheta=t;
			}
		}
		
 
		// indexes -> (rho,theta)
		double rho   = ((double)winrho/this.maxIndexRho - 0.5)*this.maxRho;
		double theta = ((double)wintheta/this.maxIndexTheta)*Math.PI;
 
		return new double[] {rho,theta};
	}
 
	// convert (rho,theta) to (a,b) such that Y=a.X+b
	public double[] rhotheta_to_ab(double rho, double theta) {
		double a=0,b=0;
		if(Math.sin(theta)!=0) {
			a = -Math.cos(theta)/Math.sin(theta);
			b = rho/Math.sin(theta)+Height/2-a*Width/2; // use origin = (0,0)
		} else {
			a=Double.MAX_VALUE;
			b=0;
		}
		return new double[] {a,b};
	}
}
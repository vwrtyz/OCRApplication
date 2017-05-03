import java.util.ArrayList;
import ij.process.ImageProcessor;

public class Scanners {
	
	public Scanners()
	{
		
	}
	
	public ArrayList<Integer> leftScanning(ImageProcessor image)
	{
		ArrayList<Integer> value= new ArrayList<Integer>();
		int whitePix = 0;
		int i=0,j=0;
		for (i = 0; i < image.getHeight(); i++) {
			for (j = 0; j < image.getWidth(); j++) {				
				if (image.getPixel(j, i) == 1 && j != image.getWidth()-1)
				{
					whitePix++;
				}
				else if (image.getPixel(j, i) == 1 && j == image.getWidth()-1)
				{
					value.add(whitePix);
					whitePix=0;
					break;
				}
				else
				{
					value.add(whitePix);
					whitePix=0;
					break;
				}
			}
			
		}
		
		return value;
		
	}
	
	public ArrayList<Integer> rightScanning(ImageProcessor image)
	{
		ArrayList<Integer> value= new ArrayList<Integer>();
		int whitePix = 0;
		int i=0,j=0;
		for (i = 0; i < image.getHeight(); i++) {
			for (j = image.getWidth()-1; j >= 0; j--) {				
				if (image.getPixel(j, i) == 1 && j != 0)
				{
					whitePix++;
				}
				else if (image.getPixel(j, i) == 1 && j == 0)
				{
					value.add(whitePix);
					whitePix=0;
					break;
				}
				else
				{
					value.add(whitePix);
					whitePix=0;
					break;
				}
			}
		}
		
		return value;
		
	}
	
	public ArrayList<Integer> bottomScanning(ImageProcessor image)
	{
		ArrayList<Integer> value= new ArrayList<Integer>();
		int whitePix = 0;
		int i=0,j=0;
		for (i = 0; i < image.getWidth(); i++) {
			for (j = image.getHeight()-1; j >= 0 ; j--) {				
				if (image.getPixel(i, j) == 1 && j != 0)
				{
					whitePix++;
				}
				else if (image.getPixel(i, j) == 1 && j == 0)
				{
					value.add(whitePix);
					whitePix=0;
					break;
				}
				else
				{
					value.add(whitePix);					
					whitePix=0;
					break;
				}
			}
		}
		
		return value;
		
	}
	
	public ArrayList<Integer> upperScanning(ImageProcessor image)
	{
		ArrayList<Integer> value= new ArrayList<Integer>();
		int whitePix = 0;
		int i=0,j=0;
		for (i = 0; i < image.getWidth();i++) {
			for (j = 0; j < image.getHeight(); j++) {				
				if (image.getPixel(i, j) == 1 && j != image.getHeight()-1)
				{
					whitePix++;
				}
				else if (image.getPixel(i, j) == 1 && j == image.getHeight()-1)
				{
					value.add(whitePix);
					whitePix=0;
					break;
				}
				else
				{
					value.add(whitePix);					
					whitePix=0;
					break;
				}
			}
		}
		
		return value;
		
	}
}

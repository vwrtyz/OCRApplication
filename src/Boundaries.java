import ij.process.ImageProcessor;

public class Boundaries {

	public Boundaries() {
		// TODO Auto-generated constructor stub
	}
	
	public int leftBoundaries(ImageProcessor image)
	{
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				if(image.getPixel(i, j) != 1)
					return i;
			}
		}
		return 0;
	}
	
	public int rightBoundaries(ImageProcessor image)
	{
		for (int i = image.getWidth()-1; i >=0 ; i--) {
			for (int j = 0; j < image.getHeight(); j++) {
				if(image.getPixel(i, j) != 1)
				{
//					System.out.println(image.getWidth()-1);
//					System.out.println("pixel:"+image.getPixel(i, j));
					return i;
				}
			}
		}
		return 0;
	}
	
	public int upperBoundaries(ImageProcessor image)
	{
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				if(image.getPixel(j, i) != 1)
				{
					return i;
				}
			}
		}
		return 0;
	}
	
	public int lowerBoundaries(ImageProcessor image)
	{
		for (int i = image.getHeight()-1; i >=0 ; i--) {
			for (int j = 0; j < image.getWidth() ; j++) {
				if(image.getPixel(j, i) != 1)
				{
//					System.out.println(j+","+i);
					return i;
				}
			}
		}
		return 0;
	}
	
	 
}

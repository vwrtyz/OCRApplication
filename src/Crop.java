import ij.process.ImageProcessor;

public class Crop {

	
	public Crop() {
		// TODO Auto-generated constructor stub
	}
	
	public ImageProcessor cropPicture(ImageProcessor image,int x1, int y1, int x2, int y2)
	{		
		int croppedWidth = Math.abs(x1-x2);
		int croppedHeight = Math.abs(y1-y2);
		
		image.setInterpolationMethod(ImageProcessor.BILINEAR);
		image.setRoi(x1, y1, croppedWidth, croppedHeight);
		return image.crop(); 
	}
		
	public ImageProcessor cropAndResizePicture(ImageProcessor image,int x1, int y1, int x2, int y2,int width, int height)
	{
		int croppedWidth = Math.abs(x1-x2);
		int croppedHeight = Math.abs(y1-y2);
		
		image.setInterpolationMethod(ImageProcessor.BILINEAR);
		image.setRoi(x1, y1, croppedWidth, croppedHeight);
		
//		System.out.println(image);
		
		return image.crop().resize(width,height); 
	}
}

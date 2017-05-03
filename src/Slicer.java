import java.util.ArrayList;

import ij.process.ImageProcessor;

public class Slicer {

	int black,i,j;
	int bound = 0;
	ArrayList<Integer> arrBound;
	
	public Slicer() {
		// TODO Auto-generated constructor stub
	}

	public ArrayList<Integer> sliceHorizontal(ImageProcessor image){
		
		arrBound = new ArrayList<Integer>();
		
		black = 0;
		for(i = 0;i < image.getHeight(); i++){
			for (j = 0; j < image.getWidth(); j++) {
				if(black == 1 && image.getPixel(j, i) != 1){
					if(i == image.getHeight()-1)
					{
						black = 0;					
						bound = i;
						arrBound.add(bound);
					}
					break;
				}				
				else if(black == 1 && j == image.getWidth()-1){
					black = 0;					
					bound = i-1;
					arrBound.add(bound);
					break;
				}				
				else if(image.getPixel(j, i) != 1){
					black = 1;
					bound = i;
					arrBound.add(bound);
					break;
				}
			}							
		}
		
		return arrBound;
	}

	public ArrayList<Integer> sliceVertical(ImageProcessor image){
		
		arrBound = new ArrayList<Integer>();
		
		black = 0;
		for(i = 0;i < image.getWidth(); i++){
			for (j = 0; j < image.getHeight(); j++) {
				if(black == 1 && image.getPixel(i, j) != 1){
					if(i == image.getWidth())
					{
						black = 0;					
						bound = i;
						arrBound.add(bound);
					}
					break;
				}				
				else if(black == 1 && j == image.getHeight()-1){
					black = 0;					
					bound = i-1;
					arrBound.add(bound);
					break;
				}				
				else if(image.getPixel(i, j) != 1){
					black = 1;
					bound = i;
					arrBound.add(bound);
					break;
				}
			}							
		}
		
		return arrBound;
	}

	
}

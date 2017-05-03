import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import ij.process.ImageProcessor;

public class Segment {

	Scanners scan = new Scanners();
	Crop crop = new Crop();
	
	public Segment() {
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList<ImageProcessor> doSegmentation(ImageProcessor image)
	{
		ArrayList<ImageProcessor> listImage = new ArrayList<>();
		ArrayList<Integer> verticalPointsWithZeroValue = new ArrayList<Integer>();
		ArrayList<Integer> horizontalPointsWithZeroValue = new ArrayList<Integer>();
		ArrayList<Integer> segmentationPoint = new ArrayList<>();
		ArrayList<Integer> segmentationPoint2 = new ArrayList<>();
		ImageProcessor fixImage;
		Boundaries boundaries = new Boundaries();
		
		final Pattern patternN = Pattern.compile("00*01(01)*");
		final Pattern patternNoCut = Pattern.compile("(010)+0+");
		ArrayList<Integer> up = new ArrayList<>();
		ArrayList<Integer> left = new ArrayList<>();
		
		left = scan.leftScanning(image);
		
		int selisih=1,temp=1;
		int horizontal=0;
		
		for (int i = 0; i < left.size(); i++) 
		{
			if(left.get(i) == image.getWidth()-1)
			{
				horizontalPointsWithZeroValue.add(i);
			}
		}
		
		//cek ada horizontal nggak
		if(horizontalPointsWithZeroValue.size() != 0)
		{
			temp = horizontalPointsWithZeroValue.get(0);
			segmentationPoint.add(temp);
			for (int i = 1; i < horizontalPointsWithZeroValue.size(); i++) 
			{
				if(i == horizontalPointsWithZeroValue.size()-1)
				{
					horizontal++;
				}
				else if(horizontalPointsWithZeroValue.get(i) - temp == selisih)
				{
					selisih++;
				}
				else
				{
					temp = horizontalPointsWithZeroValue.get(i);
					horizontal++;
					selisih = 1;
					segmentationPoint.add(horizontalPointsWithZeroValue.get(i));	
				}
			}
			
			//coba tambahan
			if(horizontalPointsWithZeroValue.size() == 1)
				horizontal++;
				
//			System.out.println(horizontal);
			//kalo ada 2
			if(horizontal == 2)
			{
				fixImage = crop.cropPicture(image, 0, 0, image.getWidth(), segmentationPoint.get(0));
				//check whether the cropped Image is only just a line straight or the hat ㅗ
				if((double)fixImage.getHeight()/(double) fixImage.getWidth() < 0.2)
				{
//					System.out.println("a");
					fixImage = crop.cropPicture(image, 0, 0, image.getWidth(), segmentationPoint.get(1));
					listImage.add(fixImage.resize(300,207));
					fixImage = crop.cropPicture(image, 0, segmentationPoint.get(1), image.getWidth(), image.getHeight());
					listImage.add(fixImage);
					
				}
				else
				{
//					System.out.println("b");
					listImage.add(fixImage.resize(300,207));
					fixImage = crop.cropPicture(image, 0, segmentationPoint.get(0), image.getWidth(), segmentationPoint.get(1));
					listImage.add(fixImage);
					fixImage = crop.cropPicture(image, 0, segmentationPoint.get(1), image.getWidth(), image.getHeight());
					listImage.add(fixImage.resize(300, 207));
				}
			}
			//kalo ada 1
			else if(horizontal == 1)
			{
				fixImage = crop.cropPicture(image, 0, 0, image.getWidth(), segmentationPoint.get(0));
				
				int batasKiri = boundaries.leftBoundaries(fixImage);
				int batasKanan = boundaries.rightBoundaries(fixImage);
				
				fixImage = crop.cropPicture(image, batasKiri, 0, batasKanan, segmentationPoint.get(0));
				
				up = scan.upperScanning(fixImage);
				
				for (int i = 0; i < up.size(); i++) 
				{
					if(up.get(i) == fixImage.getHeight()-1)
					{
						verticalPointsWithZeroValue.add(i);
					}
				}
				
				//check if vertical isn't 0
				if(verticalPointsWithZeroValue.size() != 0)
				{
					temp = verticalPointsWithZeroValue.get(0);
					segmentationPoint2.add(temp);
					for (int i = 1; i < verticalPointsWithZeroValue.size(); i++) 
					{
						if(i == verticalPointsWithZeroValue.size()-1)
						{
							continue;
						}
						else if(verticalPointsWithZeroValue.get(i) - temp == selisih)
						{
							selisih++;
						}
						else
						{
							temp = verticalPointsWithZeroValue.get(i);
							selisih = 1;
							segmentationPoint2.add(verticalPointsWithZeroValue.get(i));	
						}
					}
					
					//potong jadi 2 kanan kiri
					if(crop.cropPicture(fixImage, 0, 0, segmentationPoint2.get(0), fixImage.getHeight()).getHeight() != 0 && crop.cropPicture(fixImage, 0, 0, segmentationPoint2.get(0), fixImage.getHeight()).getWidth() != 0 )
					{	
						listImage.add(crop.cropPicture(fixImage, 0, 0, segmentationPoint2.get(0), fixImage.getHeight()));
					}
						
					if(crop.cropPicture(fixImage, segmentationPoint2.get(0), 0, fixImage.getWidth(), fixImage.getHeight()).getHeight() != 0 && crop.cropPicture(fixImage, segmentationPoint2.get(0), 0, fixImage.getWidth(), fixImage.getHeight()).getWidth() != 0)
					{
						listImage.add(crop.cropPicture(fixImage, segmentationPoint2.get(0), 0, fixImage.getWidth(), fixImage.getHeight()));
					}
				}
				else
				{
					fixImage = crop.cropPicture(image, 0, 0, image.getWidth(), segmentationPoint.get(0));
					listImage.add(fixImage.resize(300, 207));
				}
				fixImage = crop.cropPicture(image, 0, segmentationPoint.get(0), image.getWidth(), image.getHeight());
				listImage.add(fixImage);
			}
			else if(horizontal > 2)
			{
//				System.out.println(segmentationPoint);
				fixImage = crop.cropPicture(image, 0, 0,image.getWidth(),segmentationPoint.get(1));
				listImage.add(fixImage.resize(300,207));
				fixImage = crop.cropPicture(image, 0, segmentationPoint.get(1), image.getWidth(), segmentationPoint.get(2));
				listImage.add(fixImage.resize(300,147));
				fixImage = crop.cropPicture(image, 0, segmentationPoint.get(2), image.getWidth(), image.getHeight());
				listImage.add(fixImage.resize(300,207));
			}
		}
		//kalo nggak ada horizontal
		else
		{				
			ArrayList<ArrayList<Integer>> scanData = new ArrayList<>();
			
			for (int a = 0; a < image.getWidth(); a++) 
			{
				ArrayList rowData = new ArrayList<>();
				for (int b = image.getHeight()-1; b >= 0; b--) 
				{	
					rowData.add(image.getPixel(a, b));
				}
				scanData.add(rowData);
			}
					
			int batas=image.getHeight();
			
			//Cari batas potong dari bawah
			for (int x = 0; x < image.getWidth(); x++) 
			{
				for(int y = 0 ; y <image.getHeight();y++)
				{
					//cek pertama item kagak
					if(y == 0 && scanData.get(x).get(y) == 1)
					{
						break;
					}
					else if(y == image.getHeight()-1 )
					{
						break;
					}
					else if(scanData.get(x).get(y) == 1 && scanData.get(x).get(y+1) == 0)
					{	
						if(y < batas)
						{
							batas = y;
							break;
						}
					}
				}
			}
			
			batas+=1;
			
			//crop huruf N bawah
			if(crop.cropPicture(image, 0, image.getHeight()-batas, image.getWidth(), image.getHeight()).getHeight() != 0 && crop.cropPicture(image, 0, image.getHeight()-batas, image.getWidth(), image.getHeight()).getWidth() != 0 )
			{	
				ImageProcessor hasilPotong = crop.cropAndResizePicture(image, 0, image.getHeight()-batas, image.getWidth(), image.getHeight(),300,207);
				
				String tempPattern="";
				int tempValue = -1;
				for(int lebar=boundaries.leftBoundaries(hasilPotong); lebar < boundaries.rightBoundaries(hasilPotong); lebar++)
				{
					for(int tinggi=boundaries.lowerBoundaries(hasilPotong); tinggi >= boundaries.upperBoundaries(hasilPotong); tinggi--)
					{
						if(tinggi == boundaries.lowerBoundaries(hasilPotong))
						{
							tempValue = hasilPotong.getPixel(lebar, tinggi);
							tempPattern = tempPattern + tempValue;
						}
						else if(hasilPotong.getPixel(lebar, tinggi) != tempValue && tinggi != boundaries.lowerBoundaries(hasilPotong))
						{
							tempValue = hasilPotong.getPixel(lebar, tinggi);
							tempPattern+=tempValue;
						}
					}
				}
				
				//cek pattern untuk huruf N
				if(patternN.matcher(tempPattern).matches())
				{
					fixImage = crop.cropPicture(image, 0, 0, image.getWidth(), image.getHeight()-batas);
					
					BufferedImage bf = fixImage.getBufferedImage();
					
//					try {
//					ImageIO.write(bf, "png", new File("D:/Photos/OCR/Hasil/pottong-patern.png"));
//					} catch (IOException e) {
//					// TODO Auto-generated catch block
//						e.printStackTrace();
//					}

					
					double ratio = (double)fixImage.getHeight() / (double)fixImage.getWidth();
					
					//cek yang dipotong itu huruf utuh atau gabungan huruf
					if(fixImage.getWidth() != 0 && fixImage.getHeight() != 0 && ratio > 0.5)
					{
						int batasKiri = boundaries.leftBoundaries(fixImage);
						int batasKanan = boundaries.rightBoundaries(fixImage);
						
						fixImage = crop.cropPicture(image, batasKiri, 0, batasKanan, image.getHeight()-batas);
						
//						try {
//							ImageIO.write(bf, "png", new File("D:/Photos/OCR/Hasil/pottong-patern.png"));
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
						
						up = scan.upperScanning(fixImage);
						
						for (int i = 0; i < up.size(); i++) 
						{
							if(up.get(i) == fixImage.getHeight()-1)
							{
								verticalPointsWithZeroValue.add(i);
							}
						}
						
						//cek ada vertikal nggak
						if(verticalPointsWithZeroValue.size() != 0)
						{
							temp = verticalPointsWithZeroValue.get(0);
							segmentationPoint2.add(temp);
							for (int i = 1; i < verticalPointsWithZeroValue.size(); i++) 
							{
								if(i == verticalPointsWithZeroValue.size()-1)
								{
									continue;
								}
								else if(verticalPointsWithZeroValue.get(i) - temp == selisih)
								{
									selisih++;
								}
								else
								{
									temp = verticalPointsWithZeroValue.get(i);
									selisih = 1;
									segmentationPoint2.add(verticalPointsWithZeroValue.get(i));	
								}
							}
							
							//potong jadi 2 kanan kiri
							if(crop.cropPicture(fixImage, 0, 0, segmentationPoint2.get(0), fixImage.getHeight()).getHeight() != 0 && crop.cropPicture(fixImage, 0, 0, segmentationPoint2.get(0), fixImage.getHeight()).getWidth() != 0 )
							{	
								listImage.add(crop.cropPicture(fixImage, 0, 0, segmentationPoint2.get(0), fixImage.getHeight()));
							}
								
							if(crop.cropPicture(fixImage, segmentationPoint2.get(0), 0, fixImage.getWidth(), fixImage.getHeight()).getHeight() != 0 && crop.cropPicture(fixImage, segmentationPoint2.get(0), 0, fixImage.getWidth(), fixImage.getHeight()).getWidth() != 0)
							{
								listImage.add(crop.cropPicture(fixImage, segmentationPoint2.get(0), 0, fixImage.getWidth(), fixImage.getHeight()));
							}
							
							listImage.add(hasilPotong.resize(300, 207));
						}
						else
						{
							//kalo huruf diatas tdk ada vertical tapi huruf bawahnya N
							tempPattern="";
							tempValue = -1;
							for(int lebar=boundaries.leftBoundaries(fixImage); lebar < boundaries.rightBoundaries(fixImage); lebar++)
							{
								for(int tinggi=boundaries.lowerBoundaries(fixImage); tinggi >= boundaries.upperBoundaries(fixImage); tinggi--)
								{
									if(tinggi == boundaries.lowerBoundaries(fixImage))
									{
										tempValue = fixImage.getPixel(lebar, tinggi);
										tempPattern = tempPattern + tempValue;
									}
									else if(fixImage.getPixel(lebar, tinggi) != tempValue && tinggi != boundaries.lowerBoundaries(fixImage))
									{
										
										tempValue = fixImage.getPixel(lebar, tinggi);
										tempPattern+=tempValue;
									}
								}
							}
							
							if(patternNoCut.matcher(tempPattern).matches())
							{
								listImage.add(image);
							}
							else
							{
								listImage.add(fixImage);
								listImage.add(hasilPotong);
							}
						}//end-if
					}
					else//proses huruf N saja
					{
						listImage.add(image);
					}
					//end-if
				}
				else
				{
					listImage.add(image);
				}
				//end-if
			}
			else
			{
				listImage.add(image);
			}
			//end-if
		}
		return listImage;
	}

}

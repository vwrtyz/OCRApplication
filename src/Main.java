import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import ij.ImagePlus;
import ij.process.ImageProcessor;

public class Main{
	
	BufferedImage img,newImg;
	Scanners scan = new Scanners();
	ArrayList<Double> finalEucdDist= new ArrayList<Double>();
	HashMap<Integer, ArrayList<ArrayList<Integer>>> trainingDataKonsonan;
	HashMap<Integer, ArrayList<ArrayList<Integer>>> trainingDataVokal;
	ArrayList<Integer> w,x,y,z = new ArrayList<Integer>();
	ArrayList<Integer> potongHorizontal,potongVertical = new ArrayList<Integer>();
	ArrayList<ImageProcessor> tempImages = new ArrayList<ImageProcessor>();
	ArrayList<ImageProcessor> fixedImages = new ArrayList<ImageProcessor>();
	HashMap<Integer, ArrayList<ImageProcessor>> letter = new HashMap<>(); 
	ArrayList<String> exceptionList = new ArrayList<>();
	ArrayList<String> recognitionResult = new ArrayList<>();
	String padanan;
	
	final Pattern pattern1 = Pattern.compile("(01)(01)+0+(01)+0+(01)+01");
	final Pattern pattern2 = Pattern.compile("(01)(01)+0+(01)+01");
//	final Pattern pattern3 = Pattern.compile("1*00*(01)+1*");
 //	final Pattern pattern2 = Pattern.compile("[01]*0*[01*]0*[01]");
	DatabaseManager dm = new DatabaseManager();
	String query;
	
	
	Boundaries boundaries = new Boundaries();
	Crop crop = new Crop();
	Slicer slice = new Slicer();
	OtsuTreshold otsu = new OtsuTreshold();
	Segment	segments = new Segment();
	Euclidian ecd = new Euclidian();
	ArrayList<Integer> segmentationPoint = new ArrayList<>();
	ArrayList<ArrayList<Integer>> scanData = new ArrayList<>();
	ArrayList<Integer> rowData;
	String tempPattern = "";
	String tabel = "";
	String hasilAkhir="";
	ImageProcessor inputImage;
	
	int left,right,upper,lower;
	int countH=0, countV=0;
	int tempAlpha=0,tempBeta=0,temp=0,selisih=1;
	int verticalLine=0;
	int match=0,a=0,b=0;
	int counter=0;
	double ratio = 0.00;
	boolean flagNoProcess;
	
	public void paint(Graphics g) {
        g.drawImage(img, 0, 0, null);
    }
	
	public BufferedImage blackWhite(BufferedImage image,int threshold)
	{
		BufferedImage binarized = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
		
		int red,newPixel;
		
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {
				red = new Color(image.getRGB(j, i)).getRed();
				int alpha = new Color(image.getRGB(j, i)).getAlpha();
				if(red < threshold)
				{
					newPixel = 0;
				}
				else
				{
					newPixel = 255;
				}
				newPixel = colorToRGB(alpha,newPixel,newPixel,newPixel);
				binarized.setRGB(j, i, newPixel);
			}
		}
		return binarized;
	}
	
	private static int colorToRGB(int alpha,int red, int green, int blue)
	{
		int newPixel = 0;
		newPixel += alpha;
		newPixel = newPixel << 8;
		newPixel += red; newPixel = newPixel << 8;
		newPixel +=green; newPixel = newPixel << 8;
		newPixel += blue;
		
		return newPixel;
	}
	
	public int cekPattern(ArrayList<ArrayList<Integer>> scanData, int batas, int batasKiri, int batasKanan)
	{
		tempPattern="";
		int tempValue = -1,flagCocok=0;
		for(int lebar=batasKiri; lebar < batasKanan; lebar++){
			for(int tinggi=0; tinggi < batas; tinggi++){
				if(tinggi == 0)
				{
					tempValue = scanData.get(lebar).get(0);
					tempPattern = tempPattern + tempValue;
				}
				else if(scanData.get(lebar).get(tinggi) != tempValue && tinggi != 0)
				{
					
					tempValue = scanData.get(lebar).get(tinggi);
					tempPattern+=tempValue;
				}
			}
		}
//		System.out.println(tempPattern);
		if(pattern1.matcher(tempPattern).matches() || pattern2.matcher(tempPattern).matches())
		{
			flagCocok = 1;
		}
		
		return flagCocok;
	}
	
	public Main()
	{
		query = "select sisi_kiri,sisi_bawah,sisi_kanan,sisi_atas from konsonan";
		ResultSet rs = dm.queryResult(query);
		String result="";
		String[] dataSplitByComma;
		int key=1,keys=1;
		
		try {
			trainingDataKonsonan = new HashMap<Integer, ArrayList<ArrayList<Integer>>>();
			while(rs.next())
			{
				ArrayList<ArrayList<Integer>> dataPerLetter = new ArrayList<ArrayList<Integer>>();
				for (int g = 1; g < 5	; g++) 
				{
					
					ArrayList<Integer> data = new ArrayList<Integer>();
					result = rs.getString(g);
					dataSplitByComma = result.split(",");
					for (int h = 0; h < dataSplitByComma.length; h++) 
					{
						data.add(Integer.parseInt(dataSplitByComma[h]));
					}
					dataPerLetter.add(data);
				}
				trainingDataKonsonan.put(key, dataPerLetter);
				key++;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		query = "select sisi_kiri,sisi_bawah,sisi_kanan,sisi_atas from vokal";
		rs = dm.queryResult(query);
		result="";
		dataSplitByComma = null;
		key=1;
		
		
		try {
			trainingDataVokal = new HashMap<Integer, ArrayList<ArrayList<Integer>>>();
			while(rs.next())
			{
				ArrayList<ArrayList<Integer>> dataPerLetter = new ArrayList<ArrayList<Integer>>();
				for (int g = 1; g < 5; g++) 
				{
					ArrayList<Integer> data = new ArrayList<Integer>();
					result = rs.getString(g);
					dataSplitByComma = result.split(",");
					for (int h = 0; h < dataSplitByComma.length; h++) 
					{
						data.add(Integer.parseInt(dataSplitByComma[h]));
					}
					dataPerLetter.add(data);
				}
				trainingDataVokal.put(key, dataPerLetter);
				key++;
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for (int ulang = 1; ulang <= 100; ulang++) 
		{
			keys=1;
			letter = new HashMap<>();
			potongHorizontal = new ArrayList<Integer>();
			potongVertical = new ArrayList<Integer>();
			finalEucdDist = new ArrayList<Double>();
			w = new ArrayList<Integer>();
			x = new ArrayList<Integer>();
			y = new ArrayList<Integer>();
			z = new ArrayList<Integer>();
			tempImages = new ArrayList<ImageProcessor>();
			fixedImages = new ArrayList<ImageProcessor>();
			exceptionList = new ArrayList<>();
			recognitionResult = new ArrayList<>();
			segmentationPoint = new ArrayList<>();
			scanData = new ArrayList<>();
			rowData = new ArrayList<Integer>();
			left=0;right=0;upper=0;lower=0;
			countH=0; countV=0;
			tempAlpha=0;tempBeta=0;temp=0;selisih=1;
			verticalLine=0;
			match=0;a=0;b=0;
			counter=0;
			ratio = 0.00;
			flagNoProcess=false;
			hasilAkhir="";
		
		try {
//	           img = ImageIO.read(new File("D:/Photos/OCR/gabung2.jpg"));
//			img = ImageIO.read(new File("D:/Photos/OCR/Testing/training/23.png"));
			img = ImageIO.read(new File("D:/Photos/OCR/Testing/training/"+ulang+".png"));
//			img = ImageIO.read(new File("C:/Photos/prata/Documents/Training/"".png"));
			} catch (IOException e) {
				System.out.println("error opening image");
	    }
		
		Raster raster = img.getData();
		DataBuffer buffer = raster.getDataBuffer();
		
		DataBufferByte byteBuffer = (DataBufferByte) buffer;
		byte[] srcData = byteBuffer.getData(0);
		
		byte[] dstData = new byte[srcData.length];
		
		OtsuTreshold thresholder = new OtsuTreshold();
		int threshold = thresholder.doTreshold(srcData, dstData);
		
		newImg = blackWhite(img,threshold);
		
//		ImageProcessor ip = new ImagePlus("A", img).getProcessor();
		
		ImageProcessor ip = new ImagePlus("A", newImg).getProcessor();
		
//		ip.blurGaussian(3.086555910238);

//		BufferedImage bf = ip.getBufferedImage();
//		
//		try {
//			ImageIO.write(bf, "png", new File("D:/Photos/OCR/Hasil/hasil-blur.png"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		potongHorizontal = slice.sliceHorizontal(ip);
//		
//		for (int i = 0; i < (potongHorizontal.size())/2; i++) 
//		{
//			ImageProcessor horizontalCroppedImage = crop.cropPicture(ip, 0, potongHorizontal.get(countH), ip.getWidth()-1, potongHorizontal.get(countH+1));
//			countH=countH+2;
//			
			left  = boundaries.leftBoundaries(ip);
			right = boundaries.rightBoundaries(ip);
			
			ImageProcessor fixedHorizontal = crop.cropPicture(ip, left, 0, right, ip.getHeight());

			segmentationPoint = slice.sliceVertical(fixedHorizontal);			
			segmentationPoint.add(fixedHorizontal.getWidth()-1);
			
			for (int j = 0; j < segmentationPoint.size(); j+=2) 
			{
				fixedImages = new ArrayList<>();
				ImageProcessor verticalCroppedImage = crop.cropPicture(fixedHorizontal,segmentationPoint.get(j),0,segmentationPoint.get(j+1),fixedHorizontal.getHeight());		
				
				upper = boundaries.upperBoundaries(verticalCroppedImage);
				lower = boundaries.lowerBoundaries(verticalCroppedImage);
				left  = boundaries.leftBoundaries(verticalCroppedImage);
				right = boundaries.rightBoundaries(verticalCroppedImage);				
				
//				System.out.println("lower:"+lower);
				
				ImageProcessor croppedImage = crop.cropPicture(verticalCroppedImage, left, upper, right, lower);
				
				BufferedImage bf;
//				bf = croppedImage.getBufferedImage();
//
//				try {
//					ImageIO.write(bf, "png", new File("D:/Photos/OCR/Hasil/nyoba"+j+".png"));
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				tempImages = segments.doSegmentation(croppedImage);
				
//				System.out.println("temp:"+tempImages.size());
				
				for (int k = 0; k < tempImages.size(); k++) 
				{
					upper = boundaries.upperBoundaries(tempImages.get(k));
					lower = boundaries.lowerBoundaries(tempImages.get(k));
					left  = boundaries.leftBoundaries(tempImages.get(k));
					right = boundaries.rightBoundaries(tempImages.get(k));
					
					ImageProcessor temporaryImage = crop.cropPicture(tempImages.get(k), left, upper, right, lower);
					
//					bf = temporaryImage.getBufferedImage();
//					
//					try {
//						ImageIO.write(bf, "png", new File("D:/Photos/OCR/Hasil/hasil-crop"+j+"-"+k+".png"));
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}								
					
					scanData = new ArrayList<>();
					
					//Scan dari bawah untuk cek pattern
					for (a = 0; a < temporaryImage.getWidth(); a++) 
					{
						rowData = new ArrayList<>();
						for (b = temporaryImage.getHeight()-1; b >= 0; b--) 
						{	
							rowData.add(temporaryImage.getPixel(a, b));
						}
						scanData.add(rowData);
					}
							
					int batas=temporaryImage.getHeight();
					
					//Cari batas potong dari bawah
					for (int x = 0; x < temporaryImage.getWidth(); x++) 
					{
						for(int y = 0 ; y <temporaryImage.getHeight();y++)
						{
							//cek pertama item kagak
							if(y == 0 && scanData.get(x).get(y) == 1)
							{
								break;
							}
							else if(y == temporaryImage.getHeight()-1 ){
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

					match = cekPattern(scanData, batas, 0, temporaryImage.getWidth());
					
					if (match == 1 && batas != temporaryImage.getHeight())
					{
//						cocok bawah
//						System.out.println("cocok bawah");
						ImageProcessor fixCroppedImage;
						if(crop.cropPicture(temporaryImage, 0, 0, temporaryImage.getWidth(), temporaryImage.getHeight()-batas).getHeight() != 0)
						{
							fixCroppedImage = crop.cropAndResizePicture(temporaryImage, 0, 0, temporaryImage.getWidth(), temporaryImage.getHeight()-batas,300,207);
							fixedImages.add(fixCroppedImage);
						}
						
						fixCroppedImage = crop.cropAndResizePicture(temporaryImage, 0, temporaryImage.getHeight()-batas, temporaryImage.getWidth(), temporaryImage.getHeight(),300,143);
						fixedImages.add(fixCroppedImage);
						
						if((k+1) < tempImages.size())
						{
							fixedImages.add(tempImages.get(k+1).resize(300, 207));
							k++;
						}
					}
					else
					{
						scanData = new ArrayList<>();
						
						//Scan data dari kanan atas
						for (a = 0; a < temporaryImage.getHeight(); a++) 
						{
							rowData = new ArrayList<>();
							for (b = temporaryImage.getWidth()-1; b >= 0; b--) 
							{	
								rowData.add(temporaryImage.getPixel(b, a));
							}
							scanData.add(rowData);
						}
						
						batas=temporaryImage.getWidth()-1;
						
						for (int x = 0; x < temporaryImage.getHeight(); x++) 
						{
							for(int y = 0 ; y <temporaryImage.getWidth();y++)
							{
								if(y == temporaryImage.getWidth()-1 )
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
						batas-=1;
						match = cekPattern(scanData, batas, 0, temporaryImage.getHeight());
//						System.out.println(tempPattern);
						if(match == 1)
						{
//							cocok kanan
//							System.out.println("cocok kanan");
							ImageProcessor fixCroppedImage = crop.cropPicture(temporaryImage, 0, 0, temporaryImage.getWidth()-batas, temporaryImage.getHeight());
							
							if((double)fixCroppedImage.getWidth()/(double)fixCroppedImage.getHeight() <= 0.2)
							{
								fixedImages.add(fixCroppedImage.resize(95, 300));
//								bf = fixCroppedImage.getBufferedImage();
//
//								try {
//									ImageIO.write(bf, "png", new File("D:/Photos/OCR/Hasil/cocokKanan-2.png"));
//								} catch (IOException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
							}
							else
							{
								fixedImages.add(fixCroppedImage.resize(300, 207));
							}
							
							fixCroppedImage = crop.cropAndResizePicture(temporaryImage, temporaryImage.getWidth()-batas, 0, temporaryImage.getWidth(), temporaryImage.getHeight(),95,300);
							fixedImages.add(fixCroppedImage);
							
//							bf = fixCroppedImage.getBufferedImage();
//
//							try {
//								ImageIO.write(bf, "png", new File("D:/Photos/OCR/Hasil/cocokKanan-2.png"));
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
						}
						else
						{
							scanData = new ArrayList<>();
							
							for (a = 0; a < temporaryImage.getWidth(); a++) 
							{
								rowData = new ArrayList<>();
								for (b = 0; b < temporaryImage.getHeight(); b++) 
								{	
									rowData.add(temporaryImage.getPixel(a, b));
								}
								scanData.add(rowData);
							}
							
							batas=temporaryImage.getHeight()-1;
							
							for (int x = 0; x < temporaryImage.getWidth(); x++) 
							{
								for(int y = 0 ; y <temporaryImage.getHeight();y++)
								{
									if(y == 0 && scanData.get(x).get(y) == 1)
									{
										break;
									}
									else if(y == temporaryImage.getHeight()-1 )
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
							match = cekPattern(scanData, batas, 0, temporaryImage.getWidth());

							if(match == 1)
							{
//								cocok atas
								
								ImageProcessor fixCroppedImage = crop.cropAndResizePicture(temporaryImage, 0, 0, temporaryImage.getWidth(), batas,300,143);
								fixedImages.add(fixCroppedImage);
								if(crop.cropPicture(temporaryImage, 0, batas+1, temporaryImage.getWidth(), temporaryImage.getHeight()).getHeight() != 0)
								{
									fixCroppedImage = crop.cropAndResizePicture(temporaryImage, 0, batas+1, temporaryImage.getWidth(), temporaryImage.getHeight(),300,207);
									fixedImages.add(fixCroppedImage);
								}
							}
							else
							{
								if(temporaryImage.getWidth() > 1 && temporaryImage.getHeight() > 1)
								{
									fixedImages.add(temporaryImage);
								}
							}
						}
					}
				}
//				System.out.println("fixed:"+fixedImages.size());
				letter.put(keys, fixedImages);
				keys++;
			}//end-if potong vertical			
//		}//end-if potong horizontal
		

		BufferedImage bf1;
//		bf1 = fixedImages.get(14).getBufferedImage();

//		try {
//			ImageIO.write(bf1, "png", new File("D:/Photos/OCR/Hasil/nyoba.png"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		System.out.println(fixedImages.size());
//		System.out.println(letter.size());
		
		for (int i = 1; i <= letter.size(); i++) 
		{
//			System.out.println("i-size:"+letter.get(i).size());
			for (int j = 0; j < letter.get(i).size(); j++) 
			{
//				System.out.println("j:"+j);
				
//				bf1 = letter.get(i).get(j).getBufferedImage();
//
//				try {
//					ImageIO.write(bf1, "png", new File("D:/Photos/OCR/Hasil/"+i+"nyoba"+j+".png"));
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				flagNoProcess = false;
	
				if(letter.get(i).get(j).getWidth() < letter.get(i).get(j).getHeight())
				{
					ratio = (double)letter.get(i).get(j).getWidth() / letter.get(i).get(j).getHeight();
				}
				else if(letter.get(i).get(j).getWidth() >= letter.get(i).get(j).getHeight())
				{
					ratio = (double)letter.get(i).get(j).getHeight() / letter.get(i).get(j).getWidth();
				}
	
				upper = boundaries.upperBoundaries(letter.get(i).get(j));
				lower = boundaries.lowerBoundaries(letter.get(i).get(j));
				left  = boundaries.leftBoundaries(letter.get(i).get(j));
				right = boundaries.rightBoundaries(letter.get(i).get(j));
				
				if (ratio <= 0.5)
				{
					if(letter.get(i).get(j).getWidth() < letter.get(i).get(j).getHeight())
					{
						if(crop.cropAndResizePicture(letter.get(i).get(j), left, upper, right, lower, 95, 300).getHeight() !=0 && crop.cropAndResizePicture(letter.get(i).get(j), left, upper, right, lower, 95, 300).getWidth() !=0)
						{
							
							inputImage = crop.cropAndResizePicture(letter.get(i).get(j), left, upper, right, lower, 95, 300);
						}
						else
						{
							
							flagNoProcess = true;
						}
					}
					else
					{
						if(crop.cropAndResizePicture(letter.get(i).get(j), left, upper, right, lower, 300, 143).getHeight() !=0 && crop.cropAndResizePicture(letter.get(i).get(j), left, upper, right, lower, 300, 143).getWidth() !=0)
						{
							inputImage = crop.cropAndResizePicture(letter.get(i).get(j), left, upper, right, lower, 300, 143);
						}
						else
						{
							flagNoProcess = true;
						}
					}
				}
				else
				{
					if(crop.cropAndResizePicture(letter.get(i).get(j), left, upper, right, lower, 300, 207).getHeight() != 0 && crop.cropAndResizePicture(letter.get(i).get(j), left, upper, right, lower, 300, 207).getWidth() != 0)
					{
						inputImage = crop.cropAndResizePicture(letter.get(i).get(j), left, upper, right, lower, 300, 207);
					}
					else
					{
						flagNoProcess = true;
					}
				}
				
				if(flagNoProcess == false)
				{
					match = 0;
//					bf1 = inputImage.getBufferedImage();
//					
//					try {
//						ImageIO.write(bf1, "png", new File("D:/Photos/OCR/Hasil/tes"+i+"."+j+".png"));
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					
					finalEucdDist = new ArrayList<>();
					w = new ArrayList<Integer>();
					x = new ArrayList<Integer>();
					y = new ArrayList<Integer>();
					z = new ArrayList<Integer>();
					
					w = scan.leftScanning(inputImage);
					x = scan.bottomScanning(inputImage);
					y = scan.rightScanning(inputImage);
					z = scan.upperScanning(inputImage);
					
					if(ratio > 0.5)
					{
						for (int k = 0; k < trainingDataKonsonan.size(); k++) {
							finalEucdDist.add(ecd.calculateEuclidianDistance(w, trainingDataKonsonan.get(k+1).get(0)) + ecd.calculateEuclidianDistance(x, trainingDataKonsonan.get(k+1).get(1)) + ecd.calculateEuclidianDistance(y, trainingDataKonsonan.get(k+1).get(2)) + ecd.calculateEuclidianDistance(z, trainingDataKonsonan.get(k+1).get(3)));
						}
						match = finalEucdDist.indexOf(Collections.min(finalEucdDist)) + 1;
						tabel = "konsonan";
					}
					else
					{
						if(inputImage.getWidth() < inputImage.getHeight())
						{
							for (int k = 0; k < 5; k++) {
								finalEucdDist.add(ecd.calculateEuclidianDistance(w, trainingDataVokal.get(k+1).get(0)) + ecd.calculateEuclidianDistance(x, trainingDataVokal.get(k+1).get(1)) + ecd.calculateEuclidianDistance(y, trainingDataVokal.get(k+1).get(2)) + ecd.calculateEuclidianDistance(z, trainingDataVokal.get(k+1).get(3)));
							}
							match = finalEucdDist.indexOf(Collections.min(finalEucdDist)) + 1;
//							if(i==1) 
//								for (int itungaja = 0; itungaja < finalEucdDist.size(); itungaja++) {
//									System.out.println(finalEucdDist.get(itungaja));
//								}
						}
						else if(inputImage.getWidth() > inputImage.getHeight())
						{
							for (int k = 5; k < 10; k++) {
								finalEucdDist.add(ecd.calculateEuclidianDistance(w, trainingDataVokal.get(k+1).get(0)) + ecd.calculateEuclidianDistance(x, trainingDataVokal.get(k+1).get(1)) + ecd.calculateEuclidianDistance(y, trainingDataVokal.get(k+1).get(2)) + ecd.calculateEuclidianDistance(z, trainingDataVokal.get(k+1).get(3)));
							}	
							match = finalEucdDist.indexOf(Collections.min(finalEucdDist)) + 6;
//							if(i==1) System.out.println(finalEucdDist);
						}
						tabel = "vokal";
					}
					
					query = "select karakter,kode from "+tabel+" where kunci ="+match+"";
					
					rs = dm.queryResult(query);
					
					try {
						if(rs.next())
						{
//							System.out.println("huruf ke-"+j+":"+rs.getString(1));
							recognitionResult.add(rs.getString(2));
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else
				{
					System.out.println("skip");
				}
			}//end-if per HashMap
		}//end-if letter
			
		
//		System.out.print(recognitionResult.size()+",");		
		
		for (int j = 0; j < recognitionResult.size(); j++) 
		{
			counter=0;
			query="";
			
			//start-first-checking
			if(recognitionResult.get(j).substring(0,1).equals("k"))
			{
				//check j+1 if more than size
				if((j+1) < recognitionResult.size())
				{
					//check j+1 is k or v
					if(recognitionResult.get(j+1).substring(0,1).equals("v"))
					{
						//check j+2 if more than size
						if((j+2) < recognitionResult.size())
						{
							//check j+2 is k or v
							if(recognitionResult.get(j+2).substring(0,1).equals("k"))
							{
								//check j+3 if more than size
								if((j+3) < recognitionResult.size())
								{
									//check j+3 is k or v
									if(recognitionResult.get(j+3).substring(0,1).equals("k"))
									{
										//check j+4 if more than size
										if((j+4) < recognitionResult.size())
										{
											//check j+4 is k or v
											if(recognitionResult.get(j+4).substring(0,1).equals("k"))
											{
												//kvkk
												query = "select hasil_padanan from padanan where huruf_pertama = '"+recognitionResult.get(j)+"' and huruf_kedua = '"+recognitionResult.get(j+1)+"' and huruf_ketiga = '"+recognitionResult.get(j+2)+"' and huruf_keempat = '"+recognitionResult.get(j+3)+"'";
												j += 3;
												counter = counter + 4;
											}
											else
											{
												//kvk
												query = "select hasil_padanan from padanan where huruf_pertama = '"+recognitionResult.get(j)+"' and huruf_kedua = '"+recognitionResult.get(j+1)+"' and huruf_ketiga = '"+recognitionResult.get(j+2)+"' and huruf_keempat = ' '";
												j += 2;
												counter = counter + 3;
											}
											//end-check j+4 is k or v
										}
										else
										{
											//kvkk
											query = "select hasil_padanan from padanan where huruf_pertama = '"+recognitionResult.get(j)+"' and huruf_kedua = '"+recognitionResult.get(j+1)+"' and huruf_ketiga = '"+recognitionResult.get(j+2)+"' and huruf_keempat = '"+recognitionResult.get(j+3)+"'";
											j += 3;
											counter = counter + 4;
										}
										//end-check j+4 if more than size
									}
									else
									{
										//kv mid
										query = "select hasil_padanan from padanan where huruf_pertama = '"+recognitionResult.get(j)+"' and huruf_kedua = '"+recognitionResult.get(j+1)+"' and huruf_ketiga = ' ' and huruf_keempat = ' '";
										j += 1;
										counter = counter + 2;
									}
									//end-check j+3 is k or v
								}
								else
								{
									//kvk only
									query = "select hasil_padanan from padanan where huruf_pertama = '"+recognitionResult.get(j)+"' and huruf_kedua = '"+recognitionResult.get(j+1)+"' and huruf_ketiga = '"+recognitionResult.get(j+2)+"' and huruf_keempat = ' '";
									j += 2;
									counter = counter + 3;
								}
								//end-check j+3 if more than size
							}
							else
							{
								//kvv
								query = "select hasil_padanan from padanan where huruf_pertama = '"+recognitionResult.get(j)+"' and huruf_kedua = '"+recognitionResult.get(j+1)+"' and huruf_ketiga = '"+recognitionResult.get(j+2)+"' and huruf_keempat = ' '";
								j += 2;
								counter = counter + 3;
							}
							//end-check j+2 is k or v
						}
						else
						{
							//kv only
							query = "select hasil_padanan from padanan where huruf_pertama = '"+recognitionResult.get(j)+"' and huruf_kedua = '"+recognitionResult.get(j+1)+"' and huruf_ketiga = ' ' and huruf_keempat = ' '";
							j+=1;
							counter = counter + 2;
						}
						//end-check j+2 if more than size
					}
					else
					{
						query = "select hasil_padanan from padanan where huruf_pertama = '"+recognitionResult.get(j)+"' and huruf_kedua = ' ' and huruf_ketiga = ' ' and huruf_keempat = ' '";
						counter+=1;
					}
					//end-check j+1 is v
				}
				else
				{
					query = "select hasil_padanan from padanan where huruf_pertama = '"+recognitionResult.get(j)+"' and huruf_kedua = ' ' and huruf_ketiga = ' ' and huruf_keempat = ' '";
					counter+=1;
				}
				//end-check j+1 if more than size
			}
			else
			{
				query = "select hasil_padanan from padanan where huruf_pertama = '"+recognitionResult.get(j)+"' and huruf_kedua = ' ' and huruf_ketiga = ' ' and huruf_keempat = ' '";
				counter+=1;
			}
			//end-first
			
//			System.out.println(query);
			
			if(!"".equals(query))
			{
				rs = dm.queryResult(query);
				
				try {
					if(rs.next())
					{
						hasilAkhir = hasilAkhir+rs.getString(1);
					}
					else
					{
						for (int i = j - (counter - 1); i <= j; i++) {
							query = "select hasil_padanan from padanan where huruf_pertama = '"+recognitionResult.get(i)+"' and huruf_kedua = ' ' and huruf_ketiga = ' ' and huruf_keempat = ' '";
							rs = dm.queryResult(query);
							
							while(rs.next())
							{
								hasilAkhir = hasilAkhir+rs.getString(1);
							}
						}
						
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
//		System.out.println(hasilAkhir);
		System.out.println(ulang+","+hasilAkhir);
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		new Main();
	}

}

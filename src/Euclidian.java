import java.util.ArrayList;

public class Euclidian {

	public Euclidian()
	{
		
	}
	
	public Double calculateEuclidianDistance(ArrayList<Integer> inputData, ArrayList<Integer> trainingData)
	{
		Double euclidianDistance = -1.00;
		Double temp = 0.00,sum = 0.00;
		
//		System.out.println(inputData.size());
//		System.out.println(trainingData.size());
		
		for (int i = 0; i < inputData.size(); i++) {
			temp = Math.pow(trainingData.get(i) - inputData.get(i), 2);
			sum = sum + temp;
		}				
		euclidianDistance = Math.sqrt(sum);
		
		return euclidianDistance;
	}
	
}

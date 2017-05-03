
public class OtsuTreshold {

	private int histData[];
	private int maxLevelValue;
	private int treshold;
	
	public OtsuTreshold() {
		histData = new int[256];
	}

	public int[] getHistData() {
		return histData;
	}

	public int getMaxLevelValue() {
		return maxLevelValue;
	}

	public int getTreshold() {
		return treshold;
	}
	
	public int doTreshold(byte[] srcData, byte[] monoData){
		int ptr = 0;
		while(ptr < histData.length) histData[ptr++] = 0;
		
		ptr = 0;
		maxLevelValue = 0;
		while(ptr < srcData.length){
			int h = 0xFF & srcData[ptr];
			histData[h]++;
			if(histData[h] > maxLevelValue) maxLevelValue = histData[h];
			ptr++;
		}
		
		int total = srcData.length;
		
		float sum = 0;
		for(int t=0;t<256;t++) sum += t * histData[t];
		
		float sumB = 0;
		int wB = 0;
		int wF = 0;
		
		float varMax = 0;
		treshold = 0;
		
		for(int t=0;t<256;t++){
			wB += histData[t];
			if(wB == 0) continue;
			
			wF = total - wB;
			if(wF == 0) break;
			
			sumB += (float) (t * histData[t]);
			
			float mB = sumB / wB;
			float mF = (sum - sumB) / wF;
			
			float varBetween = (float)wB * (float)wF * (mB - mF) * (mB - mF);
			
			if(varBetween > varMax){
				varMax = varBetween;
				treshold = t;
			}
		}
		
		if(monoData != null){
			ptr = 0;
			while(ptr < srcData.length){
				monoData[ptr] = ((0xFF & srcData[ptr]) >= treshold) ? (byte) 255 : 0;
				ptr++;
			}
		}
		
		return treshold;
	}
}

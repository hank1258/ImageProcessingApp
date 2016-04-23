package scherzando.specialeffectcamera;

import java.nio.IntBuffer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Matrix;

public class SpecialEffectBuilder {
	private Bitmap image;		//�ݳB�z���w�ĹϤ�
	private int height;					//�Ϥ����סA���pixel
	private int width;					//�Ϥ��e�סA���pixel
	private int[][][] rgb;				//�Ϥ�RGB�ȡA���uint8(0~255)
	private int[][] rgbValue;			//RGB��(TYPE_INT_ARGB)
	
	/* �غc�l	�S�Ļs�y��------------------------------------------------------------------
	 * �ΨӪ�l�ƯS�Ļs�y��
	 * 
	 * @param image ���B�z���w�ĹϤ�
	 * 
	 * Example: SpecialEffectBuilder sb = new SpecialEffectBuilder(bufferedImage);
	 ------------------------------------------------------------------------------*/
	public SpecialEffectBuilder(Bitmap image){
		this.image = image;
		this.height = image.getHeight();
		this.width = image.getWidth();
		rgb = new int[height][width][3];
		rgbValue = new int[height][width];
		for( int i=0 ; i<height ; i++ ){
			for( int j=0 ; j<width ; j++ ){
				int pixel = image.getPixel(j, i);
				rgb[i][j][0] = Color.red(pixel);
				rgb[i][j][1] = Color.green(pixel);
				rgb[i][j][2] = Color.blue(pixel);
				rgbValue[i][j] = Color.rgb(rgb[i][j][0], rgb[i][j][1], rgb[i][j][2]);
			}
		}
	}
	
	public SpecialEffectBuilder(){
		
	}
	
	/* Method alphaModification-----------------------------------------------------
	 * �Ψӽվ�w�ĹϤ����z���{��
	 * 
	 * @param  alphaParameter	�@�Ө㦳0.0f~1.0f��float�ѼơA1.0f�N�������z���A0.0f�N�����z��
	 * @return none
	 * 
	 * Example: SpecialEffectBuilder sb = new SpecialEffectBuilder(bufferedImage);
	 * 			sb.alphaModification(0.3f);
	 ------------------------------------------------------------------------------*/
	
	public Bitmap alphaModification(int alphaParameter){
		
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		
		for(int i=0 ; i<height ; i++){
			for(int j=0 ; j<width ; j++){
				int color = image.getPixel(j, i);
				color += alphaParameter<<24;
				bitmap.setPixel(j, i, color);
			}
		}
		
		return bitmap;
	}
	
	/* Method negativeFilmEffect-----------------------------------------------------
	 * �t���S��
	 * 
	 * @param  none
	 * @return none
	 * 
	 * Example: SpecialEffectBuilder sb = new SpecialEffectBuilder(bufferedImage);
	 * 			sb.negativeFilmEffect();
	 ------------------------------------------------------------------------------*/
	public Bitmap negativeFilmEffect(){
		
		Bitmap bitmap = Bitmap.createBitmap(width, height, image.getConfig());
		
		for( int i=0 ; i<height ; i++ ){
			for( int j=0 ; j<width ; j++ ){
				for( int k=0 ; k<3 ; k++ ){
					rgb[i][j][k] = 255 - rgb[i][j][k];
				}
				rgbValue[i][j] = Color.rgb(rgb[i][j][0],rgb[i][j][1], rgb[i][j][2]);
				bitmap.setPixel(j, i, rgbValue[i][j]);
			}
		}
		
		return bitmap;
	}
	
	/* Method grayScaleEffect------------------------------------------------------
	 * �Ƕ��S��
	 * 
	 * @param  none
	 * @return none
	 * 
	 * Example: SpecialEffectBuilder sb = new SpecialEffectBuilder(bufferedImage);
	 * 			sb.grayScaleEffect();
	 ------------------------------------------------------------------------------*/
	public Bitmap grayScaleEffect(){
		
		Bitmap bitmap = Bitmap.createBitmap(width, height, image.getConfig());
		
		for( int i=0 ; i<height ; i++ ){
			for( int j=0 ; j<width ; j++ ){
				rgb[i][j][0] = (int)(rgb[i][j][0]*0.299);
				rgb[i][j][1] = (int)(rgb[i][j][1]*0.587);
				rgb[i][j][2] = (int)(rgb[i][j][2]*0.114);
				int red = rgb[i][j][0];
				int green = rgb[i][j][1];
				int blue = rgb[i][j][2];
				rgbValue[i][j] = Color.rgb(red+green+blue,red+green+blue,red+green+blue);
				bitmap.setPixel(j, i, rgbValue[i][j]);
			}
		}
		
		return bitmap;
	}
	
	/* Method gammaModification------------------------------------------------------
	 * �Ψӽվ�Gamma��(���G�{��)
	 * 
	 * @param	redGamma	���⪺���G��(�d�򥲶��O�@�Ӥj��0��double��)
	 * @param	greenGamma	��⪺���G��(�d�򥲶��O�@�Ӥj��0��double��)
	 * @param	blueGamma	�Ŧ⪺���G��(�d�򥲶��O�@�Ӥj��0��double��)
	 * @return none
	 * 
	 * Example: SpecialEffectBuilder sb = new SpecialEffectBuilder(bufferedImage);
	 * 			sb.gammaModification(1.5, 1.5, 1.5);
	 ------------------------------------------------------------------------------*/
	
	public Bitmap gammaModification(double redGamma, double greenGamma, double blueGamma){
		final int MAX_SIZE = 256;
		final double MAX_VALUE_DBL = 255.0;
		final int MAX_VALUE_INT = 255;
		final double REVERSE = 1.0;
		
		int[] gammaR = new int[MAX_SIZE];
		int[] gammaG = new int[MAX_SIZE];
		int[] gammaB = new int[MAX_SIZE];
		
		Bitmap bitmap = Bitmap.createBitmap(width, height, image.getConfig());
		
		for( int i=0 ; i<MAX_SIZE ; i++){
			gammaR[i] = (int)Math.min(MAX_VALUE_INT, 
					(int)((MAX_VALUE_DBL * Math.pow(i/MAX_VALUE_DBL, REVERSE/redGamma))));
			gammaG[i] = (int)Math.min(MAX_VALUE_INT, 
					(int)((MAX_VALUE_DBL * Math.pow(i/MAX_VALUE_DBL, REVERSE/greenGamma))));
			gammaB[i] = (int)Math.min(MAX_VALUE_INT, 
					(int)((MAX_VALUE_DBL * Math.pow(i/MAX_VALUE_DBL, REVERSE/blueGamma))));
		}
		
		for( int i=0 ; i<height ; i++ ){
			for( int j=0 ; j<width ; j++ ){
				rgb[i][j][0] = gammaR[rgb[i][j][0]];
				rgb[i][j][1] = gammaG[rgb[i][j][1]];
				rgb[i][j][2] = gammaB[rgb[i][j][2]];
				rgbValue[i][j] = Color.rgb(rgb[i][j][0], rgb[i][j][1], rgb[i][j][2]);
				bitmap.setPixel(j, i, rgbValue[i][j]);
			}
		}
		
		return bitmap;
	}
	
	/* Method writeImage------------------------------------------------------
	 * �Ψӿ�X�Ϥ��ɮ�
	 * 
	 * @param	format		��X�Ϥ����榡(���gif,jpg,png)
	 * @param	filePath	��X�Ϥ������|
	 * @return none
	 * 
	 * Example: SpecialEffectBuilder sb = new SpecialEffectBuilder(bufferedImage);
	 * 			File f = new File("....");
	 *			sb.writeImage("png", f);
	 ------------------------------------------------------------------------------*/
	
	/*
	public void writeImage(String format, File filePath){
		try {
			ImageIO.write(image, format, filePath);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Write Image Failed!!");
		}
	}
	*/
	
	/* Method edgeHighLightEffect------------------------------------------------------
	 * �N�I���ܶ¡A��t�u���H�զ�N����X
	 * 
	 * @param	highThreshold	���H��(���Afloat�A�d��q1.0f~0.0f�A��highThreshold����>lowThreshold)
	 * @param	lowThreshold	�C�H��(���Afloat�A�d��q1.0f~0.0f)
	 * @param	r				���w�C�⤧R��(uint8,0~255)
	 * @param	g				���w�C�⤧G��(uint8,0~255)
	 * @param	b				���w�C�⤧B��(uint8,0~255)
	 * @return none
	 * 
	 * Example: SpecialEffectBuilder sb = new SpecialEffectBuilder(bufferedImage);
	 *			sb.edgeHighLightEffect(1.0f,0.5f,0,0,0);
	 ------------------------------------------------------------------------------*/
	/*
	public Bitmap edgeHighLightEffect(float highThreshold, float lowThreshold, int r, int g, int b){
				
		CannyEdgeDetector ed = new CannyEdgeDetector();
		ed.setSourceImage(image);
		ed.setHighThreshold(highThreshold);
		ed.setLowThreshold(lowThreshold);
		ed.process();
		Bitmap edges = ed.getEdgesImage();
		for( int i=0 ; i<height ; i++ ){
			for( int j=0 ; j<width ; j++ ){
				int pixel = edges.getPixel(j,i);
				int red = Color.red(pixel);
				int green  = Color.green(pixel);
				int blue = Color.blue(pixel);
				if(red!=255 && green!=255 && blue!=255){
					edges.setPixel(j,i, Color.argb(0, r, g, b));
				}
			}
		}
		image = edges;
		updateRGBValue();
		
		return edges;
	}
	*/
	
	/* Method edgeColorChanging------------------------------------------------------
	 * ��Ϥ��ʡA����t�u���H���w�C��N����X
	 * 
	 * @param	highThreshold	���H��(���Afloat�A�d��q1.0f~0.0f�A��highThreshold����>lowThreshold)
	 * @param	lowThreshold	�C�H��(���Afloat�A�d��q1.0f~0.0f)
	 * @param	r				���w�C�⤧R��(uint8,0~255)
	 * @param	g				���w�C�⤧G��(uint8,0~255)
	 * @param	b				���w�C�⤧B��(uint8,0~255)
	 * @return none
	 * 
	 * Example: SpecialEffectBuilder sb = new SpecialEffectBuilder(bufferedImage);
	 *			sb.edgeColorChangingEffect(1.0f,0.5f,100,200,255);
	 ------------------------------------------------------------------------------*/
	
	/*
	public void edgeColorChangingEffect(float highThreshold, float lowThreshold, int r, int g, int b){
		CannyEdgeDetector ed = new CannyEdgeDetector();
		ed.setSourceImage(image);
		ed.setHighThreshold(highThreshold);
		ed.setLowThreshold(lowThreshold);
		ed.process();
		BufferedImage edges = ed.getEdgesImage();
		Color color = new Color(r,g,b);
		int edgeColor = color.getRGB();
		for( int i=0 ; i<height ; i++ ){
			for( int j=0 ; j<width ; j++ ){
				Color pixel = new Color(edges.getRGB(j,i));
				int red = pixel.getRed();
				int green  = pixel.getGreen();
				int blue = pixel.getBlue();
				if(red==255 && green==255 && blue==255){
					edges.setRGB(j,i, edgeColor);
				}
				else{
					edges.setRGB(j,i, rgbValue[i][j]);
				}
			}
		}
		image = edges;
		updateRGBValue();
	}
	*/
	
	/* Method decreaseColorDepth------------------------------------------------------
	 * �Ncolor channel��apply range���CbitOffest�����C
	 * 
	 * @param  bitOffest(uin8�A0~255)
	 * @return none
	 * 
	 * Example: SpecialEffectBuilder sb = new SpecialEffectBuilder(bufferedImage);
	 *			sb.decreaseColorDepth(64);
	 ------------------------------------------------------------------------------*/
	
	public Bitmap decreaseColorDepth(int bitOffset){
		
		Bitmap bitmap = Bitmap.createBitmap(width, height, image.getConfig());
		
		for(int i=0; i<height ; i++) {
			for(int j=0; j<width ; j++) {
				rgb[i][j][0] = ((rgb[i][j][0] + (bitOffset/2)) - ((rgb[i][j][0] + (bitOffset/2)) % bitOffset) - 1);
		        if(rgb[i][j][0] < 0){
		        	rgb[i][j][0] = 0; 
		        }
		        rgb[i][j][1] = ((rgb[i][j][1] + (bitOffset/2)) - ((rgb[i][j][1] + (bitOffset/2)) % bitOffset) - 1);
		        if(rgb[i][j][1] < 0){
		        	rgb[i][j][1] = 0; 
		        }
		        rgb[i][j][2] = ((rgb[i][j][2] + (bitOffset/2)) - ((rgb[i][j][2] + (bitOffset/2)) % bitOffset) - 1);
		        if(rgb[i][j][2] < 0){
		        	rgb[i][j][2] = 0;
		        }
		        rgbValue[i][j] = Color.rgb(rgb[i][j][0], rgb[i][j][1], rgb[i][j][2]);
		        bitmap.setPixel(j, i, rgbValue[i][j]);
		    }
		}
		
		return bitmap;
	}
	
	/* Method sepiaTonningEffect------------------------------------------------------
	 * �|���N�Ϥ��ন�Ƕ��A����A�N�Ϥ��̷ӰѼư���V
	 * 
	 * @param  depth	�C��W�j�j��
	 * @param  r		R��
	 * @param  g		G��
	 * @param  b		B��
	 * @return none
	 * 
	 * @Note	�U���ѼƨS���S�O����A�u�Or��g��b�Pdepth�ۭ��W�L255�N�|�S���N�q
	 * 
	 * Example: SpecialEffectBuilder sb = new SpecialEffectBuilder(bufferedImage);
	 *			sb.sepiaTonningEffec(1, 90, 20, 0);	PS.�����x���Ӥ��ĪG
	 ------------------------------------------------------------------------------*/
	
	public Bitmap sepiaTonningEffect(int depth, int r, int g, int b){
		final double GS_RED = 0.299;
		final double GS_GREEN = 0.587;
		final double GS_BLUE = 0.114;
		
		Bitmap bitmap = Bitmap.createBitmap(width, height, image.getConfig());
		
		for(int i=0 ; i<height ; i++){
			for(int j=0 ; j<width ; j++){
				rgb[i][j][0] = rgb[i][j][1] = rgb[i][j][2] = 
						(int)(GS_RED*rgb[i][j][0] + GS_GREEN*rgb[i][j][1] + GS_BLUE*rgb[i][j][2]);
				rgb[i][j][0] += (depth*r);
				if(rgb[i][j][0]>255){
					rgb[i][j][0] = 255;
				}
				rgb[i][j][1] += (depth*g);
				if(rgb[i][j][1]>255){
					rgb[i][j][1] = 255;
				}
				rgb[i][j][2] += (depth*b);
				if(rgb[i][j][2]>255){
					rgb[i][j][2] = 255;
				}
				rgbValue[i][j] = Color.rgb(rgb[i][j][0], rgb[i][j][1], rgb[i][j][2]);
				bitmap.setPixel(j, i, rgbValue[i][j]);
			}
		}
		
		return bitmap;
	}
	
	/* Method brightnessControl------------------------------------------------------
	 * �|���N�Ϥ��ন�Ƕ��A����A�N�Ϥ��̷ӰѼư���V
	 * 
	 * @param  value	�G�׼W�j�{��(-255~255)
	 * @return none
	 * 
	 * @Note	value���Ȭ��ܫG�A�t�Ȭ��ܷt�C���M�z�׽d��O����-255~255�C����ڨϥήɤ���ĳvalue���W�L150
	 * 
	 * Example: SpecialEffectBuilder sb = new SpecialEffectBuilder(bufferedImage);
	 *			sb.brightnessControl(-30);
	 ------------------------------------------------------------------------------*/
	
	public Bitmap brightnessControl(int value){
		
		Bitmap bitmap = Bitmap.createBitmap(width, height, image.getConfig());
		
		for(int i=0 ; i<height ; i++){
			for(int j=0 ; j<width ; j++){
				for(int k=0 ; k<3 ; k++){
					rgb[i][j][k] += value;
					if(rgb[i][j][k]<0){
						rgb[i][j][k] = 0;
					}
					else if(rgb[i][j][k]>255){
						rgb[i][j][k] = 255;
					}
				}
				rgbValue[i][j] = Color.rgb(rgb[i][j][0], rgb[i][j][1], rgb[i][j][2]);
				bitmap.setPixel(j, i, rgbValue[i][j]);
			}
		}
		
		return bitmap;
	}
	
	/* Method rotateImage------------------------------------------------------
	 * �N�Ϥ��̫��w�ѼƱ���
	 * 
	 * @param  degree	����(���󨤫׬ҥi�A���O��)
	 * @return none 
	 * 
	 * Example: SpecialEffectBuilder sb = new SpecialEffectBuilder(bufferedImage);
	 *			sb.rotateImage(30f);
	 ------------------------------------------------------------------------------*/
	
	public Bitmap rotateImage(float degree){
		
		Bitmap bitmap = Bitmap.createBitmap(width, height, image.getConfig());
		
		for(int i=0 ; i<height ; i++){
			for(int j=0 ; j<width ; j++){
				bitmap.setPixel(j, i, rgbValue[i][j]);
			}
		}
		
		Matrix matrix = new Matrix();
	    matrix.postRotate(degree);
		
	    bitmap = Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
	    
	    return bitmap;
	}
	
	/* Method gaussianBlurEffect------------------------------------------------------
	 * �N�Ϥ��̷Ӥ��ذ����ҽk�x�}�Ӽҽk
	 * 
	 * @param  type	���دx�}�N��(1~3)
	 * @return none 
	 * 
	 * Example: SpecialEffectBuilder sb = new SpecialEffectBuilder(bufferedImage);
	 *			sb.gaussianBlur(45);
	 ------------------------------------------------------------------------------*/
	
	public Bitmap gaussianBlurEffect(int type){
		
		Bitmap bitmap = Bitmap.createBitmap(width, height, image.getConfig());
		
		double[][] GaussianBlurConfig = null;
		int MatrixCoefficient = 0;
		int Factor = 0;
		switch(type){
			case 1:
				GaussianBlurConfig = new double[][] {
					{ 1, 1, 1 },
					{ 1, 1, 1 },
					{ 1, 1, 1 }		
				};
				MatrixCoefficient = 3;
				Factor = 9;
				break;
			case 2:
				GaussianBlurConfig = new double[][] {
					{ 1, 2, 1 },
					{ 2, 4, 2 },
					{ 1, 2, 1 }
		    	};
				MatrixCoefficient = 3;
				Factor = 16;
				break;
			default:
				System.out.println("Only TYPE 1~3 is available");
				return image;
		}
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(MatrixCoefficient);
		convMatrix.applyConfig(GaussianBlurConfig);
		convMatrix.Factor = Factor ;
		convMatrix.Offset = 0;
		bitmap = ConvolutionMatrix.computeConvolution3x3(image, convMatrix);
		
		return bitmap;
	}
	
	/* Method imageSharpenEffect------------------------------------------------------
	 * �N�Ϥ��̷Ӥ��ذ����ҽk�x�}�Ӽҽk
	 * 
	 * @param  weight	�j��8�����ȧY�i
	 * @return none 
	 * 
	 * Example: SpecialEffectBuilder sb = new SpecialEffectBuilder(bufferedImage);
	 *			sb.imageSharpenEffect(12);
	 ------------------------------------------------------------------------------*/
	
	public Bitmap imageSharpenEffect(double weight){
		
		Bitmap bitmap = Bitmap.createBitmap(width, height, image.getConfig());
		
		double[][] SharpConfig = new double[][] {
		        { 0 , -2    , 0  },
		        { -2, weight, -2 },
		        { 0 , -2    , 0  }
		    };
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		convMatrix.applyConfig(SharpConfig);
		convMatrix.Factor = weight - 8;
		bitmap = ConvolutionMatrix.computeConvolution3x3(image, convMatrix);
		
		return bitmap;
	}
	
	 public Bitmap processingBitmap(){
		 
		 final int KERNAL_WIDTH = 3;
		 final int KERNAL_HEIGHT = 3;
		  
		 int[][] kernal ={
		   {0, -1, 0},
		   {-1, 4, -1},
		   {0, -1, 0}
		 };
		 
	     Bitmap bitmap = Bitmap.createBitmap(
	       width, height, Bitmap.Config.ARGB_8888);
	      
	     int bmWidth = width;
	     int bmHeight = height;
	     int bmWidth_MINUS_2 = bmWidth - 2;
	     int bmHeight_MINUS_2 = bmHeight - 2;
	      
	     for(int i = 1; i <= bmWidth_MINUS_2; i++){
	    	 for(int j = 1; j <= bmHeight_MINUS_2; j++){
	        
	    	 //get the surround 3*3 pixel of current src[i][j] into a matrix subSrc[][]
	    	 int[][] subSrc = new int[KERNAL_WIDTH][KERNAL_HEIGHT];
	    	 for(int k = 0; k < KERNAL_WIDTH; k++){
	    		 for(int l = 0; l < KERNAL_HEIGHT; l++){
	    			 subSrc[k][l] = image.getPixel(i-1+k, j-1+l);
	    		 }
	    	 }
	        
	       int subSumA = 0;
	       int subSumR = 0;
	       int subSumG = 0;
	       int subSumB = 0;
	 
	       for(int k = 0; k < KERNAL_WIDTH; k++){
	    	   for(int l = 0; l < KERNAL_HEIGHT; l++){
	    		   subSumR += Color.red(subSrc[k][l]) * kernal[k][l];
	    		   subSumG += Color.green(subSrc[k][l]) * kernal[k][l];
	    		   subSumB += Color.blue(subSrc[k][l]) * kernal[k][l];
	    	   }
	       }
	 
	       subSumA = Color.alpha(image.getPixel(i, j));
	 
	       if(subSumR <0){
	    	   subSumR = 0;
	       }else if(subSumR > 255){
	    	   subSumR = 255; 
	       }
	        
	       if(subSumG <0){
	    	   subSumG = 0;
	       }else if(subSumG > 255){
	    	   subSumG = 255;
	       }
	        
	       if(subSumB <0){
	    	   subSumB = 0;
	       }else if(subSumB > 255){
	    	   subSumB = 255;
	       }
	 
	       bitmap.setPixel(i, j, Color.argb(subSumA, subSumR, subSumG, subSumB));
	       } 
	     }
	      
	     return bitmap;
	}
	 
	public Bitmap meanRemovalEffect(){
		 double[][] MeanRemovalConfig = new double[][] {
			        { -1 , -1, -1 },
			        { -1 ,  9, -1 },
			        { -1 , -1, -1 }
			    };
		ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
		convMatrix.applyConfig(MeanRemovalConfig);
		convMatrix.Factor = 1;
		convMatrix.Offset = 0;
		
		Bitmap bitmap = ConvolutionMatrix.computeConvolution3x3(image, convMatrix);
		
		return bitmap;
	}
	
	public Bitmap smoothEffect(double value) {
	    ConvolutionMatrix convMatrix = new ConvolutionMatrix(3);
	    convMatrix.setAll(1);
	    convMatrix.Matrix[1][1] = value;
	    convMatrix.Factor = value + 8;
	    convMatrix.Offset = 1;
	    Bitmap bitmap = ConvolutionMatrix.computeConvolution3x3(image, convMatrix);
	    
	    return bitmap;
	}
	
	/* Method updateRGBValue()------------------------------------------------------
	 * ��s�w�ĹϤ����U���ƭ�
	 * 
	 * @param  none
	 * @return none
	 ------------------------------------------------------------------------------*/
	
	/*
	private void updateRGBValue(){
		
		this.height = image.getHeight();
		this.width = image.getWidth();
		rgb = new int[height][width][3];
		rgbValue = new int[height][width];
		for(int i=0 ; i<height ; i++){
			for(int j=0 ; j<width ; j++){
				int color = image.getPixel(j,i);
				rgb[i][j][0] = Color.red(color);
				rgb[i][j][1] = Color.green(color);
				rgb[i][j][2] = Color.blue(color);
				rgbValue[i][j] = Color.rgb(rgb[i][j][0], rgb[i][j][1], rgb[i][j][2]);
			}
		}
	}
	*/
	
	public Bitmap boostColor(int type, float percent) {
		
	    Bitmap bitmap = Bitmap.createBitmap(width, height, image.getConfig());
	    int A, R, G, B;
	    int pixel;
	 
	    for(int x = 0; x < width; ++x) {
	        for(int y = 0; y < height; ++y) {
	            pixel = image.getPixel(x, y);
	            A = Color.alpha(pixel);
	            R = Color.red(pixel);
	            G = Color.green(pixel);
	            B = Color.blue(pixel);
	            if(type == 1) {
	                R = (int)(R * (1 + percent));
	                if(R > 255) R = 255;
	            }
	            else if(type == 2) {
	                G = (int)(G * (1 + percent));
	                if(G > 255) G = 255;
	            }
	            else if(type == 3) {
	                B = (int)(B * (1 + percent));
	                if(B > 255) B = 255;
	            }
	            bitmap.setPixel(x, y, Color.argb(A, R, G, B));
	        }
	    }
	    return bitmap;
	}
	
	
	 
	public Bitmap flipEffect(int type) {
		final int FLIP_VERTICAL = 1;
		final int FLIP_HORIZONTAL = 2;
		
	    Matrix matrix = new Matrix();
	    
	    if(type == FLIP_VERTICAL) {
	        matrix.preScale(1.0f, -1.0f);
	    }
	    else if(type == FLIP_HORIZONTAL) {
	        matrix.preScale(-1.0f, 1.0f);
	    } else {
	        return null;
	    }
	 
	    return Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
	}
	
	public Bitmap tintImageEffect(int degree) {
		 
		final double PI = 3.14159d;
		final double HALF_CIRCLE_DEGREE = 180d;
		final double RANGE = 256d;
 
        int[] pix = new int[width * height];
        image.getPixels(pix, 0, width, 0, 0, width, height);
 
        int RY, BY, RYY, GYY, BYY, R, G, B, Y;
        double angle = (PI * (double)degree) / HALF_CIRCLE_DEGREE;
        
        int S = (int)(RANGE * Math.sin(angle));
        int C = (int)(RANGE * Math.cos(angle));
 
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                int r = ( pix[index] >> 16 ) & 0xff;
                int g = ( pix[index] >> 8 ) & 0xff;
                int b = pix[index] & 0xff;
                RY = ( 70 * r - 59 * g - 11 * b ) / 100;
                BY = (-30 * r - 59 * g + 89 * b ) / 100;
                Y  = ( 30 * r + 59 * g + 11 * b ) / 100;
                RYY = ( S * BY + C * RY ) / 256;
                BYY = ( C * BY - S * RY ) / 256;
                GYY = (-51 * RYY - 19 * BYY ) / 100;
                R = Y + RYY;
                R = ( R < 0 ) ? 0 : (( R > 255 ) ? 255 : R );
                G = Y + GYY;
                G = ( G < 0 ) ? 0 : (( G > 255 ) ? 255 : G );
                B = Y + BYY;
                B = ( B < 0 ) ? 0 : (( B > 255 ) ? 255 : B );
                pix[index] = 0xff000000 | (R << 16) | (G << 8 ) | B;
            }
         
        Bitmap bitmap = Bitmap.createBitmap(width, height, image.getConfig());    
        bitmap.setPixels(pix, 0, width, 0, 0, width, height);
        
        pix = null;
        
        return bitmap;
    }
	
	private int colordodge(int in1, int in2) {
	    float image = (float)in2;
	    float mask = (float)in1;
	    return ((int) ((image == 255) ? image:Math.min(255, (((long)mask << 8 ) / (255 - image)))));

	}

	public Bitmap ColorDodgeBlend(Bitmap layer) {
	    Bitmap base = image.copy(Config.ARGB_8888, true);
	    Bitmap blend = layer.copy(Config.ARGB_8888, false);

	    IntBuffer buffBase = IntBuffer.allocate(base.getWidth() * base.getHeight());
	    base.copyPixelsToBuffer(buffBase);
	    buffBase.rewind();

	    IntBuffer buffBlend = IntBuffer.allocate(blend.getWidth() * blend.getHeight());
	    blend.copyPixelsToBuffer(buffBlend);
	    buffBlend.rewind();

	    IntBuffer buffOut = IntBuffer.allocate(base.getWidth() * base.getHeight());
	    buffOut.rewind();

	    while (buffOut.position() < buffOut.limit()) {
	        int filterInt = buffBlend.get();
	        int srcInt = buffBase.get();

	        int redValueFilter = Color.red(filterInt);
	        int greenValueFilter = Color.green(filterInt);
	        int blueValueFilter = Color.blue(filterInt);

	        int redValueSrc = Color.red(srcInt);
	        int greenValueSrc = Color.green(srcInt);
	        int blueValueSrc = Color.blue(srcInt);

	        int redValueFinal = colordodge(redValueFilter, redValueSrc);
	        int greenValueFinal = colordodge(greenValueFilter, greenValueSrc);
	        int blueValueFinal = colordodge(blueValueFilter, blueValueSrc);

	        int pixel = Color.argb(255, redValueFinal, greenValueFinal, blueValueFinal);

	        buffOut.put(pixel);
	    }

	    buffOut.rewind();

	    base.copyPixelsFromBuffer(buffOut);
	    blend.recycle();

	    return base;
	}
	
}
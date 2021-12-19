
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class CountcFosNumber {
    public static final int MY_MAX_VALUE = 3000;

    public static void main(String[] args) {
        String input_cFos = "the result_path of DetectcFos.java";
        String input_model = "please match the Atlas and microscope images";
        String grayscale_path = "the result_path of Grayscale.java";
        String final_cFos_path = "make the file to write the result images";
        String oriimage_path =  "the file of microscope images";
        String result_path = "make the newly file to output the csv file";

        File file_model = new File(input_model);
        File files_model[] = file_model.listFiles();       

        //please set the number as you want to try, lines 31 to 33 are variable
        int pixelvalue_threshold = 170;//input the minimum pixel value of cFos
        int number_of_subjectRegios = 100; //please count your dataset
        int MaximumNumberofImages = 100; //please count your dataset
        int[][] nLabels_table = new int[MaximumNumberofImages][number_of_subjectRegios];
        double[][][] MinPixel = new double[MaximumNumberofImages][number_of_subjectRegios][MY_MAX_VALUE];
       
        for (int i = 0; i < MaximumNumberofImages; i++) {
            for (int j = 0; j < number_of_subjectRegios; j++) {
                for (int k = 0; k < MY_MAX_VALUE; k++) {
                    MinPixel[i][j][k] = -1;
                }
            }
        }

        try {
            File lib_folder = new File("lib");
            String lib_path = lib_folder.getAbsolutePath();
            System.load(lib_path + "\\" + Core.NATIVE_LIBRARY_NAME + ".dll");

            for (int nfile = 0; nfile < files_model.length; nfile++) {
                String model_path = files_model[nfile].toString();
                String cFos_path = input_cFos + files_model[nfile].getName().split("_")[0] + ".png";

                BufferedImage cFos_image = ImageIO.read(new File(cFos_path));
                BufferedImage model_image = ImageIO.read(new File(model_path));

                int width = cFos_image.getWidth();
                int height = cFos_image.getHeight();

                BufferedImage final_cFos = new BufferedImage(width, height,BufferedImage.TYPE_BYTE_GRAY);

                for (int regionN = 2; regionN < number_of_subjectRegios; regionN++) {
                    BufferedImage midReult = ImageIO.read(new File(cFos_path));
                    
                    String file_name = "middle" + regionN;

                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            int[] pixel = new int[3];
                            model_image.getRaster().getPixel(x, y, pixel);
                            int model_value = pixel[0];
                                if (model_value != regionN) {                           
                                Color color = new Color(0, 0, 0);
                                midReult.setRGB(x, y, color.getRGB());
                            }
                        }
                    }
                    //save the cFos image of each subject area
                    ImageIO.write(midReult, "png", new File(result_path + file_name + ".png"));

                    Mat source = Imgcodecs.imread(result_path + file_name + ".png");
                    Imgproc.cvtColor(source, source, Imgproc.COLOR_BGR2GRAY);

                    // Labeling
                    Mat stats = new Mat(); 
                    Mat centroids = new Mat(); 
                    Mat labels = new Mat();
                    int nLabels = Imgproc.connectedComponentsWithStats(source, labels, stats, centroids, 8, CvType.CV_32S);                           
                    int frame_number = Integer.parseInt(files_model[nfile].getName().split("_")[0]);

                    BufferedImage grayscale_image = ImageIO.read(new File(grayscale_path + files_model[nfile].getName().split("_")[0] + ".png"));

                    //the minimun pixel coordinate(x, y) of each candidate cFos
                    int[][] minpixel_loc = new int[nLabels+1][2];
                    //check candiadate cFos table
                    int[] minpixel_check = new int[nLabels+1];
                    //the minimun pixel value of each candidate cFos
                    int[] value_small = new int[nLabels+1];                

                    for (int i = 0; i < nLabels+1; i++) {
                        value_small[i] = 256;
                    }
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            int[] pixel = new int[3];
                            grayscale_image.getRaster().getPixel(x, y, pixel);
                            int gray_value = pixel[0];
                            int label_value = (int) labels.get(y, x)[0];
                            
                            if (label_value != 0) {
                                if (value_small[label_value] > gray_value) {
                                    value_small[label_value] = gray_value;
                                    minpixel_loc[label_value][0] = x;
                                    minpixel_loc[label_value][1] = y;
                                }
                            }
                        }
                    }

                    int label_i = 0;
                    int count_final = 0;
                    //calculate the average of pixel value of 5 coordinates, the minimun pixel value and 4 neighbor pixels 
                    for (int i = 1; i < nLabels; i++) {
                        if (value_small[i] != 256) {
                            int x = minpixel_loc[i][0];
                            int y = minpixel_loc[i][1];

                            int[] pixel = new int[3];
                            grayscale_image.getRaster().getPixel(x, y, pixel);
                            int value = pixel[0];
                            int neighborPixel = 1;

                            if(y != 0 ){ 
                            grayscale_image.getRaster().getPixel(x, y-1, pixel);
                            value = value + pixel[0];
                            neighborPixel++;
                            }
                            if(x != 0){
                            grayscale_image.getRaster().getPixel(x-1, y, pixel);
                            value = value + pixel[0];
                            neighborPixel++;
                            }
                            if(y != height-1){
                            grayscale_image.getRaster().getPixel(x, y+1, pixel);
                            value = value + pixel[0];
                            neighborPixel++;
                            }
                            if(x != width-1){
                            grayscale_image.getRaster().getPixel(x+1, y, pixel);
                            value = value + pixel[0];
                            neighborPixel++;
                            }                            
                            MinPixel[frame_number][regionN][label_i] = value/neighborPixel;                            
                           
                            if(MinPixel[frame_number][regionN][label_i] <= pixelvalue_threshold){
                                minpixel_check[i] = 1;
                                count_final++;
                            }else{
                                minpixel_check[i] = 0;
                            }
                            label_i++;
                        }
                    }
                    
                    nLabels_table[frame_number][regionN] = count_final;

                    int label_max = 0;
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            int label_value = (int) labels.get(y, x)[0];                           
                            if(label_value > label_max){
                                label_max = label_value;
                            }

                            if (label_value != 0) {
                                if (minpixel_check[label_value] == 1) {
                                    Color color = new Color(255, 255, 255);
                                    final_cFos.setRGB(x, y, color.getRGB());                                   
                                }
                            }
                        }
                    }
                   
                }

            //Make overlayed image
            String ori_path = oriimage_path + files_model[nfile].getName().split("_")[0] + ".jpg";
            BufferedImage res = ImageIO.read(new File(ori_path));
			BufferedImage mask = final_cFos;
			try {
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						int mask_value = mask.getRGB(x, y);                    

						if (mask_value == -1) {
							Color color = new Color(255, 0, 0);
							res.setRGB(x, y, color.getRGB());
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			ImageIO.write(res, "png", new File(final_cFos_path + files_model[nfile].getName().split("_")[0] + ".png"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String outputPath = result_path + "result.csv";
        PrintWriter out = null;
        try {
            out = new PrintWriter(new FileWriter(outputPath));
            out.print("frameNumber");
            for (int i = 2; i < number_of_subjectRegios; i++) {
                out.print("," + i);
            }
            out.println("");
            for (int nfile = 0; nfile < files_model.length; nfile++) {
                String frameNumber = files_model[nfile].getName().split("_")[0];
                out.print(frameNumber);
                int frame = Integer.parseInt(frameNumber);
                for (int i = 2; i < number_of_subjectRegios; i++) {
                    out.print("," + nLabels_table[frame][i]);
                }
                out.println("");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }

        String outputPath2 = result_path + "pixelresult.csv";
        PrintWriter out2 = null;
        try {
            out2 = new PrintWriter(new FileWriter(outputPath2));
            out2.print("frame_number,region_number,MinPixel_value");
            out2.println("");
            for (int nfile = 0; nfile < files_model.length; nfile++) {
                String frameNumber = files_model[nfile].getName().split("_")[0];
                int frame = Integer.parseInt(frameNumber);
                for (int i = 2; i < number_of_subjectRegios; i++) {
                    for (int j = 1; j < MY_MAX_VALUE; j++) {
                        if (MinPixel[frame][i][j] != -1) {
                            out2.println(frameNumber + "," + i + "," + MinPixel[frame][i][j]);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out2 != null) {
                out2.close();
            }
        }

    }
}



import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class DetectcFos {
	public static void main(String[] args) {

		String input_name = "C:\\AA\\";
		String result_path = "C:\\BB\\";

        //please set the number as you want to try, lines 20 to 26 are variable
		int gaussian_kernel = 5;
		double gaussian_sigma = 1.3; 
		int median_kernel = 5;
		double binarization_threshold = 20; 
		int opening_kernel = 5;
		int area_min= 10;
		int area_max = 550;

		File file = new File(input_name);
		File files[] = file.listFiles();

		try {

			File lib_folder = new File("lib");//select your opencv folder
			String lib_path = lib_folder.getAbsolutePath();
			System.load(lib_path + "\\" + Core.NATIVE_LIBRARY_NAME + ".dll");

			for (int nfile = 0; nfile < files.length; nfile++) {

				// read the images that subtracted background in the ImgeJ
				Mat image = Imgcodecs.imread(files[nfile].toString());
				BufferedImage oriimage = ImageIO.read(files[nfile]);

				new File(result_path).mkdirs();

                // Convert to grayscale image
				Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);

				// Median filter
				Mat source2 = new Mat();
				Imgproc.medianBlur(image, source2, median_kernel);

				// Gaussian filter
				Mat source = new Mat();
				Imgproc.GaussianBlur(source2, source, new Size(gaussian_kernel, gaussian_kernel), gaussian_sigma);

				// Binarizaion
				Imgproc.threshold(source, source, binarization_threshold, 255.0, Imgproc.THRESH_BINARY);

				// Opening
				Imgproc.morphologyEx(source, source, Imgproc.MORPH_OPEN,
				Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(opening_kernel, opening_kernel)));

				int width = oriimage.getWidth();
				int height = oriimage.getHeight();

				// Labeling
				Mat stats = new Mat(); 
				Mat centroids = new Mat(); 
				Mat labels = new Mat(); 
				int nLabels = Imgproc.connectedComponentsWithStats(source, labels, stats, centroids, 8, CvType.CV_32S);

				// Get the area of each labelled area
                // if the labeling area is 10 px< candidate <550 px, the area is remained
				int[] check_table = new int[nLabels];
				check_table[0] = 0;
				for (int i = 1; i < nLabels; i++) {
					int[] result = new int[1];
					stats.get(i, Imgproc.CC_STAT_AREA, result);
					if (result[0] > area_min&& result[0] < area_max) {
						check_table[i] = 1;
					} else {
						check_table[i] = 0;
					}
				}

                BufferedImage resimage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						double[] label_value = labels.get(y, x);
						int v = (int) label_value[0];
						if (check_table[v] == 1) {
							Color color = new Color(255, 255, 255);
							resimage.setRGB(x, y, color.getRGB());
						} else {
							Color color = new Color(0, 0, 0);
							resimage.setRGB(x, y, color.getRGB());
						}
					}
				}

				String file_name = files[nfile].getName().split("\\.")[0];
				ImageIO.write(resimage, "png", new File(result_path + file_name + ".png"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

import java.io.File;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
/**
 * Convert to grayscale images
 */

public class Grayscale {
	public static void main(String[] args) {

		String input_name = "C:\\AAA\\";
		String result_path = "C:\\BBB\\";		

		File file = new File(input_name);
		File files[] = file.listFiles();

		try {
			File lib_folder = new File("lib");
			String lib_path = lib_folder.getAbsolutePath();
			System.load(lib_path + "\\" + Core.NATIVE_LIBRARY_NAME + ".dll");

			for (int nfile = 0; nfile < files.length; nfile++) {
			

				// read input images
				Mat image = Imgcodecs.imread(files[nfile].toString());

				new File(result_path).mkdirs();

				// convert to grayscale image
				Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);
				Imgcodecs.imwrite(result_path + "grayscale.png", image);				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


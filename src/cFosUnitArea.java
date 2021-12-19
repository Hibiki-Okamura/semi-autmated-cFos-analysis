import java.io.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class cFosUnitArea {
    
    public static void main(String[] args) {
        String separate_mask = "select the file after exclude edge in ImageJ";
        String model_dir =  "please match the Atlas and microscope images";
        int number_of_subjectRegios = 100;//please count your dataset
        File csv_path = new File("Select the file made in CountcFosNumber.java");

		File file = new File(model_dir);
		File files[] = file.listFiles();

        int[] areaOfEachRegion = new int[number_of_subjectRegios];
        int[] nLabels_table = new int[number_of_subjectRegios];
        double areaPerPixel = 0.000454*0.000454; 

        try {

			for (int nfile = 0; nfile < files.length; nfile++) {

           String model_path = files[nfile].toString();
           String separate_region = separate_mask + files[nfile].getName().split("_")[0] + "_mask.png";
           
            BufferedImage separate_image = ImageIO.read(new File(separate_region));
            BufferedImage model_image = ImageIO.read(new File(model_path));

            //
            int xsize = model_image.getWidth();
            int ysize = model_image.getHeight();

            for (int regionN = 2; regionN < number_of_subjectRegios; regionN++) {
               
                for (int y = 0; y < ysize; y++) {
                    for (int x = 0; x < xsize; x++) {
                        int separate_value = separate_image.getRGB(x, y);
                        separate_value = (separate_value >>16) & 0xff;
                        int model_value = model_image.getRGB(x, y);
                        model_value = (model_value >>16) & 0xff;

                        if (separate_value == 0 && model_value == regionN) {
                            areaOfEachRegion[regionN]++;
                        }

                    }
                }
            }
        }


            try {
                FileReader fileReader = new FileReader(csv_path);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String data = bufferedReader.readLine();
                String[] r_num_str = data.split(",");
                while ((data = bufferedReader.readLine()) != null) {
                    String[] str = data.split(",");
                    
                    for (int i = 1; i < str.length; i++) {
                        
                        int r_num = Integer.parseInt(r_num_str[i]);
                        nLabels_table[r_num] += Integer.parseInt(str[i]);
                    }
                    
                }
                bufferedReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            

        } catch (Exception e) {
            e.printStackTrace();
        }
        //calculate the number of cFos signals per unit area
        for (int regionN = 2; regionN < number_of_subjectRegios; regionN++) {
            double result = 0;
            if(areaOfEachRegion[regionN]!= 0) {
                result = (double) nLabels_table[regionN]/(double)(areaOfEachRegion[regionN]*areaPerPixel);
            }
            //The result of the number of cFos signals per unit area
            System.out.println(result);            
        }
    
    }

    
}




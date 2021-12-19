# How to use 
### 1. Run Grayscale.java  
Set your path.  
- input: microscope image  
- output: grayscale image
### 2. Subtract backgrount in ImageJ  
For example, radius = 50.  
- input: grayscale image of 1.
- output: subtract background image.
### 3. Run DetectcFos.java  
Set your path. Please set the variables as you want from line 20 to 26.  
- input: image obtained from 2.  
- output: cFos candidate image.  
### 4. Run CountcFosNumber.java  
Set your path. Please set the variables as you want from line 31 to 33.  
- input: image obtained from 3  
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; matched image between atlas and microscope image.  
- output: cFos image  
 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;the csv file include the number of cFos per each subject area  
### 5. Make "separate_mask" in ImageJ using exclude edge function in Analyze Particles.  
- input: grayscale image  
- output: binarization image obtained from ImageJ

### 6. Run cFosUnitArea.java  
Set your path. Please set the variables as you want from line 11 and 19.  
- input: separate_mask obtained from ImageJ  
&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; matched image  
- output: the number of c-Fos signals per unit area is showed in the terminal

# The rule of the image name
- please make the matched image between Atlas and microscope image
- The name of matched image is "frame number+_+(ANY NAME IS OK)"
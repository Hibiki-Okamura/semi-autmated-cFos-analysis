# loading library for the further analysis
library(readr)

# data-importaing and data-processing for analysis 
okamura <- read_csv("mean_autoscale.csv") # loading autoscaled dataset 1/4, .csv file is deposited in this project
moka<-as.matrix(okamura[,-1]) # conversion to numetric matrix data with excluding characters from original csv.file 2/4
rownames(moka)<- c(okamura$X1) # renaming rows with regions' name 3/4
t_moka<-t(moka) # transposing dataset for group-wise HCA 4/4

# Calculating the euclidean distances
groups.d<- dist(t_moka) # the euclidean distances between groups 
region.d<- dist(moka) # the euclidean distances between regions

# HCA using Ward's method
groups.hc<-hclust(groups.d, method="ward.D") # group-wise HCA using "not"-squared euclidean distances
region.hc<-hclust(region.d, method="ward.D") # region-wise HCA using "not"-squared euclidean distances
groups.hc2<-hclust(groups.d, method="ward.D2") # group-wise HCA using squared euclidean distances
region.hc2<-hclust(region.d, method="ward.D2") # region-wise HCA using squared euclidean distances

# ploting dendograms and exporting the dendograms into jpg.file
jpeg("ward_plot.jpg", width = 1000, height = 1000) # specifying .file type
par(mfrow=c(2,2)) # separating the drawing space into 2x2
plot(groups.hc,hang=-1,main="Ward 1") # ploting dendorogram [group-wise, "not"-squared euclidean distances]
plot(groups.hc2,hang=-1,main="Ward 2") # ploting dendorogram [group-wise, squared euclidean distances]
plot(region.hc,hang=-1,main="Ward 1") # ploting dendorogram [region-wise, "not"-squared euclidean distances]
plot(region.hc2,hang=-1,main="Ward 2") # ploting dendorogram [region-wise, squared euclidean distances]
dev.off() # finish drawing

# Generation of Rdata for HCA, all .Rdata are deposited in this project
save(region.hc, file ="regionalWard1_Summary.RData " )
save(region.hc2, file ="regionalWard2_Summary.RData " )
save(groups.hc, file ="group_Ward1_Summary.RData " )
save(groups.hc2, file ="group_Ward2_Summary.RData " )

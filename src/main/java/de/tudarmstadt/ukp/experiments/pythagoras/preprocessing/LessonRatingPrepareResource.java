/**
 * 
 */
package de.tudarmstadt.ukp.experiments.pythagoras.preprocessing;

import java.awt.Toolkit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

/**
 * Generates WEKA-supported .arff files for binary classification and regression.
 * @param training-data.arff file generated by WEKA for the whole corpus. 
 * 
 * @author Tahir Sousa
 * @version last updated: Jun 11, 2014 [Sousa]
 */
public class LessonRatingPrepareResource {

	//Put correct File Directory here
	public static final String WEKA_FILEDIR = 
			"E:/Eclipse/DKPro_Workspace/Pythagoras/de.tudarmstadt.ukp.dkpro.lab/repository";
	//Put correct File Name here
	public static final String WEKA_FILENAME = "/training-dataOverall.arff";	
	public static final String[] DIM_NAMES = {"relev","rez","gem","denk","koop","rueck","anerl"};
	
	public static void main(String[] args) throws IOException {
		
		//Generate .arff files for each dimension. Meant for regression.
		//generateRegressionFiles();
		
		//Generate .arff files for each dimension. Meant for binary classification.
		generateClassificationFiles();
		
        //Alert user when done
        Toolkit.getDefaultToolkit().beep();
		Toolkit.getDefaultToolkit().beep();
		Toolkit.getDefaultToolkit().beep();
	}
	
	/**
	 * Generate .arff files for each dimension. Meant for binary classification.
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */
	private static void generateClassificationFiles() throws IOException, FileNotFoundException {

		for	(int i=0; i< DIM_NAMES.length; i++)	{
			String dimName = DIM_NAMES[i];
			String OUTPUT_FILEPATH = WEKA_FILEDIR + "/Classification_" + dimName + "-training-data.arff";
			
			String RATING_FILEPATH = "src/main/resources/ExpertRatings/" + dimName + ".txt";
			
			List<String> wekaOutputLines = FileUtils.readLines(new File(WEKA_FILEDIR + WEKA_FILENAME));
			List<String> ratingsLines = FileUtils.readLines(new File(RATING_FILEPATH));
			Map<String, String[]> ratingsMap = new HashMap<String, String[]>();
			String finalOutput = "";
			Boolean dataFlag = false;

			for	(String ratingsLine : ratingsLines)	{
				String[] splitRatingsLine = ratingsLine.split(" ",3);
				String ratingString = "";
				String dimensionName = "";
				String fileName = "";
				
				dimensionName = splitRatingsLine[0];
				fileName = splitRatingsLine[1];
				ratingString = splitRatingsLine[2];
				
				String[] ratingsParams = {dimensionName, ratingString};
				ratingsMap.put(fileName, ratingsParams);
			}
			
			Set<String> ratingsFileNames = ratingsMap.keySet();
			for	(String wekaLine : wekaOutputLines)	{
				
				if	(wekaLine.contains("@attribute outcome"))	{
					finalOutput += "@attribute " + dimName.toUpperCase() + "_Outcome {" + dimName + "Bad," + dimName + "Good}\n\n";
					finalOutput += "@data\n\n";
					dataFlag = true;							//Ready to accept WEKA "data".
				}
				
				if	(!dataFlag)	{
					finalOutput += wekaLine + "\n";
				}
				
				if	(dataFlag)	{
					for	(String rFileName : ratingsFileNames)	{
						if	(wekaLine.contains(rFileName))	{
							String ratingString = ratingsMap.get(rFileName)[1];
							double rating;
							rating = (ratingString.equals("") ? 0 : Double.parseDouble(ratingString)); 
							String ratedClass = null;
							String wekaLineSubstring = wekaLine.substring(0, wekaLine.lastIndexOf(",") + 1);
							
							if	(rating>=1 && rating <= 2)	{
								System.out.println(rFileName + "\t"+ dimName+ "Bad");
								ratedClass = dimName + "Bad"; 
							}
							
							else if (rating>=3)	{
								System.out.println(rFileName + "\t"+ dimName+ "Good");
								ratedClass = dimName + "Good";
							}
							
							else	{
								ratedClass = dimName + "Neutral";
							}
							
							if	(ratedClass.contains("Bad") || ratedClass.contains("Good"))	{
								if	(!ratingString.equals(""))	{
									finalOutput += wekaLineSubstring + ratedClass + "\n";	
								}
								else
									finalOutput += wekaLineSubstring + "?" + "\n";						//In case of a missing rating value, put '?' sign.	
							}
							
						}
					}	
				}
				
			}
			System.out.println(finalOutput);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(OUTPUT_FILEPATH),"UTF-8"));
			
			writer.write(finalOutput);	
			writer.close();	
		}
		
	}

	/**
	 * Generates .arff files for each dimension. Meant for regression.
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static void generateRegressionFiles() throws IOException, FileNotFoundException	{
		for	(int i=0; i< 7; i++)	{
			String dimName = DIM_NAMES[i];
			String outputFilePath = WEKA_FILEDIR + "/Regression_" + dimName + "-training-data.arff";
			
			String ratingFilePath = "src/main/resources/ExpertRatings/" + dimName + ".txt";
			
			List<String> wekaOutputLines = FileUtils.readLines(new File(WEKA_FILEDIR + WEKA_FILENAME));
			List<String> ratingsLines = FileUtils.readLines(new File(ratingFilePath));
			Map<String, String[]> ratingsMap = new HashMap<String, String[]>();
			String finalOutput = "";
			Boolean dataFlag = false;

			for	(String ratingsLine : ratingsLines)	{
				String[] splitRatingsLine = ratingsLine.split(" ",3);
				String ratingString = "";
				String dimensionName = "";
				String fileName = "";
				
				dimensionName = splitRatingsLine[0];
				fileName = splitRatingsLine[1];
				ratingString = splitRatingsLine[2];
				
				String[] ratingsParams = {dimensionName, ratingString};
				ratingsMap.put(fileName, ratingsParams);
			}
			
			Set<String> ratingsFileNames = ratingsMap.keySet();
			for	(String wekaLine : wekaOutputLines)	{
				
				if	(!dataFlag)	{
					finalOutput += wekaLine + "\n";
				}
				
				if	(wekaLine.contains("@attribute outcome"))	{
					finalOutput += "@attribute " + dimName.toUpperCase() + "_AverageRating numeric\n\n";
					finalOutput += "@data\n\n";
					dataFlag = true;							//Ready to accept WEKA "data".
				}
				
				if	(dataFlag)	{
					for	(String rFileName : ratingsFileNames)	{
						if	(wekaLine.contains(rFileName))	{
							String rating = ratingsMap.get(rFileName)[1];
							
							if	(!rating.equals(""))	{
								finalOutput += wekaLine + "," + rating + "\n";	
							}
							else
								finalOutput += wekaLine + ",?" + "\n";						//In case of a missing rating value, put '?' sign.
							
						}
					}	
				}
				
			}
			System.out.println(finalOutput);

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath),"UTF-8"));
			
			writer.write(finalOutput);	
			writer.close();	
		}
	}

}

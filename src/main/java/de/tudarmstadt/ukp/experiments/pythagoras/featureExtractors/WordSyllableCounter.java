package de.tudarmstadt.ukp.experiments.pythagoras.featureExtractors;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Counts syllables in words.  
 * 
 * This class is based on the methods of 'syll_en' and 'syll_de' 
 * in Linux'Style' command (a part of 'diction' package). 
 * 
 * @author zhu, zesch
 * @version last updated: Apr 03, 2014[Sousa]
 */
public class WordSyllableCounter {

    private final String[] vowelsArray   = {"a", "e", "i", "o", "u"};
    private final String[] enVowelsArray = {"a", "e", "i", "o", "u", "y"};
    private final String[] deVowelsArray = {"a", "e", "i", "o", "u", "ä", "ö", "ü"};

    private final Set<String> vowels;
    private final Set<String> deVowels;
    private final Set<String> enVowels;
    
    private final String languageCode;
    
    public WordSyllableCounter(String languageCode)
    {
        vowels   = new HashSet<String>(Arrays.asList(vowelsArray));
        deVowels = new HashSet<String>(Arrays.asList(deVowelsArray));
        enVowels = new HashSet<String>(Arrays.asList(enVowelsArray));
        
        this.languageCode = languageCode;
    }
    
    private boolean isVowel(String character)
    {
        if(languageCode.equals("en")) { 
            return enVowels.contains(character);
        }
        else if(languageCode.equals("de")) {
            return deVowels.contains(character);
        }
        else {
            return vowels.contains(character);
        }
    }
    
    public int countSyllables(Iterable<String> words) {
        int count = 0;
        for (String word : words) {
            count = count + countSyllables(word);
        }
        return count;
    }
    
    public int countSyllables(String word){
    	String lowcaseWord = word.toLowerCase();
        int count = 0;
        
        if (this.languageCode.equals("en")) {
            if (lowcaseWord.length() >=2 &&
                lowcaseWord.substring(lowcaseWord.length() - 2, lowcaseWord.length()).equals("ed"))
            {
            	lowcaseWord = lowcaseWord.substring(0, lowcaseWord.length() - 2);
            }
        }
        else if (this.languageCode.equals("de")) {
            if (lowcaseWord.length() >= 2 &&
                lowcaseWord.charAt(lowcaseWord.length() - 1) == 'e' && 
                !isVowel(lowcaseWord.substring(lowcaseWord.length() - 2, lowcaseWord.length() - 1)))
            {
                    count++;
                    lowcaseWord = lowcaseWord.substring(0, lowcaseWord.length() - 2);
            }
            
        }
        
        for(int i = 0; i < lowcaseWord.length() - 1; ++ i){
          String curCh = lowcaseWord.substring(i, i+ 1);
          String nextCh = lowcaseWord.substring(i + 1, i + 2);
             if(isVowel(curCh) && !isVowel(nextCh))
            	 ++ count;
        }
        return (count == 0 ? 1 : count);
    }
}
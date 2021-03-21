/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yugiohproxymaker;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alan
 */
public class Main {
    private static Scanner scan = new Scanner(System.in);
    
    private static final float marginLeft = 36;
    private static final float marginTop = 50;
    private static final float marginRight = 36;
    private static final float marginBottom = 50;
    
    private static final String DEFAULT_FILE_NAME = "Generated Deck";
    
    public static void main(String[] args) {
        /*
        // <desk_list_location>
        // <desk_list_location> <destination>
        // <desk_list_location> <use_image> <colored>
        // <desk_list_location> <destination> <use_image> <colored> [--left=<px>] [--top=<px>] [--right=<px>] [--bottom=<px>]
        */
        
        if(args.length == 1){//desk_list_location
            File deckListFile = new File(args[0]);
            if(!deckListFile.exists()){
                System.out.println("File doesn't exist");
                System.exit(0);
            }
            File dest = getDefaultFileName(DEFAULT_FILE_NAME, 0);
            System.out.println(dest);
            boolean overwrite = overwrite(dest);
            if(overwrite){
                System.out.println("Generating...");
                generate(deckListFile, dest, false, false, marginLeft, marginTop, marginRight, marginBottom);
                System.out.println("Finished, File Location : "+dest.getAbsolutePath());
            }
        }
        else if(args.length == 2){//desk_list_location destination
            File deckListFile = new File(args[0]);
            if(!deckListFile.exists()){
                System.out.println("File doesn't exist");
                System.exit(0);
            }
            File destinationFile = new File(touchUpFileName(args[1]));
            boolean overwrite = overwrite(destinationFile);
            if(overwrite){
                System.out.println("Generating...");
                generate(deckListFile, destinationFile, false, false, marginLeft, marginTop, marginRight, marginBottom);
                System.out.println("Finished, File Location : "+destinationFile.getAbsolutePath());
            }
        }
        else if(args.length == 3){//desk_list_location <use_image> <colored>
            File deckListFile = new File(args[0]);
            if(!deckListFile.exists()){
                System.out.println("File doesn't exist");
                System.exit(0);
            }
            boolean useImage = getBoolean(args[1], false);
            boolean colored = getBoolean(args[2], false);
            
            File dest = getDefaultFileName(DEFAULT_FILE_NAME, 0);
            boolean overwrite = overwrite(dest);
            if(overwrite){
                System.out.println("Generating...");
                generate(deckListFile, dest, useImage, colored, marginLeft, marginTop, marginRight, marginBottom);
                System.out.println("Finished, File Location : "+dest.getAbsolutePath());
            }
        }
        else if(args.length >= 4 && args.length <= 8){//<desk_list_location> <destination> <use_image> <colored> [--left=<px>] [--top=<px>] [--right=<px>] [--bottom=<px>]
            File deckListFile = new File(args[0]);
            if(!deckListFile.exists()){
                System.out.println("File doesn't exist");
                System.exit(0);
            }
            
            boolean useImage = getBoolean(args[2], false);
            boolean colored = getBoolean(args[3], false);
            
            float left = marginLeft;
            float top = marginTop;
            float right = marginRight;
            float bottom = marginBottom;
            
            for(int i=4;i<args.length;i++){
                String param = args[i];
                if(param.startsWith("--")){
                    if(param.startsWith("left", 2)){
                        left = getFloat(param.substring(param.indexOf("=") + 1), marginLeft);
                    }
                    else if(param.startsWith("top", 2)){
                        top = getFloat(param.substring(param.indexOf("=") + 1), marginTop);
                    }
                    else if(param.startsWith("right", 2)){
                        right = getFloat(param.substring(param.indexOf("=") + 1), marginRight);
                    }
                    else if(param.startsWith("bottom", 2)){
                        bottom = getFloat(param.substring(param.indexOf("=") + 1), marginBottom);
                    }
                }
            }
            
            File destinationFile = new File(touchUpFileName(args[1]));
            boolean overwrite = overwrite(destinationFile);
            if(overwrite){
                System.out.println("Generating...");
                generate(deckListFile, destinationFile, useImage, colored, left, top, right, bottom);
                System.out.println("Finished, File Location : "+destinationFile.getAbsolutePath());
            }
        }
        else{
            System.out.println("Invalid Command");
        }
    }
    
    /**
     * Attempts to convert a string into a boolean value, if it fails it will return the provided default value
     * @param boolString  the string that will be converted to a boolean
     * @param defaultValue  the default value that will be return if the conversion fails
     * @return  a converted boolean value or the default value
     */
    private static boolean getBoolean(String boolString, boolean defaultValue){
        try{
            boolean f = Boolean.parseBoolean(boolString);
            return f;
        } catch(Exception e){
            return defaultValue;
        }
    }
    
    /**
     * Attempts to convert a string into a float value, if it fails it will return the provided default value
     * @param floatString  the string that will be converted to a float
     * @param defaultValue  the default value that will be return if the conversion fails
     * @return  a converted float value or the default value
     */
    private static float getFloat(String floatString, float defaultValue){
        try{
            float f = Float.parseFloat(floatString);
            return f;
        } catch(Exception e){
            return defaultValue;
        }
    }
    
    /**
     * Adds that .pdf extension to the end of the filename, even if the filename already contains an extension
     * @return  a filename that will always have .pdf at the end of it
     */
    private static String touchUpFileName(String fileName){
        if(fileName.lastIndexOf(".pdf") == -1){
            fileName += ".pdf";
        }
        return fileName;
    }
    
    /**
     * 
     * @param defaultFileName
     * @param number
     * @return 
     */
    private static File getDefaultFileName(String defaultFileName, int number){
        String num = number != 0 ? "("+number+")" : "";
        File file = new File(defaultFileName + num + ".pdf");
        if(!file.exists()){
            return file;
        }
        return getDefaultFileName(defaultFileName, ++number);
    }
    
    /**
     * Checks if the file exists, if the file does exist, the system will ask the user if they want to overwrite the file
     * @return  
     *      - true if the user wants to overwrite the file
     *      - false if the user does not want to overwrite the file
     *      - true is the file does not exist
     * 
     * return true to write file, false if you do not want to write the file
     */
    private static boolean overwrite(File file){
        if(file.exists()){
            System.out.println("This file already exists, do you want to overwrite? [y/n]");
            String input = scan.next();
            return input.equalsIgnoreCase("y");
        }
        return true;
    }
    
    /**
     * Generates a decklist based on a list of parameters
     * @param deckList  the decklist location
     * @param destination  the location in which the pdf will be saved to
     * @param useImage  a boolean value which will determine if the pdf will contain minimalistic cards or the full image
     * @param colored  whether or not the cards should be colored 
     * @param left  the left margin of the pdf page
     * @param top  the top margin of the pdf page
     * @param right  the right margin of the pdf page
     * @param bottom  the bottom margin of the pdf page
     * @return  true if the deck successfully generated, false if an error occured
     */
    private static boolean generate(File deckList, File destination, boolean useImage, boolean colored, float left, float top, float right, float bottom){
        if(!deckList.exists()){
            return false;
        }
        ArrayList<String> ids = getCardIds(deckList);
        ArrayList<Card> cards = getCards(ids);
        
        try {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.A4, left, right, top, bottom);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(destination));
                                    
            document.open();
            PdfContentByte cb = writer.getDirectContent();
        
            CardCreator creator = new CardCreator(document, cb);
            for(Card card : cards){
                creator.renderCard(card, useImage, !colored);
            }
            document.close();
            return true;
        } catch (FileNotFoundException | DocumentException ex) {}
        
        return false;
    }
    
    /**
     * Parses out all of the comments from a ydk file and only gets the card id's
     * @param file  the ydk file
     * @return  an array of strings that should only contain card id's
     */
    private static ArrayList<String> getCardIds(File file){
        if(file == null){
            return null;
        }
        
        ArrayList<String> cardIds = new ArrayList<>();
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine()) != null){
                line = line.trim();
                if(line.isEmpty()){
                    continue;
                }
                if(!line.startsWith("#") || !line.startsWith("!")){
                    cardIds.add(line);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return cardIds;
    }
    
    /**
     * Creates an array of card objects from card id's
     * @param cardIds  a list of card id's
     * @return  an array of cards
     */
    private static ArrayList<Card> getCards(ArrayList<String> cardIds){
        CardFetcher fandom = new CardFetcher();
        CardFetcherNew cardFetch = new CardFetcherNew();
        ArrayList<Card> cards = new ArrayList<>();
        for(String id : cardIds){
            if(!id.startsWith("!") && !id.startsWith(("#"))){
                Card card = cardFetch.fetch(id);
                if(card != null){
                    cards.add(card);
                }
                else{
                    Card attempt2 = fandom.fetch(id);
                    if(attempt2 != null){//will attempt to get the card from fandom
                        cards.add(attempt2);
                        System.out.println("Obtained card id: '" + id + "' from fandom");
                    }
                    else{
                        Card attempt3 = fandom.fetchBySearch(id);
                        if(attempt3 != null){//will attempt to get the card from fandom by searching
                            cards.add(attempt3);
                            System.out.println("Obtained card id: '" + id + "' from fandom search");
                        }
                        else{
                            System.out.println("Could not obtain card Id: " + id);
                        }
                    }
                }
            }
        }
        return cards;
    }
}

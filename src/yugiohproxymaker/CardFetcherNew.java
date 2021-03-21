/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yugiohproxymaker;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import javax.imageio.ImageIO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import yugiohproxymaker.Card.CardType;

/**
 *
 * @author Alan
 */
public class CardFetcherNew {
    private final String BASE_URL = "https://yugipedia.com/wiki/";
    
    //add different image urls
    
    private final String SEARCH_URL = "https://yugipedia.com/index.php?search=";
    
    private Document currentDoc;
    
    private HashMap<String, String> cardTableDetails;
    
    public CardFetcherNew(){}
    
    private Card fetch(String passcode, boolean search){
        currentDoc = null;
        passcode = passcodeCorrector(passcode);
        if(search){
            getDocBySearch(passcode);
        }
        else{
            getDoc(passcode);
        }
        
        if(!doesCardExist()){
            return null;
        }
        
        cardTableDetails = getTableDetails();
        if(cardTableDetails == null){
            return null;
        }
        
        Card card = new Card();
        
        String name = getName();
        boolean englishVariant = isInEnglish();
        String[] englishDescription = getEnglishDescription();
        
        Card.CardType cardType = Card.getCardType(getCardType());
        Card.Attribute attr = Card.getAttribute(getAttribute());
        Card.Property property = Card.getProperty(getProperty());
        
        card.setName(name);
        card.setDescription(englishDescription);
        card.setPasscode(passcode);
        card.setInEnglish(englishVariant);
        card.setImage(getImage());
        card.setCardType(cardType);
        card.setAttribute(attr);
        card.setProperty(property);
        card.setLevel(getLevel());
        card.setLinkArrows(getLinkArrows());
        card.setAttack(getAttack());
        card.setDefense(getDefOrLink());
        card.setMonsterTypes(getTypes());
        card.setMainMonsterType(getMainType(card.getMonsterTypes(), cardType));
        card.setPendulumScale(getPendulumScale());
        
        return card;
    }
    
    public Card fetch(String passcode){
        return fetch(passcode, false);
    }
    
    /**
     * Gets the card by searching the id
     * @param passcode
     * @return 
     */
    public Card fetchBySearch(String passcode){
        return fetch(passcode, true);
    }
    
    /**
     * Adds X amount of zeros to the beginning of the passcode until the passcode is 
     * of length 8 
     * @param passcode  the passcode
     * @return  a 8 digit passcode
     */
    public static String passcodeCorrector(String passcode){
        if(passcode == null){
            return passcode;
        }
        if(passcode.length() < 8){
            return passcodeCorrector("0"+passcode);
        }
        else{
            return passcode;
        }
    }
    
    private BufferedImage getImage() {
        BufferedImage image;
        try {
            URL url = new URL(getImageUrl());
            image = ImageIO.read(url);
            return image;
        } catch (IOException e) {
            return null;
        }
    }
    
    /**
     * checks if the webpage exists
     * @return 
     */
    private boolean doesCardExist(){
        return currentDoc != null;
    }
    
    private HashMap<String, String> getTableDetails(){
        if(!doesCardExist()){
            return null;
        }
        HashMap<String, String> cardDetails = new HashMap<>();
        
        Elements cardTable = currentDoc
                .getElementsByClass("card-table-columns").get(0)
                .getElementsByClass("innertable");//find the table with the card details
        if(cardTable.isEmpty()){
            return null;
        }
        
        Elements cardHeader = cardTable.get(0).getElementsByTag("th");//table headers
        
        for(int i=0;i<cardHeader.size();i++){
            Element headerElement = cardHeader.get(i);
            
            Element dataRow = headerElement.parent().getElementsByTag("td").get(0);
            
            String header = headerElement.text();
            String data = dataRow.text();
            
            cardDetails.put(header, data);
        }
        
        return cardDetails;
    }
    
    private String getName(){
        if(!doesCardExist()){
            return null;
        }
        
        return currentDoc.getElementsByClass("heading").get(0).text();
    }
    
    private String getCardType(){
        if(!doesCardExist()){
            return null;
        }
        if(cardTableDetails != null){
           return cardTableDetails.get("Card type"); 
        }
        return null;
    }
    
    private String getAttribute(){
        if(!doesCardExist()){
            return null;
        }
        if(cardTableDetails != null){
           return cardTableDetails.get("Attribute"); 
        }
        return null;
    }
    
    private String getProperty(){
        if(!doesCardExist()){
            return null;
        }
        if(cardTableDetails != null){
           return cardTableDetails.get("Property"); 
        }
        return null;
    }
    
    private int getLevel(){
        if(!doesCardExist()){
            return 0;
        }
        if(cardTableDetails != null){
            if(cardTableDetails.get("Level") != null){
                return Integer.parseInt(cardTableDetails.get("Level")); 
            }
            else if(cardTableDetails.get("Rank") != null){
                return Integer.parseInt(cardTableDetails.get("Rank")); 
            }
        }
        return 0;
    }
    
    private int getPendulumScale(){
        if(!doesCardExist()){
            return 0;
        }
        if(cardTableDetails != null){
            if(cardTableDetails.get("Pendulum Scale") != null){
                return Integer.parseInt(cardTableDetails.get("Pendulum Scale")); 
            }
        }
        return 0;
    }
    
    private Card.MonsterType getMainType(String[] types, CardType cardType){
        if(types == null){
            return null;
        }
        for(int i=0;i<types.length;i++){//Looks for pendulum
            if(types[i].equalsIgnoreCase(Card.MonsterType.PENDULUM.name())){
                return Card.MonsterType.PENDULUM;
            }
        }
        for(int i=0;i<types.length;i++){
            for(int j=0;j<Card.MonsterType.values().length;j++){
                if(types[i].equalsIgnoreCase(Card.MonsterType.values()[j].name())){
                    return Card.MonsterType.values()[j];
                }
            }
        }
        if(cardType.equals(CardType.MONSTER)){//if the cardtype is a monster and it doesnt show the type of monster, assume it is an effect monster
            return Card.MonsterType.EFFECT;
        }
        return null;
    }
    
    private String[] getTypes(){
        if(!doesCardExist()){
            return null;
        }
        if(cardTableDetails != null){
            if(cardTableDetails.get("Types") != null){
                String[] types = cardTableDetails.get("Types").split("/");
                for(int i=0;i<types.length;i++){
                    String s = types[i];
                    types[i] = s.trim();
                }
                return types;
            }
        }
        return null;
    }
    
    private String getAttack(){
        if(!doesCardExist()){
            return "";
        }
        if(cardTableDetails != null){
            if(cardTableDetails.get("ATK / DEF") != null){
                String attack = cardTableDetails.get("ATK / DEF").split("/")[0].trim();
                return attack;
            }
            else if(cardTableDetails.get("ATK / LINK") != null){
                String attack = cardTableDetails.get("ATK / LINK").split("/")[0].trim();
                return attack;
            }
        }
        return "";
    }
    
    private String getDefOrLink(){
        if(!doesCardExist()){
            return "";
        }
        if(cardTableDetails != null){
            if(cardTableDetails.get("ATK / DEF") != null){
                String attack = cardTableDetails.get("ATK / DEF").split("/")[1].trim();
                return attack;
            }
            else if(cardTableDetails.get("ATK / LINK") != null){
                String attack = cardTableDetails.get("ATK / LINK").split("/")[1].trim();
                return attack;
            }
        }
        return "";
    }
    
    private boolean[] getLinkArrows(){
        if(!doesCardExist()){
            return null;
        }
        if(cardTableDetails != null){
            if(cardTableDetails.get("Link Arrows") == null){
                return null;
            }
            String[] arrowStrings = cardTableDetails.get("Link Arrows").split(",");
            if(arrowStrings == null){
                return null;
            }
            boolean[] arrows = new boolean[8];
            for(String s : arrowStrings){
                s = s.trim();
                if(s.equalsIgnoreCase("Top-Left")){
                    arrows[0] = true;
                }
                else if(s.equalsIgnoreCase("Top")){
                    arrows[1] = true;
                }
                else if(s.equalsIgnoreCase("Top-Right")){
                    arrows[2] = true;
                }
                
                else if(s.equalsIgnoreCase("Left")){
                    arrows[3] = true;
                }
                else if(s.equalsIgnoreCase("Right")){
                    arrows[4] = true;
                }
                
                
                else if(s.equalsIgnoreCase("Bottom-Left")){
                    arrows[5] = true;
                }
                else if(s.equalsIgnoreCase("Bottom")){
                    arrows[6] = true;
                }
                else if(s.equalsIgnoreCase("Bottom-Right")){
                    arrows[7] = true;
                }
                
            }
            
            return arrows;
        }
        return null;
    }
    
    private String[] getEnglishDescription(){
        if(!doesCardExist()){
            return null;
        }
        Elements lore = currentDoc.select(".lore dl");
        if(lore.isEmpty()){
            lore = currentDoc.select(".lore p");
        }
        else{
            lore = lore.get(0).children();
        }
        String[] description = new String[lore.size()];
        for(int i=0;i<lore.size();i++){
            Element e = lore.get(i);
            description[i] = e.text();
        }
        return description;
    }
    
    /**
     * Gets the location of where the card image is located on the web
     * @return  returns an image url
     */
    private String getImageUrl(){
        if(!doesCardExist()){
            return null;
        }
        Elements elements = currentDoc.select(".cardtable-main_image-wrapper img");
        String url = elements.get(0).attr("src");
        return url;
    }
    
    /**
     * Checks if the image of the card is in english
     * 
     * @return  true if the image is in english, else false
     */
    private boolean isInEnglish(){
        String imageUrl = getImageUrl();
        return imageUrl.contains("EN");
    }
    
    /**
     * Gets the webpage of the card
     * @param passcode  the code at the bottom left of the card
     */
    private void getDoc(String passcode){
        try {
            String url = BASE_URL + passcode;
            currentDoc = Jsoup.connect(url).get();
        } catch (IOException ex) {
            getDocBySearch(passcode);
        }
    }
    
    /**
     * This is used if the getDoc() cannot retrieve the card webpage
     * 
     * the reason is because some passcode do not link to the wiki page of the card
     * @param passcode 
     */
    private void getDocBySearch(String passcode){
        try {
            String url = SEARCH_URL + passcode;
            currentDoc = Jsoup.connect(url).get();
            
            Elements results = currentDoc.select(".mw-search-results a");
            if(!results.isEmpty()){
                String cardUrl = results.get(0).attr("href");
                currentDoc = Jsoup.connect(cardUrl).get();
            }
            else{
                currentDoc = null;
            }
        } catch (IOException ex) {
            currentDoc = null;
        }
    }
}

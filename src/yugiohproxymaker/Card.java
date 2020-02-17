/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yugiohproxymaker;

import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 *
 * @author Alan
 */
public class Card {
    public enum MonsterType{
        NORMAL, EFFECT, PENDULUM, RITUAL, LINK, XYZ, SYNCHRO, FUSION, UNKNOWN
    }
    
    public enum CardType{
        MONSTER, SPELL, TRAP, UNKNOWN
    }
    
    public enum Attribute{
        DARK, DIVINE, EARTH, FIRE, LIGHT, WATER, WIND, SPELL, TRAP, UNKNOWN
    }
    
    public enum Property{//Only used for spells and traps
        NORMAL, CONTINUOUS, EQUIP, FIELD, QUICK_PLAY, RITUAL, COUNTER, NONE
    }
    
    /**
     * The name of the card
     */
    private String name;
    
    /**
     * The description of the card
     */
    private String[] description;
    
    /**
     * The code at the bottom left of the card, or the passcode
     */
    private String passcode;
    
    private int pendulumScale;
    
    private MonsterType mainMonsterType;
    
    private String[] monsterTypes;
    
    private CardType cardType;
    private Attribute attribute;
    private Property property;
    
    private int level;
    
    private String attack;
    private String defense;
    
    /**
     * contains an array of 8 values
     * [topleft, topcenter, topright, midleft, mid right, bottomleft, bottomcenter, bottomright
     */
    private boolean[] linkArrows;
    
    /**
     * this will be true if the image is in english, else false
     */
    private boolean inEnglish;
    
    private BufferedImage image;
    
    public Card(){
        name = "";
        description = new String[1];
        passcode = "";
        inEnglish = false;
        
        this.cardType = CardType.UNKNOWN;
        this.attribute = Attribute.UNKNOWN;
        this.property = Property.NONE;
        
        level = 0;
        pendulumScale = 0;
        
        image = null;
        
        linkArrows = new boolean[8];
        
        mainMonsterType = MonsterType.UNKNOWN;
        monsterTypes = new String[1];
    }

    public Card(String name, String[] description, String passcode, MonsterType mainMonsterType, String[] monsterTypes, CardType cardType, Attribute attribute, Property property, int level, int pendulumScale, String attack, String defense, boolean[] linkArrows, boolean inEnglish, BufferedImage image) {
        this.name = name;
        this.description = description;
        this.passcode = passcode;
        this.mainMonsterType = mainMonsterType;
        this.monsterTypes = monsterTypes;
        this.cardType = cardType;
        this.attribute = attribute;
        this.property = property;
        this.level = level;
        this.pendulumScale = pendulumScale;
        this.attack = attack;
        this.defense = defense;
        this.linkArrows = linkArrows;
        this.inEnglish = inEnglish;
        this.image = image;
    }
    
    public static CardType getCardType(String cardType){
        if(cardType == null){
            return CardType.UNKNOWN;
        }
        for(CardType type : CardType.values()){
            if(type.name().equalsIgnoreCase(cardType)){
                return type;
            }
        }
        return null;
    }
    
    public static Attribute getAttribute(String attribute){
        if(attribute == null){
            return Attribute.UNKNOWN;
        }
        for(Attribute attr : Attribute.values()){
            if(attr.name().equalsIgnoreCase(attribute)){
                return attr;
            }
        }
        return null;
    }
    
    public static Property getProperty(String property){
        if(property == null){
            return Property.NONE;
        }
        for(Property prop : Property.values()){
            if(prop.name().replaceAll("[-_]", "").equalsIgnoreCase(property.replaceAll("[-_]", ""))){
                return prop;
            }
        }
        return null;
    }
    
    public String getPropertyString(){
        switch(property){
            case NORMAL:
                return "";
            case CONTINUOUS:
                return "Continuous";
            case EQUIP:
                return "Equip";
            case FIELD:
                return "Field";
            case QUICK_PLAY:
                return "Quick Play";
            case RITUAL:
                return "Ritual";
            case COUNTER:
                return "Counter";
            default:
                return "";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getDescription() {
        return description;
    }

    public void setDescription(String[] description) {
        this.description = description;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public CardType getCardType() {
        return cardType;
    }

    public void setCardType(CardType cardType) {
        this.cardType = cardType;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPendulumScale() {
        return pendulumScale;
    }

    public void setPendulumScale(int pendulumScale) {
        this.pendulumScale = pendulumScale;
    }

    public boolean[] getLinkArrows() {
        return linkArrows;
    }

    public void setLinkArrows(boolean[] linkArrows) {
        this.linkArrows = linkArrows;
    }

    public boolean isInEnglish() {
        return inEnglish;
    }

    public void setInEnglish(boolean inEnglish) {
        this.inEnglish = inEnglish;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public String getAttack() {
        return attack;
    }

    public void setAttack(String attack) {
        this.attack = attack;
    }

    public String getDefense() {
        return defense;
    }

    public void setDefense(String defense) {
        this.defense = defense;
    }

    public MonsterType getMainMonsterType() {
        return mainMonsterType;
    }

    public void setMainMonsterType(MonsterType mainMonsterType) {
        this.mainMonsterType = mainMonsterType;
    }

    public String[] getMonsterTypes() {
        return monsterTypes;
    }

    public void setMonsterTypes(String[] monsterTypes) {
        this.monsterTypes = monsterTypes;
    }
    
    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("Name : ").append(name).append("\n");
        builder.append("Description : ").append(description).append("\n");
        builder.append("Passcode : ").append(passcode).append("\n");
        builder.append("Card Type : ").append(cardType).append("\n");
        builder.append("Attribute : ").append(attribute).append("\n");
        builder.append("Property : ").append(property).append("\n");
        builder.append("Level : ").append(level).append("\n");
        builder.append("Pendulum Scale : ").append(pendulumScale).append("\n");
        builder.append("Main Monster Type(s) : ").append(mainMonsterType).append("\n");
        builder.append("Monster Types : ").append(Arrays.toString(monsterTypes)).append("\n");
        builder.append("ATK / (DEF/LINK) : ").append(attack).append(" / ").append(defense).append("\n");
        builder.append("Link Arrows : ").append(Arrays.toString(linkArrows)).append("\n");
        
        builder.append("Is in English : ").append(inEnglish);
        
        return builder.toString();
    }
    
}

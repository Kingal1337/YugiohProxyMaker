/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Alan Tsui
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package yugiohproxymaker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Alan Tsui
 * @since 1.0
 * @version 1.1
 */
public class Text {    
    /**
     * Do not let anyone instantiate this class.
     */
    private Text(){}
    
    /**
     * Creates a toString from an object
     * @param object  the object
     * @return  returns a string
     */
    public static String createToStringOfObject(Object object) {
        StringBuilder builder = new StringBuilder();
        try {
            builder.append("[");
            builder.append("className:").append(object.getClass().getName()).append(", ");
            PropertyDescriptor[] pdArray = Introspector.getBeanInfo(object.getClass(), Object.class).getPropertyDescriptors();
            for(int i=0;i<pdArray.length;i++){
                PropertyDescriptor propertyDescriptor = pdArray[i];
                builder.append(propertyDescriptor.getName()).append(":").append(propertyDescriptor.getReadMethod().invoke(object));
                if(i < pdArray.length-1){
                    builder.append(", ");
                }
            }
            builder.append("]");
        } catch (java.beans.IntrospectionException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Text.class.getName()).log(Level.SEVERE, null, ex);
        }
        return builder.toString();
    }
    
    /**
     * Shortens string and adds a ... to the end of the string
     * @param string  the string that needs to be shortened
     * @param width  the width of your space in pixels
     * @param font  the font you are using
     * @return  returns a shortened string with a ... at the end. if the 
     * string width is less than width than it will return the original string
     */
    public static String ellipsisText(String string, int width, Font font){
        if(string == null || font == null || string.isEmpty() || getLengthOfString(string, font) < width){
            return string;
        }
        if(width < getLengthOfString(string.charAt(0)+"...", font)){
            if(width < getLengthOfString("...", font)){
                return "";
            }
            return "...";
        }
        String currentString = string;
        outerloop:
        while(true){
            String temp = currentString.substring(0, (currentString.length()/2));
            if(getLengthOfString(temp, font) > width){
                currentString = temp;
            }
            else{
                String temp2 = temp;
                int i = currentString.length()/2;
                while(true){
                    String temp3 = temp2;
                    if(getLengthOfString(temp3+"...",font) > width){
                        while(true){
                            temp3 = temp3.substring(0,temp3.length()-1);
                            if(getLengthOfString(temp3+"...",font) < width){
                                break;
                            }
                        }
                    }
                    temp2 = temp2+currentString.charAt(i);
                    if(getLengthOfString(temp2+"...",font) > width){
                        currentString = temp3+"...";
                        break outerloop;
                    }
                    i++;
                }
            }
        }
        return currentString;
    }
    
    /**
     * Splits strings without cutting into the middle of a word splits at 
     * the next space
     * @param originalString  the long long message
     * @param width  the space the message needs to fit into
     * @param font  the font you are using
     * @return  returns a array of strings that fits the width
     */
    public static String[] prettyStringWrap(String originalString, int width, Font font){
        if(originalString == null || font == null){
            return null;
        }
        ArrayList<String> strings = new ArrayList<>();
        if(getLengthOfString(originalString,font) > width){
            String[] words = originalString.split(" ");
            String nextString = "";
            for(int i=0;i<words.length;){
                String currentWord = words[i]+" ";
                if(getLengthOfString(currentWord,font) > width){
                    if(!nextString.isEmpty()){
                        strings.add(nextString);
                        nextString = "";
                    }
                    String[] newString = stringWrap(currentWord,width,font);
                    for(int j=0;j<newString.length;j++){
                        strings.add(newString[j]);
                    }
                    i++;
                }
                else if(getLengthOfString(nextString+currentWord,font) > width){
                    strings.add(nextString);
                    nextString = "";
                }
                else{
                    nextString+=currentWord;
                    i++;
                }
            }
            strings.add(nextString);
        }
        else{
            return new String[]{originalString};
        }
        return strings.toArray(new String[strings.size()]);
    }
    
    /**
     * Splits strings without cutting into the middle of a word splits at 
     * the next space
     * @param originalString  the long long message
     * @param width  the space the message needs to fit into
     * @param gd  the Graphics2D
     * @return  returns a array of strings that fits the width
     */
    public static String[] prettyStringWrap(String originalString, int width, Graphics2D gd){
        return prettyStringWrap(originalString, width, gd.getFont());
    }
    
    /**
     * Warps a string around a certain width, Will split in the middle of a word
     * @param originalString  the string that is going to be used
     * @param width  the width of the text area
     * @param font  the font you are using
     * @return  returns a array of strings that fits the width
     */
    public static String[] stringWrap(String originalString, int width, Font font){
        String originalTempString = originalString;
        String tempString = "";
        int lengthOfString = originalString.length();
        int counter = 0;
        ArrayList<String> strings = new ArrayList<>();
        if(getLengthOfString(originalString,font) > width){
            while(true){
//                System.out.println(getLengthOfString(originalTempString, font)+" "+getLengthOfString(tempString,font)+" "+width);
                if(getLengthOfString(tempString,font) <= width){
                    tempString += originalString.charAt(counter);
                }
                if(getLengthOfString(tempString,font) > width){
                    tempString = tempString.substring(0,tempString.length()-1);
                    counter--;
                    strings.add(tempString);
                    tempString = "";
                    originalTempString = originalString.substring(counter);
                }
                if(counter >= lengthOfString-1){
                    break;                    
                }
                counter++;
            }
            strings.add(originalTempString.substring(1).trim());
        }
        else{
            return new String[]{originalString};
        }
        return strings.toArray(new String[strings.size()]);
    }
    
    /**
     * Warps a string around a certain width, Will split in the middle of a word
     * @param originalString  the string that is going to be used
     * @param width  the width of the text area
     * @param gd  the Graphics2D
     * @return  returns a array of strings that fits the width
     */
    public static String[] stringWrap(String originalString,int width,Graphics2D gd){
        return stringWrap(originalString, width, gd.getFont());
    }
    
    public static Dimension getFontSize(FontMetrics metrics, Font font, String text) {
        // get the height of a line of text in this font and render context
        int hgt = metrics.getHeight();
        // get the advance of my text in this font and render context
        int adv = metrics.stringWidth(text);
        // calculate the size of a box to hold the text with some padding.
        Dimension size = new Dimension(adv + 2, hgt + 2);
        return size;
    }

    public static Font findFont(Graphics2D gd, Dimension componentSize, Font oldFont, String text) {
        //search up to 100
        Font savedFont = oldFont;
        for (int i = 0; i < 100; i++) {
            Font newFont = new Font(oldFont.getFontName(), oldFont.getStyle(), i);
            Dimension d = getFontSize(gd.getFontMetrics(newFont), newFont, text);
            if (componentSize.height < d.height || componentSize.width < d.width) {
                return savedFont;
            }
            savedFont = newFont;
        }
        return oldFont;
    }
    
    /**
     * Gets the height of all the strings in the array and adds them together
     * @param strings  the strings
     * @param font  the font you are using
     * @return  the sum of all the strings height
     */
    public static int getHeightOfStrings(String[] strings, Font font){
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        Graphics2D gd = (Graphics2D)g;
        gd.setFont(font);
        
        int height = 0;
        for(int i=0;i<strings.length;i++){
            height += getStringBounds(gd,strings[i],0,0).height;
        }
        return height;
    }
    
    /**
     * Gets the bounds of the string 
     * @param gd  the Graphics2D
     * @param string  the string
     * @param x  the x where you want to put the string
     * @param y  the y where you want to put the string
     * @return  returns the bounds of the string
     */
    public static Rectangle getStringBounds(Graphics2D gd, String string, float x, float y){
        FontRenderContext frc = gd.getFontRenderContext();
        GlyphVector gv = gd.getFont().createGlyphVector(frc, string);
        return gv.getPixelBounds(null, x, y);
    }
    
    /**
     * The length of a string in pixels
     * @param string  the string you need check the length of
     * @param font  the font the string is using
     * @return  returns a number base on how many pixels there are
     */
    public static int getLengthOfString(String string, Font font) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        FontMetrics fm = img.getGraphics().getFontMetrics(font);
        int width = fm.stringWidth(string);
        return width;
    }
    
    /**
     * Splits a string at an interval
     * @param string  the string that needs to be cut
     * @param interval  the interval 
     * @return 
     */
    public static String[] splitString(String string, int interval){
        int arrayLength = (int) Math.ceil(((string.length() / (double)interval)));
        String[] result = new String[arrayLength];

        int j = 0;
        int lastIndex = result.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            result[i] = string.substring(j, j + interval);
            j += interval;
        }
        result[lastIndex] = string.substring(j);

        return result;
    }
    
    /**
     * The height of the font in pixels
     * @param font  the font the string is using
     * @return  returns the height of the font
     */
    public static int getHeightOfString(Font font) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        FontMetrics fm = img.getGraphics().getFontMetrics(font);
        int height = fm.getMaxAscent();
        return height;
    }
    
    /**
     * Gets the largest string in the string array in pixels
     * @param strings  all the strings that need to be compared
     * @param font  the font all the strings are using
     * @return  returns the largest strings length in pixels
     */
    public static int getLargestNumber(String[] strings, Font font) {
        if(strings.length == 0){
            return 0;
        }
        int largeNumber = getLengthOfString(strings[0], font);
        for (int i = 0; i < strings.length; i++) {
            for (int j = 0; j < strings.length; j++) {
                if (i != j) {
                    if (getLengthOfString(strings[i], font) < getLengthOfString(strings[j], font)) {
                        if (getLengthOfString(strings[j], font) > largeNumber) {
                            largeNumber = getLengthOfString(strings[j], font);
                        }
                    }
                }
            }
        }
        return largeNumber;
    }
    
    /**
     * Get the largest string in the array
     * @param strings  the strings
     * @return  returns the last largest string in the array
     */
    public static String getLargestString(String[] strings){
        if(strings.length == 0){
            return null;
        }
        if(strings.length == 1){
            return strings[0];
        }
        String largest = strings[0];
        for(int i=1;i<strings.length;i++){
            if(largest.length() <= strings[i].length()){
                largest = strings[i];
            }
        }
        return largest;
    }
    
    
    /**
     * Removes the extension to string
     * @param string  The string that need the extension removed
     * @return  if the string has an extension to it
     * then returns the string without the extension 
     * else returns the original string
     */
    public static String removeExtension(String string){
        return string.contains(".") ? string.substring(0,string.lastIndexOf(".")) : string;
    }
    
    /**
     * Draws a string in the middle of the rectangle
     * @param string  the string that needs to be in the center
     * @param x  the x of the rectangle
     * @param y  the y of the rectangle
     * @param width  the width of the rectangle
     * @param height  the height of the rectangle
     * @param gd  the graphics2d from the JComponent
     */
    public static void drawCenteredString(String string,int x,int y, int width, int height, Graphics2D gd) {
        Point point = getStringPoint(string, x, y, width, height, gd);
        gd.drawString(string, point.x, point.y);
    }
    
    /**
     * Draws a string in the middle-left of the rectangle
     * @param string  string that will be aligned to the left
     * @param x  the x of the rectangle
     * @param y  the y of the rectangle
     * @param width  the width of the rectangle
     * @param height  the height of the rectangle
     * @param gd  the graphics2d from the JComponent
     */
    public static void drawCenteredLeftString(String string,int x,int y, int width, int height, Graphics2D gd) {
        Point point = getStringPoint(string, x, y, width, height, gd);
        gd.drawString(string, x, point.y);
    }
    
    /**
     * Draws a string in the middle-left of the rectangle
     * @param string  string that will be aligned to the left
     * @param x  the x of the rectangle
     * @param y  the y of the rectangle
     * @param width  the width of the rectangle
     * @param height  the height of the rectangle
     * @param gd  the graphics2d from the JComponent
     */
    public static void drawCenteredRightString(String string,int x,int y, int width, int height, Graphics2D gd) {
        Point point = getStringPoint(string, x, y, width, height, gd);
        int stringLength = getLengthOfString(string, gd.getFont());
        gd.drawString(string, width - stringLength, point.y);
    }
    
    /**
     * Draws a string in the middle of the rectangle
     * @param string  the string that needs to be in the center
     * @param x  the x of the rectangle
     * @param y  the y of the rectangle
     * @param width  the width of the rectangle
     * @param height  the height of the rectangle
     * @param gd  the graphics2d from the JComponent
     * @return  returns where the string should be drawn
     */
    public static Point getStringPoint(String string,int x,int y, int width, int height, Graphics2D gd) {
        FontMetrics fm = gd.getFontMetrics();
        int xx = ((width - fm.stringWidth(string)) / 2) + x;
        int yy = (fm.getAscent() + (height - (fm.getAscent() + fm.getDescent())) / 2) + y;
        return new Point(xx,yy);
    }
    
    /**
     * Outlines text
     * @param string  the string
     * @param stringColor  the string color
     * @param outlineColor  the outline color
     * @param point  the point where the string needs to be drawn
     * @param outLineSize  the size of the outline (should not exceed 5 or the outline will look weird)
     * @param gd  the Graphics2D
     */
    public static void outlineText(String string, Color stringColor, Color outlineColor, Point point, int outLineSize, Graphics2D gd){
        gd.setColor(outlineColor);
        gd.drawString(string, shiftWest(point.x, outLineSize), shiftNorth(point.y, outLineSize));
        gd.drawString(string, shiftWest(point.x, outLineSize), shiftSouth(point.y, outLineSize));
        gd.drawString(string, shiftWest(point.x, outLineSize), shiftNorth(point.y, outLineSize));
        gd.drawString(string, shiftWest(point.x, outLineSize), shiftSouth(point.y, outLineSize));
        gd.setColor(stringColor);
        gd.drawString(string, point.x, point.y);
    }
    
    /**
     * Creates a shadow of the text
     * @param string  the string
     * @param stringColor  the color of the string
     * @param shadowColor  the color of the shadow
     * @param point  the point where the string needs to be drawn
     * @param shadowDistance  the distance from the main text
     * @param gd  the Graphics2D
     */
    public static void shadowText(String string, Color stringColor, Color shadowColor, Point point, int shadowDistance, Graphics2D gd){
        gd.setColor(shadowColor);
        gd.drawString(string, shiftEast(point.x, shadowDistance), shiftSouth(point.y, shadowDistance));
        gd.setColor(stringColor);
        gd.drawString(string, point.x, point.y);
    }
    
    //<editor-fold defaultstate="collapsed" desc="3D Text">
    public static void draw3DToBottomLeft(String string, Color mainColor, Color highlight, Point point, int amountToPopOut, Graphics2D gd){
        for (int i = 0; i < amountToPopOut; i++) {
           gd.setColor(mainColor);
           gd.drawString(string, shiftWest(point.x, i), shiftNorth(shiftSouth(point.y, i), 1));
           gd.setColor(highlight);
           gd.drawString(string, shiftEast(shiftWest(point.x, i), 1), shiftSouth(point.y, i));
        }
        gd.setColor(mainColor);
        gd.drawString(string, shiftWest(point.x, amountToPopOut), shiftSouth(point.y, amountToPopOut));
    }

    public static void draw3DToTopRight(String string, Color mainColor, Color highlight, Point point, int amountToPopOut, Graphics2D gd){
        for (int i = 0; i < amountToPopOut; i++) {
           gd.setColor(mainColor);
           gd.drawString(string, shiftEast(point.x, i), shiftSouth(shiftNorth(point.y, i), 1));
           gd.setColor(highlight);
           gd.drawString(string, shiftWest(shiftEast(point.x, i), 1), shiftNorth(point.y, i));
        }
        gd.setColor(mainColor);
        gd.drawString(string, shiftEast(point.x, amountToPopOut), shiftNorth(point.y, amountToPopOut));
    }

    public static void draw3DToTopLeft(String string, Color mainColor, Color highlight, Point point, int amountToPopOut, Graphics2D gd){
        for (int i = 0; i < amountToPopOut; i++) {
           gd.setColor(mainColor);
           gd.drawString(string, shiftWest(point.x, i), shiftSouth(shiftNorth(point.y, i), 1));
           gd.setColor(highlight);
           gd.drawString(string, shiftEast(shiftWest(point.x, i), 1), shiftNorth(point.y, i));
        }
        gd.setColor(mainColor);
        gd.drawString(string, shiftWest(point.x, amountToPopOut), shiftNorth(point.y, amountToPopOut));
    }

    public static void draw3DToBottomRight(String string, Color mainColor, Color highlight, Point point, int amountToPopOut, Graphics2D gd){
        for (int i = 0; i < amountToPopOut; i++) {
           gd.setColor(mainColor);
           gd.drawString(string, shiftEast(point.x, i), shiftNorth(shiftSouth(point.y, i), 1));
           gd.setColor(highlight);
           gd.drawString(string, shiftWest(shiftEast(point.x, i), 1), shiftSouth(point.y, i));
        }
        gd.setColor(mainColor);
        gd.drawString(string, shiftEast(point.x, amountToPopOut), shiftSouth(point.y, amountToPopOut));
    }
    //</editor-fold>

    private static int shiftNorth(int p, int distance) {
        return (p - distance);
    }

    private static int shiftSouth(int p, int distance) {
        return (p + distance);
    }

    private static int shiftEast(int p, int distance) {
        return (p + distance);
    }

    private static int shiftWest(int p, int distance) {
        return (p - distance);
    }
}

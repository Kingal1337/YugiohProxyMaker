/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yugiohproxymaker;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import static yugiohproxymaker.MainPanel.resize;

/**
 *
 * @author Alan
 */
public class CardViewer extends JPanel{
    private Card card;
    
    private int width;
    private int height;
    
    private Font font;
    
    private BufferedImage image;
    
    private boolean saved;
    
    public CardViewer(Card card, int width, int height){
        this.card = card;
        
        this.width = width;
        this.height = height;
        setBackground(Color.WHITE);
        
        this.font = new Font("Arial", 0, 12);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D gd = (Graphics2D)g;
        
//        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//        gd = (Graphics2D)image.getGraphics();  
        
        gd.setColor(Color.WHITE);
        gd.fillRect(0, 0, width, height);
        
        gd.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        gd.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        gd.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        
        gd.setColor(Color.BLACK);
        drawName(gd);
        drawAttribute(gd);
        drawLevel(gd);
        drawDescription(gd);
        
//        if(!saved){
//            saved = true;
//            File outputfile = new File("image.png");
//            try {
//                ImageIO.write(image, "png", outputfile);
//            } catch (IOException ex) {
//                Logger.getLogger(CardViewer.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            
//            try {
//                Document document = new Document(PageSize.A4, 36,36,36,36);
//                PdfWriter.getInstance(document, new FileOutputStream(new File("SomePDF.pdf")));
//                document.open();
//
//
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                ImageIO.write(image, "png", baos);
//                Image iTextImage = Image.getInstance(baos.toByteArray());
//
//
//                document.add(iTextImage);
//                document.close();
//            } catch (FileNotFoundException ex) {
//                Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (DocumentException ex) {
//                Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (IOException ex) {
//                Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }
    
    private void drawName(Graphics2D gd){
        int x = width/32;
        int y = height/32;
        int w = width - (x * 2);
        int h = height/12;
        
        String text = " "+card.getName();
        
        Font scale = Text.findFont(gd, new Dimension(w, h), font, text);
        
        gd.setFont(scale);
        gd.drawRect(x, y, w, h);
        Text.drawCenteredLeftString(text, x, y, w, h, gd);
    }
    
    private void drawAttribute(Graphics2D gd){
        int nameX = width/32;
        int nameY = height/32;
//        int nameW = width - (nameX * 2);
        int nameH = height/12;
        
        int x = nameX;
        int y = nameY + nameH;
        int w = nameH * 2;
        int h = nameH;
        
        String text = card.getAttribute().name();
        if(card.getCardType() == Card.CardType.SPELL){
            text = "Spell";
        }
        if(card.getCardType() == Card.CardType.TRAP){
            text = "Trap";
        }
        
        Font scale = Text.findFont(gd, new Dimension(w, h), font, text);
        
        gd.setFont(scale);
        gd.drawRect(x, y, w, h);
        Text.drawCenteredString(text, x, y, w, h, gd);
        
    }
    
    private void drawLevel(Graphics2D gd){
        int nameX = width/32;
        int nameW = width - (nameX * 2);
        int nameH = height/12;
        int nameY = height/32 + nameH;
        
        int x = nameX;
        int y = nameY;
        int w = nameW;
        int h = nameH;
        
//        gd.drawRect(x, y, w, h);
        if(card.getCardType() == Card.CardType.MONSTER){
            if(card.getMainMonsterType() != Card.MonsterType.LINK){
                //Draws circles
//                int levelX = x + w - (h * card.getLevel());
//                for(int i=0;i<card.getLevel();i++){
//                    gd.drawOval(levelX, y, h, h);
//                    levelX += h;
//                }
                Text.drawCenteredRightString(card.getLevel()+"", x, y, w, h, gd);

            }
        }
        else{
            String text = card.getCardType().name();
            if(card.getProperty() != Card.Property.NORMAL && card.getProperty() != Card.Property.NONE){
                text += " ["+card.getProperty()+"]";
            }

            Font scale = Text.findFont(gd, new Dimension(w - h, h), font, text);

            gd.setFont(scale);
            Text.drawCenteredRightString(text, x, y, w, h, gd);
        }
    }
    
    private void drawImage(){
        
    }
    
    private void drawDescription(Graphics2D gd){
        int nameX = width/32;
        int nameW = width - (nameX * 2);
        int nameH = height/12;
        int nameY = height/32 + nameH * 2;
        
        int x = nameX;
        int y = nameY;
        int w = nameW;
        int h = height - nameY - nameH/2;
        
        gd.drawRect(x,y,w,h);
        
    Font font = new Font("Comic Sans MS", Font.PLAIN, 10);
    FontRenderContext frc = gd.getFontRenderContext();

//    GlyphVector gv = font.createGlyphVector(frc, s);
//    gd.drawGlyphVector(gv, 40, 60);
    
    
        gd.setFont(font);
        int heightOfFont = Text.getHeightOfString(font);
        
            y += heightOfFont;
        for(String descrip : card.getDescription()){
            String[] strings = Text.prettyStringWrap(descrip, w, font);
            for(String s : strings){
                GlyphVector gv = font.createGlyphVector(frc, s);
                gd.drawGlyphVector(gv, x + 2, y);
//                Text.drawCenteredLeftString(s, x + 2, y + 2, w, heightOfFont, gd);
                y += heightOfFont;
            }
        }
    }
    
    
}

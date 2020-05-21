/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yugiohproxymaker;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Utilities;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfShading;
import com.itextpdf.text.pdf.PdfShadingPattern;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import javax.swing.GrayFilter;

/**
 *
 * @author Alan
 */
public class CardCreator {
    public static final BaseColor TRANSPARENT = new BaseColor(0, 0, 0, 0);
    
    public static float cardWidth = Utilities.millimetersToPoints(59f);
    public static float cardHeight = Utilities.millimetersToPoints(86f);
    
    private PdfContentByte cb;
    private Document doc;
    
    private Font font;
    
    private float gap;
    
    private float startX;
    private float startY;
    
    private float currentX;
    private float currentY;
    
    private int curRow;
    private int curCol;
    
    private float maxRowsPerPage;
    private float maxColsPerPage;
    
    public CardCreator(Document document, PdfContentByte pcb){
        this.doc = document;
        this.cb = pcb;
        
        gap = 0;
        
        startX = currentX = document.leftMargin();
        startY = currentY = document.topMargin();
        
        float spaceForCardWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
        float spaceForCardHeight = document.getPageSize().getHeight()- document.topMargin()- document.bottomMargin();
        
        maxRowsPerPage = (int)Math.floor(spaceForCardHeight / (cardHeight + gap));
        maxColsPerPage = (int)Math.floor(spaceForCardWidth / (cardWidth + gap));
        
    }
    
    public void renderCard(Card card, boolean useImage, boolean blackAndWhite){
        font = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.NORMAL);
                
        if(curCol == maxColsPerPage){
            curCol = 0;
            curRow += 1;
            currentX = startX;
            currentY += cardHeight + gap;
        }
        if(curRow == maxRowsPerPage){
            curCol = 0;
            curRow = 0;
            currentX = startX;
            currentY = startY;
            doc.newPage();
        }
        
        if(useImage){
            drawCardImage(currentX, currentY, card, blackAndWhite);
        }
        else{
            drawCard(currentX, currentY, card, blackAndWhite);
        }
        currentX += cardWidth + gap;
        curCol += 1;
    }
    
//    public void init(Document document, PdfContentByte pcb){
//        
//        
//        font = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.NORMAL);
//        
//        CardFetcher fetch = new CardFetcher();
//        
//        Card card1 = fetch.fetch("46986414");//normal
//        Card card2 = fetch.fetch("38033121");//effect
//        Card card3 = fetch.fetch("30208479");//ritual
//        Card card4 = fetch.fetch("23995346");//fusion
//        Card card5 = fetch.fetch("44508094");//synchro
//        Card card6 = fetch.fetch("38273745");//xyz
//        Card card7 = fetch.fetch("80896940");//pendulum
//        Card card8 = fetch.fetch("95372220");//link
//        Card card9 = fetch.fetch("65169794");//spell equip
//        
//        Card card10 = fetch.fetch("41420027");//spell equip
//        
////        card = fetch.fetch("80896940");//Pendenulum
////        card = fetch.fetch("67598234");//link card
////        card = fetch.fetch("53129443");//spell - normal
////        card = fetch.fetch("65169794");//spell equip
//        
////        for(int i=0;i<card.getDescription().length;i++){
////            System.out.println(card.getDescription()[i]);
////        }
//        
//        drawCard(30,30, card1);//topleft
//        drawCard(30 + cardWidth + 5,30, card2);//topleft
//        drawCard(30 + (cardWidth + 5) * 2,30, card3);//topleft
//        
//        drawCard(30,30 + (cardHeight + 5), card4);//topleft
//        drawCard(30 + cardWidth + 5,30 + (cardHeight + 5), card5);//topleft
//        drawCard(30 + (cardWidth + 5) * 2,30 + (cardHeight + 5), card6);//topleft
//        
//        drawCard(30,30 + (cardHeight + 5) * 2, card7);//topleft
//        drawCard(30 + cardWidth + 5,30 + (cardHeight + 5) * 2, card8);//topleft
//        drawCard(30 + (cardWidth + 5) * 2,30 + (cardHeight + 5) * 2, card9);//topleft
//        
//        
//        document.newPage();
//        drawCard(30,30, card10);//topleft
//    }
    
    public static BufferedImage toBufferedImage(java.awt.Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }
    
    private void drawCardImage(float x, float y, Card card, boolean blackAndWhite){
        y = doc.getPageSize().getHeight() - cardHeight - y;
        
        BufferedImage bufferedImage = card.getImage();
        if(blackAndWhite){
            ImageFilter filter = new GrayFilter(true, 25);  
            ImageProducer producer = new FilteredImageSource(bufferedImage.getSource(), filter);  
            bufferedImage = toBufferedImage(Toolkit.getDefaultToolkit().createImage(producer));  
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", baos);
            Image iTextImage = Image.getInstance(baos.toByteArray());
            iTextImage.scaleAbsolute(cardWidth, cardHeight);
            iTextImage.setAbsolutePosition(x, y);
            doc.add(iTextImage);
        } catch (IOException | BadElementException ex) {
            Logger.getLogger(CardCreator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(CardCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void drawCard(float x, float y, Card card, boolean blackAndWhite){
        y = doc.getPageSize().getHeight() - y;
        
        int offsetGaps = 5;
        
        drawBackground(x, y, card, blackAndWhite);
        try {
            drawTitle(x + offsetGaps, y - offsetGaps, cardWidth - offsetGaps * 2, 20, card);
            
            drawAttribute(x + offsetGaps, y - offsetGaps - 20, (cardWidth - (offsetGaps * 2)) / 3, 20, card);
            
            float levelWidth = ((cardWidth - (offsetGaps * 2)) / 3);
            drawLevel(x + levelWidth + offsetGaps, y - offsetGaps - 20, levelWidth * 2, 20, card);
            
            drawCardType(x + offsetGaps, y - offsetGaps - 40, (cardWidth - (offsetGaps * 2)) / 3, 20, card);
            
            float descriptionHeight = (cardHeight - 40 - offsetGaps*5) / 1.5f;
            float descriptionY = y - offsetGaps - 40 - ((cardHeight - 40 - offsetGaps*5) - descriptionHeight);
            drawDescription(x + offsetGaps, descriptionY, cardWidth - offsetGaps * 2, descriptionHeight, card);
            
            float linkY = y - offsetGaps - 40 - offsetGaps;
            float linkH = linkY - (descriptionY) - offsetGaps;
            float linkW = linkH;
            float linkX = x + cardWidth/2 - linkW/2;
            
            drawLinkAndPend(linkX, linkY, linkW, linkH, card);
            
            drawAttackDef(x + offsetGaps, descriptionY - descriptionHeight, cardWidth - offsetGaps * 2, offsetGaps * 3, card);
        } catch (DocumentException | IOException ex) {
            Logger.getLogger(CardCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private Rectangle getRectangle(float x, float y, float w, float h){
        return new Rectangle(x, y - h, x + w, y);
    }
    
    private Rectangle drawRectangle(float x, float y, float w, float h, BaseColor background, BaseColor border){
        cb.saveState();
        Rectangle rect = getRectangle(x, y, w, h);
        rect.setBorder(Rectangle.BOX);
        rect.setBorderWidth(1);
        rect.setBackgroundColor(background);
        rect.setBorderColor(border);
        cb.rectangle(rect);
        cb.restoreState();
        
        return rect;
    }
    
    private void drawTextInRect(Phrase par, Rectangle rect, int vertAlign, int horAlign) throws DocumentException{
        ColumnText ct = new ColumnText(cb);

        PdfPTable table = new PdfPTable(1);
        table.setWidths(new float[] { 1 } );
        table.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell(par);
        cell.setBorder(0);
        cell.setHorizontalAlignment(horAlign);
        cell.setVerticalAlignment(vertAlign);
        cell.setFixedHeight(rect.getHeight());
        table.addCell(cell);

        ct.setSimpleColumn(rect);
        ct.addElement(table);
        ct.go();
    }
    
    private static BaseColor getMonsterColor(Card.MonsterType type){
        if(type == Card.MonsterType.NORMAL){
            return new BaseColor(242, 180, 57);
        }
        else if(type == Card.MonsterType.EFFECT){
            return new BaseColor(255, 132, 0);
        }
        else if(type == Card.MonsterType.RITUAL){
            return new BaseColor(0, 92, 170);
        }
        else if(type == Card.MonsterType.FUSION){
            return new BaseColor(116, 92, 170);
        }
        else if(type == Card.MonsterType.SYNCHRO){
            return new BaseColor(199, 199, 199);
        }
        else if(type == Card.MonsterType.XYZ){
            return new BaseColor(50,50,50);
        }
        else if(type == Card.MonsterType.PENDULUM){
            return new BaseColor(0, 156, 72);
        }
        else if(type == Card.MonsterType.LINK){
            return new BaseColor(0, 80, 200);
        }
        
        return new BaseColor(255, 0, 0);
    }
    
    private static BaseColor[] getMonsterColor(Card card){
        if(card.getCardType() == Card.CardType.SPELL){
            return new BaseColor[]{new BaseColor(0, 198, 151)};
        }
        if(card.getCardType() == Card.CardType.TRAP){
            return new BaseColor[]{new BaseColor(191, 0, 194)};
        }
        
        if(card.getMainMonsterType() == Card.MonsterType.PENDULUM){
            Card.MonsterType monsterType = null;
            for(int i=0;i<card.getMonsterTypes().length;i++){
                for(int j=0;j<Card.MonsterType.values().length;j++){
                    if(card.getMonsterTypes()[i].equalsIgnoreCase(Card.MonsterType.values()[j].name())){
                        if(!card.getMonsterTypes()[i].equalsIgnoreCase(Card.MonsterType.PENDULUM.name())){
                            monsterType = Card.MonsterType.values()[j];
                            break;
                        }
                    }
                }
            }
            if(monsterType == null){
                return new BaseColor[]{new BaseColor(0, 156, 72)};
            }
            else{
                return new BaseColor[]{getMonsterColor(monsterType), new BaseColor(0, 156, 72)};
            }
        }
        else{
            return new BaseColor[]{getMonsterColor(card.getMainMonsterType())};
        }
    }
    
    private void drawBackground(float x, float y, Card card, boolean blackAndWhite){
        BaseColor[] colors = new BaseColor[]{TRANSPARENT};
        if(!blackAndWhite){
            colors = getMonsterColor(card);
        }
        if(colors.length == 2){
            PdfShading axial = PdfShading.simpleAxial(cb.getPdfWriter(), x + cardWidth/2, y, x + cardWidth/2, y - cardHeight, colors[0], colors[1]);

            PdfShadingPattern shading = new PdfShadingPattern(axial);

            cb.saveState();

            cb.setShadingFill(shading);

            cb.moveTo(x, y);
            cb.lineTo(x + cardWidth, y);
            cb.lineTo(x + cardWidth, y - cardHeight);
            cb.lineTo(x, y - cardHeight);
            cb.closePathFillStroke();
            cb.restoreState();
        }
        else{
            BaseColor color = colors[0];
            drawRectangle(x, y, cardWidth, cardHeight, color, BaseColor.BLACK);
        }
        
    }
    
    private void drawTitle(float x, float y, float w, float h, Card card) throws DocumentException, IOException {
        
        Rectangle rect = drawRectangle(x, y, w, h, TRANSPARENT, BaseColor.BLACK);
                
        String text = card.getName();
        
        double fontSize = 16;
        
        boolean fits = false;
        
        while ((!fits) && fontSize > 5) {
            fontSize -= 0.1;
            Phrase p = new Phrase(text, new Font(Font.FontFamily.COURIER, (float)fontSize));
            fits = (ColumnText.getWidth(p) + 5) < rect.getWidth();
        }
        Paragraph par = new Paragraph(text, new Font(Font.FontFamily.COURIER, (float)fontSize));
        
        drawTextInRect(par, rect, Element.ALIGN_MIDDLE, Element.ALIGN_CENTER);
    }
    
    private void drawAttribute(float x, float y, float w, float h, Card card) throws DocumentException{
        
        Rectangle rect = drawRectangle(x, y, w, h, TRANSPARENT, BaseColor.BLACK);
        
        String text = card.getAttribute().name();
        if(card.getCardType() == Card.CardType.SPELL){
            text = "Spell";
        }
        if(card.getCardType() == Card.CardType.TRAP){
            text = "Trap";
        }
        
        boolean fits = false;
        float fontSize = 16;
        while ((!fits) && fontSize > 5) {
            fontSize -= 0.1;
            Phrase p = new Phrase(text, new Font(Font.FontFamily.COURIER, (float)fontSize));
            fits = (ColumnText.getWidth(p) + 5) < rect.getWidth();
        }
        
        Paragraph par = new Paragraph(text, new Font(Font.FontFamily.COURIER, (float)fontSize));
        drawTextInRect(par, rect, Element.ALIGN_MIDDLE, Element.ALIGN_CENTER);
    }
    
    private void drawLevel(float x, float y, float w, float h, Card card) throws DocumentException{
        if(card.getMainMonsterType() == Card.MonsterType.LINK){
            return;
        }
        
        Rectangle rect = getRectangle(x, y, w, h);
        
        String text = "";
        if(null != card.getCardType())switch (card.getCardType()) {
            case MONSTER:
                text = "Lvl : "+card.getLevel();
                break;
            case SPELL:
                text = card.getPropertyString();
                break;
            case TRAP:
                text = card.getPropertyString();
                break;
            default:
                break;
        }
                
        boolean fits = false;
        float fontSize = 16;
        while ((!fits) && fontSize > 5) {
            fontSize -= 0.1;
            Phrase p = new Phrase(text, new Font(Font.FontFamily.COURIER, (float)fontSize));
            fits = (ColumnText.getWidth(p) + 5) < rect.getWidth();
        }
        
        Paragraph par = new Paragraph(text, new Font(Font.FontFamily.COURIER, (float)fontSize));
        drawTextInRect(par, rect, Element.ALIGN_MIDDLE, Element.ALIGN_RIGHT);
    }
    
    private void drawCardType(float x, float y, float w, float h, Card card) throws DocumentException{
        if(card.getCardType() != Card.CardType.MONSTER){
            return;
        }
        Rectangle rect = getRectangle(x, y, w, h);
        
        String text = card.getMainMonsterType().name();
        
        double fontSize = 16;
        
        boolean fits = false;
        
        while ((!fits) && fontSize > 5) {
            fontSize -= 0.1;
            Phrase p = new Phrase(text, new Font(Font.FontFamily.COURIER, (float)fontSize));
            fits = (ColumnText.getWidth(p) + 5) < rect.getWidth();
        }
        Paragraph par = new Paragraph(text, new Font(Font.FontFamily.COURIER, (float)fontSize));
        
        drawTextInRect(par, rect, Element.ALIGN_MIDDLE, Element.ALIGN_CENTER);
    }
    
    private void drawLinkAndPend(float x, float y, float w, float h, Card card) throws DocumentException{
//        Rectangle rect = drawRectangle(x, y, w, h, TRANSPARENT, BaseColor.BLACK);
        Rectangle rect = getRectangle(x, y, w, h);
        if(card.getMainMonsterType() == Card.MonsterType.LINK){
            if(card.getLinkArrows()[0]){
                //topleft
                cb.moveTo(x, y);
                cb.lineTo(x + w/6, y);
                cb.lineTo(x, y - h/6);
                cb.setRGBColorFillF(0f, 0f, 0f);
                cb.closePathFillStroke();
            }

            if(card.getLinkArrows()[1]){
                //top
                cb.moveTo(x + w / 2, y + h/16);
                cb.lineTo(x + w/2 + w/4/2, y - h/16);
                cb.lineTo(x + w/2 - w/4/2, y - h/16);
                cb.setRGBColorFillF(0f, 0f, 0f);
                cb.closePathFillStroke();
            }

            if(card.getLinkArrows()[2]){
                //topright
                cb.moveTo(x + w, y);
                cb.lineTo(x + w - w/6, y);
                cb.lineTo(x + w, y - h/6);
                cb.setRGBColorFillF(0f, 0f, 0f);
                cb.closePathFillStroke();
            }

            if(card.getLinkArrows()[3]){
                //left
                cb.moveTo(x - w/16, y - h/2);
                cb.lineTo(x + w/16, y - h/2 + h/4/2);
                cb.lineTo(x + w/16, y - h/2 - h/4/2);
                cb.closePathFillStroke();
            }

            if(card.getLinkArrows()[4]){
                //right
                cb.moveTo(x + w + w/16, y - h/2);
                cb.lineTo(x + w - w/16, y - h/2 + h/4/2);
                cb.lineTo(x + w - w/16, y - h/2 - h/4/2);
                cb.closePathFillStroke();
            }

            if(card.getLinkArrows()[5]){
                //bottomleft
                cb.moveTo(x, y - h);
                cb.lineTo(x + w/6, y - h);
                cb.lineTo(x, y - h + h/6);
                cb.setRGBColorFillF(0f, 0f, 0f);
                cb.closePathFillStroke();
            }

            if(card.getLinkArrows()[6]){
                //bottom
                cb.moveTo(x + w / 2, y - h - h/16);
                cb.lineTo(x + w/2 + w/4/2, y - h + h/16);
                cb.lineTo(x + w/2 - w/4/2, y - h + h/16);
                cb.setRGBColorFillF(0f, 0f, 0f);
                cb.closePathFillStroke();
            }

            if(card.getLinkArrows()[7]){
                //topright
                cb.moveTo(x + w, y - h);
                cb.lineTo(x + w - w/6, y - h);
                cb.lineTo(x + w, y - h + h/6);
                cb.setRGBColorFillF(0f, 0f, 0f);
                cb.closePathFillStroke();
            }
        }
        else if(card.getMainMonsterType() == Card.MonsterType.PENDULUM){
            //left
            cb.moveTo(x - w/16, y - h/2);
            cb.lineTo(x + w/16, y - h/2 + h/4/2);
            cb.lineTo(x + w/16, y - h/2 - h/4/2);
            cb.closePathFillStroke();

            //right
            cb.moveTo(x + w + w/16, y - h/2);
            cb.lineTo(x + w - w/16, y - h/2 + h/4/2);
            cb.lineTo(x + w - w/16, y - h/2 - h/4/2);
            cb.closePathFillStroke();
                
                
            String text = card.getPendulumScale() + "";

            double fontSize = 16;

            boolean fits = false;

            while ((!fits) && fontSize > 5) {
                fontSize -= 0.1;
                Phrase p = new Phrase(text, new Font(Font.FontFamily.COURIER, (float)fontSize));
                fits = (ColumnText.getWidth(p) + 5) < rect.getWidth();
            }
            Paragraph par = new Paragraph(text, new Font(Font.FontFamily.COURIER, (float)fontSize));

            drawTextInRect(par, rect, Element.ALIGN_MIDDLE, Element.ALIGN_CENTER);
        }
        
    }
    
    private void drawDescription(float x, float y, float w, float h, Card card) throws DocumentException, IOException{
        Rectangle rect = drawRectangle(x, y, w, h, new BaseColor(255,255,255,200), BaseColor.BLACK);
                
        String text = "";
        if(card.getCardType() == Card.CardType.MONSTER){
            text += "[";
                for(int i=0;i<card.getMonsterTypes().length;i++){
                    text += card.getMonsterTypes()[i];
                    if(i < card.getMonsterTypes().length-1){
                        text += " / ";
                    }
                }
            text += "]\n";
        }
        for(int i=0;i<card.getDescription().length;i++){
            String s = card.getDescription()[i];
            text += s;
            if(i < card.getDescription().length - 1){
                text += "\n";
            }
        }
        
        ColumnText ct = new ColumnText(cb);
        ct.setSimpleColumn(rect);

        boolean fits = false;
        double fontsize = 16 - 2;
        while ((!fits) && fontsize > 3) {
            fontsize -= 0.1;
            Phrase p = new Phrase(text);
            p.setFont(new Font(BaseFont.createFont()));
            p.getFont().setSize((float) fontsize);
            ColumnText ctxt = new ColumnText(cb);
            ctxt.setSimpleColumn(rect.getLeft(), rect.getBottom(), rect.getRight(), rect.getTop());
            ctxt.addElement(p);
            int stat = ctxt.go(true);
            fits = !ColumnText.hasMoreText(stat);
        }
        
        Phrase par = new Phrase(text, new Font(Font.FontFamily.COURIER, (float)fontsize));

        PdfPTable table = new PdfPTable(1);
        table.setWidths(new float[]{1});
        table.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell(par);
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setFixedHeight(rect.getHeight());
        table.addCell(cell);

        ct.setSimpleColumn(rect);
        ct.addElement(table);
        ct.go();
    }
    
    private void drawAttackDef(float x, float y, float w, float h, Card card) throws DocumentException, IOException{
        if(card.getCardType() != Card.CardType.MONSTER){
            return;
        }
        
        Rectangle rect = getRectangle(x, y, w, h);
        
        String text = "ATK/"+card.getAttack() + " ";
        if(card.getMainMonsterType() == Card.MonsterType.LINK){
            text += "LINK-" + card.getDefense();
        }
        else{
            text += "DEF/" + card.getDefense();
        }

        ColumnText ct = new ColumnText(cb);
        ct.setSimpleColumn(rect);

        boolean fits = false;
        double fontsize = 12;
        while ((!fits) && fontsize > 3) {
            fontsize -= 0.1;
            Phrase p = new Phrase(text);
            p.setFont(new Font(BaseFont.createFont()));
            p.getFont().setSize((float) fontsize);
            ColumnText ctxt = new ColumnText(cb);
            ctxt.setSimpleColumn(rect.getLeft(), rect.getBottom(), rect.getRight(), rect.getTop());
            ctxt.addElement(p);
            int stat = ctxt.go(true);
            fits = !ColumnText.hasMoreText(stat);
        }
        
        Phrase par = new Phrase(text, new Font(Font.FontFamily.COURIER, (float)fontsize));

        PdfPTable table = new PdfPTable(1);
        table.setWidths(new float[]{1});
        table.setWidthPercentage(100);

        PdfPCell cell = new PdfPCell(par);
        cell.setBorder(0);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setFixedHeight(rect.getHeight());
        table.addCell(cell);

        ct.setSimpleColumn(rect);
        ct.addElement(table);
        ct.go();
    }
}

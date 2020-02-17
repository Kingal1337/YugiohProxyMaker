/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yugiohproxymaker;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Utilities;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author Alan
 */
public class YugiohProxyMaker {
    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(YugiohProxyMaker.class.getName()).log(Level.SEVERE, null, ex);
        }
//        String passcode = "45819647";
//        passcode = "41420027";
//        String wrong = "3482453823458";
//        CardFetcher fetch = new CardFetcher();
//        System.out.println(fetch.fetch(passcode.trim()));
//        createGUI();
//        pdfTest();
        testGenerateDeck();

//        System.out.println(CardFetcher.passcodeCorrector("35699"));
    }
    
    public static void testGenerateDeck(){
        File file = new File("F:\\! Spyrals (1).ydk");
        
        ArrayList<String> ids = getCardIds(file);
        ArrayList<Card> cards = getCards(ids);
        
        
        try {
            float left = 36;
            float top = 50;
            float bottom = 50;
            float right = 36;
            com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.A4, left, right, top, bottom);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(new File("SomePDF.pdf")));
                                    
            document.open();
            PdfContentByte cb = writer.getDirectContent();
        
            CardCreator creator = new CardCreator(document, cb);
            for(Card card : cards){
                creator.renderCard(card, true);
            }
            document.close();
        } catch (FileNotFoundException | DocumentException ex) {}
    }
    
    private static ArrayList<String> getCardIds(File file){
        if(file == null){
            return null;
        }
        
        ArrayList<String> cardIds = new ArrayList<>();
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine()) != null){
                cardIds.add(line);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return cardIds;
    }
    
    private static ArrayList<Card> getCards(ArrayList<String> cardIds){
        CardFetcher cardFetch = new CardFetcher();
        ArrayList<Card> cards = new ArrayList<>();
        for(String id : cardIds){
            Card card = cardFetch.fetch(id);
            if(card != null){
                cards.add(card);
            }
        }
        return cards;
    }
   
    public static void pdfTest() {
        try {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.A4, 36,36,36,36);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(new File("SomePDF.pdf")));
            System.out.println(document.getPageSize().getWidth());
                                    
            document.open();
            PdfContentByte cb = writer.getDirectContent();
            
        CardFetcher fetch = new CardFetcher();
        
        Card card1 = fetch.fetch("46986414");//normal
        Card card2 = fetch.fetch("38033121");//effect
        Card card3 = fetch.fetch("30208479");//ritual
        Card card4 = fetch.fetch("23995346");//fusion
        Card card5 = fetch.fetch("44508094");//synchro
        Card card6 = fetch.fetch("38273745");//xyz
        Card card7 = fetch.fetch("80896940");//pendulum
        Card card8 = fetch.fetch("95372220");//link
        Card card9 = fetch.fetch("65169794");//spell equip
        
        Card card10 = fetch.fetch("41420027");//spell equip
        
            CardCreator creator = new CardCreator(document, cb);
//            creator.renderCard(card1);
//            creator.renderCard(card2);
//            creator.renderCard(card3);
//            creator.renderCard(card4);
//            creator.renderCard(card5);
//            creator.renderCard(card6);
//            creator.renderCard(card7);
//            creator.renderCard(card8);
//            creator.renderCard(card9);
//            creator.renderCard(card10);
            
//            document.
//            CardCreator.init(document, cb);
//            document.
            document.close();
        } catch (FileNotFoundException | DocumentException ex) {
//            System.out.println(Arrays.toString(ex.getStackTrace()));
        }
    }
    
    public static void drawCard(PdfContentByte cb) throws DocumentException{
        float cardW = Utilities.millimetersToPoints(59f);
        float cardH = Utilities.millimetersToPoints(86f);
        Rectangle rect = new Rectangle(100, 750-20, 100 + cardW, 750);
        rect.setBorder(Rectangle.BOX);
        rect.setBorderWidth(1);
        rect.setBackgroundColor(BaseColor.GRAY);
        rect.setBorderColor(BaseColor.GREEN);
        cb.rectangle(rect);

        ColumnText ct = new ColumnText(cb);
                
        float fontSize = 100;
        
        ct.setSimpleColumn(rect);
        String text = "Very Long Title, Super Duper Long";
        Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, fontSize, Font.BOLD);
        fontSize = ColumnText.fitText(catFont, text, rect, 100, PdfWriter.RUN_DIRECTION_LTR);
        
        System.out.println(fontSize);
        
        Phrase pharse = new Phrase(text, catFont);
        ct.addText(pharse);
        ct.go();
        
//        Paragraph para = new Paragraph();
//        para.add("I have a very long bit of text");
//        para.add("\n");
//        para.add("2nd bit of very long text");
//        para.setMultipliedLeading(1);
//        ct.addElement(para);
    }
    
    public static void createGUI(){
        int width = 400;
        int height = 600;
        
        width = 185;
        height = 267;
        
//        width = 216;
//        height = 312;
//        
//        width = (int)(2.25 * 209);
//        height = (int)(3.25 * 209);
        
        Card card = new Card(
                "Ib the World Chalice Justiciar",
                new String[]{
                    "1 Tuner + 1+ non-Tuner monsters",
                    "For this card’s Synchro Summon, you can treat 1 “World Chalice” Normal Monster you control as a Tuner. You can only use each of the following effects of \"Ib the World Chalice Justiciar\" once per turn.",
                    "● If this card is Synchro Summoned: You can add 1 “World Legacy” card from your Deck to your hand.",
                    "● If this Synchro Summoned card is sent from the field to the GY: You can Special Summon 1 “World Chalice” monster from your Deck or GY, except “Ib the World Chalice Justiciar”."
                    
                },
                "94677445",
                Card.MonsterType.SYNCHRO,
                new String[]{"Spellcaster", "Synchro", "Tuner", "Effect"},
                Card.CardType.MONSTER,
                Card.Attribute.WATER,
                Card.Property.NONE,//used for spells and traps
                5,
                0,
                "1800",
                "2100",
                new boolean[8],
                true,
                null);
        
        Card card2 = new Card(
                "Solemn Judgment",
                new String[]{"When a monster(s) would be Summoned, OR a Spell/Trap Card is activated: Pay half your LP; negate the Summon or activation, and if you do, destroy that card."},
                "41420027",
                Card.MonsterType.UNKNOWN,
                new String[]{"Spellcaster", "Synchro", "Tuner", "Effect"},
                Card.CardType.TRAP,
                Card.Attribute.UNKNOWN,
                Card.Property.COUNTER,//used for spells and traps
                0,
                0,
                "0",
                "0",
                new boolean[8],
                true,
                null);
        
//        CardFetcher fetch = new CardFetcher();
//        card = fetch.fetch("73639099");
        
        JFrame frame = new JFrame();
        CardViewer panel = new CardViewer(card, width, height);
        panel.setPreferredSize(new Dimension(width, height));
        frame.add(panel);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
    }
}

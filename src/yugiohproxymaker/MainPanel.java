/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yugiohproxymaker;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 *
 * @author Alan
 */
public class MainPanel extends JPanel{
    private JButton selectFile;
    private JTextField location;
    
    private JButton toPdf;
    
    private File selectedFile;
    
    private CardFetcher cardFetch;
    
    public MainPanel(int width, int height){
        super(null);
        
        cardFetch = new CardFetcher();
        
        selectFile = new JButton("Select File");
        location = new JTextField();
        toPdf = new JButton("Convert To PDF");
        
        int buttonWidth = width/4;
        int buttonHeight = 25;
        
        int fieldW = width - (width/8)* 2;
        int fieldH = 25;
        
        
        selectFile.setBounds((width - buttonWidth) / 2, (height - buttonHeight) / 2 - buttonHeight * 2, buttonWidth, buttonHeight);
        location.setBounds((width - fieldW) / 2, (height - fieldH) / 2, fieldW, fieldH);
        toPdf.setBounds((width - buttonWidth) / 2, (height - buttonHeight) / 2 + buttonHeight * 2, buttonWidth, buttonHeight);
        
        location.setEditable(false);
        
        add(selectFile);
        add(location);
        add(toPdf);
        
        selectFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setMultiSelectionEnabled(false);
                int option = chooser.showOpenDialog(MainPanel.this);
                if(JFileChooser.APPROVE_OPTION == option){
                    File file = chooser.getSelectedFile();
                    selectedFile = file;
                    location.setText(selectedFile.getPath());
                }
            }
        });
        
        toPdf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> ids = getCardIds();
                ArrayList<Card> cards = getCards(ids);
                
//                convertToPdf(cards);
            }
        });
    }
    
    private ArrayList<String> getCardIds(){
        if(selectedFile == null){
            return null;
        }
        
        ArrayList<String> cardIds = new ArrayList<>();
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
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
    
    private ArrayList<Card> getCards(ArrayList<String> cardIds){
        ArrayList<Card> cards = new ArrayList<>();
        for(String id : cardIds){
            Card card = cardFetch.fetch(id);
            if(card != null){
                cards.add(card);
            }
        }
        return cards;
    }
    
    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        java.awt.Image tmp = img.getScaledInstance(newW, newH, java.awt.Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();

        return dimg;
    }
    
//    private void convertToPdf(ArrayList<Card> cards){
//        try {
//            Document document = new Document(PageSize.A4, 20, 20, 20, 20);
//            PdfWriter.getInstance(document, new FileOutputStream(System.getProperty("user.dir") + "/myupdf.pdf"));
//            document.open();
//            
//            
//            BufferedImage bufferedImage = resize(cards.get(0).getImage(), 216, 312);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ImageIO.write(bufferedImage, "png", baos);
//            Image iTextImage = Image.getInstance(baos.toByteArray());
//                    
//                    
//            document.add(iTextImage);
//            document.close();
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (DocumentException ex) {
//            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}

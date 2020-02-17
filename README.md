# Minimalistic Yugioh Proxy Maker
A Minimalistic Yu-Gi-Oh Proxy Generator where it generates a printable PDF with only the information you need.
All card information is pulled from `yugioh.fandom.com`

##  How to use

### Usage
	
1. Make sure you have atleast **Java 8** installed on your machine

### To run the code

1. You will also need to download 2 libraries (should be located in the libs folder)
	1. JSoup 1.12.1.jar
	2. iTextPDF-5.5.10.jar

# Generating a Deck
Currently you can only use the command line to generate a deck
You can generate 3 types of proxies

- Informational Proxies (Text based card using information from real life card)
- Colored Informational Proxies 
- Full Card Art Proxies (Image taken from `yugioh.fandom.com`)

	
`DeckListLocation [DestinationLocation] [Colored MarginLeft MarginTop MarginRight MarginBottom]`

- 
 1. Open the command prompt and navigate to where you downloaded the jar file
 2. type `java -jar Yugioh-Proxy-Maker-1.0.0.jar DECK_LIST_LOCATION` 
 3. The deck should then be generated in the game folder where the jar file is

## Examples

    java -jar Yugioh-Proxy-Maker-1.0.0.jar "decklist.txt" 

## Printing the Deck
In order to print with the correct dimensions of a Yu-Gi-Oh card, set the scale of the print to 100% 

## To-Do
- Add User-Friendly GUI
- Add text overlay to full card art proxies that lack an english card description/image/TCG printing

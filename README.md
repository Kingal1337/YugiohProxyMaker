<p align="center">
	<h1 align="center">Minimalistic Yugioh Proxy Maker</h1>  
  <p align="center">
A Minimalistic Yu-Gi-Oh Proxy Generator where it generates a printable PDF with only the information you need.
    <br />
All card information is pulled from
    <br />
    <a href="https://yugioh.fandom.com"><strong>Yu-Gi-Oh Fandom</strong></a>
</p>

## Table of Contents
* [How to Use](#how-to-use)
  * [Usage](#usage)
  * [Running the Code](#running-the-code)
* [Generating a Deck](#generating-a-deck)
  * [Steps](#steps)
  * [The Command](#the-command)
  * [Examples](#examples) 
  * [Printing](#printing-the-deck)

##  How to Use

### Usage
	
1. Make sure you have atleast **Java 8** installed on your machine

### Running the Code

1. You will also need to download 2 libraries (should be located in the libs folder)
	1. JSoup 1.12.1.jar
	2. iTextPDF-5.5.10.jar

# Generating a Deck
Currently you can only use the command line to generate a deck
You can generate 3 types of proxies

- Informational Proxies (Text based card using information from real life card)
- Colored Informational Proxies 
- Full Card Art Proxies (Image taken from `yugioh.fandom.com`)
## Steps
1) Download the lastest version of the [Yugioh Proxy Maker](https://github.com/Kingal1337/YugiohProxyMaker/releases)
2) Open the command prompt
3) Navigate to where you have downloaded the .jar file in the command promt
4) Type `java -jar Yugioh-Proxy-Maker-1.3.0.jar <desk_list_location>` to generate a Simple Deck (Be sure to replace `<deck_list_location>` with the .ydk file located on your computer)
5) The pdf that was generated should be located in the folder where you downloaded the .jar file
6) Print the PDF.
## The Command

**Usage**

`java -jar Yugioh-Proxy-Maker-1.3.0.jar <desk_list_location>`
<br>
`java -jar Yugioh-Proxy-Maker-1.3.0.jar <desk_list_location> <destination>`
<br>
`java -jar Yugioh-Proxy-Maker-1.3.0.jar <desk_list_location> <use_image> <colored>`
<br>
`java -jar Yugioh-Proxy-Maker-1.3.0.jar <desk_list_location> <destination> <use_image> <colored> [--left=<px>] [--top=<px>] [--right=<px>] [--bottom=<px>]`

**Arguments**
```
	<deck_list_location>    Where the .ydk file is located
	<destination>           Where to save the file (Note: you must include a file name)
	<use_image>             (true/false), set to true,to use the image on the fandom website 
								instead of using the minimalistic design
	<colored>               (true/false), if you want colored images or grayscale
```

**Options**
```
	--left=<px>             the left margin of the pdf in pixels [default: 36]
	--top=<px>              the top margin of the pdf in pixels [default: 50]
	--right=<px>            the right margin of the pdf in pixels [default: 36]
	--bottom=<px>           the bottom margin of the pdf in pixels [default: 50]
```

## Examples

- **Simple Deck** - This will generate a minimalistic deck with just text and will be saved on the desktop
  - `java -jar Yugioh-Proxy-Maker-1.3.0.jar "C:/Users/<NAME>/Desktop/Decklist.ydk"`
- **Deck with Original Images** - This will generate a deck with the image of the card pulled from the Yugioh Fandom
  -  `java -jar Yugioh-Proxy-Maker-1.3.0.jar "C:/Users/<NAME>/Desktop/Decklist.ydk" true true`     

## Printing the Deck
In order to print with the correct dimensions of a Yu-Gi-Oh card, set the scale of the print to 100% 

Recommended Software: `Adobe Acrobat Reader`

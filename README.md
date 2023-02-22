# TBSKmodem for Processing



This is processing implementation of TBSKmodem.
🐓[TBSKmodem](https://github.com/nyatla/TBSKmodem/)

This library is a library that allows you to use the functions of TBSKmodem from Processing. It is capable of sending and receiving digital data using audio, creating and analyzing modulated signals.

You can use the Mimim Audio Library as the audio interface.

![preview_tbsk](https://user-images.githubusercontent.com/2483108/220582436-044b8beb-1ff2-4813-9910-4306f26b7251.png)


※TBSK (Trait Block Shift Keying) modem is a low-speed, short-range audio communication implementation without FFT/IFTT.
It can modulate a byte/bitstream to PCM  and demodulate PCM to a byte/bitstream.


# License

This software is provided under the MIT license. For hobby and research purposes, use it according to the MIT license.

For industrial applications, be careful with patents.

This library is MIT licensed open source software, but not patent free.


# GetStarted

## Manual Setup
Libraries can be downloaded and manually placed within the `libraries` folder of your Processing sketchbook. To find (and change) the Processing sketchbook location on your computer, open the Preferences window from the Processing application (PDE) and look for the "Sketchbook location" item at the top.

By default the following locations are used for your sketchbook folder: 
  * For Mac users, the sketchbook folder is located inside `~/Documents/Processing` 
  * For Windows users, the sketchbook folder is located inside `My Documents/Processing`

Download [tbskpsg.zip](https://github.com/nyatla/TBSKmodem-for-Processing/releases) from [https://github.com/nyatla/TBSKmodem-for-Processing/releases](https://github.com/nyatla/TBSKmodem-for-Processing/releases)

Unzip and copy the contributed Library's folder into the `libraries` folder in the Processing sketchbook. You will need to create this `libraries` folder if it does not exist.

The folder structure for Library tbskpsg should be as follows:

```
Processing
  libraries
    tbskpsg
      examples
      library
        tbskpsg.jar
      reference
      src
```
             
Some folders like `examples` or `src` might be missing. After Library tbskpsg has been successfully installed, restart the Processing application.


## Example
The sample code for TBSKmodem can be found in the Example directory.

### Transmitter
First, let's test signal transmitter. Please open TxAsCharConsole.pde. When you run the sketch, a small window will appear, but it is a dummy.

[TxAsCharConsole](https://github.com/nyatla/TBSKmodem-for-Processing/tree/master/examples/Modem/TxAsCharConsole/TxAsCharConsole.pde)

This sketch sends TBSK-modulated sound from the speaker when you press the 'A' key. Press 'A' key to test if the signal is transmitted.

### Receiver
Next, let's test receiver. Please open RxAsCharGui.pde. When you run the sketch, a black screen and volume meter will be displayed. When a signal is sent from TxAsCharConsole, the meter will respond and display the demodulated text.

[RxAsCharGui](https://github.com/nyatla/TBSKmodem-for-Processing/blob/master/examples/Modem/RxAsCharGui/RxAsCharGui.pde)

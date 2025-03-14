package frc.robot.subsystems.led;

import java.util.Random;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LedSubsystem extends SubsystemBase{ // led test

    private final AddressableLED led;
    private final AddressableLEDBuffer ledBuffer;

    //Constants need to move to constants class
    private int rainbowFirstPixelHue = 0;
    
    private static final int LED_PORT = 0;
    private static final int LED_COUNT = 69;


    public LedSubsystem () {
        led = new AddressableLED(LED_PORT);
        ledBuffer = new AddressableLEDBuffer(LED_COUNT);
        led.setLength(ledBuffer.getLength());

        led.setData(ledBuffer);
        led.start();

    }
    
    public void setSolidColor () {
        int r = 0;
        int g = 0;
        int b = 255;

        for (int i = 0; i < ledBuffer.getLength(); i++) { //iterate ever led in strip and aplly the rbg
            ledBuffer.setRGB(i, r, g, b);
        }

        led.setData(ledBuffer);
    }

    public void setRainbow () {
        for (int i = 0; i < ledBuffer.getLength(); i++) {
            int hue = (rainbowFirstPixelHue + (i * 180 / ledBuffer.getLength())) % 180;
            ledBuffer.setHSV(i, hue, 255, 128);
        }
        rainbowFirstPixelHue += 3;
        rainbowFirstPixelHue %= 180;

    }

    public void blindTheDriver () { //arshan said i cant use ts

        Random rand = new Random();

        for (int i = 0; i < ledBuffer.getLength(); i++) {

        int hue = rand.nextInt(180);  
        ledBuffer.setHSV(i, hue, 255, 128);

        }
        
        led.setData(ledBuffer); 
    }



}

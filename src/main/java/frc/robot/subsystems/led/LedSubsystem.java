package frc.robot.subsystems.led;

import java.util.Random;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;


public class LedSubsystem extends SubsystemBase{ // led test

    private final AddressableLED led;
    private final AddressableLEDBuffer ledBuffer;

    public LedSubsystem () {
        led = new AddressableLED(Constants.LEDConstants.LED_PORT);
        ledBuffer = new AddressableLEDBuffer(Constants.LEDConstants.LED_COUNT);
        led.setLength(ledBuffer.getLength());

        led.setData(ledBuffer);
        led.start();

    }
    
    public void setBlue () {
        int r = 0;
        int g = 0;
        int b = 255;

        for (int i = 0; i < ledBuffer.getLength(); i++) { //iterate ever led in strip and aplly the rbg
            ledBuffer.setRGB(i, r, g, b);
        }

        led.setData(ledBuffer);
    }

    public void setRed () {
        int r = 250;
        int g = 0;
        int b = 0;

        for (int i = 0; i < ledBuffer.getLength(); i++) { //iterate ever led in strip and aplly the rbg
            ledBuffer.setRGB(i, r, g, b);
        }

        led.setData(ledBuffer);
    }



    public void setRainbow() {
        for (int i = 0; i < ledBuffer.getLength(); i++) {
            int hue = (Constants.LEDConstants.rainbowFirstPixelHue + (i * 180 / ledBuffer.getLength())) % 180;
            ledBuffer.setHSV(i, hue, 255, 128);
        }
    
        led.setData(ledBuffer); // Update LED strip
    }



    public void blindTheDriver () { //arshan said i cant use ts

        Random rand = new Random();

        for (int i = 0; i < ledBuffer.getLength(); i++) {

        int hue = rand.nextInt(180);  
        ledBuffer.setHSV(i, hue, 255, 128);

        }
        
        led.setData(ledBuffer); 
    }

    public void setLedAlliance() {

        var alliance = DriverStation.getAlliance();
        
        if (alliance.isPresent()) { 
            if (alliance.get() == DriverStation.Alliance.Red) {
                setRed();
            } else if (alliance.get() == DriverStation.Alliance.Blue) {
                setBlue();
            }
        } else {
            setRainbow();
        }
    }
}



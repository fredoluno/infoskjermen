package no.infoskjermen.tjenester;


import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import no.infoskjermen.Settings;
import no.infoskjermen.utils.DivUtils;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class DisplayService extends GoogleService {

    private final String DEFAULT="default";
    private final String PERSONAL = ".personal";

    public DisplayService(Settings settings) throws Exception{
        super(settings);
        log = LoggerFactory.getLogger(DisplayService.class);
        log.debug("generalSettings: "+ DivUtils.printHashMap(generalSettings));

    }

    public String getPopulatedSVG(String navn) throws Exception{
        log.debug("getPopulatedSVG");
        HashMap personalSettings  = settings.getGoogleSettings(navn);

        Events events = getEvents(personalSettings, LocalDateTime.now(), LocalDateTime.now().plusSeconds(1),this.DISPLAY_CALENDAR);
        String displayName = DEFAULT;
        if( events.getItems().size() > 0){
            Event event = events.getItems().get(0);
            displayName = event.getSummary();
        }
        log.debug("displayName: " + displayName);


        String svg = getSVG(displayName, navn );
        log.debug("svg is populated. length:" +svg.length());

        return getSVG(displayName, navn );
    }



    private String getSVG(String displayName, String navn) throws Exception {
        HashMap<String,String> displayMap;
        String displayN = displayName;
        if(displayName.endsWith(PERSONAL)){
            displayMap = settings.getDisplaySettings(navn);
            displayN = displayName.replaceAll(PERSONAL,"");
        }else{
            displayMap = settings.getDisplaySettings("general");
        }

        return displayMap.get(displayN);

    }

    public ByteArrayOutputStream getKindleBilde(String svg)throws Exception{
        return getBilde(svg,"png");

    }

    public ByteArrayOutputStream getBMPBilde(String svg)throws Exception{
        return getBilde(svg,"bmp");

    }


    public ByteArrayOutputStream getBilde(String svg,String outputType) throws Exception{
        setUpFonts();


        String mySvg = svg;
        long startTime = System.nanoTime();

        //        MÅ GJØRES INNTIL JEG VEIT HVA SOM SKAPER ISSUES PÅ HEROKU. SER UT TIL AT DE IKKE GREIER Å SETTE MAX HEAP
        System.gc();

        PNGTranscoder t = new PNGTranscoder();

        ByteArrayOutputStream ostream ;

        ostream = new ByteArrayOutputStream();    //("public/images/out.png");
        TranscoderOutput output = new TranscoderOutput(ostream );
        TranscoderInput input = new TranscoderInput( new ByteArrayInputStream(mySvg.getBytes("UTF-8")));
        t.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR, Color.white);

        t.transcode(input, output);

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(ostream.toByteArray()));
        ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        BufferedImage image2 = new BufferedImage(600,800, BufferedImage.TYPE_BYTE_GRAY );
        colorConvert.filter(image, image2);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image2, outputType,baos );
        ostream.close();
        long endTime = System.nanoTime();

        long duration = (endTime - startTime);
        log.info("Tidbrukt=" + duration);
        return baos;
    }



    private void setUpFonts() {
        String fontConfig = System.getProperty("java.home")
                + File.separator + "lib"
                + File.separator + "fontconfig.Prodimage.properties";
        if (new File(fontConfig).exists()) {
            log.debug("found logfile: " + fontConfig);
            System.setProperty("sun.awt.fontconfig", fontConfig);
        } else {
            log.error("didn't fint logfile: " + fontConfig);
        }
    }


}

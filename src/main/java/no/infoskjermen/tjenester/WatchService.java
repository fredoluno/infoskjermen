package no.infoskjermen.tjenester;

import no.infoskjermen.utils.DateTimeUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WatchService implements PopulateInterface {

    public WatchService() {

    }

    @Override
    public String populate(String svg, String navn) {
        return svg.replaceAll("@@OPPDATERT@@",DateTimeUtils.getTime(LocalDateTime.now()));
    }

    @Override
    public boolean isPresentInSVG(String svg) {
        return svg.contains("@@OPPDATERT@@");
    }
}

package no.infoskjermen.tjenester;

import no.infoskjermen.utils.DateTimeUtils;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class WatchService implements PopulateInterface {

    public WatchService() {

    }

    @Override
    public String populate(String svg, String navn) {
        return svg.replaceAll("@@OPPDATERT@@",DateTimeUtils.getTime(ZonedDateTime.now(ZoneId.of("Europe/Oslo")).toLocalDateTime()));
    }

    @Override
    public boolean isPresentInSVG(String svg) {
        return svg.contains("@@OPPDATERT@@");
    }
}

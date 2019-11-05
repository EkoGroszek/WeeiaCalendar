package com.example.demo;

import lombok.Value;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import net.fortuna.ical4j.util.ResourceLoader;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/calendar")
public class WeeiaCalendar {

    public String generateFile() throws IOException {
        Connection connect = Jsoup.connect("http://www.weeia.p.lodz.pl/");
        Document document = connect.get();
        Elements elements = document.select("td.active");

        Map<String, String> events = new HashMap();

        for (Element el : elements) {
            events.put(el.select("a.active").text(), el.select("div.InnerBox").text());
        }

        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Wiktor Jachimczak//iCal4j 1.0//PL"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        java.util.Calendar cal = java.util.Calendar.getInstance();

        Date date = new Date();
        for (String evKey : events.keySet()) {
            Integer i = 1;
            cal.set(java.util.Calendar.MONTH, date.getMonth());
            cal.set(java.util.Calendar.DAY_OF_MONTH, Integer.parseInt(evKey));

            VEvent event = new VEvent(new Date(cal.getTime()), events.get(evKey));
            UUID uuid = UUID.randomUUID();
            Uid ug = new Uid(uuid.toString());
            event.getProperties().add(ug);
            calendar.getComponents().add(event);
        }


        FileOutputStream fout = new FileOutputStream("mycalendar.ics");
        CalendarOutputter outputter = new CalendarOutputter();
        outputter.output(calendar, fout);
        return calendar.toString();
    }


    @GetMapping(value = "/weeia", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getTest() throws IOException {
        generateFile();
        Resource resource = loadAsResource("mycalendar.ics");
        return new ResponseEntity<Resource>(resource, HttpStatus.OK);
    }

    private Resource loadAsResource(String filename) throws FileNotFoundException, MalformedURLException {
        try {
            Path file = Paths.get(filename);
            org.springframework.core.io.Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return (Resource) resource;
            } else {
                throw new FileNotFoundException();
            }
        } catch (MalformedURLException e) {

            throw new FileNotFoundException();
        }
    }

}


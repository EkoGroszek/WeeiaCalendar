package com.example.demo;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/calendar")
public class WeeiaCalendar {

    public void generateFile() throws IOException {
        Connection connect = Jsoup.connect("http://www.weeia.p.lodz.pl/");
        Document document = connect.get();
        Elements elements = document.select("td.active");

        Map<String, String> events = new HashMap();

        for(Element el: elements){
            System.out.println(el.select("a.active").text());
            System.out.println(el.select("div.InnerBox").text());

            events.put(el.select("a.active").text(), el.select("div.InnerBox").text() );
        }

    }

    @GetMapping("/test/{testval}")
    public ResponseEntity<String> getTest(@PathVariable String testval) throws IOException {
        generateFile();
        return new ResponseEntity<>(testval, HttpStatus.OK);
    }

}


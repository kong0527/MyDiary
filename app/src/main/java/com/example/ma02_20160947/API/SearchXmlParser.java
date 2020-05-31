package com.example.ma02_20160947.API;

import com.example.ma02_20160947.model.LocalDto;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class SearchXmlParser {
    public enum TagType { NONE, TITLE, TELEPHONE, ADDRESS, MAPX, MAPY };

    public SearchXmlParser() {

    }

    public ArrayList<LocalDto> parse (String xml) {
        ArrayList<LocalDto> resultList = new ArrayList();
        LocalDto dto = null;
        TagType tagType = TagType.NONE;

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT :
                        break;

                    case XmlPullParser.END_DOCUMENT:
                        break;

                    case XmlPullParser.START_TAG :
                        if (parser.getName().equals("item")) {
                            dto = new LocalDto();
                        } else if (parser.getName().equals("title")) {
                            if (dto != null) tagType = TagType.TITLE;
                        } else if (parser.getName().equals("telephone")) {
                            if (dto != null) tagType = TagType.TELEPHONE;
                        } else if (parser.getName().equals("address")) {
                            if (dto != null) tagType = TagType.ADDRESS;
                        } else if (parser.getName().equals("mapx")) {
                            if (dto != null) tagType = TagType.MAPX;
                        } else if (parser.getName().equals("mapy")) {
                            if (dto != null) tagType = TagType.MAPY;
                        }
                        break;

                    case XmlPullParser.END_TAG :
                        if (parser.getName().equals("item")) {
                            resultList.add(dto);
                            dto = null;
                        }
                        break;

                    case XmlPullParser.TEXT :
                        switch (tagType) {
                            case TITLE:
                                dto.setTitle(parser.getText());
                                break;
                            case TELEPHONE:
                                dto.setTelephone(parser.getText());
                                break;
                            case ADDRESS:
                                dto.setAddress(parser.getText());
                                break;
                            case MAPX:
                                dto.setMapx(Integer.parseInt(parser.getText()));
                                break;
                            case MAPY:
                                dto.setMapy(Integer.parseInt(parser.getText()));
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }
}

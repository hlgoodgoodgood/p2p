package com.bjpowernode.p2p;

import org.dom4j.*;

import java.util.List;
import java.util.UUID;

/**
 * 测试dom4j+xpath
 */
public class TestXpath {

    public static void main(String[] args) throws DocumentException {
       // System.out.println(UUID.randomUUID().toString());


        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                " \n" +
                "<bookstore>\n" +
                " \n" +
                "<book>\n" +
                "  <title lang=\"eng\">Harry Potter</title>\n" +
                "  <price>29.99</price>\n" +
                "</book>\n" +
                " \n" +
                "<book>\n" +
                "  <title lang=\"china\">Learning XML</title>\n" +
                "  <price>39.95</price>\n" +
                "</book>\n" +
                " \n" +
                "</bookstore>";


        Document document = DocumentHelper.parseText(xml);
        //document.getRootElement().element("//title") /book//title
        Node node = document.selectSingleNode("//title[@lang='eng']");

        Element element=(Element)node;
        System.out.println(node.getText());
        System.out.println(element.attributeValue("lang"));
        element.addAttribute("font-size","15px");
        element.addElement("color").setText("red");


        List<Node> nodes = document.selectNodes("//title");
        for(Node node1:nodes){
            System.out.println(node1.asXML());
        }



    }
}

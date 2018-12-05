package com.mycompany.app;
import java.io.File;
import static spark.Spark.*;
import spark.ModelAndView;
import javax.xml.parsers.DocumentBuilder;
import spark.template.mustache.MustacheTemplateEngine;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;


public class App {
    public static String search(String input1, String input2) {
        String name,surname;
        String id;
        StringBuilder deniz  = new StringBuilder();
        try {
            File myfile = new File("EEAS.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(myfile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("ENTITY");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node gez = nList.item(temp);
                if (gez.getNodeType() == Node.ELEMENT_NODE) {
                    Element myElm = (Element) gez;
                    id =myElm.getAttribute("Id");
                    name=myElm.getElementsByTagName("FIRSTNAME").item(0).getTextContent();
                    surname=myElm.getElementsByTagName("LASTNAME").item(0).getTextContent();

                     if(  !(input1.equals("") ) &&  !(input2.equals("") )  ) {
                       if(myElm.getElementsByTagName("FIRSTNAME").item(0).getTextContent().contains(input1) && myElm.getElementsByTagName("LASTNAME").item(0).getTextContent().contains(input2)) {
                           deniz.append(" " + id + "\n" + name + "\n" + surname + "\n");
                        }
                     }else if( input1.equals("") && !(input2.equals("") )  ) {
                        if(myElm.getElementsByTagName("LASTNAME").item(0).getTextContent().contains(input2)) {
                            deniz.append(" " + id + "\n" + name + "\n" + surname + "\n");
                        }
                    }else if( !(input1.equals("") ) && input2.equals("")  ) {
                        if(myElm.getElementsByTagName("FIRSTNAME").item(0).getTextContent().contains(input1)) {
                            deniz.append(" " + id + "\n" + name + "\n" + surname + "\n");
                        }
                    }else if(input1.equals("") && !(input2.equals(""))) {
                        return "";
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return deniz.toString();
    }

    public static void main(String[] args) {
        port(getHerokuAssignedPort());
       
        get("/", (req, res) -> "Hello, World");

        post("/search", (req, res) -> {
            String input1 = req.queryParams("input1");
            String input2 = req.queryParams("input2");
            
            String result= App.search(input1, input2);

            Map map = new HashMap();
            map.put("result", result);
            return new ModelAndView(map, "compute.mustache");
        }, new MustacheTemplateEngine());

        get("/search", (rq, rs) -> {
            Map map = new HashMap();
            map.put("result", "not computed yet!");
            return new ModelAndView(map, "compute.mustache");
        }, new MustacheTemplateEngine());
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; // return default port if heroku-port isn't set (i.e. on localhost)
    }
}

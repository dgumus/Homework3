package com.mycompany.app;
import java.io.File;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.HashMap;
import java.util.Map;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import java.io.IOException;


public class App {
    public static String search(String firstName, String lastName) throws SAXException, IOException, ParserConfigurationException {
    	SAXParserFactory factory = SAXParserFactory.newInstance();
    	SAXParser parser = factory.newSAXParser();
    	App myobj = new App();
    	MyHandler handler = myobj.new MyHandler();
        parser.parse(new File("EEAS.xml"), handler);
       
        ArrayList<String> res=new ArrayList<>();
        res.toString();
        HashMap<String,ArrayList<String>> hashma = handler.getMyMap();
        if( (!firstName.equals("")) && (!lastName.equals(""))){
             res=hashma.get(firstName+ " " + lastName);
             System.out.print(res.toString());
             return res.toString();
        }else if((!firstName.equals("")) && lastName.equals("")){
            res= hashma.get(firstName);
            System.out.print(res.toString());
            return res.toString();
        }else if( firstName.equals("") && ( !lastName.equals("") )    ){
            res= hashma.get(lastName);
            System.out.print(res.toString());
            return res.toString();
        }
        return "";
    	
    }


    class MyHandler extends DefaultHandler{
        //key for firstname+lastname
        HashMap<String,ArrayList<String>> hashma1 = new HashMap<>();
    	int nameid,entityid=0;//if name is equal then print entity id not name id
    	boolean readFirstName,readMiddleName,readLastName,readName,readWholeName = false;
        String firstName,lastName,middleName,wholeName = "";
        
    	public HashMap<String,ArrayList<String>> getMyMap(){
            return hashma1;
        }
    	
    	public void startElement(String uri, String localName, String qName, Attributes attributes)throws SAXException{
            //qName.equals("
    		if(qName.equals("NAME")) {
    			readName = true;
                entityid= Integer.parseInt(attributes.getValue("Entity_id"));
                nameid= Integer.parseInt(attributes.getValue("Entity_id"));
            }else if(qName.equals("LASTNAME")){
                readLastName = true;
            }else if(qName.equals("FIRSTNAME")){
                readFirstName = true;
            }else if(qName.equals("MIDDLENAME")){
                readMiddleName = true;
            }else if(qName.equals("WHOLENAME")){
                readWholeName = true;
            }
        }
        
    	public void characters(char[] ch, int start, int length) throws SAXException{
            String[] splitt ;
    		String data = new String(ch, start, length);
    		if(readName) {
    			if(readFirstName) {
    				firstName = data;
    				readFirstName = false;
    			}
    			if(readLastName) {
    				lastName = data;
    				readLastName = false;
    			}
    			if(readMiddleName) {
                    middleName = data;
    				readMiddleName = false;
    			}
    			if(readWholeName) {
                    wholeName = data;
    				splitt = wholeName.split(" ");
    				firstName = splitt[0];
    				for(int i = 1; i < splitt.length - 1; i++)
    					middleName = middleName + ' ' + splitt[i] ;
    				lastName = splitt[splitt.length - 1];
    				readWholeName = false;
    			}
    			readName = false;
    		}
        }
        

        public void endElement(String uri, String localName,String qName) throws SAXException{
    		if(qName.equalsIgnoreCase("name")) {
                ArrayList<String> entity = new ArrayList<>();
                entity.add("" + entityid);
                entity.add(firstName);
                entity.add(middleName);
                entity.add(lastName);
                //entity.add(wholeName);
                
                hashma1.put(firstName + " " + lastName, entity );
    		}
    	}
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

/**
 * Created by Arjun Bhardwaj on 12-11-2015.
 */
/*
* Copyright (c) 2015 arjun bhardwaj
This is a free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
* */
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.exceptions.UnirestException;
import edu.usc.ict.vhmsg.MessageEvent;
import edu.usc.ict.vhmsg.MessageListener;
import edu.usc.ict.vhmsg.VHMsg;
import com.mashape.unirest.http.*;
import com.mashape.unirest.request.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Handle implements MessageListener
{
    public static VHMsg vhmsg;
    HTTP_manager http_man;
//    FinalResult f;
    Pattern p;
    Matcher m;
    WebJsonGetter wj = new WebJsonGetter();
    String config_loc = "config.json";
    String badge_file_loc ="";
    String wefUrl ="";
    String vht = "";

    public Handle()
    {
        //// connect to the vhtoolkit server, and set up.

        //vhmsg = new VHMsg("ENTERPRISE.wv.cc.cmu.edu","DEFAULT_SCOPE");
        vhmsg = new VHMsg();// local host
        //// read config file

        // read the config
        BufferedReader br=null;
        try{
            br=new BufferedReader(new FileReader(config_loc));
        }catch(IOException e){
            e.printStackTrace();
        }
        JsonParser parser=new JsonParser();
        JsonObject configs=parser.parse(br).getAsJsonObject();

        // store teh location of the badge file
        badge_file_loc = configs.get("badge_file_loc").getAsString();
        wefUrl = configs.get("wef_url").getAsString();
        vht = configs.get("vht").getAsString();

        boolean ret = vhmsg.openConnection(vht);
        if (!ret)
        {
            System.out.println("Connection error!");
            return;
        }
        vhmsg.enableImmediateMethod();
        vhmsg.addMessageListener(this);
        vhmsg.subscribeMessage("vrRecommendation");
        vhmsg.subscribeMessage("vrBadge");



        // single time steps to set up teh HTTP adapter module ...

        http_man = new HTTP_manager();

        // indicate ready status.

        vhmsg.sendMessage("HTTP_adapter is ready !!!!");

        // Run your app, messages are received via a different thread
    }

    public void messageAction(MessageEvent e)
    {
        System.out.println("\nreceived message ...");
        // fxn fires whenever message (any message the system has subscribed to) arrives

        String raw_msg = e.toString();// case matters
        String saved_type = "";

        // handle message type : recommendation

        p = Pattern.compile("^vrRecommendation(.*)$");
        m = p.matcher(raw_msg);
        if( m.find() )
        {
            // the technical, actual part of the code
            System.out.println("\n* recommendation request received *");

            String input_string = m.group(1);
            input_string = input_string.trim();
            JsonElement je=null;

            // extract the type

            p = Pattern.compile("^(\\w+) .*$");
            m = p.matcher(input_string);
            if( m.find() )
            {
                saved_type = m.group(1);
            }

            // test case

            p = Pattern.compile("test(.*)$");
            m = p.matcher(raw_msg);
            if( m.find() )
            {
                String url = m.group(1);
                input_string = input_string.trim();

                je = wj.get_json( "http://lookup.dbpedia.org/api/search.asmx/KeywordSearch?MaxHits=5&QueryString="+input_string.replace(" ","%20") );
            }

            //// recommend people

            p = Pattern.compile("recommend_people(.*)$");
            m = p.matcher(raw_msg);
            if( m.find() )
            {
                String str = m.group(1);
                str = str.trim();
                //http://at02.broadway.gq1.yahoo.com
                je = wj.get_json(wefUrl+"wef/v1/people/recommend?" + str.replace(" ", "%20"));
            }

            //// recommend sessions

            p = Pattern.compile("recommend_sessions(.*)$");
            m = p.matcher(raw_msg);
            if( m.find() )
            {
                String str = m.group(1);
                str = str.trim();

                je = wj.get_json(wefUrl+"wef/v1/session/recommend?" + str.replace(" ", "%20"));
            }

            //// recommend popular sessions

            p = Pattern.compile("popular_sessions(.*)$");
            m = p.matcher(raw_msg);
            if( m.find() )
            {
                String str = m.group(1);
                str = str.trim();

                je = wj.get_json(wefUrl+"wef/v1/session/popular?" + str.replace(" ", "%20"));
            }

            //// search people

            p = Pattern.compile("search_people(.*)$");
            m = p.matcher(raw_msg);
            if( m.find() )
            {
                String str = m.group(1);
                str = str.trim();

                je = wj.get_json(wefUrl+"wef/v1/people/search?" + str.replace(" ", "%20"));
            }

            //// search sessions

//            p = Pattern.compile("search_sessions (.*)$");
//            m = p.matcher(raw_msg);
//            if( m.find() )
//            {
//                String str = m.group(1);
//                str = str.trim();
//
//                je = wj.get_json("http://at02.broadway.gq1.yahoo.com/wef/v1/session/recommend?" + str.replace(" ", "%20"));
//            }

            //// recommend food

            p = Pattern.compile("recommend_food(.*)$");
            m = p.matcher(raw_msg);
            if( m.find() )
            {
                String str = m.group(1);
                str = str.trim();

                je = wj.get_json(wefUrl+"wef/v1/food/recommendation?" + str.replace(" ", "%20"));
            }

            //// recommend party

            p = Pattern.compile("recommend_party(.*)$");
            m = p.matcher(raw_msg);
            if( m.find() )
            {
                String str = m.group(1);
                str = str.trim();

                je = wj.get_json(wefUrl+"wef/v1/party/recommendation?" + str.replace(" ", "%20"));
            }

            ///Send Message
            //// search people

            p = Pattern.compile("vrSendMessage(.*)$");
            m = p.matcher(raw_msg);
            if( m.find() )
            {
                String str = m.group(1);
                str = str.trim();
                try {
                    HttpResponse<String> response = Unirest.post("https://eventservicesqa.weforum.org/api/v1/messages/")
                            .header("content-type", "multipart/form-data; boundary=---011000010111000001101001")
                            .header("authorization", "Bearer 2a017d191123431240dccd5c7a53fc4097154985")
                            .header("cache-control", "no-cache")
                            .header("postman-token", "9842b667-928f-e77f-e402-49a23b8df41b")
                            .body("-----011000010111000001101001\r\nContent-Disposition: form-data; name=\"to\"\r\n\r\n001b0000002lzJVAAY\r\n" +
                                    "-----011000010111000001101001\r\nContent-Disposition: form-data; name=\"subject\"\r\n\r\nPostman test Klaus to Lacy\r\n" +
                                    "-----011000010111000001101001\r\nContent-Disposition: form-data; name=\"body\"\r\n\r\nThis message was written in postman\r\n" +
                                    "-----011000010111000001101001\r\nContent-Disposition: form-data; name=\"threadId\"\r\n\r\n1879\r\n-----011000010111000001101001--")
                            .asString();
                } catch (UnirestException e1) {
                    e1.printStackTrace();
                }

                je = wj.get_json(wefUrl+"wef/v1/people/search?" + str.replace(" ", "%20"));
            }
            // end of all the options !! thank god !


            // send the messages ...

            if(je!=null)
            {
                String message = je.toString();

                vhmsg.sendMessage("vrRecommendationResults "+saved_type+" "+message);
                System.out.println(message);

            }
            else
            {
                vhmsg.sendMessage("vrHTTP_adapter SystemFailsToUnderstand");
                System.out.println("vrHTTP_adapter SystemFailsToUnderstand");
            }

        }


        p = Pattern.compile("^vrBadge (.*) (.*)$");
        m = p.matcher(raw_msg);
        if( m.find() )
        {
            // the technical, actual part of the code
            System.out.println("\n* badge request received *");

            String b_id = m.group(1);
            String b_type = m.group(2);

            try
            {
                String content = "This is the content to write into file";

                File file = new File(badge_file_loc);
                // if file doesnt exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);

                bw.write(b_id);

                bw.close();

            } catch (IOException ex) {
                ex.printStackTrace();

            }
        }

        return;
        //System.out.println("Received Message '" + e.toString() + "'");
    }

    public static void main(String[] args)
    {
        Handle han = new Handle();

        //Topic.get_extra_information_dbpedia_type_property("steelers", "rdf:type");


//        Pattern p = Pattern.compile("hi ([\\w ]+)");
//        Matcher m = p.matcher("hi jack my");
//        while(m.find())
//        {
//            System.out.println(m.group(1));
//        }


//        WebJsonGetter w = new WebJsonGetter();
//        w.dbpedia_lookup("steelers");

    }

}
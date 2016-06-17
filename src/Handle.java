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



import com.google.gson.Gson;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import wef.articulab.test.WEFConnectorTest;


public class Handle implements MessageListener
{
    public static VHMsg vhmsg;
    Pattern p;
    Matcher m;
    WebJsonGetter wj = new WebJsonGetter();
    String config_loc = "config.json";
    String badge_file_loc ="";
    String wefUrl ="";
    String vht = "";
    String person_broad_type = "participant"; // guest, participant, unsupported

    String default_userId = "001b0000003y8PDAAY";
    String current_userId = "001b0000003y8PDAAY";
    String current_accessToken = "";
	 HashMap<String, ArrayList<String>> recom_set;
	 
	 public final static int number_of_recommendation_people=10;

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
        WEFConnectorTest.main(new String[]{vht});

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
        vhmsg.subscribeMessage("vrSendMessage");
        vhmsg.subscribeMessage("vrRecommendationSearchModuleResult");

        // indicate ready status.
        vhmsg.sendMessage("vrWEFAdapter WEF adapter is ready !!!!");

        // Run your app, messages are received via a different thread
    }

    public void messageAction(MessageEvent e)
    {
        System.out.println("\nreceived message : "+e.toString());
        // fxn fires whenever message (any message the system has subscribed to) arrives

        String raw_msg = e.toString();// case matters
        String saved_type = "";

        //// handle message type : recommendation

        p = Pattern.compile("^vrRecommendation (.*)$");
        m = p.matcher(raw_msg);
        if( m.find() )
        {
            // the technical, actual part of the code
            System.out.println("\n* recommendation request received *");

            String input_string = m.group(1);
            input_string = input_string.trim();// something like "recommend_people topic=computers&limit=2"
            JsonElement je=null;

            // extract the type

            p = Pattern.compile("^(\\w+) .*$");
            m = p.matcher(input_string);
            if( m.find() )
            {
                saved_type = m.group(1);
            }

            // test case

            /*p = Pattern.compile("test(.*)$");
            m = p.matcher(raw_msg);
            if( m.find() )
            {
                String url = m.group(1);
                input_string = input_string.trim();

                je = wj.get_json( "http://lookup.dbpedia.org/api/search.asmx/KeywordSearch?MaxHits=5&QueryString="+input_string.replace(" ","%20") );
            }*/

            //// recommend people

            p = Pattern.compile("recommend_people(.*)$");
            m = p.matcher(raw_msg);
            if( m.find() )
            {
                String str = m.group(1);// "" or "topic=computer%20science;it&userId=0"
                str = str.trim();

                // if its a participant
                if(person_broad_type.equals("participant"))
                {
                    Pattern p_topic = Pattern.compile("topic=([\\;\\w\\ ]+)");
                    m = p_topic.matcher(str);

                    if(m.find())// if we have a topic specified
                    {
                        String topic_string = m.group(1).trim();
                        System.out.println("1. Sending to Oscar's module: " + topic_string);
                        if( topic_string == null || topic_string.isEmpty() ){
                            topic_string = str;
                        }
                        vhmsg.sendMessage("vrRecommendationSearchModule people "+topic_string);
                        return;
                    }
                    else
                    {
                        System.out.println("2. Sending to Oscar's module: people Justine");
                        vhmsg.sendMessage("vrRecommendationSearchModule people Justine");
                        return;
                    }
                }

                /*if(str.contains("topic"))
                {
                    // yahoo's module

                    String s = wefUrl+"wef/v1/people/recommend?" + str.replace(" ", "%20");
                    System.out.println("Sent out to yahoo's rec sys\t:::\t"+s);
                    je = wj.get_json(s);
                }
                else*/
                {
                    // ran's module

                    /*Pattern pat=Pattern.compile("userId=(\\w+)");
                    Matcher ma = pat.matcher(raw_msg);
                    if(ma.find())
                    {
                        String s = "vrRecommendationPB "+ma.group(1);
                        System.out.println("Sent out to ran's rec sys\t:::\t"+s);
                        vhmsg.sendMessage(s);
                    }
                    else
                    {
                        vhmsg.sendMessage("vrWEFAdapter no topic no userid");
                    }
                    return;*/

                    /*
                    String s = wefUrl+"wef/v1/people/recommend?topic=business&userId=0";
                    System.out.println("Sent out to yahoo's rec sys\t:::\t"+s);
                    je = wj.get_json(s);*/

                }
            }

            //// recommend sessions

            p = Pattern.compile("recommend_sessions(.*)$");
            m = p.matcher(raw_msg);
            if( m.find() )
            {/*
                String str = m.group(1);
                str = str.trim();

                je = wj.get_json(wefUrl+"wef/v1/session/recommend?" + str.replace(" ", "%20"));

                String str = m.group(1);
                str = str.trim();*/

                //je = wj.get_json(wefUrl+"wef/v1/session/popular?");


                String str = m.group(1);
                str = str.trim();

                // if its a participant
                if(person_broad_type.equals("participant"))
                {
                    Pattern p_topic = Pattern.compile("topic=([\\;\\w\\ ]+)");
                    m = p_topic.matcher(str);

                    if(m.find())// if we have a topic specified
                    {
                        String topic_string = m.group(1).trim();
                        vhmsg.sendMessage("vrRecommendationSearchModule sessions "+topic_string);
                        if( topic_string == null || topic_string.isEmpty() ){
                            topic_string = str;
                        }
                        System.out.println("3. Sending to Oscar's module: " + topic_string);
                        return;
                    }
                    else
                    {
                        System.out.println("4. Sending to Oscar's module: sessions business");
                        vhmsg.sendMessage("vrRecommendationSearchModule sessions business");
                        return;
                    }
                }
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
                /*String str = m.group(1);
                str = str.trim();

                je = wj.get_json(wefUrl+"wef/v1/people/search?" + str.replace(" ", "%20"));*/


                String s = wefUrl+"wef/v1/people/recommend?topic=business&userId=0";
                System.out.println("Sent out to yahoo's rec sys\t:::\t"+s);
                je = wj.get_json(s);

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

            p = Pattern.compile("food(.*)$");
            m = p.matcher(raw_msg);
            if( m.find() )
            {
                System.out.println("5. Sending results for food: ");
                vhmsg.sendMessage("vrRecommendationResults food [{  \"@@Location-two\": \"The espresso cafeteria\",  \"@@Location-one\": \"The isle\"  }]");
                return;
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

            // end of all the options !! thank god !



            // send the messages ...

            if(je!=null)
            {
                String message = je.toString();
                System.out.println("6. Sending results: " + saved_type+" "+message);
                vhmsg.sendMessage("vrRecommendationResults " + saved_type + " " + message);
                System.out.println(message);

            }
            else
            {
                vhmsg.sendMessage("vrWEFAdapter SystemFailsToUnderstand");
                System.out.println("vrWEFAdapter SystemFailsToUnderstand");
            }
        }

        //// handle message type : vrBadge

        p = Pattern.compile("^vrBadge (.*) (.*)$");
        m = p.matcher(raw_msg);
        if( m.find() )
        {
            // the technical, actual part of the code

            System.out.println("\n* badge request received *");
            vhmsg.sendMessage("vrWEFAdapter vrBadge received");

            // extract information from teh badge message

            String b_id = m.group(1);
            String b_type = m.group(2);

            // write teh badge id to a file

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

                //bw.write(b_id);
                bw.write("485755");

                bw.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }

            // send out teh conversation start message

            vhmsg.sendMessage("vrStartConversation");

            //// user id ... ? cred endpoint ...

            // delay for a while, till yahoo authenticates

            try {
                Thread.sleep(30*1000);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }

            // get the authentication information

            String id="";
            String token="";
            try
            {
                String s = wefUrl+"wef/v1/user/cred";
                vhmsg.sendMessage("vrWEFAdapter authenticating");
                JsonObject jo = wj.get_json(s).getAsJsonObject();

                id = jo.get("userId").getAsString();
                token = jo.get("userId").getAsString();

                if(id.contentEquals(""))
                {
                    current_userId = default_userId; //////////// just for now ... ?
                    vhmsg.sendMessage("vrWEFAdapter got no userId");
                }
                else
                {
                    current_userId = id;
                    vhmsg.sendMessage("vrWEFAdapter userId:"+id);
                }

                if(token.contentEquals(""))
                {
                    current_accessToken = "-"; //////////// just for now ... ?
                    vhmsg.sendMessage("vrWEFAdapter got no accessToken");
                }
                else
                {
                    current_accessToken = token;
                    vhmsg.sendMessage("vrWEFAdapter accessToken:"+token);
                }

            }
            catch(Exception ex)
            {
                Thread.currentThread().interrupt();
                vhmsg.sendMessage("vrWEFAdapter some error with authentication ->"+id+"->"+token);
            }

        }

        //// Send Message

        p = Pattern.compile("vrSendMessage(.*)$");
        m = p.matcher(raw_msg);
        if( m.find() )
        {
            String str = m.group(1);
            str = str.trim();
            try {
                HttpResponse<String> response = Unirest.post("https://eventservices.weforum.org/api/v1/messages/")
                        .header("content-type", "multipart/form-data; boundary=---011000010111000001101001")
                        .header("authorization", "Bearer badeeee055d45adefab246111f24fbb189971cdb")
                        .header("cache-control", "no-cache")
                        .header("postman-token", "9842b667-928f-e77f-e402-49a23b8df41b")
                        .body("-----011000010111000001101001\r\nContent-Disposition: form-data; name=\"to\"\r\n\r\n001b0000002lzJVAAY\r\n" +
                                "-----011000010111000001101001\r\nContent-Disposition: form-data; name=\"subject\"\r\n\r\nPostman test Klaus to Lacy\r\n" +
                                "-----011000010111000001101001\r\nContent-Disposition: form-data; name=\"body\"\r\n\r\nThis message was written in postman\r\n" +
                                "-----011000010111000001101001\r\nContent-Disposition: form-data; name=\"threadId\"\r\n\r\n1879\r\n-----011000010111000001101001--")
                        .asString();
                System.out.println(response.toString());
            }
            catch (UnirestException e1)
            {
                e1.printStackTrace();
            }
        }

        //// Oscar's results

        p = Pattern.compile("vrRecommendationSearchModuleResult (\\w+) (.*)$");
        m = p.matcher(raw_msg);
        if( m.find() )
        {
            String t = m.group(1).trim();
            if(t.contentEquals("people"))
            {
                String s = m.group(2).trim();
                if(s.contentEquals("[]"))
                {
                    if(person_broad_type.equals("participant"))
                    {
                        vhmsg.sendMessage("vrRecommendationPB "+current_userId);
                    }
                    else
                    {
                        System.out.println("7. Sending to Oscar's module: people Justine ");
                        vhmsg.sendMessage("vrRecommendationSearchModule people Justine");
                    }
                }
                else
                {
                	String ordered_recommendation="";
                	try {
						 ordered_recommendation = order_search(m.group(2));
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                    System.out.println("8. Sending results: " +ordered_recommendation);
                    vhmsg.sendMessage("vrRecommendationResults recommend_people " + ordered_recommendation);
                }
            }

            if(t.contentEquals("sessions"))
            {
                String s = m.group(2).trim();
                if(s.contentEquals("[]"))
                {
                    System.out.println("9. Sending to Oscar's module: sessions business ");
                    vhmsg.sendMessage("vrRecommendationSearchModule sessions business");
                }
                else
                {
                    System.out.println("10. Sending results recommend_sessions "+m.group(2));
                    vhmsg.sendMessage("vrRecommendationResults recommend_sessions "+m.group(2));
                }
            }


            return;
        }

        return;
    }

    private String order_search(String group) throws JSONException {
    	int counter=0;
    	String rec_result_list="";
    	//From Oscar's result
    	List<Participant> participants = Participant.parse(new JSONArray(group));
    	ArrayList<Participant> candidates =new ArrayList<>();
    	//From Ran's matrix
    	ArrayList<String> recommend_list = recom_set.get(current_userId);
    	System.out.println(recommend_list.size());
    	//From Oscar's result
    	HashMap<String,Integer> search_list=new HashMap<>();
    	for(int i=0;i<participants.size();i++){
    		Participant people=participants.get(i);
    		search_list.put(people.getId(), i);
    	}
    	//Filter ran's result by using oscar's result
    	for(int i=1;i<recommend_list.size();i++){
    		String current_recommendID=recommend_list.get(i);
    		if(search_list.containsKey(current_recommendID)){
    			int pos=search_list.get(current_recommendID);
    			Participant candidate =participants.get(pos);
    			candidates.add(candidate);
    			counter++;
    		}
    		if(counter==number_of_recommendation_people){
    			rec_result_list = new Gson().toJson(candidates);
    		}
    	}
    	System.out.println(rec_result_list);
        if( rec_result_list == null || rec_result_list.isEmpty() ){
            rec_result_list = group;
        }
		return rec_result_list;
		
	}

    private void initializeDatabase() throws ClassNotFoundException, IOException {
		FileInputStream serRecom = new FileInputStream(
				 "people_recommendation.ser");

		ObjectInputStream oosFull = null;
		
		try {
			oosFull = new ObjectInputStream(serRecom);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		recom_set = ( HashMap<String, ArrayList<String>>) oosFull
				.readObject();
		oosFull.close();
		serRecom.close();	
	}
    
	public static void main(String[] args) throws ClassNotFoundException, IOException
    {
        Handle han = new Handle();
        han.initializeDatabase();
    }

}
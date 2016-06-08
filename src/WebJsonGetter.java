import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Arjun Bhardwaj on 12-11-2015.
 */
public class WebJsonGetter {

    private static String readAll(Reader rd) throws IOException {
        BufferedReader reader = new BufferedReader(rd);
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public JsonObject dbpedia_lookup(String query)
    {
        JsonObject jo=null;
        query = query.replace(" ","%20");
        try
        {
            URL url = new URL("http://lookup.dbpedia.org/api/search.asmx/KeywordSearch?MaxHits=5&QueryString="+query);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "application/json");

            InputStream is = urlConnection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);

            jo = (JsonObject) new JsonParser().parse(jsonText);
            //System.out.println(jo);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return(jo);

    }

    public JsonElement get_json(String complete_url)
    {
        JsonElement je=null;
        complete_url = complete_url.replace(" ","%20");
        try
        {
            URL url = new URL(complete_url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept", "application/json");

            InputStream is = urlConnection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);

            je = (JsonElement) new JsonParser().parse(jsonText);
            //System.out.println(jo);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return(je);

    }



}

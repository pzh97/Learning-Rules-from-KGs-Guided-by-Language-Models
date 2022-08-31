package de.mpii.embedding;

import de.mpii.util.Crawler;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.util.*;
import java.net.URI;
import org.apache.hc.core5.net.URIBuilder;

public class HypernymClient {
    public static void main(String[] args) {
        ArrayList<String> term = new ArrayList<String>();
        int top_k = 10;
        try {
            String line = null;
            //BufferedReader br = new BufferedReader(new FileReader("./data/imdb/rules.txt"));
            BufferedReader br = new BufferedReader(new FileReader("." + "/" + args[0] + "/" + "rules.txt"));
            for (line = br.readLine(); line != null; line = br.readLine()) {
                String ln = line.replaceAll("[-+.^:,'()<>_-]", " ");
                String[] l = ln.split(" ");
                String word = l[1];
                String new_word = splitString(word);
                term.add(new_word);
            }
            Set<String> set = new HashSet<>(term);
            term.clear();
            term.addAll(set);
            String[] termArray = term.toArray(new String[0]);
            //System.out.println(Arrays.toString(termArray));
            br.close();
            PrintStream o = new PrintStream(new File("o.txt"));
            PrintStream console = System.out;
            System.setOut(o);
            for (int i = 0; i < (termArray.length); i++) {
                String str = termArray[i];
                //System.out.println(str);
                URIBuilder b = new URIBuilder("http://localhost:6993/predict");
                b.addParameter("query", str + " is a type of " + "[MASK].");
                String Uri = b.toString();
                //System.out.println(Uri);
                Crawler queryCrawler = new Crawler();
                String crawlerContent = queryCrawler.getContentFromUrl(Uri);
                JSONArray jArray = new JSONArray(crawlerContent);
                ArrayList<String> hypernym = new ArrayList<String>();
                for (int j = 0; j < top_k; j++) {
                    JSONObject jIndex = jArray.getJSONObject(j);
                    String jPrediction = jIndex.getString("prediction");
                    hypernym.add(jPrediction);
                    //System.out.println(str + " : " + hypernym);
                }
                //System.out.println(str + " -> " + jArray + "\n");
                String hypernyms = hypernym.toString().replace("[", "").replace("]", "");
                System.out.println(str + " : " + hypernyms);
            }
        } catch (Exception e) {
            System.err.println("Error: Target File Cannot Be Read!");
        }
    }

    public static String splitString(String splitC) {
        String new_string = "";
        for (int i = 0; i < splitC.length(); i++) {
            char c = splitC.charAt(i);
            if (Character.isUpperCase(c)) {
                new_string = new_string + " " + Character.toLowerCase(c);
            } else {
                new_string = new_string + c;
            }
        }
        return new_string;
    }
}

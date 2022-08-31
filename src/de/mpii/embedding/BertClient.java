package de.mpii.embedding;

import de.mpii.util.Crawler;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.hc.core5.net.URIBuilder;

public class BertClient extends EmbeddingClient {
    public static final int top_k = 100;


    public BertClient(String workspace){
	super(workspace);
    }
     
    @Override
    public double getScore(int subject, int predicate, int object) {
		double score = 0.0;
		try {
			String s_init = String.valueOf(entitiesString.get(subject));
			String p_init = String.valueOf(relationsString.get(predicate));
			String o_init = String.valueOf(entitiesString.get(object));
			String s1 = s_init.replaceAll("[-+.^:,'()<>_-]", " ");
			String s = s1.replaceAll("Q+[0-9]+", " ");
			String o_upper = o_init.replaceAll("[-+.^:,'()<>_-]", " ").replaceAll("Q+[0-9]+", " ");
			//System.out.println(o_upper); need to remove id of object as well.
			String o_conca = o_upper.toLowerCase();
			String[] o = o_conca.split(" ");
			String p_removed = p_init.replaceAll("[-+.^:,'()<>_-]", " ");
			String p_removed_1 = p_removed.replaceAll("P+[0-9]+", " ");
			String p = splitString(p_removed_1);
			URIBuilder b = new URIBuilder("http://localhost:6993/predict");
			b.addParameter("query", s + p + "[MASK].");
			String Uri = b.toString();
			System.out.println(Uri);
			Crawler queryCrawler = new Crawler();
			String crawlerContent = queryCrawler.getContentFromUrl(Uri);
			JSONArray jArray = new JSONArray(crawlerContent);
			ArrayList<Double> scoreArray = new ArrayList<Double>();
			for (int i = 0; i < top_k; i++) {
				JSONObject jIndex = jArray.getJSONObject(i);
				String jPrediction = jIndex.getString("prediction");
				for (int j = 0; j < o.length; j++) {
					if (o[j].equals(jPrediction)) {
						score = 1/(i+1);
						scoreArray.add(score);
					}
				}
			}
			if (!(scoreArray.isEmpty())) {
				return Collections.min(scoreArray);
			}
			else
				return 0.0;
		} catch (URISyntaxException e) {
			System.out.println(e);
		}
		return -1.0;
	}

    public static void main (String[] args) {
	BertClient bertclient = new BertClient("./data/wiki44k/");
	System.out.println(bertclient.getScore(0,0,0));
    }

    public static String splitString (String splitC){
	String new_string = "";
	for (int i=0; i < splitC.length(); i++){
	    char c = splitC.charAt(i);
	    if(Character.isUpperCase(c)){
		new_string = new_string + " " + Character.toLowerCase(c);
	    }
	    else{
		new_string = new_string + c;
	    }
	}
	return new_string;
    }
}

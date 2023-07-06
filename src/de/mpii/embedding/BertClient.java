package de.mpii.embedding;

import de.mpii.util.Crawler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileReader;

import org.apache.hc.core5.net.URIBuilder;

public class BertClient extends EmbeddingClient {
    public static final int top_k = 100;
	public Pattern q_pattern;
	public JSONObject qids_label;

    public BertClient(String workspace){
		super(workspace);
		System.out.println(workspace);
		this.q_pattern = Pattern.compile("Q[0-9]+");
		try{
			JSONTokener tokener = new JSONTokener(new FileReader("./data/wiki44k/qids_label.json"));
			this.qids_label = new JSONObject(tokener);
		} catch (java.io.FileNotFoundException e) {
			System.out.println(e);
		}
    }
     
    @Override
    public double getScore(int subject, int predicate, int object) {
		double score = 0.0;
		try {
			String s_init = String.valueOf(entitiesString.get(subject));
			Matcher s_matcher = this.q_pattern.matcher(s_init);
			s_matcher.find();
			String s_qid = s_matcher.group(0);
			String s = "";
			try{
				s = String.valueOf(this.qids_label.get(s_qid));
			} catch (JSONException e) {
				s = s_init.replaceAll("[-+.^:,'()<>_-]", " ").replaceAll("Q+[0-9]+", " ").replaceAll(" +", " ").strip();
			}

			String o_init = String.valueOf(entitiesString.get(object));
			Matcher o_matcher = this.q_pattern.matcher(o_init);
			o_matcher.find();
			String o_qid = o_matcher.group(0);
			String o = "";
			try{
				o = String.valueOf(this.qids_label.get(o_qid));
			} catch (JSONException e) {
				o = o_init.replaceAll("[-+.^:,'()<>_-]", " ").replaceAll("Q+[0-9]+", " ").replaceAll(" +", " ").strip();
			}

			String p_init = String.valueOf(relationsString.get(predicate));
			String p = p_init.replaceAll("[-+.^:,'()<>_-]", " ").replaceAll("P+[0-9]+", " ").replaceAll(" +", " ").strip();


			URIBuilder b = new URIBuilder("http://localhost:6995/predict");
			
			b.addParameter("head", s);
			b.addParameter("relation", p);
			b.addParameter("tail", o);
			String Uri = b.toString();
			System.out.println(Uri);
			Crawler queryCrawler = new Crawler();
			float mrr = Float.parseFloat(queryCrawler.getContentFromUrl(Uri));
			return mrr;
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

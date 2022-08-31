package de.mpii.embedding;

import org.apache.hc.core5.net.URIBuilder;
import de.mpii.util.Crawler;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URISyntaxException;
import java.text.Normalizer;
import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class HyperContxClient extends EmbeddingClient {
    public double score = 0.0;
	public int top_k_hypernyms = 5;
	public static final int top_k = 100;
    protected ArrayList<String> descString;
    protected HashMap<Integer, String> descStringMap;

    public HyperContxClient(String workspace){
	super(workspace);
	try {
	    BufferedReader DescIn = new BufferedReader(new InputStreamReader(new FileInputStream(new File(workspace + "/e_desc.txt"))));
	    descStringMap = new HashMap<>();
	    descString = new ArrayList<>();
	    String line;
	    while ((line = DescIn.readLine()) != null) {
		if (line.isEmpty()){
		    break;
		}
		String[] arr = line.split("\t");
		descStringMap.put(Integer.parseInt(arr[0]), arr[1]);
	    }
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
    }

    @Override
    public double getScore(int subject, int predicate, int object) {
		try {
			String s_init = String.valueOf(entitiesString.get(subject));
			String p_init = String.valueOf(relationsString.get(predicate));
			String o_init = String.valueOf(entitiesString.get(object));
			String s = s_init.replaceAll("[-+.^:,'()<>_-]", " ").replaceAll("Q+[0-9]+", " ");
			String o_conca = o_init.replaceAll("[-+.^:,'()<>_-]", " ").replaceAll("Q+[0-9]+", " ").toLowerCase();
			//split the object into several components; "Cristiano Ronaldo" -> "Cristiano", "Ronaldo"
			String[] o = o_conca.split(" ");
			String p = p_init.replaceAll("[-+.^:,'()<>_-]", " ").replaceAll("P+[0-9]+", " ");
			//System.out.println(descStringMap.get(0));
			String descSubj_init = descStringMap.get(subject);
			String descSubj = removeAccent(descSubj_init.replaceAll("[-+.^:,'()<>_-]", " ").replaceAll("P+[0-9]+", " "));
			String descObj_init = descStringMap.get(object);
			String descObj = removeAccent(descObj_init.replaceAll("[-+.^:,'()<>_-]", " ").replaceAll("P+[0-9]+", " "));
			URIBuilder b = new URIBuilder("http://localhost:6993/predict");
			b.addParameter("query", s + " is " + descSubj + ", " + o_conca + " is " + descObj + ", " + s + p + o_conca + ", " + "[MASK]" + " is a type of " + p + ".");
			String Uri = b.toString();
			//System.out.println(Uri);
			Crawler queryCrawler = new Crawler();
			String crawlerContent = queryCrawler.getContentFromUrl(Uri);
			JSONArray jArray = new JSONArray(crawlerContent);
			ArrayList<String> hypernym = new ArrayList<String>();
			double sum = 0.0;
			for (int i = 0; i < top_k_hypernyms; i++){
				JSONObject jIndex = jArray.getJSONObject(i);
				String jPrediction = jIndex.getString("prediction");
				hypernym.add(jPrediction);
			}
			//System.out.println(hypernym);
			ArrayList<Double> scoreArray = new ArrayList<Double>();
			ArrayList<Double> minArray = new ArrayList<Double>();
			for (int j = 0; j < hypernym.size(); j++){
				URIBuilder c = new URIBuilder("http://localhost:6993/predict");
				c.addParameter("query", s + p + " or " + hypernym.get(j) + " [MASK]" + ".");
				String hyconxUri = c.toString();
				Crawler hyCrawler = new Crawler();
				String hyperContent = hyCrawler.getContentFromUrl(hyconxUri);
				JSONArray hyArray = new JSONArray(hyperContent);
				//System.out.println(hyconxUri);
				for (int k = 0; k < top_k; k++){
					JSONObject hyIndex = hyArray.getJSONObject(k);
					String hyPrediction = hyIndex.getString("prediction");
					for (int t = 0; t < o.length; t++){
						if (o[t].equals(hyPrediction)) {
							score = 1/(k+1);
							scoreArray.add(score);
						}
					}
				}
				if (!(scoreArray.isEmpty())) {
					double min = Collections.min(scoreArray);
					minArray.add(min);//get min and add to an arraylist.
				}
			}
			if (!(minArray.isEmpty())) {
				for (double element : minArray) {
					sum += element;
				}
				return sum / minArray.size();
			}
			else
				return 0.0;
		} catch (URISyntaxException e) {
			System.out.println(e);
		}
	return 0.0;
    }

	

    public static void main (String[] args){
		HyperContxClient hypercontxclient = new HyperContxClient("./data/wiki44k/");
        System.out.println(hypercontxclient.getScore(0, 0, 0));
    }

	public static String removeAccent (String str) {
		return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}
}




import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class Model{
	public HashMap<String, State> states;
	public ArrayList<String[]> phaseStates;
	private static Gson gson = new Gson();
	private static boolean saveModel = false;
	private static boolean loadModel = true;
	private ArrayList<State> history = new ArrayList<>();
	public State current;
	public ArrayList<String> scenarios;
	public String triggeredBy = "start";
	public String currentIntent = "start_greeting";
	static boolean sessionRecommendation = false;
	static boolean foodRecommendation = false;
	static boolean personRecommendation = false;
	static boolean partiesRecommendation = false;
	private boolean runScenario;
	private static Model model;
	//public static String jsonFile = "central_model_fsm.json";
	//public static String dotFile = "central_model_fsm.dot";

	static public int fontsize = 20;
	static public String shape = "box";

	
	public static void main(String[] args) throws IOException {
		//Scanner sc = new Scanner(new File(jsonFile));
		String json = "";

		BufferedReader br = new BufferedReader(new FileReader(new File("FSM.json")));
		//br.readLine();
		String line = "";
		while ((line=br.readLine())!=null) {
			json += line;
			System.out.println(line);
		}
		//System.out.println(json);
		Model model = gson.fromJson( json, Model.class);
		for( String key : model.states.keySet() ){
			model.states.get(key).name = key;
		}
		if( !model.runScenario ){
			if( model.scenarios != null ){
				model.scenarios.clear();;
			}
		}
	
	}

	private Model(){
		states = new HashMap<>();
		phaseStates = new ArrayList<>();
	}

	public static Model getInstance(){
		if( model == null ){
			model = new Model();
		}
		return model;
	}


	public Collection<String> getStates() {
		return states.keySet();
	}

	public State get(String intention){
		return states.get(intention);
	}

	public void saveModel(Model model){
		try {
			String json = gson.toJson(model);
			PrintWriter out = new PrintWriter("FSM.json");
			out.println(json);
			out.flush();
			out.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static Model loadModel() {
		try{
			//Scanner sc = new Scanner(new File(jsonFile));
			String json = "";

			BufferedReader br = new BufferedReader(new FileReader(new File("FSM.json")));
			//br.readLine();
			String line = "";
			while ((line=br.readLine())!=null) {
				json += line;
				System.out.println(line);
			}
			//System.out.println(json);
			Model model = gson.fromJson( json, Model.class);
			for( String key : model.states.keySet() ){
				model.states.get(key).name = key;
			}
			if( !model.runScenario ){
				if( model.scenarios != null ){
					model.scenarios.clear();;
				}
			}
			return model;
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	public static Model createNodes(){
		if( loadModel ){
			model = loadModel();
		}else {
			model = new Model();
			//            model.phaseStates.add("recommend_sessions");
			//            model.phaseStates.add("recommend_people");
			//            model.phaseStates.add("recommend_food");
			//            model.phaseStates.add("recommend_parties");
			//            model.phaseStates.add("farewell");
			//
			//            model.states.put("start", createNode("start_greeting", new String[][]{ new String[]{"start_trigger", "greeting"}}, "Start Conversation"));
			//            model.states.put("start_trigger", createNodeUser("greeting", new String[][]{ new String[]{"self_naming", "pleasure_coming_together"}}, "Hello, I'm Rachel. Whom do i have the pleasure of\n" +
			//                    "meeting ?"));
			//            model.states.put("self_naming", createNode("pleasure_coming_together", new String[][]{ new String[]{"pleasure_coming_together", "introduction"}}, "Great! Nice to meet you"));
			//            model.states.put("pleasure_coming_together", createNode("introduction", new String[][]{ new String[]{"introduction", "ask_if_first_time_attending"}}, "I'm here in Tianjin to be your personal assistant"));
			//            model.states.put("introduction", createNodeUser("ask_if_first_time_attending", new String[][]{new String[]{"positive_confirmation", "feedback_positive"}, new String[]{"negative_confirmation", "feedback_negative"}}, "Is this your first time here, or have you been before?"));
			//            if( saveModel ){
			//                saveModel(model);
			//            }
		}
		model.current = model.states.get("start");
		return model;
	}

	public State createNodeUser(String name, String[][] tranStates, String systemUtterance){
		State state = new State(name, tranStates, systemUtterance);
		state.typeNextIntent = "user_intent";
		return state;
	}

	public State createNode(String name, String[][] transitionStates, String systemUtterance){
		State state = new State(name, transitionStates, systemUtterance);
		state.typeNextIntent = "system_intent";
		return state;
	}

	public String processSystemUtterance() {
		String[] utterance = current.systemUtterance.split("]");
		for(int i = 0; i < utterance.length; i++ ){
			utterance[i] = utterance[i].replace("[", "").replace("]", "").trim();
		}
		if(utterance.length == 1){
			return utterance[0];
		}
		return utterance[ getPos(utterance, current.phase) ];
	}

	public int getPos(String[] array, String element){
		for(int i = 0; i < array.length; i++){
			if( array[i].equals(element) ){
				return i + 1;
			}
		}
		return 0;
	}

	public String extractSystemIntent(String intent, boolean extractStateName) {
		for( String[] phase : phaseStates ){
			if( phase[0].equals(intent) ){
				return phase[1];
			}
		}
		if( current.nextTransitionsStates.size() == 1 ){
			return current.nextTransitionsStates.get(0).state;
		}
		for( TransitionState state : current.nextTransitionsStates){
			if( state.intention.equals( intent ) && state.operation == null){
				return state.state;
			}
		}
		if( extractStateName ) {
			return current.name;
		}
		return "";
	}

	public void storeInHistory(String input) {
		if( current.typeNextIntent.equals("user_intent") ){
			history.add( new State(input, true) );
		}else{
			history.add( current );
		}
		triggeredBy = input;
	}

	public void validateStates() {
		if( current.name.equals("ask_meet_more_people") ){
			personRecommendation = true;
		}
		if( current.name.equals("ask_attend_more_session") ){
			sessionRecommendation = true;
		}
		if( current.name.equals("enjoy_food") ){
			foodRecommendation = true;
		}
		if( current.name.equals("enjoy_party") ){
			partiesRecommendation = true;
		}
		currentIntent = current.name;
	}

	public void createScenario() {
		scenarios = new ArrayList<>();
		scenarios.add("self_naming");
		scenarios.add("positive_confirmation");
		scenarios.add("recommend_peopolitos");
		scenarios.add("recommend_people");
		scenarios.add("dislike");
		scenarios.add("negative_confirmation"); //n_rp6
		scenarios.add("positive_confirmation"); //n_rp7
		scenarios.add("gratitude");
		scenarios.add("positive_confirmation"); //n_rp5
		scenarios.add("positive_confirmation");
		scenarios.add("gratitude");
		scenarios.add("positive_confirmation");
		scenarios.add("positive_confirmation");
		scenarios.add("gratitude");              //n_rp10 : max_num_people_recommendations
		scenarios.add("positive_confirmation");  //rp11
		scenarios.add("negative_confirmation");  //n_rs3
		scenarios.add("positive_confirmation"); // n_rs4
		scenarios.add("negative_confirmation");  //n_rs5
		scenarios.add("positive_confirmation"); // n_rs6
		scenarios.add("positive_confirmation"); // n_rs8
		scenarios.add("positive_confirmation"); // n_rs6, n_rs7
		scenarios.add("positive_confirmation"); // n_rs4
		scenarios.add("positive_confirmation"); // n_rs6, n_rs7, n_rs9, n_rf0
		scenarios.add("positive_confirmation"); // n_rf1
		scenarios.add("negative_confirmation"); // n_rf2
		scenarios.add("dislike"); // n_rf3, n_rf4, n_rf5
		scenarios.add("negative_confirmation"); // n_rf7, n_rf4, n_rf5
		scenarios.add("positive_confirmation"); // n_rf6, n_rf8
		scenarios.add("party");
	}

	public State jumpNextNode(String intent) {
		current = getNexNode(intent);
		return current;
	}

	private State getNexNode(String intent){
		String validated = current.validate(intent);
		if( validated != null ){
			return get(validated);
		}
		State result = get(intent);
		if( result == null ){
			String newIntent = extractSystemIntent( intent, false );
			result = get(newIntent);
			if( result == null ){
				result = get(extractSystemIntent(newIntent, false));
				if( result == null ) {
					result = get(extractSystemIntent("not_supported", false));
				}
			}
			if( result != null){
				return result;
			}
		}
		if( "not_supported".equals(intent) ){
			if( result == null ) {
				result = get("not_supported");
				result.typeNextIntent = current.typeNextIntent;
				result.phase = current.phase;
				result.name = current.name;
			}
			result.nextTransitionsStates = current.nextTransitionsStates;
			return result;
		}
		if( result != null && (current.contains(intent) || phaseStates.contains(intent) || current.contains("any")) ){
			if(current.phase != null && ( result.changePhase || result.phase == null) ){
				result.phase = current.phase;
				result.changePhase = true;
			}
			if( result.nextTransitionsStates == null ){
				current = result;
				result = jumpNextNode(result.name);
			}
			return result;
		}
		return jumpNextNode("not_supported");
	}

	public String transformSystemIntent(String systemIntent){
		if(systemIntent.equals("start_people_recommendation") || systemIntent.equals("start_session_recommendation")
				|| systemIntent.equals("start_food_recommendation") || systemIntent.equals("start_party_recommendation")){
			return "start_recommendation";
		}
		if(systemIntent.equals("feedback_like_work") || systemIntent.equals("feedback_dislike_work")){
			return "feedback_user_work";
		}
		if(systemIntent.equals("people_recommendation_results") || systemIntent.equals("session_recommendation_results")
				|| systemIntent.equals("food_recommendation_results") || systemIntent.equals("party_recommendation_results")){
			return "recommendation_results";
		}
		if(systemIntent.equals("feedback_positive_for_food")){
			return "feedback_positive";
		}
		return systemIntent;
	}
	
	

}


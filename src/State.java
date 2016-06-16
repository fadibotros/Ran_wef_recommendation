

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by oscarr on 5/24/16.
 */
public class State implements Comparable{
    public String id;
    public String name;
    public String phase;
    public String typeNextIntent;
    public ArrayList<TransitionState> nextTransitionsStates;
    public String systemUtterance;
    public boolean internalValidation;
    boolean isUserIntent;
    transient boolean changePhase;
    public transient int order;

    public State(String name, String[][] nextTransitionsStates, String systemUtterance) {
        this(name, nextTransitionsStates, "system_intent", systemUtterance, false);
    }

    public State(String name, String[][] nextTransitionsStates, String typeNextIntent, String systemUtterance, boolean isUserIntent) {
        this.nextTransitionsStates = createTransitionStates(nextTransitionsStates);
        this.typeNextIntent = typeNextIntent;
        this.systemUtterance = systemUtterance;
        this.name = name;
        this.isUserIntent = isUserIntent;
    }

    public State(String name, boolean isUserIntent) {
        this(name, null, null, null, isUserIntent);
    }

    public State(String name, String phase) {
        this.name = name;
        this.phase = phase;
    }

    private ArrayList<TransitionState> createTransitionStates(String[][] nextTransitionsStates) {
        if( nextTransitionsStates == null ) return null;
        ArrayList<TransitionState> transitionStates = new ArrayList<>();
        for( String[] tranState : nextTransitionsStates ){
            transitionStates.add( new TransitionState(tranState) );
        }
        return transitionStates;
    }

    public boolean contains(String intent) {
        if( nextTransitionsStates == null ){
            return true;
        }
        for(TransitionState ts : nextTransitionsStates){
            if( ts.state.equals(intent) || ts.intention.equals(intent) ){
                return true;
            }
        }
        return false;
    }

    public String validate(String transition){
        String result = null;
        if( nextTransitionsStates != null ) {
            for (TransitionState ts : nextTransitionsStates) {
                if (ts.intention.equals(transition)) {
                    if ( (result = ts.validate()) != null ) {
                        return result;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public int compareTo(Object o) {
        if( o instanceof State){
            return Integer.compare( this.order, ((State) o).order);
        }
        throw new IllegalStateException("Trying to add a non-State object to the queue");
    }
}

class TransitionState{
    String state;
    String intention;
    String operation;
    String value;
    int cont = 1;

    public TransitionState(String state, String intention) {
        this.state = state;
        this.intention = intention;
    }

    public TransitionState(String[] transState){
        this.intention = transState[0];
        this.state = transState[1];
    }

    public String validate(){
        if( operation == null ){
            return null;
        }else{
            cont++;
            if( operation.equals(">=") ){
                if( cont >= Integer.valueOf(value)){
                    return state;
                }
            }else if( operation.equals("<") ){
                if( cont < Integer.valueOf(value)){
                    return state;
                }
            }else if( operation.equals("=") ){
                if( cont == Integer.valueOf(value)){
                    return state;
                }
            }else if( operation.equals("equals") ){
                if( value.equals("isSessionNotCovered") && !Model.sessionRecommendation ){
                    return state;
                }else if( value.equals("isFoodNotCovered") && !Model.foodRecommendation ){
                    return state;
                }else if( value.equals("isPersonNotCovered") && !Model.personRecommendation ){
                    return state;
                }else if( value.equals("isPartiesNotCovered") && !Model.partiesRecommendation ){
                    return state;
                }else if( value.equals("areAllCovered") && Model.sessionRecommendation && Model.foodRecommendation
                        && Model.personRecommendation && Model.partiesRecommendation ){
                    return state;
                }
            }
        }
        return null;
    }
}

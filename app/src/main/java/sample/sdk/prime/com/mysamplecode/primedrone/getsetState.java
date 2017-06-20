package sample.sdk.prime.com.mysamplecode.primedrone;

/**
 * Created by 김병현 on 2017-04-20.
 */

public class getsetState {
    enum State { RUNNING, NONE, VERSION_CHECKED, SAVED, LOADED, TSP_COMPLETE };
    private State state;

    public getsetState(){
        state = State.NONE;
    }

    public State getState(){
        return this.state;
    }
    public void setState(State state){
        this.state = state;
    }
}

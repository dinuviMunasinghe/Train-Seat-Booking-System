public class Passenger {
    private String name;
    private String id;
    private String seatNumber;
    private int secondsInQueue;

    public Passenger() {
    }

    public String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getId(){
        return this.id;
    }
    public void setId(String id){
        this.id=id;
    }
    public String getSeatNumber(){
        return this.seatNumber;
    }
    public void setSeatNumber(String seatNumber){
        this.seatNumber=seatNumber;
    }
    public int getSecondsInQueue(){
        return this.secondsInQueue;
    }
    public void setSecondsInQueue(int secondsInQueue){
        this.secondsInQueue=secondsInQueue;
    }
}

import java.util.ArrayList;
import com.mongodb.BasicDBObject;
import org.bson.Document;
import com.mongodb.client.*;

public class PassengerQueue {
    private int size;
    private int first;
    private int last;
    private int maxStayInQueue;
    private int maxLength;
    private Passenger[] queueArray = new Passenger[42];

    public PassengerQueue() {
    }

    public void add(Passenger next){
        if(!isFull()){
            queueArray[last] = next;
            last = (last+1)%42;
            size = size + 1;
        } else {
            System.out.println("QUEUE IS FULL");
        }
    }

    public Passenger remove(){
        Passenger data = queueArray[first];
        if(!isEmpty()){
            first = (first+1)%42;
            size = size - 1;
        }
        return data;
    }

    public boolean isFull(){
        return getSize()==42;
    }

    public boolean isEmpty(){
        return getSize()==0;
    }

    public int getSize(){
        return size;
    }

    public String getNameId(int i){
        if(queueArray[i]!=null){
            String name = queueArray[i].getName();
            String seatNo = queueArray[i].getSeatNumber();
            return name + "/" +seatNo;
        } else {
            return " ";
        }
    }

    public String getNameIdOne(int i){
        String nameId="";
        for(int count=0; count<42; count++){
            if(queueArray[count]!=null){
                String name = queueArray[count].getName();
                String seatNo = queueArray[count].getSeatNumber();
                if(seatNo.equals(Integer.toString(i+1))){
                    nameId=name + "/" + seatNo;
                }
            }
        }
        return nameId;
    }

    public void deleteSeat(String seatNoEnteredByUser){
        int position = 0;
        ArrayList<Passenger> otherPassengerData = new ArrayList<>();
        String name="", seatNum="", id="";
        boolean isFound =  false;
        for(int i=0; i<42; i++){
            if(queueArray[i]!=null){
                name = queueArray[i].getName();
                seatNum = queueArray[i].getSeatNumber();
                id = queueArray[i].getId();
                if(seatNum.equals(seatNoEnteredByUser)) {
                    position = i;
                    isFound =  true;
                    break;
                }
            }
        }

        if(isFound){
            for(int j=0; j<42; j++){
                if (j != position) {
                    otherPassengerData.add(queueArray[j]);
                }
                remove();
            }

            for(int k=0; k<42; k++){
                if(k<otherPassengerData.size()){
                    queueArray[k] = otherPassengerData.get(k);
                } else {
                    queueArray[k] = null;
                }
            }

            System.out.println("The deleted record was: \n  Name:" + name + "\n  Id:" + id + "\n  SeatNo:" + seatNum);
        } else {
            System.out.println("No record was found for the data entered");
        }
    }

    public void storingData(String queueCollectionName){
        try{
            MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase database = mongoClient.getDatabase("programmingIICw2");          //get the database
            MongoCollection<Document> collection = database.getCollection(queueCollectionName);           //get the collection
            BasicDBObject row = new BasicDBObject();
            collection.deleteMany(row);
            for(int i=0; i<42; i++){
                String name, id, seatNo;
                if(queueArray[i]!=null){
                    name = queueArray[i].getName();
                    id = queueArray[i].getId();
                    seatNo = queueArray[i].getSeatNumber();
                    if(name!=null && id!=null && seatNo!=null){
                        Document document = new Document("name",name)
                                .append("id", id)
                                .append("seatNo", seatNo);
                        collection.insertOne(document);
                    }
                }
            }
        } catch(Exception e) {
            System.out.println("An error occurred");
        }
    }

    public void loadingData(String queueCollectionName){
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> id = new ArrayList<>();
        ArrayList<String> seatNo = new ArrayList<>();
        ArrayList<Passenger> passengerObjects = new ArrayList<>();
        int noOfRecords = 0;
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("trainSeatBooking");                    //gets the database
        MongoCollection<Document> collection = database.getCollection(queueCollectionName);               //get the collection
        FindIterable<Document> data = collection.find();
        for(Document record:data){                                  //for the number of documents in the database the loop iterates
            name.add(record.getString("name"));
            id.add(record.getString("id"));
            seatNo.add(record.getString("seatNo"));
            noOfRecords = noOfRecords +1;
        }

        if(name.size()==noOfRecords && id.size()==noOfRecords && seatNo.size()==noOfRecords){
            for(int i=0; i<42; i++){
                if(i<noOfRecords){

                    queueArray[i].setName(name.get(i));
                    queueArray[i].setId(id.get(i));
                    queueArray[i].setSeatNumber(seatNo.get(i));
                }
            }
        }

        for (Passenger passenger : queueArray) {
            if(passenger!=null){
                String nameV = passenger.getName();
                String idV = passenger.getId();
                String seatNum = passenger.getSeatNumber();
                if(nameV!=null && idV!=null && seatNum!=null){
                    System.out.println("Name : " + passenger.getName() + "       id : " + passenger.getId() + "       SeatNumber : " +passenger.getSeatNumber() );
                }
            }
        }
    }

    public int getNoOfPassengersInQueue(){
       int noOfPassengersInQueue = 0;
        for (Passenger passenger : queueArray) {
            if(passenger!=null){
                String nameV = passenger.getName();
                String idV = passenger.getId();
                String seatNum = passenger.getSeatNumber();
                if(nameV!=null && idV!=null && seatNum!=null){
                    noOfPassengersInQueue = noOfPassengersInQueue +1;
                }
            }
        }
        return noOfPassengersInQueue;
    }

    public Passenger getDetailsOfPassengersInQueue(int i){
        Passenger details=null;
        if(queueArray[i]!=null){
            if(queueArray[i].getName()!=null && queueArray[i].getId()!=null && queueArray[i].getSeatNumber()!=null){
                details  = queueArray[i];
            }
        }
        return details;
    }

    public void assignSecondsInQueue(int delayTime){
        for(int i=0; i<42; i++){
            if(queueArray[i]!=null){
                if(queueArray[i].getName()!=null && queueArray[i].getId()!=null && queueArray[i].getSeatNumber()!=null && queueArray[i].getSecondsInQueue() != 1000000000){
                    int newTime = queueArray[i].getSecondsInQueue() + delayTime;
                    queueArray[i].setSecondsInQueue(newTime);
                }
            }
        }
    }

    public void deleteSeatForRun(String seatNoEnteredByUser){
        int position = 0;
        ArrayList<Passenger> otherPassengerData = new ArrayList<>();
        String name="", seatNum="", id="";
        boolean isFound =  false;
        for(int i=0; i<42; i++){
            if(queueArray[i]!=null){
                name = queueArray[i].getName();
                seatNum = queueArray[i].getSeatNumber();
                id = queueArray[i].getId();
                if(seatNum.equals(seatNoEnteredByUser)) {
                    position = i;
                    isFound =  true;
                    break;
                }
            }
        }

        if(isFound){
            for(int j=0; j<42; j++){
                if (j != position) {
                    otherPassengerData.add(queueArray[j]);
                }
                remove();
            }

            for(int k=0; k<42; k++){
                if(k<otherPassengerData.size()){
                    queueArray[k] = otherPassengerData.get(k);
                } else {
                    queueArray[k] = null;
                }
            }
        } else {
            System.out.println("No record was found for the data entered");
        }
    }
}

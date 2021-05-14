import com.mongodb.BasicDBObject;
import org.bson.Document;
import com.mongodb.client.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.TimeUnit;

public class TrainStation extends Application {
    public static void main(String[] args) {
        Application.launch();
    }

    public void start(Stage primaryStage){
        Scanner optionScanner = new Scanner(System.in);
        //to keep track of what seats are booked or not
        String[] no_Of_Seats = {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
        String[] trainQueueSeats = {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
        HashMap<String, String> customerNameInfo = new HashMap<>();                  //stores the passengers name and id
        HashMap<String, String> customerSeatInfo = new HashMap<>();                  //stores the passengers seat number and id
        ArrayList<String> checkInSeats = new ArrayList<>();
        int positionOfPassengersInWaitingRoom=0;
        System.out.println("                            ----Welcome to the Denuwara Menike seat booking system of the A/C compartment----");
        System.out.println("In this program the stored data for a particular train on a particular date is loaded and the checked in passengers are taken fraom the awaiting room to thir seats");
        System.out.println();
        String collectionName="";
        boolean loop1 = true;
        while (loop1) {
            String[] arr = trainDateDirection();               //calls the method which has the date picker
            collectionName = arr[1];
            int count = loadData(customerNameInfo, customerSeatInfo, no_Of_Seats, collectionName);      //calls the method to load the data from the database
            if (count > 0) {                        //checks if there is any data saved for the date and the train that have been chosen
                loop1 = false;
                System.out.println("The data have been extracted from the database successfully.");
            } else { System.out.println("No data have been saved on the date you entered. Try again"); }
        }
        Passenger[] waitingRoom = new Passenger[42];
        int waitingRoomPosition=0;
        checkInAndTransferToWaitingRoom(no_Of_Seats, waitingRoom, customerNameInfo, customerSeatInfo, waitingRoomPosition, checkInSeats);    //calls the method that checks if the the passengers have checked in or not
        PassengerQueue trainQueue = new PassengerQueue();
        //----------------------------------------the main menu of the program starts here---------------------------------------//
        boolean loop = true;
        while (loop) {
            System.out.println("A,a-add a customer to a seat\nV,v-view the seating arrangements\nQ,q-quit the program\nD,d-delete customer from a seat\nL,l-load program data from database\nS,s-store program data in to database");
            System.out.print("Please choose an option: ");
            String option = optionScanner.nextLine();
            option = option.toUpperCase();
            switch (option) {
                case "A":
                case "a":
                    addPassengerToTrainQueue(trainQueue, waitingRoom, positionOfPassengersInWaitingRoom, checkInSeats, trainQueueSeats);
                    break;
                case "V":
                case "v":
                    viewTrainQueue(trainQueue, no_Of_Seats, trainQueueSeats);
                    break;
                case "D":
                case "d":
                    deletePassengerFromTrainQueue(trainQueue);
                    break;
                case "S":
                case "s":
                    storeTrainQueueData(trainQueue, waitingRoom, collectionName);
                    break;
                case "L":
                case "l":
                    loadTrainQueueData(trainQueue, waitingRoom, collectionName);
                    break;
                case "R":
                case "r":
                    runStimulationAndProduceReport(trainQueue);
                    break;
                case "Q":
                case "q":
                    System.out.println("Your quiting the program. Hope to see you soon.");
                    loop = false;
                    break;

                default:
                    System.out.println("Invalid option entered.");
                    System.out.println("");
            }
            System.out.println("");
        }
    }

    public static String[] trainDateDirection(){
        Scanner optionScanner = new Scanner(System.in);
        System.out.println("Do you want to load the booking details from the train Colombo to Badulla or from Badulla to Colombo.\n  1-Colombo to Badulla\n  2-Badulla to Colombo");
        boolean isOption=true;
        String destination="";
        String direction="";
        String collectionName="";
        String date;
        String[] train = new String[2];
        while(isOption){
            System.out.print("Enter the either '1' or '2': ");                  //the user enter whether he is going from Colombo to Badulla or from Badulla to Colombo
            destination=optionScanner.nextLine();
            if (destination.equals("1") || destination.equals("2")){                          // validating the user input
                isOption = false;
                if(destination.equals("1") ){
                    direction="You have selected the train from Colombo to Badulla on ";
                } else {
                    direction="You have selected the train from Badulla to Colombo on ";
                }
            } else {
                System.out.println("You have entered a wrong data. Please enter either '1' or '2':  ");
            }
        }

        boolean isOption1=true;
        while(isOption1) {
            Pane root = new Pane();
            root.setStyle("-fx-background-color:LightGray");
            Label lbl = new Label();
            Label lbl1 = new Label("Select the date");
            lbl1.setLayoutX(30);
            lbl1.setLayoutY(5);
            lbl1.setFont(new Font("Arial Black", 14));
            Button btn = new Button("QUIT");
            btn.setLayoutX(250);
            btn.setLayoutY(350);
            btn.setStyle("-fx-background-color:red; -fx-text-fill:white; ");
            btn.setFont(new Font("Arial Black", 16));
            DatePicker datePicker = new DatePicker();
            datePicker.setLayoutX(30);
            datePicker.setLayoutY(45);
            lbl.setLayoutX(30);
            lbl.setLayoutY(45);
            EventHandler<ActionEvent> event = e -> {                         //set on action happens as soon as the user press the date picker
                LocalDate i = datePicker.getValue();                          //the value from the date picker is stored in a label
                lbl.setText(i.toString());
            };
            datePicker.setShowWeekNumbers(true);
            datePicker.setOnAction(event);
            btn.setOnAction(event1 -> {
                Stage currentStage = (Stage) lbl.getScene().getWindow();
                currentStage.close();                                             //closes the GUI
            });
            root.getChildren().add(btn);
            root.getChildren().add(lbl);
            root.getChildren().add(lbl1);
            root.getChildren().add(datePicker);
            Scene scene = new Scene(root, 360, 400);
            Stage primaryStage = new Stage();
            primaryStage.setScene(scene);
            primaryStage.showAndWait();

            date = lbl.getText();
            LocalDate generatedDate = LocalDate.now();
            if (date.compareTo(generatedDate.toString()) > 0) {                 //checking if the date is correct(if the user has entered an old date)
                collectionName = date + "/" + destination;                     //creating the collection name for the database
                System.out.println(direction + date);
                isOption1=false;
                Stage currentStage = (Stage) lbl.getScene().getWindow();
                currentStage.close();
            } else {
                System.out.println("You have entered a wrong date. Please enter again ");
            }
            train[0] = date;
            train[1] = collectionName;
        }
        return train;
    }

    public static int loadData(HashMap<String, String> customerNameInfo, HashMap<String, String> customerSeatInfo, String[] no_Of_Seats, String collectionName) {
        int count = 0;
        try {
            MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase database = mongoClient.getDatabase("trainSeatBooking");                    //gets the database
            MongoCollection<Document> collection = database.getCollection(collectionName);               //get the collection
            FindIterable<Document> data = collection.find();
            for (Document record : data) {                                  //for the number of documents in the database the loop iterates
                for (int i = 1; i <= 42; i++) {
                    int seatNum = record.getInteger("seatNumber");
                    if (seatNum == i) {                                      //if the condition satidfiess the data in the record is put to the two hash maps
                        String id = record.getString("customerId");
                        no_Of_Seats[i - 1] = "1";
                        String name = record.getString("customerName");
                        String num = Integer.toString(seatNum);
                        customerSeatInfo.put(num, id);
                        customerNameInfo.put(id, name);
                        count = count + 1;
                    }
                }
            }
            System.out.println("The database and the collection was found.");
        } catch (Exception e) {
            System.out.println("An error occurred");
        }
        return count;
    }

    public static void checkInAndTransferToWaitingRoom(String[] no_Of_Seats, Passenger[] waitingRoom, HashMap<String, String> customerNameInfo, HashMap<String, String> customerSeatInfo, int waitingRoomPosition, ArrayList<String> checkInSeats){
        for (int i = 0; i < waitingRoom.length; i++) {
            waitingRoom[i] = new Passenger();
        }
        AtomicBoolean isOption1= new AtomicBoolean(true);
        while(isOption1.get()) {
            Pane root = new Pane();
            root.setStyle("-fx-background-color:DimGray");
            Label lbl = new Label("WELCOME TO THE A/C COMPARTMENT IN DENUWARA MENIKE TRAIN");
            lbl.setFont(new Font("Arial Black", 20));
            lbl.setLayoutX(150);
            lbl.setLayoutY(50);
            Button button = new Button("QUIT");
            button.setLayoutX(700);
            button.setLayoutY(680);
            button.setFont(new Font("Arial Black", 16));
            Button confirmCheckIn = new Button("CONFIRM CHECK IN");
            confirmCheckIn.setLayoutX(650);
            confirmCheckIn.setLayoutY(350);
            confirmCheckIn.setFont(new Font("Arial Black", 14));
            Label lbl1 = new Label("As the passengers check in \n press their relevant seat.\n After all the passengers have \n checked in press the button to \n confirm the check in process.");
            lbl1.setFont(new Font("Arial Black", 16));
            lbl1.setLayoutX(650);
            lbl1.setLayoutY(200);
            int[] layoutX = {150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500};
            int[] layoutY = {150, 150, 200, 200, 250, 250, 300, 300, 350, 350, 400, 400, 450, 450, 500, 500, 550, 550, 600, 600, 150, 150, 200, 200, 250, 250, 300, 300, 350, 350, 400, 400, 450, 450, 500, 500, 550, 550, 600, 600, 650, 650};
            for (int i = 0; i <42; i++) {               //creates the 42 buttons for the train seats
                Button btn = new Button();
                btn.setLayoutX(layoutX[i]);
                btn.setLayoutY(layoutY[i]);
                int num1 = i;
                int num = i + 1;
                String buttonName = Integer.toString(num);
                btn.setText(buttonName);
                root.getChildren().add(btn);
                if(no_Of_Seats[i].equals("0")){                                         //if no one has booked the seat it disables the seat
                    btn.setStyle("-fx-background-color:black; -fx-text-fill:white; ");
                    btn.setText("X");
                    btn.setDisable(true);
                }
                btn.setOnAction(event -> {                                //when a button is clicked to be booked the set on action occurs
                    btn.setStyle("-fx-background-color:DarkBlue; -fx-text-fill:white; ");                //the seat color is changed
                    no_Of_Seats[num1] = "1";                                              //necessary data are stored
                    checkInSeats.add(buttonName);
                    btn.setDisable(true);
                });
            }

            confirmCheckIn.setOnAction(event -> {           //when the confirm button is pressed the objects are created and then put to the waiting room array
                int count=waitingRoomPosition;
                for (String i : checkInSeats) {
                    for (String seatNum : customerSeatInfo.keySet()) {
                        if (i.equals(seatNum)){
                            String id2 = customerSeatInfo.get(seatNum);
                            for (String id1 : customerNameInfo.keySet()) {
                                if (id1.equals(id2)) {
                                    String name = customerNameInfo.get(id1);
                                    waitingRoom[count].setName(name);
                                    waitingRoom[count].setId(id1);
                                    waitingRoom[count].setSeatNumber(seatNum);
                                    waitingRoom[count].setSecondsInQueue(0);
                                    count=count+1;
                                }
                            }
                        }
                    }
                }
            });

            button.setOnAction(event -> {
                isOption1.set(false);
                Stage currentStage = (Stage) lbl.getScene().getWindow();
                currentStage.close();                              //closes the GUI opened
            });

            root.getChildren().add(button);
            root.getChildren().add(lbl);
            root.getChildren().add(confirmCheckIn);
            root.getChildren().add(lbl1);
            Scene scene = new Scene(root, 1000, 800);
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Adding a customer to a seat");
            primaryStage.setScene(scene);
            primaryStage.showAndWait();

            System.out.println("");
            System.out.println("Passengers in the waiting room: ");                      //prints the details of the passssengers in the waiting room
            int noOfPassengersCheckedIn = 0;
            for (Passenger passenger : waitingRoom) {
                String name = passenger.getName();
                String id = passenger.getId();
                String seatNum = passenger.getSeatNumber();
                int secondsInQueue = passenger.getSecondsInQueue();
                if(name!=null && id!=null && seatNum!=null){
                    noOfPassengersCheckedIn = noOfPassengersCheckedIn +1;
                    System.out.println("Name: " + name + "            id: " + id + "            seatNo: " + seatNum + "            secondsInQueue: " + secondsInQueue);
                }
            }
            System.out.println("Total of " + noOfPassengersCheckedIn + " are there in the waiting room");
            System.out.println("");
        }
    }

    public static void addPassengerToTrainQueue(PassengerQueue trainQueue, Passenger[] waitingRoom, int positionOfPassengersInWaitingRoom, ArrayList<String> checkInSeats, String[] trainQueueSeats){
        int noOfPassengersCheckedIn = 0;                           //counts the number od passengers in the waiting room
        for (Passenger passenger : waitingRoom) {
            if(passenger!=null){
                String name = passenger.getName();
                String id = passenger.getId();
                String seatNum = passenger.getSeatNumber();
                if(name!=null && id!=null && seatNum!=null){
                    noOfPassengersCheckedIn = noOfPassengersCheckedIn +1;
                }
            }
        }
        boolean displayGui = true;     int count=0;
        Random random = new Random();
        int randomNumber = random.nextInt(6) + 1;                      //generates a random number
        System.out.println("The random number generated is : " + randomNumber);
        if(noOfPassengersCheckedIn>0){                      //checks if the waiting room is empty
            for(int i=positionOfPassengersInWaitingRoom; i<checkInSeats.size(); i++){                     //for the no of passengers in the check in array it loops
                if(waitingRoom[i]!=null){                                   //if that particular object is not null it is added to the train queue
                    String name = waitingRoom[i].getName();
                    String id = waitingRoom[i].getId();
                    String seatNum = waitingRoom[i].getSeatNumber();
                    if(name!=null && id!=null && seatNum!=null){
                        trainQueue.add(waitingRoom[i]);
                        waitingRoom[i]=null;
                        count=count+1;
                        trainQueueSeats[Integer.parseInt(seatNum)-1] = "1";
                    }
                    if(count==randomNumber){                //checks if the loop as only gone for the number of times generated by the random number
                        break;
                    }
                }
            }
        } else {
            System.out.println("There are no passengers in the waiting room left all have joined the queue to board the train.");
            displayGui = false;

        }

        if(displayGui){
            Pane root = new Pane();
            root.setStyle("-fx-background-color:LightGrey");
            Label lbl = new Label("WAITING ROOM");
            lbl.setLayoutX(500);
            lbl.setLayoutY(50);
            lbl.setFont(new Font("Arial Black", 16));
            Label lbl1 = new Label("TRAIN QUEUE");
            lbl1.setLayoutX(1300);
            lbl1.setLayoutY(50);
            lbl1.setFont(new Font("Arial Black", 16));
            Label lbl2 = new Label("The random number generated is : " + randomNumber);
            lbl2.setLayoutX(1500);
            lbl2.setLayoutY(300);
            lbl2.setFont(new Font("Arial Black", 16));
            Button button = new Button("QUIT");
            button.setLayoutX(1700);
            button.setLayoutY(700);
            button.setFont(new Font("Arial Black", 16));
            int[] layoutX = {100, 250, 400, 550, 700, 850, 100, 250, 400, 550, 700, 850, 100, 250, 400, 550, 700, 850, 100, 250, 400, 550, 700, 850, 100, 250, 400, 550, 700, 850, 100, 250, 400, 550, 700, 850, 100, 250, 400, 550, 700, 850};
            int[] layoutY = {150, 150, 150, 150, 150, 150, 250, 250, 250, 250, 250, 250, 350, 350, 350, 350, 350, 350, 450, 450, 450, 450, 450, 450, 550, 550, 550, 550, 550, 550, 650, 650, 650, 650, 650, 650, 750, 750, 750, 750, 750, 750};
            int[] layoutYy= {100, 140, 180, 220, 260, 300, 340, 380, 420, 460, 500, 540, 580, 620, 660, 700, 740, 780, 820, 860, 900, 100, 140, 180, 220, 260, 300, 340, 380, 420, 460, 500, 540, 580, 620, 660, 700, 740, 780, 820, 860, 900};
            int[] layoutXx= {1200,1200,1200,1200,1200,1200,1200,1200,1200,1200,1200,1200,1200,1200,1200,1200,1200,1200,1200,1200,1200,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400,1400};
            for (int i = 0; i < 42; i++){               //creates the 42 buttons for the train seats
                Button btn = new Button();
                btn.setLayoutX(layoutX[i]);
                btn.setLayoutY(layoutY[i]);
                btn.setMinWidth(100);
                if(waitingRoom[i] != null){                                  //buttons to display the waiting room
                    btn.setText(waitingRoom[i].getName() + "\n" + waitingRoom[i].getId() + "\n" + waitingRoom[i].getSeatNumber());
                    btn.setStyle("-fx-background-color:LightSeaGreen; -fx-text-fill:black; ");
                } else {
                    btn.setStyle("-fx-background-color:LightSeaGreen;");
                }
                Button btn1 = new Button();                           //buttons to display the train queue
                btn1.setLayoutX(layoutXx[i]);
                btn1.setLayoutY(layoutYy[i]);
                btn1.setStyle("-fx-background-color:LightSeaGreen;");
                btn1.setText(trainQueue.getNameId(i));
                root.getChildren().add(btn);
                root.getChildren().add(btn1);
            }
            button.setOnAction(event -> {
                Stage currentStage = (Stage) lbl.getScene().getWindow();
                currentStage.close();                              //closes the GUI opened
            });
            root.getChildren().add(lbl);
            root.getChildren().add(lbl1);
            root.getChildren().add(lbl2);
            root.getChildren().add(button);
            Scene scene = new Scene(root, 1950, 1000);
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Transfer from waiting room to train queue");
            primaryStage.setScene(scene);
            primaryStage.showAndWait();
        } else {
            System.out.println("------------------NO GUI TO DISPLAY---------------------");
        }
    }

    public static void viewTrainQueue(PassengerQueue trainQueue, String[] no_Of_Seats, String[] trainQueueSeats){
        Pane root = new Pane();
        root.setStyle("-fx-background-color:grey");
        Label lbl = new Label("Welcome to the  seating arrangement of A/C Compartment in Denuwara Menike");
        lbl.setFont(new Font("Arial Black", 18));
        lbl.setLayoutX(130);
        lbl.setLayoutY(50);
        Button button = new Button();
        button.setText("QUIT");
        button.setFont(new Font("Arial Black", 16));
        button.setLayoutX(800);
        button.setLayoutY(680);

        Button button1 = new Button("Name/Seat number");
        button1.setLayoutX(750);
        button1.setLayoutY(250);
        Label lbl1 = new Label("Shows the passenger \n is in the queue");
        lbl1.setFont(new Font("Arial Black", 15));
        lbl1.setLayoutX(950);
        lbl1.setLayoutY(250);

        Button button2 = new Button("Empty");
        button2.setLayoutX(750);
        button2.setLayoutY(325);
        Label lbl2 = new Label("Shows the passenger is in \n the waiting room");
        lbl2.setFont(new Font("Arial Black", 15));
        lbl2.setLayoutX(950);
        lbl2.setLayoutY(325);

        Button button3 = new Button("XXX");
        button3.setLayoutX(750);
        button3.setLayoutY(400);
        button3.setStyle("-fx-background-color:black; -fx-text-fill:white; ");
        button3.setDisable(true);
        Label lbl3 = new Label("No booking has been \n done for that seat");
        lbl3.setFont(new Font("Arial Black", 15));
        lbl3.setLayoutX(950);
        lbl3.setLayoutY(400);

        Button button4 = new Button();
        button4.setLayoutX(750);
        button4.setLayoutY(475);
        Label lbl4 = new Label("A passenger that have been \n deleted from the train queue ");
        lbl4.setFont(new Font("Arial Black", 15));
        lbl4.setLayoutX(950);
        lbl4.setLayoutY(475);

        int[] layoutX = {50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 50, 200, 400, 550, 400, 550, 400, 550, 400, 550, 400, 550, 400, 550, 400, 550, 400, 550, 400, 550, 400, 550, 400, 550};
        int[] layoutY = {150, 150, 200, 200, 250, 250, 300, 300, 350, 350, 400, 400, 450, 450, 500, 500, 550, 550, 600, 600, 150, 150, 200, 200, 250, 250, 300, 300, 350, 350, 400, 400, 450, 450, 500, 500, 550, 550, 600, 600, 650, 650};
        for (int i = 0; i < 42; i++) {          //creates the 42 buttons
            Button btn = new Button();
            btn.setLayoutX(layoutX[i]);
            btn.setLayoutY(layoutY[i]);
            int num = i + 1;
            String buttonName = Integer.toString(num);
            btn.setText(buttonName);
            root.getChildren().add(btn);

            if(no_Of_Seats[i].equals("1")){
                if(trainQueueSeats[i].equals("1")){
                    btn.setText(trainQueue.getNameIdOne(i));
                } else {
                    btn.setText("Empty");
                }

            } else {
                btn.setStyle("-fx-background-color:black; -fx-text-fill:white; ");
                btn.setDisable(true);
                btn.setText("XXX");
            }
        }
        button.setOnAction(event -> {
            Stage currentStage = (Stage) lbl.getScene().getWindow();
            currentStage.close();                        //closes the GUI
        });
        root.getChildren().add(button);
        root.getChildren().add(button1);
        root.getChildren().add(button2);
        root.getChildren().add(button3);
        root.getChildren().add(button4);
        root.getChildren().add(lbl);
        root.getChildren().add(lbl1);
        root.getChildren().add(lbl2);
        root.getChildren().add(lbl3);
        root.getChildren().add(lbl4);
        Scene scene = new Scene(root, 1300, 800);
        Stage primaryStage = new Stage();
        primaryStage.setTitle("View the seat arrangement");
        primaryStage.setScene(scene);
        primaryStage.showAndWait();
    }

    public static void deletePassengerFromTrainQueue(PassengerQueue trainQueue){
        if(trainQueue.isEmpty()){                         //checks if the queue is empty or not
            System.out.print("No passengers have boarded the train queue therefore no record to delete!!!");
            System.out.print("");
        } else {
            Scanner optionScanner = new Scanner(System.in);
            System.out.print("Please enter the seat number of the booking you want to delete: ");     //if it is not empty ask the user the seat number to delete
            String seatNoEnteredByUser = optionScanner.nextLine();
            if(Integer.parseInt(seatNoEnteredByUser)>0 && Integer.parseInt(seatNoEnteredByUser)<43){
                trainQueue.deleteSeat(seatNoEnteredByUser);      //calls the method to delete
            } else {
                System.out.println("Wrong seat number entered try again.");
            }

        }

    }

    public static void storeTrainQueueData(PassengerQueue trainQueue,Passenger[] waitingRoom, String collectionName){
        /*------------------------------------------saves the queue data -------------------------------------------------------------------*/
        String waitingRoomCollectionName = "waitingRoom-" + collectionName;  //creating the collection name
        try{
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("programmingIICw2");          //get the database
        MongoCollection<Document> collection = database.getCollection(waitingRoomCollectionName);           //get the collection
        BasicDBObject row = new BasicDBObject();
        collection.deleteMany(row);
        for(int i=0; i<42; i++){
            String name="", id="", seatNo="";
            if(waitingRoom[i]!=null){
                name = waitingRoom[i].getName();
                id = waitingRoom[i].getId();
                seatNo = waitingRoom[i].getSeatNumber();
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
        /*------------------------------------------saves the queue data ---------------------------------------------------------------------*/
        String queueCollectionName = "queue-" + collectionName;           //creating the collection name
        trainQueue.storingData(queueCollectionName);
    }

    public static void loadTrainQueueData(PassengerQueue trainQueue,Passenger[] waitingRoom, String collectionName){
        //--------------------------------------LOADS THE WAITING ROOM------------------------------------------------------//
        String waitingRoomCollectionName = "waitingRoom-" + collectionName;
        ArrayList<String> name = new ArrayList<>();
        ArrayList<String> id = new ArrayList<>();
        ArrayList<String> seatNo = new ArrayList<>();
        ArrayList<Passenger> passengerObjects = new ArrayList<>();
        int noOfRecords = 0;
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("trainSeatBooking");                    //gets the database
        MongoCollection<Document> collection = database.getCollection(waitingRoomCollectionName);               //get the collection
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
                    waitingRoom[i].setName(name.get(i));
                    waitingRoom[i].setId(id.get(i));
                    waitingRoom[i].setSeatNumber(seatNo.get(i));
                }
            }
        }
        //--------------------------------------LOADS THE TRAIN QUEUE------------------------------------------------------//
        String queueCollectionName = "queue-" + collectionName;
        trainQueue.loadingData(queueCollectionName);

    }

    public static void runStimulationAndProduceReport(PassengerQueue trainQueue){
        //---------------------------------transferring the passenger from train queue to their seat----------------------------------//
        ArrayList<Passenger> boardedDetails = new ArrayList<>();
        Random random = new Random();
        int noOfPassengersInQueue = trainQueue.getNoOfPassengersInQueue();            //calls the method that returns the number of passengers in the train queue
        ArrayList<Integer> delayTimes = new ArrayList<>();
        int count = 1;
        if(noOfPassengersInQueue>0){
            for(int i=0; i<noOfPassengersInQueue; i++) {                                 //for the number of passengers in the generate the delay times and put them into an arraylist
                int randomNumber = random.nextInt(6) + 1;
                int randomNumberOne = random.nextInt(6) + 1;
                int randomNumberTwo = random.nextInt(6) + 1;
                int delayTime = randomNumber + randomNumberOne + randomNumberTwo;
                delayTimes.add(delayTime);
            }

            for(int delayTime:delayTimes){                   //for the arraylist containing the delay times loop it
                try{
                    System.out.println("The " + count + " in the train queue is boarding the train. The passenger has a delay time of " + delayTime + " seconds");
                    TimeUnit.SECONDS.sleep(delayTime);                    //delays the process according to the delay time
                    Passenger details = trainQueue.getDetailsOfPassengersInQueue(0);             //put the information about that seat into a different array
                    boardedDetails.add(details);
                    trainQueue.assignSecondsInQueue(delayTime);                   //change the waiting time of all the other passengers in the train queue
                    if(details!=null){
                        String seatNum = details.getSeatNumber();
                        trainQueue.deleteSeatForRun(seatNum);                        //delete that record
                    }
                } catch (InterruptedException e){
                    System.out.println("An error occurred");
                }
                count = count +1;
            }
            //---------------------------------showing the summary of the stimulation in the file----------------------------------//
            System.out.println("");
            try {
                File file = new File("programmingIIcw2Report.txt");                                   //creating a file and checks whether it already exist or not
                if (file.createNewFile()) {
                    System.out.println("A new file called " + file.getName() + " was created.");
                } else {
                    System.out.println(file.getName() +" already exist.");
                }

                FileWriter line = new FileWriter("programmingIIcw2Report.txt");                                // writing to the file
                line.write("                   Details of the stimulation");
                line.write("\r\n");
                line.write("");
                line.write("The maximum length of the queue: " +  noOfPassengersInQueue);
                int maximumWaitingTime = 0;
                for(int i:delayTimes){
                    maximumWaitingTime = maximumWaitingTime + i;
                }
                line.write("\r\n");
                line.write("The maximum waiting time: " +  maximumWaitingTime + " seconds");
                line.write("\r\n");
                line.write("The minimum waiting time: " +  delayTimes.get(0) + " seconds");
                line.write("\r\n");
                int average = maximumWaitingTime/delayTimes.size();
                line.write("The average waiting time: " +  average);
                line.write("\r\n");
                line.write("");
                for(Passenger details:boardedDetails){
                    if(details!=null){
                        String name = details.getName();
                        String id = details.getId();
                        String seatNo = details.getSeatNumber();
                        int secondsInQueue = details.getSecondsInQueue();
                        line.write("Name: " + name +"        Id: " + id + "        Seat No: "+ seatNo +"        Seconds In Queue: " + secondsInQueue);
                        line.write("\r\n");
                    }
                }
                line.close();
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
            //---------------------------------showing the summary of the stimulation in the GUI----------------------------------//
            Pane root = new Pane();
            root.setStyle("-fx-background-color:grey");
            Label lbl = new Label("DETAILS OF THE STIMULATION");
            lbl.setFont(new Font("Arial Black", 20));
            lbl.setLayoutX(200);
            lbl.setLayoutY(50);
            Button button = new Button();
            button.setText("QUIT");
            button.setFont(new Font("Arial Black", 16));
            button.setLayoutX(850);
            button.setLayoutY(800);

            Label lbl1 = new Label("The maximum length of the queue: " +  noOfPassengersInQueue);
            lbl1.setFont(new Font("Arial Black", 16));
            lbl1.setLayoutX(100);
            lbl1.setLayoutY(150);

            int maximumWaitingTime = 0;
            for(int i:delayTimes){
                maximumWaitingTime = maximumWaitingTime + i;
            }
            Label lbl2 = new Label("The maximum waiting time: " +  maximumWaitingTime + " seconds");
            lbl2.setFont(new Font("Arial Black", 16));
            lbl2.setLayoutX(100);
            lbl2.setLayoutY(200);

            Label lbl3 = new Label("The minimum waiting time: " +  delayTimes.get(0) + " seconds");
            lbl3.setFont(new Font("Arial Black", 16));
            lbl3.setLayoutX(100);
            lbl3.setLayoutY(250);

            int average = maximumWaitingTime/delayTimes.size();
            Label lbl4 = new Label("The average waiting time: " +  average + " seconds");
            lbl4.setFont(new Font("Arial Black", 16));
            lbl4.setLayoutX(100);
            lbl4.setLayoutY(300);

            int yPos = 400;
            for(Passenger details:boardedDetails){
                Label lbl5 = new Label();
                lbl5.setLayoutX(140);
                lbl5.setLayoutY(yPos);
                lbl5.setFont(new Font("Arial Black", 16));
                if(details!=null){
                    String name = details.getName();
                    String id = details.getId();
                    String seatNo = details.getSeatNumber();
                    int secondsInQueue = details.getSecondsInQueue();
                    lbl5.setText("Name: " + name +"        Id: " + id + "        Seat No: "+ seatNo +"        Seconds In Queue: " + secondsInQueue);
                }
                yPos = yPos + 30;
                root.getChildren().add(lbl5);
            }
            button.setOnAction(event -> {
                Stage currentStage = (Stage) lbl.getScene().getWindow();
                currentStage.close();                        //closes the GUI
            });
            root.getChildren().add(button);
            root.getChildren().add(lbl);
            root.getChildren().add(lbl1);
            root.getChildren().add(lbl2);
            root.getChildren().add(lbl3);
            root.getChildren().add(lbl4);
            Scene scene = new Scene(root, 1000, 950);
            Stage primaryStage = new Stage();
            primaryStage.setTitle("REPORT");
            primaryStage.setScene(scene);
            primaryStage.showAndWait();
        } else {
            System.out.println("No passengers in the train queue!");
        }

    }
}

import com.mongodb.BasicDBObject;
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
import org.bson.Document;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class javaExtended2Alteration extends Application {
    public static void main(String[] args) {
        Application.launch();
    }

    public void start(Stage primaryStage){
        final int SEATING_CAPACITY = 42;
        Scanner optionScanner = new Scanner(System.in);
        //to keep track of what seats are booked or not
        String[] no_Of_Seats = {"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0"};
        HashMap<String, String> customerNameInfo = new HashMap<>();                  //stores the passengers name and id
        HashMap<Integer, String> customerSeatInfo = new HashMap<>();                  //stores the passengers seat number and id
        String collectionName;
        //introduction starts here
        System.out.println("                            ----Welcome to the Denuwara Menike seat booking system of the A/C compartment----");
        System.out.println();
        System.out.println("The train from Colombo to Badulla will leave Colombo at 06:45 and reach Badulla at 15:27\nThe train from Badulla to Colombo will leave Badulla at 07:20 and and reach Colombo at 16:03");
        System.out.println();
        String date;
        String[] arr = trainDateDirection();               //calls the method which has the date picker
        date=arr[0];
        collectionName=arr[1];
        System.out.println();
        System.out.println("This system performs many tasks.The tasks are as follows:");
        boolean loop = true;
        //the main menu of the program starts here
        while (loop) {
            System.out.println("A,a-add a customer to a seat\nV,v-view the seating arrangements\nE.e-display the empty seats\nQ,q-quit the program\nD,d-delete customer from a seat\nF,f-find a seat\nO.o-view thw seats that have been ordered alphabetically\nL,l-load program data from database\nS,s-store program data in to database");
            System.out.print("Please choose an option: ");
            String option = optionScanner.nextLine();
            option = option.toUpperCase();
            switch (option) {
                case "A":
                case "a":
                    addSeat(customerNameInfo, no_Of_Seats, customerSeatInfo, SEATING_CAPACITY);
                    break;
                case "V":
                case "v":
                    viewSeatingArrangement(no_Of_Seats, SEATING_CAPACITY);
                    break;
                case "E":
                case "e":
                    displayEmptySeats(no_Of_Seats);
                    break;
                case "D":
                case "d":
                    deleteSeat(customerNameInfo, customerSeatInfo,no_Of_Seats);
                    break;
                case "F":
                case "f":
                    findSeat(customerNameInfo, customerSeatInfo, date);
                    break;
                case "Q":
                case "q":
                    System.out.println("Your quiting the program. Hope to see you soon.");
                    loop = false;
                    break;
                case "O":
                case "o":
                    orderAlphabetically(customerNameInfo, customerSeatInfo);
                    break;
                case "S":
                case "s":
                    storeData(customerNameInfo, customerSeatInfo, collectionName);
                    break;
                case "L":
                case "l":
                    loadData(customerNameInfo, customerSeatInfo, no_Of_Seats, collectionName);
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
        System.out.println("Do you want book the train from Colombo to Badulla or from Badulla to Colombo.\n  1-Colombo to Badulla\n  2-Badulla to Colombo");
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

            //without a button
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

    public static void addSeat(HashMap<String, String> customerNameInfo, String[] no_Of_Seats, HashMap<Integer, String> customerSeatInfo, int SEATING_CAPACITY)  {
        Pane root = new Pane();
        root.setStyle("-fx-background-color:grey");
        Label lbl = new Label("WELCOME TO THE A/C COMPARTMENT IN DENUWARA MENIKE TRAIN");
        lbl.setFont(new Font("Arial Black", 20));
        lbl.setLayoutX(150);
        lbl.setLayoutY(50);
        Button button = new Button("CONFIRM BOOKING");
        button.setLayoutX(700);
        button.setLayoutY(680);
        button.setFont(new Font("Arial Black", 16));
        Button red = new Button("XX");
        red.setLayoutX(650);
        red.setLayoutY(270);
        red.setStyle("-fx-background-color:red; -fx-text-fill:white; ");
        red.setDisable(true);
        Label lbl1 = new Label("Seats are not \n available for booking");
        lbl1.setFont(new Font("Arial Black", 16));
        lbl1.setLayoutX(700);
        lbl1.setLayoutY(270);
        Scanner myObj = new Scanner(System.in);
        System.out.print("Enter your name: ");                //takes the customer name from the user
        String customerName = myObj.nextLine();
        System.out.print("Enter your Id: ");                  //takes the customer id from the user
        String customerId = myObj.nextLine();
        int[] layoutX = {150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500};
        int[] layoutY = {150, 150, 200, 200, 250, 250, 300, 300, 350, 350, 400, 400, 450, 450, 500, 500, 550, 550, 600, 600, 150, 150, 200, 200, 250, 250, 300, 300, 350, 350, 400, 400, 450, 450, 500, 500, 550, 550, 600, 600, 650, 650};
        for (int i = 0; i <SEATING_CAPACITY; i++) {               //creates the 42 buttons for the train seats
            Button btn = new Button();
            btn.setLayoutX(layoutX[i]);
            btn.setLayoutY(layoutY[i]);
            int num1 = i;
            int num = i + 1;
            String buttonName = Integer.toString(num);
            btn.setText(buttonName);
            root.getChildren().add(btn);
            if (no_Of_Seats[i].equals("1")) {                                       //checks if the seats are already booked, if so they are given the color red
                btn.setStyle("-fx-background-color:red; -fx-text-fill:white; ");
                btn.setDisable(true);
            }
            btn.setOnAction(event -> {                                //when a button is clicked to be booked the set on action occurs
                btn.setStyle("-fx-background-color:darkslateblue; -fx-text-fill:white; ");                //the seat color is changed
                no_Of_Seats[num1] = "1";                                              //necessary data are stored
                customerSeatInfo.put(num, customerId);
                customerNameInfo.put(customerId, customerName.toLowerCase());
            });
        }
        button.setOnAction(event -> {
            Stage currentStage = (Stage) lbl.getScene().getWindow();
            currentStage.close();                              //closes the GUI opened
            String seatNum="";
            for (int i : customerSeatInfo.keySet()) {              //loops to see the seats the current customer have booked
                String value=customerSeatInfo.get(i);
                if(value.equals(customerId)){
                    seatNum= seatNum + " " + i;
                }
            }
            System.out.println("You have made a booking under the name " + customerName + "." + "The Id given is " + customerId + " and have booked the following seats " + seatNum);
        });                   //prints the details of the booking

        root.getChildren().add(button);
        root.getChildren().add(lbl);
        root.getChildren().add(red);
        root.getChildren().add(lbl1);
        Scene scene = new Scene(root, 1000, 800);
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Adding a customer to a seat");
        primaryStage.setScene(scene);
        primaryStage.showAndWait();
    }

    public static void viewSeatingArrangement(String[] no_Of_Seats, int SEATING_CAPACITY){
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
        Button red = new Button("Booked seats");
        red.setLayoutX(600);
        red.setLayoutY(250);
        red.setStyle("-fx-background-color:red; -fx-text-fill:white; ");
        red.setDisable(true);
        Label lbl1 = new Label("Seats not \n available for booking");
        lbl1.setFont(new Font("Arial Black", 15));
        lbl1.setLayoutX(750);
        lbl1.setLayoutY(250);
        Button green = new Button("Available seats");
        green.setLayoutX(600);
        green.setLayoutY(325);
        green.setStyle("-fx-background-color:forestgreen; -fx-text-fill:white; ");
        green.setDisable(true);
        Label lbl2 = new Label("Seats available \n for booking");
        lbl2.setFont(new Font("Arial Black", 15));
        lbl2.setLayoutX(750);
        lbl2.setLayoutY(325);
        int[] layoutX = {150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500};
        int[] layoutY = {150, 150, 200, 200, 250, 250, 300, 300, 350, 350, 400, 400, 450, 450, 500, 500, 550, 550, 600, 600, 150, 150, 200, 200, 250, 250, 300, 300, 350, 350, 400, 400, 450, 450, 500, 500, 550, 550, 600, 600, 650, 650};
        for (int i = 0; i < SEATING_CAPACITY; i++) {          //creates the 42 buttons
            Button btn = new Button();
            btn.setLayoutX(layoutX[i]);
            btn.setLayoutY(layoutY[i]);
            int num = i + 1;
            String buttonName = Integer.toString(num);
            btn.setText(buttonName);
            root.getChildren().add(btn);
            if (no_Of_Seats[i].equals("1")) {                     //checks if the seat is booked
                btn.setStyle("-fx-background-color:red; -fx-text-fill:white; ");            //if the seat is booked it is shown in red
            } else {
                btn.setStyle("-fx-background-color:forestgreen; -fx-text-fill:white; ");   //if the seat is not booked it is shown in greem
            }
        }
        button.setOnAction(event -> {
            Stage currentStage = (Stage) lbl.getScene().getWindow();
            currentStage.close();                        //closes the GUI
        });
        root.getChildren().add(red);
        root.getChildren().add(button);
        root.getChildren().add(lbl);
        root.getChildren().add(lbl1);
        root.getChildren().add(lbl2);
        root.getChildren().add(green);
        Scene scene = new Scene(root, 1000, 800);
        Stage primaryStage = new Stage();
        primaryStage.setTitle("View the seat arrangement");
        primaryStage.setScene(scene);
        primaryStage.showAndWait();
    }

    public static void displayEmptySeats(String[] no_Of_Seats){
        Pane root = new Pane();
        root.setStyle("-fx-background-color:grey");
        Label lbl = new Label("Empty seats of the  A/C Compartment in Denuwara Menike");
        lbl.setFont(new Font("Arial Black", 18));
        lbl.setLayoutX(130);
        lbl.setLayoutY(50);
        Button button = new Button();
        button.setText("QUIT");
        button.setLayoutX(600);
        button.setLayoutY(680);
        button.setFont(new Font("Arial Black", 16));
        int[] layoutX = {150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 150, 250, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500, 400, 500};
        int[] layoutY = {150, 150, 200, 200, 250, 250, 300, 300, 350, 350, 400, 400, 450, 450, 500, 500, 550, 550, 600, 600, 150, 150, 200, 200, 250, 250, 300, 300, 350, 350, 400, 400, 450, 450, 500, 500, 550, 550, 600, 600, 650, 650};
        for (int i = 0; i < 42; i++) {     //creates the 42 buttons
            if (no_Of_Seats[i].equals("0")) {              //checks if the button is booked if not it is created
                Button btn = new Button();
                btn.setLayoutX(layoutX[i]);
                btn.setLayoutY(layoutY[i]);
                int num = i + 1;
                String buttonName = Integer.toString(num);
                btn.setText(buttonName);
                root.getChildren().add(btn);
                btn.setStyle("-fx-background-color:forestgreen; -fx-text-fill:white; ");
            }
        }
        button.setOnAction(event -> {
            Stage currentStage = (Stage) lbl.getScene().getWindow();    //closes the GUI
            currentStage.close();
        });

        root.getChildren().add(button);
        root.getChildren().add(lbl);

        Scene scene = new Scene(root, 800, 800);
        Stage primaryStage = new Stage();
        primaryStage.setTitle("Adding a seat");
        primaryStage.setScene(scene);
        primaryStage.showAndWait();
    }

    public static void deleteSeat(HashMap<String, String> customerNameInfo, HashMap<Integer, String> customerSeatInfo, String[] no_Of_Seats){
        Scanner myObj2 = new Scanner(System.in);
        System.out.print("Enter the customer id you gave when doing the booking");
        String customerId = myObj2.nextLine();
        boolean keyFound = false;
        boolean keyFound1 = false;
        String name="";
        for (String i : customerNameInfo.keySet()) {                   //checks if the customer id entered is there in customerNameInfo hashmap
            if (customerId.equals(i)) {
                keyFound = true;
                name=customerNameInfo.get(i);
                break;
            }
        }
        for (String i : customerSeatInfo.values()) {                    //checks if the customer id entered is there in customerSeatInfo hash map
            if (customerId.equals(i)) {
                keyFound1 = true;
                break;
            }
        }
        if (keyFound && keyFound1) {
            System.out.print("Enter either 1 or 2 to do one of these tasks.\n1-cancellation of a single seat\n2-cancellation of the whole booking");
            String option = myObj2.nextLine();
            if(option.equals("1")){                                            //deletes a single seat
                System.out.print("Enter the seat number you want to delete: ");
                int seatNum = myObj2.nextInt();
                boolean found=false;
                for (int i : customerSeatInfo.keySet()){
                    if (i==seatNum ){
                        customerSeatInfo.remove(i);
                        no_Of_Seats[i-1]="0";
                        //prints the deleted record
                        System.out.println("The following record have been deleted: Name-" + name + " customer id-" + customerId + "   seat number-" + i);
                        found=true;
                        break;
                    }
                }
                if(!found){
                    System.out.println("The seat number entered have not been booked by you"); //if the user enter a wrong seat
                }
            } else if(option.equals("2")){                                           //deletes a whole booking made my a customer
                String seats="";
                customerNameInfo.remove(customerId);
                for (int i=1;i<43;i++){
                    if(customerSeatInfo.containsKey(i) && customerSeatInfo.get(i).equals(customerId)){
                        customerSeatInfo.remove(i);
                        customerNameInfo.remove(customerId);
                        no_Of_Seats[i-1]="0";
                        seats=seats + " " + i;
                    }
                }
                System.out.println("The following record have been deleted: Name-" + name + " customer id-" + customerId + "   seat number-" + seats);  //prints the details of what was deleted
            } else {
                System.out.println("You have entered a wrong data");
            }
        } else {
            System.out.println("No booking have been made under the id: " + customerId);                //prints if the given id is wrong
        }
    }

    public static void findSeat(HashMap<String, String> customerNameInfo, HashMap<Integer, String> customerSeatInfo, String date){
        Scanner myObj2 = new Scanner(System.in);
        System.out.print("Enter your name: ");         //asks the user to enter the name
        String name = myObj2.nextLine();
        Scanner myObj3 = new Scanner(System.in);
        System.out.print("Enter your id: ");           //asks the user to enter the id
        String id = myObj3.nextLine();
        boolean keyFound=false;
        StringBuilder seats= new StringBuilder();
        int count =0;
        for(String i:customerNameInfo.keySet()){               //checks if the name and id entered is there in the hash map
            String hashMapName=customerNameInfo.get(i);
            if(hashMapName.equals(name) && id.equals(i)){
                keyFound = true;
                break;
            }
        }
        if(keyFound){                                    //if the name and id is present
            for(int i:customerSeatInfo.keySet()){
                String hashMapId=customerSeatInfo.get(i);
                if(hashMapId.equals(id) ){
                    seats.append(" ").append(i);
                    count=count+1;
                }
            }
            System.out.println("You have made a booking under the name " + name + ", the customer Id given as " + id  +". You have booked the following seats " + seats +" on " +date);
        } else {
            System.out.println("No booking have been made with the customer name " + name + " and id " + id + ".");  //if the name and id entered is not there in the hash map
        }
    }

    public static void orderAlphabetically(HashMap<String, String> customerNameInfo, HashMap<Integer, String> customerSeatInfo) {
        int len = customerNameInfo.size();
        int count2 = len - 1;
        String temp;
        ArrayList<String> customerNameArray = new ArrayList<>();
        for (String i : customerNameInfo.values()) {                  //put the customer names into an array
            customerNameArray.add(i);
        }

        for (int count3 = 0; count3 < len; count3++) {                        //ordering the names alphabetically using bubble sort
            for (int j = 0; j < count2; j++) {
                if (customerNameArray.get(j).compareTo(customerNameArray.get(j + 1)) > 0) {
                    temp = customerNameArray.get(j);
                    customerNameArray.set(j, customerNameArray.get(j + 1));
                    customerNameArray.set(j + 1, temp);
                }
            }
        }
        System.out.println("Customer Details Sorted Alphabetically:");        //prints the records according to the names sorted alphabetically
        String seats="";
        for (String customerName : customerNameArray) {
            for(String id : customerNameInfo.keySet()) {
                String name = customerNameInfo.get(id);
                if (name.equals(customerName)) {
                    System.out.println("Name: " + customerName);
                    System.out.println("Id: " + id);
                    for(int seatNum : customerSeatInfo.keySet()) {
                        String customerId = customerSeatInfo.get(seatNum);
                        if (customerId.equals(id)) {
                            seats=seats + " " + seatNum;
                        }
                    }
                    System.out.println("Seats Booked: " + seats);
                    seats="";
                }
            }
            System.out.println("");
        }
    }

    public static void storeData(HashMap<String, String> customerNameInfo, HashMap<Integer, String> customerSeatInfo, String collectionName){
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("trainSeatBooking");          //get the database
        MongoCollection<Document> collection = database.getCollection(collectionName);           //get the collection
        BasicDBObject row = new BasicDBObject();
        collection.deleteMany(row);
        for(int i:customerSeatInfo.keySet()){                          //stores the data in the two hash maps to the database
            String customerId=customerSeatInfo.get(i);
            for(String j:customerNameInfo.keySet()){
                if(j.equals(customerId)){
                    String customerName=customerNameInfo.get(j);
                    Document document = new Document("seatNumber",i)
                            .append("customerId", customerId)
                            .append("customerName", customerName);
                    collection.insertOne(document);
                }
            }
        }
    }

    public static void loadData(HashMap<String, String> customerNameInfo, HashMap<Integer, String> customerSeatInfo, String[] no_Of_Seats, String collectionName) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("trainSeatBooking");                    //gets the database
        MongoCollection<Document> collection = database.getCollection(collectionName);               //get the collection
        FindIterable<Document> data = collection.find();
        for(Document record:data){                                  //for the number of documents in the database the loop iterates
            for(int i=1; i<=42; i++){
                int seatNum=record.getInteger("seatNumber");
                if(seatNum==i){                                      //if the condition satidfiess the data in the record is put to the two hash maps
                    String id=record.getString("customerId");
                    no_Of_Seats[i-1]="1";
                    String name=record.getString("customerName");
                    customerSeatInfo.put(seatNum,id);
                    customerNameInfo.put(id,name);
                }
            }
        }
    }
}




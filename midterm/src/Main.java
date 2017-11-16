import java.lang.*;
import java.io.*;
import java.util.*;

class Theater{
    // object variable
    private BoxOfficeStaff boxOfficeStaff;
    private CustomerServiceStaff customerServiceStaff;
    private HashMap<Integer, Performance> performanceList;
    private BookingSystem bookingSystem;
    private Integer time;

    // constructor
    public Theater(){
        this.bookingSystem = new BookingSystem(this);
        this.boxOfficeStaff = new BoxOfficeStaff(this, this.bookingSystem);
        this.customerServiceStaff = new CustomerServiceStaff(this, this.bookingSystem);
        this.performanceList = new HashMap<Integer, Performance>();
        this.time = 0;
    }
    // object method
    public void updateTime(Integer _date, Integer _hour){ 
        time = 24 * _date + _hour;
        //System.out.println(_date);
        //System.out.println(_hour);
        bookingSystem.updateTime(_date, _hour, time);
     }
    public void addPerformance(Integer _date, Integer _hour, Integer _numSeat){ 
        Performance performance = new Performance(_date, _hour, _numSeat);
        //System.out.println(_date);
        performanceList.put(_date, performance);
    }
    public BookingSystem getBookingSystem() { return bookingSystem; }
    public HashMap<Integer, Performance> getPerformanceList(){ return performanceList; }
    public BoxOfficeStaff getBoxOfficeStaff(){ return boxOfficeStaff; }
    public CustomerServiceStaff getCustomerServiceStaff(){ return customerServiceStaff; }
}
class BookingSystem{
    // ojbect variable
    private Theater theater;
    private HashMap<Integer, Reservation> reservationList;
    private Integer time;
    private Integer currentReservationNum;
    // constructor
    public BookingSystem(Theater _theater){
        this.theater = _theater;
        this.reservationList = new HashMap<Integer, Reservation>();
        this.currentReservationNum = 1;
    }

    // getter and setter

    // object method
    public void updateTime(Integer _date, Integer _hour, Integer _time){ 
        time = _time;
        //System.out.println(_date);
        //System.out.println(_hour);
        Performance _performance = theater.getPerformanceList().get(_date);
        if(_performance != null){
            cancelNonPickUpReservation(_performance, _hour);
            clearNoShowReservation(_performance, _hour);
        }
    }
    public void cancelNonPickUpReservation(Performance _performance, Integer _hour){
        if(_performance.getHour() - _hour <= 2){
            SortedMap<Integer, String> outputList = new TreeMap<Integer, String>();
            for(Reservation i : reservationList.values() ){
                Performance performance = i.getPerformance();
                if(performance == _performance){
                    if(i.getState() == 0){
                        i.setState(1);
                        Integer numAttendees = i.getNumSeat();
                        performance.addSeat(numAttendees);
                        Integer reservationNum = i.getReservationNum();
                        String name = i.getName();
                        String outStr = "Cancel #"+reservationNum+". name: "+name+", tickets: "+numAttendees;
                        outputList.put(reservationNum, outStr);
                    }
                }
            }
            for(String str : outputList.values()){
                System.out.println(str);
            }
        }
    }
    public void clearNoShowReservation(Performance _performance, Integer _hour){
        if(_performance.getHour() == _hour){
            SortedMap<Integer, String> outputList = new TreeMap<Integer, String>();
            ArrayList<Integer> rmList = new ArrayList<Integer>();
            for(Reservation i : reservationList.values() ){
                Performance performance = i.getPerformance();
                if(performance == _performance){
                    if(i.getState() == 1){
                        Integer numAttendees = i.getNumSeat();
                        performance.addSeat(numAttendees);
                        Integer reservationNum = i.getReservationNum();
                        String name = i.getName();
                        String outStr = "Clear #"+reservationNum+". name: "+name+", tickets: "+numAttendees;
                        rmList.add(reservationNum);
                        outputList.put(reservationNum, outStr);
                    }
                }
            }
            for(int i = 0 ; i < rmList.size() ; ++i)
                reservationList.remove(rmList.get(i));
            for(String str : outputList.values()){
                System.out.println(str);
            }
        }
    }
    
    public void addReservation(Performance _performance, String _name, String _phone, Integer _numSeat){ 
        Reservation reservation = new Reservation(_performance, _name, _phone, _numSeat, currentReservationNum);
        if(_performance.getRemainSeat() - _numSeat >= 0){ //  succesfully
            _performance.subSeat(_numSeat);
            //System.out.println(_peformance.getRemainSeat());
            //System.out.println(_name);
            System.out.println("Reserve #"+currentReservationNum+". name: "+_name+", tickets: "+_numSeat);
            reservationList.put(currentReservationNum, reservation);
            currentReservationNum += 1;
        }
        else{// not enough seat
            System.out.println("Book fail. Not enough seats in requested performance.");
        }
    }
    public void pickUpReservation(Integer reservationNum, String name, String phone){
        if(reservationList.get(reservationNum) == null) //  no such reservation fail
            System.out.println("Pickup fail. No such reservation.");
        else{
            Reservation reservation = reservationList.get(reservationNum);
            // get wrong ID
            if(!(reservation.getName().equals(name) && reservation.getPhone().equals(phone)))
                System.out.println("Pickup fail. Identification check fail.");
            else{
                if(reservation.getState() == 1) // already cancel
                    System.out.println("Pickup fail. Reservation already cancelled.");
                else if(reservation.getState() == 2) // already pickup
                    System.out.println("Pickup fail. Reservation already picked up.");
                else{ // successfully
                    reservation.setState(2);
                    Integer numAttendees = reservation.getNumSeat();
                    System.out.println("Pickup #"+reservationNum+". name: "+name+", tickets: "+numAttendees);
                }
            }
        }
    }
    public void cancelReservation(Integer reservationNum, String name, String phone){
        if(reservationList.get(reservationNum) == null) //  no such reservation fail
            System.out.println("Cancel fail. No such reservation.");
        else{
            Reservation reservation = reservationList.get(reservationNum);
            // get wrong ID
            if(!(reservation.getName().equals(name) && reservation.getPhone().equals(phone)))
                System.out.println("Cancel fail. Identification check fail.");
            else{
                if(reservation.getState() == 1) // already cancel
                    System.out.println("Cancel fail. Reservation already cancelled.");
                else if(reservation.getState() == 2) // already pickup
                    System.out.println("Cancel fail. Reservation already picked up.");
                else{ // successfully
                    reservation.setState(1);
                    Integer numAttendees = reservation.getNumSeat();
                    reservation.getPerformance().addSeat(numAttendees);
                    System.out.println("Cancel #"+reservationNum+". name: "+name+", tickets: "+numAttendees);
                }
            }
        }
    }
}
class Reservation{
    // private variable
    private Performance performance;
    private String name;
    private String phone;
    private Integer numSeat;
    private Integer reservationNum; // order in booking system
    private Integer state; // 0 for nonPickUp, 1 for cancel, 2 for PickUp

    // constructor
    public Reservation(Performance _performance, String _name, String _phone, Integer _numSeat, Integer _reservationNum){
        this.performance = _performance;
        this.name = _name;
        this.phone = _phone;
        this.numSeat = _numSeat;
        this.reservationNum = _reservationNum;
        this.state = 0;
    }

    // object method
    public void setPerformance(Performance _performance) { performance = _performance; }
    public Performance getPerformance() { return performance; }
    public void setState(Integer _state){ state = _state; }
    public Integer getState(){ return state; }
    public String getName(){ return name; }
    public String getPhone(){ return phone; }
    public Integer getNumSeat(){ return numSeat; }
    public Integer getReservationNum(){ return reservationNum; }
}

class Performance{
    // object variable
    private Integer date;
    private Integer hour;
    private Integer numSeat;
    private Integer remainSeat;

    //constructor
    public Performance(Integer _date, Integer _hour, Integer _numSeat){
        this.date = _date;
        this.hour = _hour;
        this.numSeat = _numSeat;
        this.remainSeat = _numSeat;
    }
    
    // object method
    // setter and getter
    public void setDate(Integer _date){ date = _date; }
    public void setHour(Integer _hour){ hour = _hour; }
    public void setNumSeat(Integer _numSeat){ numSeat = _numSeat; }
    public void setRemainSeat(Integer _remainSeat){ remainSeat = _remainSeat; }
    public Integer getDate(){ return date; }
    public Integer getHour(){ return hour; }
    public Integer getNumSeat(){ return numSeat; }
    public Integer getRemainSeat(){ return remainSeat; }
    
    public void addSeat(Integer _seatNum){ remainSeat = remainSeat + _seatNum; }
    public void subSeat(Integer _seatNum){ remainSeat = remainSeat - _seatNum; }

}

class Staff{
    // private var
    protected Theater theater;
    protected BookingSystem bookingSystem;

    // constructor
    public Staff(Theater _theater, BookingSystem _bookingSystem){
        this.theater = _theater;
        this.bookingSystem = _bookingSystem;
    }
}
class BoxOfficeStaff extends Staff{
    //private Theater theater;
    //private BookingSystem bookingSystem;
    public BoxOfficeStaff(Theater _theater, BookingSystem _bookingSystem){
        super(_theater, _bookingSystem);
    }
    public void pickUp(Integer _date, Integer _hour, Integer _reservationNum, String _name, String _phone){
        theater.updateTime(_date, _hour);
        bookingSystem.pickUpReservation(_reservationNum, _name, _phone);
    }
}
class CustomerServiceStaff extends Staff{
    //private Theater theater;
    //private BookingSystem bookingSystem;
    public CustomerServiceStaff(Theater _theater, BookingSystem _bookingSystem){
        super(_theater, _bookingSystem);
    }
    public void book(Integer _date, Integer _hour, String _name, String _phone, Integer _numSeat, Integer _perDate){
        //System.out.println(_date);
        //System.out.println(_hour);
        theater.updateTime(_date, _hour);
        
        Performance _performance  = theater.getPerformanceList().get(_perDate);
        //System.out.println(_performance);
        //System.out.println(_name);
        //System.out.println(_phone);
        //System.out.println(_numSeat);
        bookingSystem.addReservation(_performance, _name, _phone, _numSeat);
    }
    public void cancel(Integer _date, Integer _hour, Integer _reservationNum, String _name, String _phone){
        theater.updateTime(_date, _hour);
        bookingSystem.cancelReservation(_reservationNum, _name, _phone);
    }
}

public class Main{
    public static void main(String[] args){
        File file = new File(args[0]);
        try{
            Scanner sc = new Scanner(file);
            Theater theater = new Theater();
            while(sc.hasNextLine()){
                String[] strTok = sc.nextLine().split(" ");
                if (strTok[0].equals("performance")){
                    Integer date = Integer.parseInt(strTok[1]);
                    Integer hour = Integer.parseInt(strTok[2]);
                    Integer numSeat = Integer.parseInt(strTok[3]);
                    theater.addPerformance(date, hour, numSeat);
                }
                else if(strTok[0].equals("book")){
                    String[] timeTok = strTok[1].split(",");
                    Integer date = Integer.parseInt(timeTok[0]);
                    Integer hour = Integer.parseInt(timeTok[1]);
                    Integer perDate = Integer.parseInt(strTok[2]);
                    String name = strTok[3];
                    String phone = strTok[4];
                    Integer numSeat = Integer.parseInt(strTok[5]);
                    theater.getCustomerServiceStaff().book(date, hour, name, phone, numSeat, perDate);
                }
                else if(strTok[0].equals("cancel")){
                    String[] timeTok = strTok[1].split(",");
                    Integer date = Integer.parseInt(timeTok[0]);
                    Integer hour = Integer.parseInt(timeTok[1]);
                    Integer reservationNum = Integer.parseInt(strTok[2]);
                    String name = strTok[3];
                    String phone = strTok[4];
                    theater.getCustomerServiceStaff().cancel(date, hour, reservationNum, name, phone);
                }
                else if(strTok[0].equals("pickup")){
                    String[] timeTok = strTok[1].split(",");
                    Integer date = Integer.parseInt(timeTok[0]);
                    Integer hour = Integer.parseInt(timeTok[1]);
                    Integer reservationNum = Integer.parseInt(strTok[2]);
                    String name = strTok[3];
                    String phone = strTok[4];
                    theater.getBoxOfficeStaff().pickUp(date, hour, reservationNum, name, phone);
                }
                else if(strTok[0].equals("time")){
                    String[] timeTok = strTok[1].split(",");
                    Integer date = Integer.parseInt(timeTok[0]);
                    Integer hour = Integer.parseInt(timeTok[1]);
                    theater.updateTime(date, hour);
                }

            }
            sc.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }
}
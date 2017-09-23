import java.io.*;
import java.util.Scanner;
import java.util.Collections;
import java.util.ArrayList;

class Pair{
    // object variable
    private int time;
    private String status;
    // constructor
    public Pair(int _time, String _status){
        this.time = _time;
        this.status = _status;
    }
    int getTime(){ return time; }
    String getStatus(){ return status; }
    void setTime(int _time){ this.time = _time; }
    void setStatus(String _status){ this.status = _status; }
}
class Sensor{
    // object variable
    private String type;
    private String name;
    private ArrayList<Integer> data;
    private int lowerBound;
    private int upperBound;
    private ArrayList<Integer> warningList;

    // class variable
    private static int period;

    // constructor
    public Sensor(String _type, String _name, ArrayList<Integer> _data, int _lowerBound, int _upperBound, ArrayList<Integer> _warningList){
        this.type = _type;
        this.name = _name;
        this.data = _data;
        this.lowerBound = _lowerBound;
        this.upperBound = _upperBound;
        this.warningList = _warningList;
    }
    // copy constructor
    public Sensor(Sensor _sensor){
        this.type = _sensor.getType();
        this.name = _sensor.getName();
        this.data = _sensor.getData();
        this.lowerBound = _sensor.getLowerBound();
        this.upperBound = _sensor.getUpperBound();
        this.warningList = _sensor.getWarningList();
    }

    // destructor

    // object method
    void setType(String _type){this.type = _type;}
    void setName(String _name){this.name = _name;}
    void setData(ArrayList<Integer> _data){this.data = _data;}
    void setLowerBound(int _lowerBound){this.lowerBound = _lowerBound;}
    void setUpperBound(int _upperBound){this.upperBound = _upperBound;}
    void setWarningList(ArrayList<Integer> _warningList){this.warningList = _warningList;}
    String getType(){return type;}
    String getName(){return name;}
    ArrayList<Integer> getData(){return data;}
    int getLowerBound(){return lowerBound;}
    int getUpperBound(){return upperBound;}
    ArrayList<Integer> getWarningList(){return warningList;}

    // class method
    static void setPeriod(int _period){period = _period; }

}

class Patient{
    // object variable
    private ArrayList<Sensor> sensorList;
    private String name;
    private int freq;

    // class variable
    private static ArrayList<Patient> patientList;

    // constructor
    public Patient( ArrayList<Sensor> _sensorlist, String _name, int _freq){
        this.sensorList = _sensorlist;
        this.name = _name;
        this.freq = _freq;
    }

    // copy constructor
    public Patient(Patient _patient){
        this.sensorList = _patient.getSensorList();
        this.name = _patient.getName();
        this.freq = _patient.getFreq();
    }
    // destructor

    // object member
    void setSensorList(ArrayList<Sensor> _sensorList){ this.sensorList = _sensorList;}
    void addSensor(Sensor _sensor){ this.sensorList.add(_sensor); }
    void setName(String _name){ this.name = _name; }
    void setFreq(int _freq){ this.freq = _freq; }
    ArrayList<Sensor> getSensorList() {return sensorList; }
    String getName() { return name; }
    int getFreq() { return freq; }

    // class method
    static void addPatient(Patient _patient){ patientList.add(_patient); }
    static ArrayList<Patient> getPatientList() {return patientList; }

}

public class Quiz{
    public static void main(String[] args){
        // File Parsing
        File file = new File(args[0]);
        int period = 0;
        ArrayList<Patient> patientList = new ArrayList<Patient>();
        try{
            Scanner sc = new Scanner(file);
            String strFirst = sc.nextLine();
            period = Integer.parseInt(strFirst);
            // set class variable
            Sensor.setPeriod(period);

            // add Sensor & Patient
            while(sc.hasNextLine()){
                String str = sc.nextLine();
                String [] strTok = str.split(" ");

                // new Patient
                if(strTok.length == 3){
                    String patientName = strTok[1];
                    int patientFreq = Integer.parseInt(strTok[2]);
                    //System.out.println(patientFreq);
                    ArrayList<Sensor> sensorList = new ArrayList<Sensor>();
                    Patient p1 = new Patient( sensorList, patientName, patientFreq);
                    patientList.add(p1);
                    //Patient.addPatient(p1);
                }
                // new device
                else if(strTok.length == 5){
                    String deviceType = strTok[0];
                    String deviceName = strTok[1];
                    String dataFileName = strTok[2];
                    int deviceLowerBound = Integer.parseInt(strTok[3]);
                    int deviceUpperBound = Integer.parseInt(strTok[4]);
                    Patient patient = patientList.get(patientList.size() - 1);
                    // parse data from dataFile
                    File deviceDataFile = new File(dataFileName);
                    ArrayList<Integer> data = new ArrayList<Integer>(); 
                    ArrayList<Integer> warning = new ArrayList<Integer>();
                    try{
                        Scanner sc2 = new Scanner(deviceDataFile);
                        while(sc2.hasNextLine() ){
                            if(patient.getFreq() * data.size() > period)
                                break;
                            String str2 = sc2.nextLine();
                            Integer dataNum = Integer.parseInt(str2);
                            data.add(dataNum);
                        }
                        while(data.size() *  patient.getFreq() <= period){
                            data.add(-1);
                        }
                    }
                    catch(FileNotFoundException e2){
                        e2.printStackTrace();
                    }
                    for(int i = 0 ; i < data.size() ; ++i){
                        if(data.get(i) == -1)
                            warning.add(0);
                        else if(data.get(i) > deviceUpperBound || data.get(i) < deviceLowerBound)
                            warning.add(1);
                        else
                            warning.add(2);
                    }
                    //for(int i = 0 ; i < warning.size() ; ++i)
                    //    System.out.println(warning.get(i));
                    // add a new sensor
                    Sensor s1 = new Sensor(deviceType, deviceName, data, deviceLowerBound, deviceUpperBound, warning);
                    // add sensor to current patient
                    patient.addSensor(s1);
                }
            }
            sc.close();
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        
        // Output File
        // first part output(sorting by time)
        // sorting
        ArrayList<Pair> pairList = new ArrayList<Pair>();
        for(int i = 0 ; i < patientList.size() ; ++i){
            Patient p = patientList.get(i);
            ArrayList<Sensor> sensorList = p.getSensorList();
            for(int j = 0 ; j < sensorList.size() ; ++j){
                Sensor s = sensorList.get(j);
                ArrayList<Integer> data = s.getData();
                ArrayList<Integer> wList = s.getWarningList();
                for(int k = 0 ; k < wList.size() ; ++k){
                    int time = k * p.getFreq();
                    String status = p.getName() + " " + s.getName() + " " + wList.get(k) + " " + data.get(k);
                    Pair pair = new Pair(time, status);
                    pairList.add(pair); 
                }
            }
        }
        for(int i = 0 ; i < pairList.size() - 1 ; ++i){
            for(int j = 0 ; j < pairList.size() - 1 - i; ++j){
                if(pairList.get(j).getTime() > pairList.get(j+1).getTime())
                    Collections.swap(pairList, j, j+1);
            } 
        }

        //for(int i = 0 ; i < pairList.size() ; ++i)
        //    System.out.println(pairList.get(i).getTime() + " " + pairList.get(i).getStatus());
        
        // print out part
        for(int i = 0 ; i < pairList.size() ; ++i){
            Pair pair = pairList.get(i);
            int time = pair.getTime();
            String status = pair.getStatus();
            String [] strTok = status.split(" ");
            String name = strTok[0];
            String sensorName = strTok[1];
            int warning = Integer.parseInt(strTok[2]);
            int data = Integer.parseInt(strTok[3]);
            if(warning == 1){
                System.out.println("[" + time + "] " + name + " is in danger! Cause: " + sensorName + " " + data);
            }
            else if(warning == 0){
                System.out.println("[" + time + "] " + sensorName + " falls");
            }
        }
        
        // second part output
        for(int i = 0 ; i < patientList.size() ; ++i){
            Patient p = patientList.get(i);
            System.out.println("patient " + p.getName());
            ArrayList<Sensor> sensorList = p.getSensorList();
            for(int j = 0 ; j < sensorList.size() ; ++j){
                Sensor s = sensorList.get(j);
                System.out.println(s.getType() + " " + s.getName());
                ArrayList d = s.getData();
                for(int k = 0 ; k < d.size() ; ++k){
                    int time = k * p.getFreq() ;
                    System.out.println("["+ time +"] "+ d.get(k));
                }
            }
        }
    }
}
import java.io.*;
import java.util.*;


public class Quiz{
    public static void main(String[] args){
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
                    ArrayList<Double> data = new ArrayList<Double>(); 
                    ArrayList<Integer> warning = new ArrayList<Integer>();
                    try{
                        Scanner sc2 = new Scanner(deviceDataFile);
                        while(sc2.hasNextLine() ){
                            if(patient.getFreq() * data.size() > period)
                                break;
                            String str2 = sc2.nextLine();
                            Double dataNum = Double.parseDouble(str2);
                            data.add(dataNum);
                        }
                        while(data.size() *  patient.getFreq() <= period){
                            data.add(-1.0);
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
                    if(deviceType == "BloodPressureSensor"){
                        BloodPressureSensor bps1 = new BloodPressureSensor(deviceName, data, deviceLowerBound, deviceUpperBound, warning);
                        patient.addSensor(bps1);
                    }
                    else if(deviceType == "PulseSensor"){
                        PulseSensor ps1 = new PulseSensor(deviceName, data, deviceLowerBound, deviceUpperBound, warning);
                        patient.addSensor(ps1);
                    }
                    else if(deviceType == "TemperatureSensor"){
                        TemperatureSensor ts1 = new TemperatureSensor(deviceName, data, deviceLowerBound, deviceUpperBound, warning);
                        patient.addSensor(ts1);
                    }
                    else{
                        Sensor s1 = new Sensor(deviceType, deviceName, data, deviceLowerBound, deviceUpperBound, warning);
                        // add sensor to current patient
                        patient.addSensor(s1);
                    }
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
                ArrayList<Double> data = s.getData();
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
            Double data = Double.parseDouble(strTok[3]);
            if(warning == 1){
                //outFile.write("[" + time + "] " + name + " is in danger! Cause: " + sensorName + " " + data+"\n");                
                System.out.println("[" + time + "] " + name + " is in danger! Cause: " + sensorName + " " + data);
            }
            else if(warning == 0){
                System.out.println("[" + time + "] " + sensorName + " falls");
                //outFile.write("[" + time + "] " + sensorName + " falls\n");
            }
        }
        
        // second part output
        for(int i = 0 ; i < patientList.size() ; ++i){
            Patient p = patientList.get(i);
            System.out.println("patient " + p.getName());
            //outFile.write("patient " + p.getName()+"\n");
            ArrayList<Sensor> sensorList = p.getSensorList();
            for(int j = 0 ; j < sensorList.size() ; ++j){
                Sensor s = sensorList.get(j);
                System.out.println(s.getType() + " " + s.getName());
                //outFile.write(s.getType() + " " + s.getName()+"\n");
                ArrayList d = s.getData();
                for(int k = 0 ; k < d.size() ; ++k){
                    int time = k * p.getFreq() ;
                    System.out.println("["+ time +"] "+ d.get(k));
                    //outFile.write("["+ time +"] "+ d.get(k)+"\n");    
                }
            }
        }
        
    }
}

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
    private ArrayList<Double> data;
    private int lowerBound;
    private int upperBound;
    private ArrayList<Integer> warningList;
    
    // class variable
    private static int period;

    // constructor
    public Sensor(String _type, String _name, ArrayList<Double> _data, int _lowerBound, int _upperBound, ArrayList<Integer> _warningList){
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

    // object method
    void setType(String _type){this.type = _type;}
    void setName(String _name){this.name = _name;}
    void setData(ArrayList<Double> _data){this.data = _data;}
    void setLowerBound(int _lowerBound){this.lowerBound = _lowerBound;}
    void setUpperBound(int _upperBound){this.upperBound = _upperBound;}
    void setWarningList(ArrayList<Integer> _warningList){this.warningList = _warningList;}
    
    String getType(){return type;}
    String getName(){return name;}
    ArrayList<Double> getData(){return data;}
    int getLowerBound(){return lowerBound;}
    int getUpperBound(){return upperBound;}
    ArrayList<Integer> getWarningList(){return warningList;}

    // class method
    static void setPeriod(int _period){period = _period; }
    int getPeriod() {return period; }
}

class PulseSensor extends Sensor{
    public PulseSensor(String _name, ArrayList<Double> _data, int _lowerBound, int _upperBound, ArrayList<Integer> _warningList){
        super("PulseSensor", _name, _data, _lowerBound, _upperBound, _warningList);
    }
}

class BloodPressureSensor extends Sensor{
    public BloodPressureSensor(String _name, ArrayList<Double> _data, int _lowerBound, int _upperBound, ArrayList<Integer> _warningList){
        super("BloodPressureSensor", _name, _data, _lowerBound, _upperBound, _warningList);
    }
}

class TemperatureSensor extends Sensor{
    public TemperatureSensor(String _name, ArrayList<Double> _data, int _lowerBound, int _upperBound, ArrayList<Integer> _warningList){
        super("TemperatureSensor", _name, _data, _lowerBound, _upperBound, _warningList);
    }
}

class Patient{
    // object variable
    private ArrayList<Sensor> sensorList;
    private String name;
    private int freq;

    // class variable
    private static ArrayList<Patient> patientList;

    // constructor
    public Patient( ArrayList<Sensor> _sensorList, String _name, int _freq){
        this.sensorList = _sensorList;
        this.name = _name;
        this.freq = _freq;
    }

    // copy constructor
    public Patient(Patient _patient){
        this.sensorList = _patient.getSensorList();
        this.name = _patient.getName();
        this.freq = _patient.getFreq();
    }

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
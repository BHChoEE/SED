import java.util.*;
import java.io.*;
import java.lang.*;
class Wsystem{
    // object variable
    private WeatherData usaWeatherData;
    private WeatherData asiaWeatherData;

    //constructor
    public Wsystem(){
        this.usaWeatherData = new WeatherData();
        this.asiaWeatherData = new WeatherData();
    }

    // object method
    public void update(String area, Float temperature, Float humidity, Float pressure){ 
        if(area.equals("Asia")){
            asiaWeatherData.update(temperature, humidity, pressure);
            asiaWeatherData.wnotify(temperature, humidity, pressure);
        }
        else if(area.equals("US")){
            usaWeatherData.update(temperature, humidity, pressure);
            usaWeatherData.wnotify(temperature, humidity, pressure);            
        }
    }
    public void attach(String area, String displayType){
        if(area.equals("Asia")){
            asiaWeatherData.attach(displayType);
        }
        else if(area.equals("US")){
            usaWeatherData.attach(displayType);
        }
    }
    public void detach(String area, String displayType){
        if(area.equals("Asia"))
            asiaWeatherData.detach(displayType);
        else if(area.equals("US"))
            usaWeatherData.detach(displayType);
    }

}
class WeatherData{
    // object variable
    private ArrayList<Float> temperatures;
    private ArrayList<Float> humidities;
    private ArrayList<Float> pressures;
    private ArrayList<DisplayElement> displayElements;

    // constructor
    public WeatherData(){
        this.temperatures = new ArrayList<Float>();
        this.humidities = new ArrayList<Float>();
        this.pressures = new ArrayList<Float>();
        this.displayElements = new ArrayList<DisplayElement>();
    }

    // object method
    public void attach(String _displayElement){ 
        if (_displayElement.equals("Current")){
            Float temp = temperatures.get(temperatures.size() - 1);
            Float hum = humidities.get(humidities.size() - 1);
            Float pre = pressures.get(pressures.size() - 1);
            CurrentConditions current = new CurrentConditions(temp, hum, pre);
            displayElements.add(current);
        }
        else if(_displayElement.equals("Statistics")){
            ArrayList<Float> temps = new ArrayList<>(temperatures);
            ArrayList<Float> humis = new ArrayList<>(humidities);
            ArrayList<Float> press = new ArrayList<>(pressures);
            WeatherStatistics stat = new WeatherStatistics(temps, humis, press);
            displayElements.add(stat);
        }
        else if(_displayElement.equals("Forecast")){
            String state;
            if(humidities.isEmpty())
                state = "none";
            else if(humidities.get(humidities.size() - 1) < 0.2)
                state = "sunny";
            else if(humidities.get(humidities.size() - 1) > 0.8)
                state = "rain";
            else
                state = "cloudy";
            SimpleForecast fore = new SimpleForecast(state);
            displayElements.add(fore);
        }
        /*for(int i = 0 ; i < displayElements.size() ; ++i)
            System.out.println(displayElements.get(i).getName());*/
        
    }
    public void detach(String _displayElement){ 
        for(int i = 0 ; i < displayElements.size() ; ++i)
            if(displayElements.get(i).getName().equals(_displayElement))
                displayElements.remove(i);   
    }
    public void update(Float _temperature, Float _humidity, Float _pressure){
        temperatures.add(_temperature);
        humidities.add(_humidity);
        pressures.add(_pressure);
        /*System.out.print("UPDATE \n");
        for(int i = 0 ; i < temperatures.size() ; i++)
            System.out.print(temperatures.get(i) + " ");
        System.out.print("\n");
        for(int i = 0 ; i < humidities.size() ; i++)
            System.out.print(humidities.get(i) + " ");
        System.out.print("\n");
        for(int i = 0 ; i < pressures.size() ; i++)
            System.out.print(pressures.get(i) + " ");
        System.out.print("\n");*/
    }
    public void wnotify(Float _temperature, Float _humidity, Float _pressure){
        for(int i = 0 ; i < displayElements.size() ; ++i){
            displayElements.get(i).update(_temperature, _humidity, _pressure);
        }
        /*System.out.print("NOTIFY \n");
        for(int i = 0 ; i < temperatures.size() ; i++)
            System.out.print(temperatures.get(i) + " ");
        System.out.print("\n");
        for(int i = 0 ; i < humidities.size() ; i++)
            System.out.print(humidities.get(i) + " ");
        System.out.print("\n");
        for(int i = 0 ; i < pressures.size() ; i++)
            System.out.print(pressures.get(i) + " ");
        System.out.print("\n");*/
    }

}
interface DisplayElement{
    public void update(Float _temperature, Float _humidity, Float _pressure);
    public String getName();
}
class CurrentConditions implements DisplayElement{
    // object variable
    private Float temperature;
    private Float humidity;
    private Float pressure;
    private String name;

    // constructor
    public CurrentConditions(Float _temperature, Float _humidity, Float _pressure){
        this.name = "Current";
        this.temperature = _temperature;
        this.humidity = _humidity;
        this.pressure = _pressure;
    }
    // object method
    public void update(Float _temperature, Float _humidity, Float _pressure){
        temperature = _temperature;
        System.out.println("Temperature " + temperature);
        humidity = _humidity;
        System.out.println("Humidity " + humidity);
        pressure = _pressure;
        System.out.println("Pressure " + pressure);
    }
    public String getName(){ return name; }
}
class WeatherStatistics implements DisplayElement{
    // object variabe
    private ArrayList<Float> temps;
    private ArrayList<Float> humis;
    private ArrayList<Float> press;
    private String name;

    // constructor
    public WeatherStatistics(ArrayList<Float>_temperatures, ArrayList<Float> _humidities, ArrayList<Float> _pressures){
        this.name = "Statistics";
        this.temps = _temperatures;
        this.humis = _humidities;
        this.press = _pressures;
    }
    // object method
    public void update(Float _temperature, Float _humidity, Float _pressure){
        temps.add(_temperature);
        System.out.print("Temperature");
        for(int i = 0 ; i < temps.size() ; ++i)
            System.out.print(" " + temps.get(i));
        System.out.print("\n");
        humis.add(_humidity);
        System.out.print("Humidity");
        for(int i = 0 ; i < humis.size() ; ++i)
            System.out.print(" " + humis.get(i));
        System.out.print("\n");
        press.add(_pressure);
        System.out.print("Pressure");
        for(int i = 0 ; i < press.size() ; ++i)
            System.out.print(" " + press.get(i));
        System.out.print("\n");
    }
    public String getName(){ return name; }
}
class SimpleForecast implements DisplayElement{
    // object variable
    private String forecastState;
    private String name;

    // constructor
    public SimpleForecast(String _state){
        this.name = "Forecast";
        this.forecastState = _state;
    }
    // object method
    public void update(Float _temperature, Float _humidity, Float _pressure){
        if( _humidity > 0.8){
            forecastState = "rain";
            System.out.println("Forecast " + forecastState + ".");
        }
        else if( _humidity < 0.2){
            forecastState = "sunny";
            System.out.println("Forecast " + forecastState + ".");
        }
        else{
            forecastState = "cloudy";
            System.out.println("Forecast " + forecastState + ".");
        }
    }
    public String getName(){ return name; }
}

public class Main{
    public static void main(String[] args){
        File file = new File(args[0]);
        Wsystem system = new Wsystem();
        try{
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine()){
                String[] strTok = sc.nextLine().split(" ");
                if(strTok[0].equals("data")){
                    String area = strTok[1];
                    Float temperature = Float.parseFloat(strTok[2]);
                    Float humidity = Float.parseFloat(strTok[3]);
                    Float pressure = Float.parseFloat(strTok[4]);
                    system.update(area, temperature, humidity, pressure);
                }
                else if(strTok[0].equals("attach")){
                    String area = strTok[1];
                    String displayType = strTok[2];
                    system.attach(area, displayType);
                }
                else if(strTok[0].equals("detach")){
                    String area = strTok[1];
                    String displayType = strTok[2];
                    system.detach(area, displayType);
                }
                else
                    System.out.println("INVALID command type!!");
            }
            sc.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }

    }
}
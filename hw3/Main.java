import java.io.*;
import java.util.*;

interface OrderHandlingFunctionality{
    public Order createOrder(Integer orderNum, String goodType, String orderData);
    public void transmitOrder(Integer orderNum);
}
class OnlineShoppingSite implements OrderHandlingFunctionality{
    // constructor
    public OnlineShoppingSite(){
        this.companyMap = new HashMap<String, Company>();
        this.orderMap = new HashMap<Integer, Order>();
    }
    // object method
    public Order createOrder(Integer orderNum, String goodType, String orderData){
        Order order = new Order(goodType);
        if(companyMap.get(goodType) != null){
            //
            switch(companyMap.get(goodType).getExpectOrderFormat()){
                case "CSV":
                    CSV csv = new CSV();
                    csv.addHeader(order);
                    csv.addData(order, orderData);
                    csv.addFooter(order);
                    order.setFormat("CSV");
                    break;
                case "XML":
                    XML xml = new XML();
                    xml.addHeader(order);
                    xml.addData(order, orderData);
                    xml.addFooter(order);
                    order.setFormat("XML");
                    break;
                case "Object":
                    CustomObject object = new CustomObject();
                    object.addHeader(order);
                    object.addData(order, orderData);
                    object.addFooter(order);
                    order.setFormat("Object");
                    break;
            }
            orderMap.put(orderNum, order);
            return order;
        }
        else{
            System.out.println("no company can fulfill such order!");
            return null;
        }
    }
    public void transmitOrder(Integer orderNum){
        if(orderMap.get(orderNum) != null){
            Order order = orderMap.get(orderNum);
            String goodType = order.getGoodType();
            Company company = companyMap.get(goodType);
            company.addGood(goodType, company.getExpectOrderFormat());
            System.out.println(goodType+" company receive order "+orderNum+":");
            System.out.println(order.getPackage());
        }
        else
            System.out.println("no such order!");
    }
    
    public void addCompany(String goodType, Company company){ companyMap.put(goodType, company); }
    public Company getCompany(String goodType){ return companyMap.get(goodType); }
    public HashMap<String, Company> getCompanyMap(){ return companyMap; }
    public HashMap<Integer, Order> getOrderMap(){ return orderMap; }
    public void addOrder(Integer orderNum, Order order){ orderMap.put(orderNum, order); }
    public Order getOrder(Integer orderNum){ return orderMap.get(orderNum); }

    // object variable
    private HashMap<String, Company> companyMap;
    private HashMap<Integer, Order> orderMap;
}
class Company{
    // object variable
    private String expectOrderFormat;
    private HashMap<String, String> goodMap;

    // constructor
    public Company(String format){
        this.expectOrderFormat = format;
        this.goodMap = new HashMap<String, String>();
    }

    // object method
    public void setExpectOrderFormat(String _expectOrderFormat){ expectOrderFormat = _expectOrderFormat; }
    public String getExpectOrderFormat(){ return expectOrderFormat; }
    public HashMap<String, String> getGoodMap() { return goodMap; }
    public void addGood(String goodType, String format){ goodMap.put(goodType, format); }
}
class Order{
    // object variable
    private String header;
    private String data;
    private String footer;
    private String goodType;
    private String format;

    // constructor
    public Order(String _goodType){ this.goodType = _goodType; }

    // object method
    String getHeader(){ return header; }
    String getData(){ return data; }
    String getFooter(){ return footer; }
    String getGoodType(){ return goodType; }
    String getPackage(){ return header + data + footer; }
    String getFormat(){ return format; }
    void setHeader(String _header){ header = _header; }
    void setData(String _data){ data = _data; }
    void setFooter(String _footer){ footer = _footer; }
    void setGoodType(String _goodType){ goodType = _goodType; }
    void setFormat(String _format){ format = _format; }
}
interface FormatWriter{
    public void addHeader(Order order);
    public void addData(Order order, String _data);
    public void addFooter(Order order);
}
class CSV implements FormatWriter{
    // consturctor
    public CSV(){}
    // class method
    public void addHeader(Order order){ order.setHeader("<CSV header>"); }
    public void addData(Order order, String _data){ order.setData(_data); }
    public void addFooter(Order order){ order.setFooter("<CSV footer>"); }
}
class XML implements FormatWriter{
    // consturctor
    public XML(){}
    // class method
    public void addHeader(Order order){ order.setHeader("<XML header>"); }
    public void addData(Order order, String _data){ order.setData(_data); }
    public void addFooter(Order order){ order.setFooter("<XML footer>"); }
}
class CustomObject implements FormatWriter{
    // consturctor
    public CustomObject(){}
    // class method
    public void addHeader(Order order){ order.setHeader("<Object header>"); }
    public void addData(Order order, String _data){ order.setData(_data); }
    public void addFooter(Order order){ order.setFooter("<Object footer>"); }
}

public class Main{
    public static void main(String[] args){
        File file = new File(args[0]);
        OnlineShoppingSite onlineShoppingSite = new OnlineShoppingSite();
        try{
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine()){
                String str = sc.nextLine();
                String[] strTok = str.split(" ");
                // command "company" => create a company and put in site's companyMap
                if(strTok[0].equals("company")){
                    String companyType = strTok[1];
                    String category = strTok[2];
                    Company company = new Company(category);
                    onlineShoppingSite.addCompany(companyType, company);
                }
                // command "order" => create a order and put in site's orderMap
                else if(strTok[0].equals("order")){
                    Integer orderNum = Integer.parseInt(strTok[1]);
                    String goodType = strTok[2];
                    String orderData = strTok[3];
                    Order order = onlineShoppingSite.createOrder(orderNum, goodType, orderData);
                    if (order != null){
                        String category = order.getFormat();
                        System.out.println("order "+orderNum+": "+goodType+" order created in "+category+" format");
                    }
                }
                else if(strTok[0].equals("transmit")){
                    Integer orderNum = Integer.parseInt(strTok[1]);
                    onlineShoppingSite.transmitOrder(orderNum);
                }
            }
            sc.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }
}
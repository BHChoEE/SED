
import java.io.*;
import java.util.*;

class Department{
    // obj variable 
    private ArrayList<MonthReport> monthReportList;
    private String name;

    // obj method
    MonthReport getMonthReport(int m) { return monthReportList.get(m); }
    ArrayList<MonthReport> getYTDChart() {return monthReportList; }
    String getName() {return name; }
    void addMonthReport(MonthReport m) {monthReportList.add(m); }
    // constructor
    public Department(String _name){
        this.name = _name;
        this.monthReportList = new ArrayList<MonthReport>();
    }
}

class MonthReport{
    // obj variable
    private ArrayList<String> productName;
    private ArrayList<Integer> productNum;
    private Integer month;

    // obj method
    String getProductName( int p ) { return productName.get(p); }
    ArrayList<String> getProductNameList() {return productName; }
    Integer getProductNum(int p) { return productNum.get(p); }
    ArrayList<Integer> getProductNumList() { return productNum; }
    Integer getMonth() { return month; }
    void addProductName (String n) { productName.add(n); }
    void addProductNum (Integer n) { productNum.add(n); }
    // constructor
    public MonthReport(Integer _month){
        this.month = _month;
        this.productName = new ArrayList<String>();
        this.productNum = new ArrayList<Integer>();
    }

}

class Store{
    // obj variable
    private ArrayList<Department> departmentList;
    private String name;

    // obj method
    Department getDepartment(int d){ return departmentList.get(d); }
    void addDepartment(Department d) { departmentList.add(d); }
    ArrayList<Department> getDepartmentList() { return departmentList; }
    // constructor
    public Store(String _name){
        this.name = _name;
        this.departmentList = new ArrayList<Department>();
    }
}
public class main{
    // main function
    public static void main(String[] args){
        Store store = new Store("user");
        // read input 
        File file = new File(args[0]);
        try{
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine()){
                String str = sc.nextLine();
                String[] strTok = str.split(" ");               
                if (strTok[0].equals ("department")){
                    String departmentName = strTok[0];
                    //System.out.println(strTok[0]);
                    ArrayList<Department> departmentList = store.getDepartmentList();
                    if(!departmentList.isEmpty()){
                        boolean find = false;
                        // find if same department is constructed
                        for(int i = 0 ; i < departmentList.size() ; ++i){
                            Department department = departmentList.get(i);
                            if(department.getName().equals(strTok[1])){
                                //System.out.println("found a same department");
                                //System.out.println(strTok[1]);
                                find = true;
                                Integer monthNum = Integer.parseInt(strTok[2]);
                                MonthReport monthReport = new MonthReport(monthNum);
                                //System.out.println(monthNum);
                                for(int tok = 3 ; tok < strTok.length ; tok ++){
                                    String[] sliceTok = strTok[tok].split(",");
                                    String productName = sliceTok[0];
                                    Integer productNum = Integer.parseInt(sliceTok[1]);
                                    monthReport.addProductName(productName);
                                    //System.out.println(productName);
                                    monthReport.addProductNum(productNum);
                                    //System.out.println(productNum);
                                }
                                department.addMonthReport(monthReport);
                            }
                        }
                        // construct a new department
                        if(!find){
                            //System.out.println("construct a new department");
                            Department department = new Department(strTok[1]);
                            store.addDepartment(department);
                            Integer monthNum = Integer.parseInt(strTok[2]);
                            MonthReport monthReport = new MonthReport(monthNum);
                            for(int tok = 3 ; tok < strTok.length ; tok ++){
                                String[] sliceTok = strTok[tok].split(",");
                                String productName = sliceTok[0];
                                Integer productNum = Integer.parseInt(sliceTok[1]);
                                monthReport.addProductName(productName);
                                //System.out.println(productName);
                                monthReport.addProductNum(productNum);
                                //System.out.println(productNum);
                            }
                            department.addMonthReport(monthReport);
                        }
                    }// nothing is in departmentList -> consturct one
                    else{
                        //System.out.println("construct a new department and nothing is in store");
                        Department department = new Department(strTok[1]);
                        store.addDepartment(department);
                        //System.out.println(strTok[1]);
                        Integer monthNum = Integer.parseInt(strTok[2]);
                        MonthReport monthReport = new MonthReport(monthNum);
                        for(int tok = 3 ; tok < strTok.length ; tok ++){
                            String[] sliceTok = strTok[tok].split(",");
                            String productName = sliceTok[0];
                            Integer productNum = Integer.parseInt(sliceTok[1]);
                            monthReport.addProductName(productName);
                            //System.out.println(productName);
                            monthReport.addProductNum(productNum);
                            //System.out.println(productNum);
                        }
                        department.addMonthReport(monthReport);
                    }
                }
                // print 
                else if(strTok[0].equals("select")){
                    String departmentName = strTok[1];
                    Integer monthNum = Integer.parseInt(strTok[2]);
                    ArrayList<Department> departmentList = store.getDepartmentList(); 
                    // find the department and print out information
                    for(int i = 0 ; i < departmentList.size() ; ++i){
                        Department department = departmentList.get(i);
                        if(departmentName.equals(department.getName())){
                            ArrayList<MonthReport> monthReportList = department.getYTDChart();
                            boolean find = false;
                            for(int month = 0 ; month < monthReportList.size() ; month ++){
                                MonthReport monthReport = monthReportList.get(month);
                                if (monthReport.getMonth() == monthNum){
                                    find = true;
                                    String firstStr = "display MonthlyReport for " + departmentName + " month " + monthNum;
                                    System.out.println(firstStr);
                                    ArrayList<String> productNameList = monthReport.getProductNameList();
                                    ArrayList<Integer> productNumList = monthReport.getProductNumList();
                                    for(int k = 0 ; k < productNameList.size() ; ++k){
                                        String pStr = productNameList.get(k) + " " + productNumList.get(k);
                                        System.out.println(pStr);
                                    }                                    
                                }
                            }
                            if(find){
                                String secStr = "display YTDSalesChart for " + departmentName;
                                System.out.println(secStr);
                                HashMap<Integer, String> outList = new HashMap<Integer, String>();
                                for(int month = 0 ; month < monthReportList.size() ; month ++){
                                    MonthReport monthReport = monthReportList.get(month);
                                    ArrayList<Integer> productNumList = monthReport.getProductNumList();
                                    Integer sum = 0;
                                    //System.out.println(productNumList.size());
                                    for(int k = 0 ; k < productNumList.size() ; ++k){
                                        sum += productNumList.get(k);
                                        //System.out.println(productNumList.get(k));
                                    }
                                    Integer mon = monthReport.getMonth();
                                    String monthStr = "month "+ mon + " price "+ sum;
                                    outList.put(mon, monthStr);
                                    //System.out.println(monthStr);
                                }
                                for(int month = 1 ; month <= 12 ; month ++){
                                    if(outList.get(month) != null){
                                        System.out.println(outList.get(month));
                                    }
                                }
                            }
                            if(!find)
                                System.out.println("no data in selected month!");
                        }
                    }
                }
            }

            sc.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }

}
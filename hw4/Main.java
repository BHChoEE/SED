import java.io.*;
import java.util.*;
import java.lang.*;

class Composition{
    // object variable
    private HashMap<Integer, Component> componentList;
    private HashMap<String, ComposeMethod> composeMethodList;

    // constructor
    public Composition(){
        this.componentList = new HashMap<Integer, Component>();
        this.composeMethodList = new HashMap<String, ComposeMethod>();
        composeMethodList.put("SimpleComposition", new SimpleComposition());
        composeMethodList.put("TexComposition", new TexComposition());
        composeMethodList.put("ArrayComposition", new ArrayComposition());
    }
    public Composition(Composition composition){
        this.componentList = composition.getComponentList();
        this.composeMethodList = composition.getComposeMethodList();
    }

    // setter & getter
    public HashMap<Integer, Component> getComponentList(){ return componentList; }
    public HashMap<String, ComposeMethod> getComposeMethodList(){ return composeMethodList; }
    public void setComponentList(HashMap<Integer, Component> _componentList){ componentList = _componentList; }
    public void setComposeMethodList(HashMap<String, ComposeMethod> _composeMethodList){ composeMethodList = _composeMethodList; }
    public void addComponent(Integer _id, Component _component) { componentList.put(_id, _component); }
    public void addComposeMethod(String _method, ComposeMethod _composeMethod) { composeMethodList.put(_method, _composeMethod); }

    // object method
    public void createComponent(Integer _id, Integer _naturalSize, Integer _stretchability, Integer _shrinkability, String _content, String _type){
        Component component = new Component(_id, _naturalSize, _stretchability, _shrinkability, _content);
        component.setType(_type);
        componentList.put(_id, component);
    }
    public void removeComponent(Integer _id){
        componentList.remove(_id);
    }

    public void changeSize(Integer id, Integer newSize){
        componentList.get(id).changeSize(newSize);
    }
    public void arrange(String breakStrategy){
        ComposeMethod composeMethod = composeMethodList.get(breakStrategy);
        composeMethod.compose(componentList);
    }

}

class Component{
    // object variable
    private Integer id;
    private Integer naturalSize;
    private Integer stretchability;
    private Integer shrinkability;
    private String content;
    private Integer currentSize;
    private String type;

    // constructor
    public Component(Integer _id, Integer _naturalSize, Integer _stretchability, Integer _shrinkability, String _content){
        this.id = _id;
        this.naturalSize = _naturalSize;
        this.stretchability = _stretchability;
        this.shrinkability = _shrinkability;
        this.content = _content;
        this.currentSize = _naturalSize;
    }

    public Component(Component _component){
        this.id = _component.getId();
        this.naturalSize = _component.getNaturalSize();
        this.stretchability = _component.getStretchability();
        this.shrinkability = _component.getShrinkability();
        this.content = _component.getContent();
        this.currentSize = _component.getNaturalSize();
        this.type = _component.getType();
    }

    // object method
    public void changeSize(Integer newSize){
        if(newSize < shrinkability || stretchability < newSize){
            Integer componentID = id;
            //System.out.println(shrinkability + " " + stretchability + " " + newSize);
            System.out.println("component " + componentID + " failed to change size");
        }
        else{
            currentSize = newSize;
            Integer componentID = id;
            System.out.println("component " + componentID + " size changed to " + newSize);
        }
    }

    // setter & getter
    public Integer getId(){ return id; }
    public Integer getNaturalSize(){ return naturalSize; }
    public Integer getStretchability(){ return stretchability; }
    public Integer getShrinkability(){ return shrinkability; }
    public String getContent(){ return content; }
    public Integer getCurrentSize(){ return currentSize; }
    public String getType(){ return type; }
    public void setId(Integer _id){ id = _id; }
    public void setNaturalSize(Integer _naturalSize){ naturalSize = _naturalSize; }
    public void setStretchability(Integer _stretchability){ stretchability = _stretchability; }
    public void setShrinkability(Integer _shrinkability){ shrinkability = _shrinkability; }
    public void setContent(String _content){ content = _content; }
    public void setCurrentSize(Integer _currentSize){ currentSize = _currentSize; }
    public void setType(String _type){ type = _type; }

}

class Text extends Component{
    public Text(Integer _id, Integer _naturalSize, Integer _stretchability, Integer _shrinkability, String _content){
        super(_id, _naturalSize, _stretchability, _shrinkability, _content);
    }
}

class GraphicalElement extends Component{
    public GraphicalElement(Integer _id, Integer _naturalSize, Integer _stretchability, Integer _shrinkability, String _content){
        super(_id, _naturalSize, _stretchability, _shrinkability, _content);
    }
}

interface ComposeMethod{
    public void compose(HashMap<Integer, Component> componentList );
}

class SimpleComposition implements ComposeMethod{
    // constructor
    public SimpleComposition(){}
    // object method
    public void compose(HashMap<Integer, Component> componentList ){
        for(int i = 0 ; i < componentList.size() ; ++i){
            Component comp = componentList.get(i);
            Integer currentSize = comp.getCurrentSize();
            String content = comp.getContent();
            System.out.println("[" + currentSize + "]" + content);
        }
    }
    
}

class TexComposition implements ComposeMethod{
    // constructor
    public TexComposition() {}
    // object method
    public void compose(HashMap<Integer, Component> componentList ){
        for(int i = 0 ; i < componentList.size() ; ++i){
            Component comp = componentList.get(i);
            Integer currentSize = comp.getCurrentSize();
            String content = comp.getContent();
            if(!content.equals("<ParagraphEnd>"))
                System.out.print("[" + currentSize + "]" + content + " ");
            else
                System.out.println("[" + currentSize + "]" + content);
            
        }
    }
    
}

class ArrayComposition implements ComposeMethod{
    // constructor
    public ArrayComposition() {}
    // object method
    public void compose(HashMap<Integer, Component> componentList ){
        for(int i = 0 ; i < componentList.size() ; ++i){
            Component comp = componentList.get(i);
            Integer currentSize = comp.getCurrentSize();
            String content = comp.getContent();
            if(i % 3 == 2)
                System.out.println("[" + currentSize + "]" + content);
            else
                System.out.print("[" + currentSize + "]" + content + " ");
        }
    }
    
}

public class Main{
    public static void main(String[] args){
        File file = new File(args[0]);
        try{
            Scanner sc = new Scanner(file);
            Composition composition = new Composition();
            while(sc.hasNextLine()){
                String [] strTok = sc.nextLine().split(" ");
                if(strTok[0].equals("Text")){
                    Integer id = Integer.parseInt(strTok[1]);
                    Integer naturalSize = Integer.parseInt(strTok[2]);
                    Integer stretchability = Integer.parseInt(strTok[3]);
                    Integer shrinkability = Integer.parseInt(strTok[4]);
                    String content = strTok[5];
                    composition.createComponent(id, naturalSize, shrinkability, stretchability, content, "Text");
                }
                else if(strTok[0].equals("GraphicalElement")){
                    Integer id = Integer.parseInt(strTok[1]);
                    Integer naturalSize = Integer.parseInt(strTok[2]);
                    Integer stretchability = Integer.parseInt(strTok[3]);
                    Integer shrinkability = Integer.parseInt(strTok[4]);
                    String content = strTok[5];
                    composition.createComponent(id, naturalSize, shrinkability, stretchability, content, "GraphicalElement");
                }
                else if(strTok[0].equals("ChangeSize")){
                    Integer id = Integer.parseInt(strTok[1]);
                    Integer newSize = Integer.parseInt(strTok[2]);
                    composition.changeSize(id, newSize);
                }
                else if(strTok[0].equals("Require")){
                    String breakStrategy = strTok[1];
                    composition.arrange(breakStrategy);
                }
                else
                    System.out.println("Invalid command!!");
            }
            sc.close();
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
    }
}
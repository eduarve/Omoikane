import javafx.application.Application;
import javafx.beans.value.*;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.scene.text.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import com.dooapp.fxform.FXForm;
import javafx.collections.FXCollections;
import com.dooapp.fxform.view.*;

Platform.runLater(new Runnable() {                          
                        @Override
                        public void run() {
                            gui();
                        }
                    });
 def gui() {
     MyBean myBean = new MyBean();
     
     Node fxForm = new FXForm(myBean); 
     fxForm.setSkin(FXFormSkinFactory.INLINE_FACTORY.createSkin(fxForm))
    
     Stage stage = new Stage(); 
     Scene scene = new Scene(new Group(fxForm));            
     stage.setScene(scene); 
     //scene.add(fxForm);
     stage.show();
 }


 private class MyBean {
      private final StringProperty name = new SimpleStringProperty();
      private final ListProperty<TableBean> list = new SimpleListProperty<TableBean>(FXCollections.<TableBean>observableArrayList());
      protected MyBean(String name) {
        this.name.set(name);
        this.list.addAll(new TableBean("Name 1", 99), new TableBean("Name 2", 98));
        
      }
 }
 
 public class TableBean {

    private final StringProperty name = new SimpleStringProperty();

    private final IntegerProperty age = new SimpleIntegerProperty();

    public TableBean(String name, int age) {
        this.name.set(name);
        this.age.set(age);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getAge() {
        return age.get();
    }

    public IntegerProperty ageProperty() {
        return age;
    }

    public void setAge(int age) {
        this.age.set(age);
    }
}
 
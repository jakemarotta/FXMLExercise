package fxmlexercise.inprogress;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SelectorController extends VBox {

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    /*
        @TODO Declare FXML-tagged controls.
     */
    @FXML private TextField topTextField, bottomTextField;

    @FXML private ColorPicker topColorPicker, bottomColorPicker;

    @FXML private Button printButton;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    /*
        @TODO Declare properties and property methods.
     */
    private StringProperty topColorName;
    public StringProperty topColorNameProperty() {
        if (topColorName == null) {
            topColorName = new SimpleStringProperty();
            topColorName.bindBidirectional(topTextField.textProperty());
        }
        return topColorName;
    }
    public String getTopColorName() {
        return topColorNameProperty().get();
    }
    public void setTopColorName(String s) {
        topColorNameProperty().set(s);
    }

    private StringProperty bottomColorName;
    public StringProperty bottomColorNameProperty() {
        if (bottomColorName == null) {
            bottomColorName = new SimpleStringProperty();
            bottomColorName.bindBidirectional(bottomTextField.textProperty());
        }
        return bottomColorName;
    }
    public String getBottomColorName() {
        return bottomColorNameProperty().get();
    }
    public void setBottomColorName(String s) {
        bottomColorNameProperty().set(s);
    }

    private ObjectProperty<Color> topColor;
    public ObjectProperty<Color> topColorProperty() {
        if (topColor == null) {
            topColor = new SimpleObjectProperty<>();
            topColor.bindBidirectional(topColorPicker.valueProperty());
        }
        return topColor;
    }
    public Color getTopColor() {
        return topColorProperty().get();
    }
    public void setTopColor(Color c) {
        topColorProperty().set(c);
    }

    private ObjectProperty<Color> bottomColor;
    public ObjectProperty<Color> bottomColorProperty() {
        if (bottomColor == null) {
            bottomColor = new SimpleObjectProperty<>();
            bottomColor.bindBidirectional(bottomColorPicker.valueProperty());
        }
        return bottomColor;
    }
    public Color getBottomColor() {
        return bottomColorProperty().get();
    }
    public void setBottomColor(Color c) {
        bottomColorProperty().set(c);
    }

    private BooleanProperty textFieldsNotEmpty;
    public final BooleanProperty textFieldsNotEmptyProperty() {

        if (textFieldsNotEmpty == null) {
            textFieldsNotEmpty = new SimpleBooleanProperty();

            textFieldsNotEmpty.bind(Bindings.and(Bindings.notEqual(topTextField.textProperty(), ""),
                                                 Bindings.notEqual(bottomTextField.textProperty(), "")));
        }
        return textFieldsNotEmpty;
    }
    public Boolean textFieldsNotEmpty() {
        return textFieldsNotEmptyProperty().get();
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /*
        @TODO Only one, actually.
     */

    //************************************************//
    //                    METHODS                     //
    //************************************************//

    @FXML
    private void printButtonAction() {

        /*
            @TODO Print the each color's name and value.
         */
        System.out.println(topTextField.getText() + ": " + topColorPicker.getValue());
        System.out.println(bottomTextField.getText() + ": " + bottomColorPicker.getValue());

    }

    @FXML
    protected void initialize() {

        /*
            @TODO Configure disable state logic for "Print" button.
         */
        printButton.disableProperty().bind( Bindings.not( textFieldsNotEmptyProperty() ) );

    }
}

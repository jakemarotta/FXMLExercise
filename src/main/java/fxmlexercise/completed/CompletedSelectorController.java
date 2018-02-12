package fxmlexercise.completed;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.cirdles.commons.util.ResourceExtractor;

import java.io.IOException;

public class CompletedSelectorController extends VBox {

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    /*
        Declare FXML-tagged controls.
     */

    @FXML private TextField topTextField, bottomTextField;

    @FXML private ColorPicker topColorPicker, bottomColorPicker;

    @FXML private Button printButton;

    //**********************************************//
    //                  PROPERTIES                  //
    //**********************************************//

    /*
        Declare properties and property methods.
     */

    private StringProperty topColorName;
    public StringProperty topColorNameProperty() {
        if ( topColorName == null ) {
            topColorName = new SimpleStringProperty();
            topColorName.bindBidirectional( topTextField.textProperty() );
        }
        return topColorName;
    }
    public final String getTopColorName() {
        return topColorNameProperty().get();
    }
    public final void setTopColorName(String s) {
        topColorNameProperty().set(s);
    }

    private StringProperty bottomColorName;
    public StringProperty bottomColorNameProperty() {
        if ( bottomColorName == null ) {
            bottomColorName = new SimpleStringProperty();
            bottomColorName.bindBidirectional( bottomTextField.textProperty() );
        }
        return bottomColorName;
    }
    public final String getBottomColorName() {
        return bottomColorNameProperty().get();
    }
    public final void setBottomColorName(String s) {
        bottomColorNameProperty().set(s);
    }

    private ObjectProperty<Color> topColor;
    public ObjectProperty<Color> topColorProperty() {
        if ( topColor == null ) {
            topColor = new SimpleObjectProperty<>();
            topColor.bindBidirectional( topColorPicker.valueProperty() );
        }
        return topColor;
    }
    public final Color getTopColor() {
        return topColorProperty().get();
    }
    public final void setTopColor(Color c) {
        topColorProperty().set(c);
    }

    private ObjectProperty<Color> bottomColor;
    public ObjectProperty<Color> bottomColorProperty() {
        if ( bottomColor == null ) {
            bottomColor = new SimpleObjectProperty<>();
            bottomColor.bindBidirectional( bottomColorPicker.valueProperty() );
        }
        return bottomColor;
    }
    public final Color getBottomColor() {
        return bottomColorProperty().get();
    }
    public final void setBottomColor(Color c) {
        bottomColorProperty().set(c);
    }

    private BooleanProperty textFieldEmpty;
    public final BooleanProperty textFieldEmptyProperty() {

        if (textFieldEmpty == null ) {
            textFieldEmpty = new SimpleBooleanProperty();

            textFieldEmpty.bind(Bindings.or(Bindings.equal(topTextField.textProperty(), "" ),
                                            Bindings.equal( bottomTextField.textProperty(), "" ) ) );
        }
        return textFieldEmpty;
    }
    public Boolean textFieldEmpty() {
        return textFieldEmptyProperty().get();
    }

    //**********************************************//
    //                 CONSTRUCTORS                 //
    //**********************************************//

    /*
        Only one, actually.
     */
    public CompletedSelectorController() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    new ResourceExtractor(CompletedSelectorController.class).extractResourceAsPath("completed-selector.fxml").toUri().toURL()
            );
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //************************************************//
    //                    METHODS                     //
    //************************************************//

    @FXML private void printButtonAction() {
        /*
            Print the each color's name and value.
         */
        System.out.println( topTextField.getText() + ": " + topColorPicker.getValue() );
        System.out.println( bottomTextField.getText() + ": " + bottomColorPicker.getValue() );
    }

    @FXML protected void initialize() {
        /*
            Configure disable state logic for "Print" button.
         */
        printButton.disableProperty().bind(textFieldEmptyProperty() );
    }
}

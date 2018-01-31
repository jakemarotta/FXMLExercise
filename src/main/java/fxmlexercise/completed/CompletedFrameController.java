package fxmlexercise.completed;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class CompletedFrameController extends VBox {

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML private Label firstLabel, secondLabel, thirdLabel, fourthLabel;

    @FXML private Rectangle firstRect, secondRect, thirdRect, fourthRect;

    @FXML private Button printLeftButton, printBothButton, printRightButton;

    @FXML private CompletedSelectorController leftSelector, rightSelector;

    //***********************************************//
    //                    METHODS                    //
    //***********************************************//

    @FXML private void printButtonAction(ActionEvent e) {

        if ( e.getSource() == printLeftButton ) {
            System.out.println(leftSelector.getTopColorName() + ": " + leftSelector.getTopColor());
            System.out.println(leftSelector.getBottomColorName() + ": " + leftSelector.getBottomColor());
        } else if ( e.getSource() == printRightButton ) {
            System.out.println(rightSelector.getTopColorName() + ": " + rightSelector.getTopColor());
            System.out.println(rightSelector.getBottomColorName() + ": " + rightSelector.getBottomColor());
        } else if ( e.getSource() == printBothButton ){
            System.out.println(leftSelector.getTopColorName() + ": " + leftSelector.getTopColor());
            System.out.println(leftSelector.getBottomColorName() + ": " + leftSelector.getBottomColor());
            System.out.println(rightSelector.getTopColorName() + ": " + rightSelector.getTopColor());
            System.out.println(rightSelector.getBottomColorName() + ": " + rightSelector.getBottomColor());
        }
    }

    @FXML protected void initialize() {

        /*
            Bind controls in this controller to the appropriate properties in the selectors' controllers
         */
        firstLabel.textProperty().bind(leftSelector.topColorNameProperty());
        secondLabel.textProperty().bind(leftSelector.bottomColorNameProperty());
        firstRect.fillProperty().bind(leftSelector.topColorProperty());
        secondRect.fillProperty().bind(leftSelector.bottomColorProperty());

        thirdLabel.textProperty().bind(rightSelector.topColorNameProperty());
        fourthLabel.textProperty().bind(rightSelector.bottomColorNameProperty());
        thirdRect.fillProperty().bind(rightSelector.topColorProperty());
        fourthRect.fillProperty().bind(rightSelector.bottomColorProperty());

        printLeftButton.disableProperty().bind(Bindings.not(leftSelector.validValuesProperty()));
        printRightButton.disableProperty().bind(Bindings.not(rightSelector.validValuesProperty()));
        printBothButton.disableProperty().bind(Bindings.or(Bindings.not(leftSelector.validValuesProperty()),
                                                           Bindings.not(rightSelector.validValuesProperty())));
    }
}

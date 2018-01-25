package fxmlexercise.completed;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class FrameController extends VBox {

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    @FXML private Label firstLabel, secondLabel, thirdLabel, fourthLabel;

    @FXML private Rectangle firstRect, secondRect, thirdRect, fourthRect;

    @FXML private Button printLeftButton, printBothButton, printRightButton;

    @FXML private SelectorController leftSelector, rightSelector;

    //***********************************************//
    //                PUBLIC METHODS                 //
    //***********************************************//

    @FXML public void initialize() {

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

        /*
            The following listeners determine the disable states of all three print buttons.
         */
        leftSelector.topColorNameProperty().addListener(( (observable, oldValue, newValue) -> {

            // if top left TextField is empty
            if ( newValue.equals("") ) {
                printLeftButton.setDisable(true);
                printBothButton.setDisable(true);

            // if left top and bottom TextFields are both not empty
            } else if ( ! leftSelector.getBottomColorName().equals("") ) {
                printLeftButton.setDisable(false);

                // if none of the TextFields are empty
                if ( ! rightSelector.getTopColorName().equals("") && ! rightSelector.getBottomColorName().equals("") ) {
                    printBothButton.setDisable(false);
                }
            }
        }));
        leftSelector.bottomColorNameProperty().addListener(( (observable, oldValue, newValue) -> {

            // if top left TextField is empty
            if ( newValue.equals("") ) {
                printLeftButton.setDisable(true);
                printBothButton.setDisable(true);

                // if left top and bottom TextFields are both not empty
            } else if ( ! leftSelector.getTopColorName().equals("") ) {
                printLeftButton.setDisable(false);

                // if none of the TextFields are empty
                if ( ! rightSelector.getTopColorName().equals("") && ! rightSelector.getBottomColorName().equals("") ) {
                    printBothButton.setDisable(false);
                }
            }
        }));
        rightSelector.topColorNameProperty().addListener(( (observable, oldValue, newValue) -> {

            // if top right TextField is empty
            if ( newValue.equals("") ) {
                printRightButton.setDisable(true);
                printBothButton.setDisable(true);

            // if right top and bottom TextFields are both not empty
            } else if ( ! rightSelector.getBottomColorName().equals("") ) {
                printRightButton.setDisable(false);

                // if none of the TextFields are empty
                if ( ! leftSelector.getTopColorName().equals("") &&
                     ! leftSelector.getBottomColorName().equals("") ) {
                    printBothButton.setDisable(false);
                }
            }
        }));
        rightSelector.bottomColorNameProperty().addListener(( (observable, oldValue, newValue) -> {

            // if top left TextField is empty
            if ( newValue.equals("") ) {
                printRightButton.setDisable(true);
                printBothButton.setDisable(true);

                // if left top and bottom TextFields are both not empty
            } else if ( ! rightSelector.getTopColorName().equals("") ) {
                printRightButton.setDisable(false);

                // if none of the TextFields are empty
                if ( ! leftSelector.getTopColorName().equals("") &&
                     ! leftSelector.getBottomColorName().equals("")) {
                    printBothButton.setDisable(false);
                }
            }
        }));

        /*
            Setting the initial disable state of the three print buttons.
         */
        printLeftButton.setDisable(
                leftSelector.getTopColorName().equals("")
                || leftSelector.getBottomColorName().equals("")
        );
        printRightButton.setDisable(
                rightSelector.getTopColorName().equals("")
                || rightSelector.getBottomColorName().equals("")
        );
        printBothButton.setDisable(
                leftSelector.getTopColorName().equals("")
                || leftSelector.getBottomColorName().equals("")
                || rightSelector.getTopColorName().equals("")
                || rightSelector.getBottomColorName().equals("")
        );
    }

    //************************************************//
    //                PRIVATE METHODS                 //
    //************************************************//

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
}

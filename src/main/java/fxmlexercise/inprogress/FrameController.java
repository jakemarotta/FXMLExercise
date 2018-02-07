package fxmlexercise.inprogress;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;

import java.io.IOException;

public class FrameController {

    //**********************************************//
    //                   CONTROLS                   //
    //**********************************************//

    /*
        @TODO Declare FXML-tagged controls.
     */
    @FXML private AnchorPane leftAnchorPane, rightAnchorPane;

    @FXML private Label firstLabel, secondLabel, thirdLabel, fourthLabel;

    @FXML private Rectangle firstRect, secondRect, thirdRect, fourthRect;

    @FXML private Button printLeftButton, printBothButton, printRightButton;

    private SelectorController leftSelector, rightSelector;


    //***********************************************//
    //                    METHODS                    //
    //***********************************************//

    @FXML
    private void printButtonAction(ActionEvent e) {

        /*
            @TODO Print colors' names and values, depending on which "Print" button was pressed.
         */
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

    @FXML
    protected void initialize() {
        /*
            @TODO Load selectors from FXML
        */
        try {
            FXMLLoader loaderOne = new FXMLLoader(getClass().getResource("selector.fxml"));
            Parent rootOne = loaderOne.load();  // load() returns the scene graph as a Node, which must be a Parent
            this.leftSelector = loaderOne.getController(); // getController() returns the controller object
            leftAnchorPane.getChildren().add(rootOne);  // add the Node to the AnchorPane

            FXMLLoader loaderTwo = new FXMLLoader(getClass().getResource("selector.fxml"));
            Parent rootTwo = loaderTwo.load();  // load() returns the scene graph as a Node, which must be a Parent
            this.rightSelector = loaderTwo.getController(); // getController() returns the controller object
            rightAnchorPane.getChildren().add(rootTwo);  // add the Node to the AnchorPane

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*
            @TODO Bind controls in this controller to the appropriate properties in the selectors' controllers.
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
            @TODO Configure disable state logic for "Print" buttons.
         */

    }
}

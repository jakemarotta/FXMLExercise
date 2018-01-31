# FXML Exercise - Instructions
This exercise demonstrates the use of FXML files within a JavaFX application, focusing on writing controller classes. Some familiarity with JavaFX controls and SceneBuilder is assumed. 

## Requirements
- [Java 8 SDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- An IDE
- [JavaFX SceneBuilder](http://www.oracle.com/technetwork/java/javafxscenebuilder-1x-archive-2199384.html) (Alternative version available from [Gluon](http://gluonhq.com/products/scene-builder/))
- [Project Files](https://github.com/marottajb/FXMLExercise)

## Setup
1. Import the project FXMLExercise into an IDE. If your IDE supports importing as a Gradle project, do so. I'm using [IntelliJ](https://www.jetbrains.com/idea/), and have not tested this exercise in other Java IDEs.
2. Open a command line in the root directory of the project and execute the command `./gradlew build`. This will tell the project's Gradle wrapper to download any missing dependencies (at time of writing, a single dependency) and build the project files.

## Project Structure
The project files are organized in a [Maven-like structure](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html). There is a directory called `gradle` that contains the files for the project's Gradle wrapper, as well as scripts for executing Gradle commands, but since Gradle isn't the focus of this exercise, we'll only be looking at the `src` directory:
```
- [src]
    - [main]
        - [java]
            - *source code files*
        - [resources]
            - *resource files*
```
- The `src` directory serves as the root for all sources and tests.
  - `main` holds the code related to the application itself, not test code. If there were tests in this project, there would be another directory at this level called `test` for holding test code.
      - `java` contains all of the Java code for the application itself.
      - `resources` contains any resources that the application source code requires. This is where the FXML files will go.

The package structure of `java` is mirrored in `resources`. For example, if a file `Foo.java` located in `src/main/java/this/directory/` needs a resource file `bar.fxml`, the apropriate location for the resource file would be `src/main/resources/this/directory`. The package for both of these files would be `this.directory`.

In this project, there are two directories within `java` and `resources`: `fxmlexercise/starthere/` and `fxmlexercise/completed/`. The former has incomplete starter files for this exercise, and the latter contains finished solutions.

## Background
As mentioned, some familiarity with JavaFX controls is assumed. However, there are some classes and concepts that are important enough to mention in detail here.

#### Scene Graphs
- A [Stage](https://docs.oracle.com/javase/8/javafx/api/javafx/stage/Stage.html) is similar to a JFrame in Java Swing. It is the top-most container in JavaFX, and acts as a program window. The primary Stage of a JavaFX Application is constructed by the platform (see the parameter "primaryStage" on line 12 of Main.java).
- Each Stage displays a [Scene](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Scene.html), which itself contains all of the content for a scene graph.
- A JavaFX [scene graph](https://docs.oracle.com/javafx/2/scenegraph/jfxpub-scenegraph.htm) is a set of tree data structures in which every item is a Node. A Node can be a "root" node (the Node has no "parent"), a "branch" node (the Node has one or more children), or a "leaf" node (the Node has no children).
- [Node](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Node.html) is the base class for all scene graph objects, including containers, controls, shapes, etc.
- A Node which has children is also a [Parent](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Parent.html).

When constructing a meaningful window, `stage.setScene()` is called to set the content of a Stage to a particular scene graph. A Scene instance is constructed with a root node, which must be a Parent. For example, you might have created an FXML file where the outermost container is a VBox. The VBox would be the root node of the scene graph, and the tree would branch through all of the children of the VBox, and their children, and so forth, until it reaches the basic shapes which have no child Nodes of their own.

*Side Note:* Parent is an abstract class, so it needs to be an appropriate concrete subclass, such as Group or Region. If a Group is used, then changes to the window's size won't affect the layout of the scene graph. If a Region is used, changes to the window's size will cause the nodes in the scene graph to be re-laid out as necessary.

#### Properties
Using Properties in conjunction with Bindings reduces the amount of code necessary for an FXML-defined GUI. When a property is bound to another, changes to one property are automatically reflected in the other. It's possible to create bindings in which only one property changes in response to the other, or bidirectional bindings where changes to either are reflected in both.

Here is an example of a fairly standard definition of a StringProperty:
```java
private StringProperty name = new SimpleStringProperty();
public StringProperty nameProperty() {
  return name;
}
public final String getName() {
  return name.get();
}
public final void setName(String s) {
  name.set(s);
}
```
The property itself is declared on line 1, and instantiated as a new SimpleStringProperty, a concrete class for StringProperty. 
On line 2, there's a getter method for the property, which is by convention the name of the property and the word "Property". This returns the property itself, not the value it contains. If you wanted to bind another property to this one, you would call `nameProperty()` to get the property instance, like this: `otherProperty.bind( nameProperty() )`.
The other two methods are a getter and setter for the value of the property. These are declared final by convention.

Let's pretend that this name property is inside of a controller class for an FXML-defined view containing a TextField. What if we want to bind the name property to that TextField so that it always reflects what the user has typed in? We would bind the name property to the TextField's textProperty. However, declaring the name property in the way that we just did would cause a few problems in this case. 
- The name property is instantiated when the object is created, but the controller won't have access to the FXML-defined TextField until the FXML file is fully loaded (more on that under "Controllers"). Do we want a property that has to wait around to have a useful value?
- When propertyA is bound to propertyB via `propertyA.bind(propertyB)`, you can no longer set the value of propertyA. It doesn't make sense to have a value bound to another if the two values can be different. In this case, if the `setName()` method were called, it would throw an Exception.
- What if the logical name value is changed somewhere else in the program, and we need to apply it to this controller? We could set the value of the TextField's textProperty, which would in turn update the bound name property, but we'd have to keep the TextField public, which might not be safe.

We can solve these problems by tweaking the standard definition a bit:
```java
@FXML
private TextField nameTextField;

private StringProperty name;
public StringProperty nameProperty() {
  if (name == null) {
    name = new SimpleStringProperty();
    name.bindBidirectional(nameTextField.textProperty());
  }
  return name;
}
public final String getName() {
  return nameProperty().get();
}
public final void setName(String s) {
  nameProperty().set(s);
}
```
In this version, the name property isn't instantiated until the first time nameProperty() is called. When it is instantiated, it is immediately bound to the textProperty() of nameTextField (and vice versa, through the bidirectional binding). The insides of the value's getter and setter have changed slightly; instead of referencing `name` directly, they access the value returned by nameProperty(), to make sure that the property is instantiated if it hasn't been already. Because of the bidirectional binding, if `setName()` is called, the value in the TextField is also updated.

## Creating the FXML Files (Optional)
There are provided FXML files in `src/main/resources/` that can be used to complete the exercise. However, if you want an extra challenge, or some practice using SceneBuilder, use the following descriptions to create your own. Since this isn't an exercise in design, layout doesn't necessarily matter, as long as the files have the required controls. If you make your own, remember to put them in `java/fxmlexercise/starthere`.

#### selector.fxml
This file is the simpler of the two. It will need two [TextFields](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TextField.html), two [ColorPickers](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/ColorPicker.html), and one [Button](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Button.html) (all found under "Controls" in the left sidebar of SceneBuilder). Each TextField corresponds to a ColorPicker, so a user can type in a custom name to describe the color they've chosen. The Button will later print the name and value of the selected colors, so the Button's text should say as much.

#### frame.fxml
This one is a little trickier. It needs two [AnchorPanes](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/layout/AnchorPane.html) (found under "Containers"), four [Labels](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Label.html) (under "Controls"), four [Rectangles](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/shape/Rectangle.html) (under "Shapes"), and three [Buttons](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Button.html). 

Each AnchorPane will hold a "selector" in it. However, we'll be adding them in programmatically, so there won't actually be anything inside of the pane at this stage. To make the AnchorPanes easier to see, you can add a border. Under "Properties" in the right sidebar, scroll down until you see the header "JavaFX CSS". Under "Style", there are two TextFields followed by two buttons. In the first text field, type `-fx-border-color`, and in the second, type `black`. Then, click the plus-sign button. This will add a style definition to the AnchorPane which adds a black border. 

Each Label will be paired with a Rectangle. We'll be changing each Label's text to reflect a custom color name in a selector, and we'll be changing each corresponding Rectangle's fill color to match the selected color in a ColorPicker. Two Label/Rectangle pairs will be bound to each "selector".

## Main.java
You won't have to do much with this class. It's fairly standard for a JavaFX application. The parts that you might have to change are on lines 13 - 17:
```java
package fxmlexercise.starthere;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("FXMLExample");

        Parent root = FXMLLoader.load(getClass().getResource("selector.fxml"));

        primaryStage.setScene(new Scene(root, 350, 200));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```
The `setTitle()` method on line 13 sets the title of the Stage. The value here is what willl show up in the title bar of the window that the application runs in. Feel free to change it as you like.

Line 15 is more important to understand. The [FXMLLoader](https://docs.oracle.com/javase/8/javafx/api/javafx/fxml/FXMLLoader.html) class is (predictably) used to load FXML files. The static `load()` method is useful when you don't have to interact with the controller instance. If you changed the name of your selector FXML file, be sure to change the name in the call to `getResource()`.

Line 17 sets the Scene of the primary Stage to a new instance of Scene with the root "root", a width of 350px, and a height of 200px. Specifying the width and height of the Scene is only important if you want the initial window to be a certain size; otherwise, the Scene will compute its initial size based on the preferred size of the root node.

## Controllers
In order to define the behavior of the controls we put in the FXML documents, we'll need a controller class for each one. These will be `SelectorController.java` and `FrameController.java`. The controller classes will have attributes and methods that FXML controls can be tied to by assigning each control an fx:id. For example, we might have an FXML file like this:
```xml
<VBox fx:controller="com.foo.ControllerClass" xmlns:fx="http://javafx.com/fxml">
    <children>
        <Button fx:id="button" text="Hello World"/>
    </children>
</VBox>
```
On the first line, the root node has its `fx:controller` attribute set to the appropriate class. It's child Button has its `fx:id` attribute set to the name of a corresponding attribute of the controller class:
```java
package com.foo;

public class ControllerClass {
    public Button button;

    public void initialize() {
        button.setOnAction(action -> {
                System.out.println("Hello World!");
        });
    }
}
```
Notice the `initialize()` method. When an FXMLLoader loads the FXML file, it will construct a new instance of the associated controller. Then, once the object is populated with the controls specified in the FXML file, the `initialize()` method will be called. This is your opportunity to apply any settings or behavior not specified in the FXML file to the controls. In this example, the behavior of the button is defined so that it prints "Hello World!" when pressed.

Also notice that both the Button attribute and the initialize method are declared as public. If they weren't, the fields wouldn't be visible to the FXMLLoader. However, this can pose some concerns when it comes to information hiding. To make the program a little more secure while keeping the fields visible to FXMLLoader, we can annotate them with the [@FXML](https://docs.oracle.com/javase/8/javafx/api/javafx/fxml/doc-files/introduction_to_fxml.html#fxml_annotation) tag.
```java
package com.foo;

public class ControllerClass {
    @FXML
    private Button button;

    @FXML
    protected void initialize() {
        button.setOnAction(action -> {
                System.out.println("Hello world!");
        });
    }
}
```
Unless otherwise specified, the FXMLLoader will use the default constructor. It's important to note that the constructor is called *before* the initialize method, so any attributes that correspond to FXML-defined controls (such as "button") haven't been loaded by the time the constructor is called, and therefore can't be referenced from inside the constructor. Later on in the exercise, we'll write our own constructor for one of the controllers, but for now, it's not necessary to have them.

## SelectorController.java
Starting under "CONTROLS", we'll start by creating attributes for the controls. It's not necessary to create one for every single Node in the FXML file, just for the controls that we want to access in the controller. Declare attributes for each TextField, each ColorPicker, and the Button. The name of each attribute will also be the control's fx:id. Remember to include the @FXML tag, like this:
```java
@FXML
private Button printButton;
```

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

In this project, there is a single directory inside both `java` and `resources`, `fxmlexercise`, which contains incomplete starter files for this exercise, and another directory, `fxmlexercise/completed/`, which contains finished solutions.

## Background
As mentioned, some familiarity with JavaFX controls is assumed. However, there are some classes and concepts that are important enough to mention in detail here.

### Declaring JavaFX Properties
Using Properties in conjunction with Bindings reduces the amount of code necessary for an FXML-defined GUI. 

Here is an example of a fairly standard definition of a StringProperty:
```java
private StringProperty address = new SimpleStringProperty();
public StringProperty addressProperty() {
  return address;
}
public final String getAddress() {
  return address.get();
}
public final void setAddress(String s) {
  address.set(s);
}
```
The property itself is declared on line 1, and instantiated as a new SimpleStringProperty, a concrete class for StringProperty. 
On line 2, there's a getter method for the property, which is by convention the name of the property and the word "Property". This returns the property itself, not the value it contains.
The other two methods are a getter and setter for the value of the property. These are declared final by convention.

### Bindings
There are two basic ways to bind a property to another property:
```java
private ObjectProperty<Color> p1 = new SimpleObjectProperty();
private ObjectProperty<Color> p2 = new SimpleObjectProperty();

public static void bindProperties() {
  p2.bind(p1);  // p2 reflects changes made to p1
  p2.bindBidirectional(p1); // both p1 and p2 reflect changes made to each other
}
```
`p1` and `p2` above are both ObjectProperties with values of type Color. If we want `p2` to always have the same value as `p1`, we can simply say `p2.bind(p1)`. This has three notable effects:
1. The value of `p2` is set to the current value of `p1`.
2. Any further changes made to the value of `p2` will also be made to that of `p1`.
3. Changes to the value of `p1` are no longer allowed. It doesn't make sense to have a value bound to another if the two values can be made different.

Alternatively, we can say `p2.bindBidirectional(p1)`, after which:
1. Like above, the value of `p2` is set to the current value of `p1`.
2. Any further changes made to the value of `p2` will also be made to that of `p1`.
3. Any further changes made to the value of `p1` will also be made to that of `p2`.

If two properties are bound bidirectionally, both properties' values can still be changed without incident.

What if we want to declare a property that depends on multiple values, or we want it to have a value that is slightly different from another property? There's a utility class called `Bindings` that has a lot of static mthods for these cases:
```java
BooleanProperty thisValue = new SimpleBooleanProperty();
BooleanProperty otherValue = new SimpleBooleanProperty(false);

thisValue.bind( Bindings.not(otherValue) );  // thisValue will always have the opposite value of otherValue

DoubleProperty partOne = new SimpleDoubleProperty(12.7);
DoubleProperty partTwo = new SimpleDoubleProperty(68.1);
DoubleProperty sum = new SimpleDoubleProperty();

sum.bind( Bindings.add(partOne, partTwo) ); // sum will always have the value of partOne.get() + partTwo.get()
```

Let's pretend that the address property that we declared earlier is inside of a controller class for an FXML-defined view containing a TextField. What if we want to bind the address property to that TextField so that it always reflects what the user has typed in? We would bind the address property to the TextField's textProperty. However, declaring the address property in the way that we just did would cause a few problems in this case. 
- The address property is instantiated when the object is constructed, but the controller won't have access to the FXML-defined TextField until the FXML file is fully loaded (more on that under "Controllers"). Do we want a property that has to wait around to have a useful value?
- When propertyA is bound to propertyB via `propertyA.bind(propertyB)`, you can no longer set the value of propertyA. If the `setName()` method were called, it would throw an Exception, because a change to the address propery would cause it to deviate from the value that it's supposed to have.
- What if the logical address value is changed somewhere else in the program, and we need to apply it to this controller? We could set the value of the TextField's textProperty, which would in turn update the bound address property, but we'd have to keep the TextField public, which might not be safe.

We can solve these problems by tweaking the standard definition a bit:
```java
@FXML
private TextField addressTextField;

private StringProperty address;
public StringProperty addressProperty() {
  if (address == null) {
    address = new SimpleStringProperty();
    address.bindBidirectional(addressTextField.textProperty());
  }
  return address;
}
public final String getAddress() {
  return addressProperty().get();
}
public final void setAddress(String s) {
  addressProperty().set(s);
}
```
In this version, the address property isn't instantiated until the first time `addressProperty()` is called. When it is instantiated, it is immediately bound to the textProperty() of `addressTextField` (and vice versa, through the bidirectional binding). The insides of the value's getter and setter have changed slightly; instead of referencing `address` directly, they access the value returned by `addressProperty()`, to make sure that the property is instantiated if it hasn't been already. Because of the bidirectional binding, if `setName()` is called, the value in the TextField is also updated.

Now our controller has a property that always reflects the value of a control, and a collection of methods for accessing, binding, and changing the value of that control, all while keeping that control private.

### Scene Graphs
- A [Stage](https://docs.oracle.com/javase/8/javafx/api/javafx/stage/Stage.html) is similar to a JFrame in Java Swing. It is the top-most container in JavaFX, and acts as a program window. The primary Stage of a JavaFX Application is constructed by the platform (see the parameter "primaryStage" on line 12 of Main.java).
- Each Stage displays a [Scene](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Scene.html), which itself contains all of the content for a scene graph.
- A JavaFX [scene graph](https://docs.oracle.com/javafx/2/scenegraph/jfxpub-scenegraph.htm) is a set of tree data structures in which every item is a Node. A Node can be a "root" node (the Node has no "parent"), a "branch" node (the Node has one or more children), or a "leaf" node (the Node has no children).
- [Node](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Node.html) is the base class for all scene graph objects, including containers, controls, shapes, etc.
- A Node which has children is also a [Parent](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Parent.html).

When constructing a meaningful window, `stage.setScene()` is called to set the content of a Stage to a particular scene graph. A Scene instance is constructed with a root node, which must be a Parent. For example, you might have created an FXML file where the outermost container is a VBox. The VBox would be the root node of the scene graph, and the tree would branch through all of the children of the VBox, and their children, and so forth, until it reaches the basic shapes which have no child Nodes of their own.

*Side Note:* Parent is an abstract class, so it needs to be an appropriate concrete subclass, such as Group or Region. If a Group is used, then changes to the window's size won't affect the layout of the scene graph. If a Region is used, changes to the window's size will cause the nodes in the scene graph to be re-laid out as necessary.

### FXMLLoader
The FXMLLoader class is responsible for turning FXML specifications into Java objects. An FXMLLoader must be given the location of the FXML resource file as a URL. This is most easily done with `getClass().getResource("my-file.fxml")`. Then, calling `load()` will tell the FXMLLoader to read the FXML file, use it to construct a scene graph out of the required Nodes, and return the root node of the scene graph. If the FXML file specifies a controller class, that class' default constructor is called during `load()` to generate an instance of the controller. Nodes in the FXML file which also have an fx:id will be injected into their appropriate fields in the controller class, allowing the controller to programmatically interact with them. This simplest way to load an FXML file is by using FXMLLoader's static `load()` method:
```java
Parent root = FXMLLoader.load(getClass().getResource("my-file.fxml"));
```
`root` is set to the root Node of the scene graph generated by loading the FXML file. If a controller class was specified in the FXML, the scene graph is backed by an instance of the controller class. This way is useful if you don't need to do anything fancy with the controller, but you can also construct an instance of FXMLLoader, which opens up some cool options. It will have to be wrapped in a try-catch block, though.
```java
try {
  FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("my-file.fxml"));
  Parent root = fxmlLoader.load();
  ControllerClass controller = fxmlLoader.getController();
} catch (IOException e) {
  throw new RuntimeException(e);
}
```
Here we have access to both the root node of the scene graph and the controller instance for that scene graph. Because we can access the controller object through `getController()`, we can do things like calling setters and other public methods in the controller class, or binding properties to properties declared in the controller instance. Important note: the controller isn't constructed until `load()` is called, so you have to wait to call `getController()` until that's done.

Calling `load()` from an instance of FXMLLoader is different from using the static `load()`: both the root node of the scene graph and the controller object are saved into an attribute of the FXMLLoader object, so every call to `load()` or `getController()` will return the same object for each method. This is good, in many contexts. But what if you want to load multiple instances from the same FXML file? There are a couple of ways to get around that, both of which are better than just declaring multiple instances of FXMLLoader:

1. Use FXMLLoader's `setControllerFactory()` method to specify a controller factory as a Callback. When a controller factory is set, the FXMLLoader will create a new scene graph and new controller object each time `load()` is called. You can control how the controller object is constructed using the controller factory. For example, it's possible to pass arguments to a custom constructor instead of using the default constructor by specifying them in the controller factory.
2. Change the FXML and controller class into a custom control class. In this case, the controller class handles the loading of its own FXML file in its constructors. Then, an FXMLLoader isn't even needed outside of the class; you can instantiate it like any other object.
```java
CustomControl cc = new CustomControl();
vBox.getChildren.add(cc);
```
```java
public class CustomControl extends VBox {

@FXML
private TextField textField;
@FXML
private ColorPicker colorPicker;

  public CustomControl() {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("my-file.fxml"));
      fxmlLoader.setRoot(this);
      fxmlLoader.setController(this);
      fxmlLoader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  @FXML
  protected void initialize() { }
}
```
```html
// custom-control.fxml

<fx:root type="javafx.scene.layout.VBox" xmlns="http://javafx.com/javafx/8.0.112" xmlns:fx="http://javafx.com/fxml/1">
  <HBox alignment="CENTER" spacing="15.0">
            <TextField fx:id="textField" minHeight="25.0" minWidth="150.0" promptText="Color Name">
               <HBox.margin>
                  <Insets left="20.0" />
               </HBox.margin>
            </TextField>
            <ColorPicker fx:id="colorPicker" minHeight="25.0" minWidth="125.0">
               <HBox.margin>
                  <Insets bottom="20.0" right="20.0" top="20.0" />
               </HBox.margin>
            </ColorPicker>
      </HBox>  
</fx:root>
```
In the custom control's constructor, we create an instance of FXMLLoader. Then, instead of getting the root node and controller from the FXML file, we set the root node and controller to be the newly created object. Finally, a call to `load()` creates the scene graph and injects nodes with an fx:id into the fields we've declared for them.

In the FXML code, there are a few things to notice:
- The root node of the defined scene graph has the tag `fx:root` instead of a class like `VBox`. 

**not finished


## Creating the FXML Files (Optional)
There are provided FXML files in `src/main/resources/` that can be used to complete the exercise. However, if you want an extra challenge, or some practice using SceneBuilder, use the following descriptions to create your own. Since this isn't an exercise in design, layout doesn't necessarily matter, as long as the files have the required controls. If you make your own, remember to put them in `java/fxmlexercise`.

### selector.fxml
This file is the simpler of the two. It will need two [TextFields](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TextField.html), two [ColorPickers](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/ColorPicker.html), and one [Button](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Button.html) (all found under "Controls" in the left sidebar of SceneBuilder). Each TextField corresponds to a ColorPicker, so a user can type in a custom name to describe the color they've chosen. The Button will later print the name and value of the selected colors, so the Button's text should say as much.

### frame.fxml
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
We'll start by creating attributes for the controls. It's not necessary to create one for every single Node in the FXML file, just for the controls that we want to access in the controller. 

Declare attributes for each TextField, each ColorPicker, and the Button. The name of each attribute will also be the control's fx:id. Remember to include the @FXML tag, like this:
```java
@FXML
private Button printButton;
```
Next, write the properties for this controller. These properties will be bound to our controls, so we'll want to use our modified property declaration style:
```java
@FXML
private TextField textField1;

private StringProperty colorName1;
public StringProperty colorName1Property() {
  if (colorName1 == null) {
    colorName1 = new SimpleStringProperty();
    colorName1.bindBidirectional(textField1.textProperty());
  }
  return colorName1;
}
public final String getColorName1() {
  return colorName1Property().get();
}
public final void setColorName1(String s) {
  colorName1Property().set(s);
}
```
At this point, we'll want four properties: one for each custom color name, and one for each color selection. The color name properties will be bound to each TextField's `textProperty()` and the color properties will be bound to each ColorPicker's `valueProperty()`. Pay attention to the type of these controls' properties to know what type yours need to be.

Next, finish the `printButtonAction()` method. It only needs to print out each custom color name and each color in a way that makes sense.

Now switch to SceneBuilder. First, you'll need to set the controller class for the FXML file. Under the "Controller" menu in the bottom-right of SceneBuilder, type "fxmlexercise.SelectorController" in the text field labelled "Controller class".

*Side note:* In my opnion, one of the shortcomings of SceneBuilder is that you can't specify a project directory. If your controller class and FXML file were in the same directory, you'd be able to see a drop-down list of available controller classes when you clicked on the text field. Once you'd specified the controller class, the same would happen when you define fx:ids. However, this is not conducive to Maven's standard directory layout, and your controller class should be in a different directory. It will still work, you'll just have to be careful to type the names correctly, and SceneBuilder will complain that it can't find fields of the same name. 

For the TextFields, ColorPickers, and Button, go to the "Code" menu in the right side of SceneBuilder, and in the text field labelled "fx:id", type the name of the attribute for that control in the controller class. For example, if you declared the Button as "printButton", you would put "printbutton" in the text field. In addition, for your Button, you'll want to put "printButtonAction" in the text field labelled "On Action". This will make it so the `printButtonAction()` method is called each time the Button is pressed.

Save the file, and try to build and run the program. Set the controls to something, make sure that the button prints the correct information when pressed, etc.

Now, we're going to make the selector a bit more responsive. Add a BooleanProperty called `textFieldsNotEmpty`, along with a property getter `textFieldsNotEmpty` and a value getter. After you construct the SimpleBooleanProperty, use the `Bindings.and()` and `Bindings.notEqual()` methods to bind `textFieldsNotEmpty` in such a way that the value is true if both TextFields aren't empty.
```java
private BooleanProperty textFieldsNotEmpty;
private BooleanProperty textFieldsNotEmptyProperty() {
  if (testFieldsNotEmpty == null) {
    textFieldsNotEmpty = new SimpleBooleanProperty();
    textFieldsNotEmpty.bind( Bindings.and( 
        Bindings.notEqual( textField1.textProperty(), "" ),
        Bindings.notEqual( textField2.textProperty(), "" ) 
      ));
  }
  return textFieldsNotEmpty;
}
public Boolean textFieldsNotEmpty() {
  return textFieldsNotEmptyProperty().get();
}
```
We don't need a setter for this property, because it's completely dependent on the states of both TextFields. There isn't a logical case in which we would need to set this value programmatically.

To make use of this property, we'll bind the Button's `disableProperty()` to it. When `textFieldsNotEmpty` is true, we want `disableProperty()` to be false, and vice versa, so make use of the `Bindings.not()` method, and place the following inside of `initialize()`:
```java
printButton.disableProperty().bind( Bindings.not( textFieldsNotEmptyProperty() ) );
```
Try building and running the program again, and make sure the button is disabling and enabling itself properly.

## FrameController.java
First and foremost, inside of Main.java, change the resource location on line 15 from the name of your selector FXML file to the name of your frame FXML file, so that the `primaryStage` loads a FrameController instead of a SelectorController.

Again, declare each of the necessary attributes from the FXML file. Each of the AnchorPanes, Labels. Rectangles, and buttons will need @FXML tags. You'll also need to declare two attributes of type SelectorController, without @FXML tags. These will be going inside of each AnchorPane.

Because the two SelectorControllers we need aren't defined in the FXML file, we'll need to construct them. You can do this in one of two places: in a constructor for the FrameController class, or in the `initialize()` method. We'll do both, eventually, but start out by doing so in the `initialize()` method. You'll need to wait until the `initialize()` method to assign each selector to its respective AnchorPane, anyway, because the AnchorPanes won't be accessible until the FXML has been loaded.

In `initialize()` of FrameController, declare two variables of type FXMLLoader and set them both as new FXMLLoaders with the location as the selector FXML resource. 
```java
FXMLLoader loader1 = new FXMLLoader(getClass().getResource("selector.fxml"));
FXMLLoader loader2 = new FXMLLoader(getClass().getResource("selector.fxml"));
```
We don't want to use the static `FXMLLoader.load()` method here. `load()` only returns the scene graph that represents a controller, but we need the instance of the controller that goes with it. Using an FXMLLoader object, we can call `getController()`, which will get us what we want.
```java
@FXML
private AnchorPane anchorPaneOne;
@FXML
private AnchorPane anchorPaneTwo;
private SelectorController selectorOne;
private SelectorController selectorTwo;
```
```java
@FXML
protected void initialize() {
  /*
    @TODO Load selectors from FXML
   */
  try {
    FXMLLoader loaderOne = new FXMLLoader(getClass().getResource("selector.fxml"));
    Parent rootOne = loaderOne.load();  // load() returns the scene graph as a Node, which must be a Parent
    this.selectorOne = loaderOne.getController(); // getController() returns the controller object
    anchorPaneOne.getChildren().add(rootOne);  // add the Node to the AnchorPane

    // Repeat for the second AnchorPane and SelectorController.
    
  } catch (IOException e) {
    throw new RuntimeException(e);
  }
  
  /*
    @TODO Bind controls in this controller to the appropriate properties in the selectors' controllers.
    */
  /*
    @TODO Configure disable state logic for "Print" buttons.
    */
}
```
When you have an instance of FXMLLoader, there are a few things that have to be done in sequence. The location has to be set first; in the above example, we pass the location in as a constructor argument. Then the FXML file at that location has to be loaded with `load()`, which will generate and return the heirarchy of Nodes described in FXML, and will construct an instance of the controller class, if one is specified. Calling `getController()` before `load()` will cause an exception, and so will calling `getController()` when the FXML doesn't specify a controller class. We want to add the scene graph for each selector (rootOne/rootTwo) into the scene graph for the FrameController, specifically inside of the AnchorPanes we defined earlier. Calling `getChildren().add(Node n)` on a Node will add Node `n` to the Node's children. 

You may be wondering why you just constructed two separate FXMLLoaders to create two equivalent objects instead of using one and calling `load()` twice. The answer is that once `load()` is called for the first time, the scene graph is saved to an attribute of the FXMLLoader object. Any subsequent call to `load()` on that FXMLLoader will return the same instance of Node, not a newly constructed one. The same is true of the instance of the controller class; a reference to the controller object is saved, and returned upon subsequent calls of `setController()`. There are ways around this, like using `setControllerFactory()` to write a Callback for the controller class. With a controller factory instead of a controller, each call to `load()` will construct new instances of scene graph and controller. You can also use controller factories to tell the FXMLLoader to use a custom constructor for the controller, so you can pass values into a controller object instead of having to call setter methods after the fact.

Try building and running the project. There should be two selectors inside of your frame. Mess around with the controls inside; you'll see that the print buttons in the selectors still behave the same way. However, we haven't done anything with the controls in FrameController yet, so they just sit there.

Now that we have access to the controllers for each selector, we can access public properties and methods for them. We're going to bind the Labels and Rectangles in FrameController to some of the properties we defined in SelectorController. Between one FrameController and two SelectorControllers, there are four pairs of TextField/ColorPicker and four pairs of Label/Rectangle. For every TextField/ColorPicker and Label/Rectangle, we're going to bind the Label's `textProperty()` to the TextField's `textProperty()`, and the Rectangle's `fillProperty()` to the ColorPicker's `valueProperty()`. Don't bind them bidirectionally. A user can't interact with the Labels and Rectangles with their default behavior, and we want the locus of control to be in the properties, so we don't need to have the properties listening for changes in the Labels and Rectangles.
```java
@FXML
private Label labelOne, labelTwo, labelThree, labelFour;
private SelectorController selectorOne, selectorTwo;
```
```java
@FXML protected void initialize() {
  /*
    Load selectors from FXML
   */
   
   /*
    @TODO Bind controls in this controller to the appropriate properties in the selectors' controllers.
    */
   labelOne.textProperty().bind(selectorOne.colorNameOneProperty());
   // Repeat for labelTwo and selectorOne.colorNameTwoProperty()
   // Repeat for labelThree and selectorTwo.colorNameOneProperty()
   // Repeat for labelFour and selectorTwo.colorNameTwoProperty()
   
   /*
    @TODO Configure disable state logic for "Print" buttons.
    */
}
```

Next, we'll define what the frame's buttons do. 

** not finished

Next, let's make the frame's buttons responsive like in Selector Controller. Luckily, we wrote a property which reflects whether or not either text field is empty for our selectors, so there is much less code to write. 
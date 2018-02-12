# FXML Exercise - Instructions
This exercise demonstrates the use of FXML files within a JavaFX application, focusing on writing controller classes. Some familiarity with JavaFX controls and SceneBuilder is assumed. 

## Contents
- [Requirements](#requirements)
- [Setup](#setup)
  - [Using Gradle](#gradle)
- [Project Structure](#structure)
- [Background](#background)
  - [Declaring JavaFX Properties](#properties)
  - [Bindings](#bindings)
    - [Binding Different Values](#bindDiff)
    - [Binding Controls](#bindControls)
  - [Scene Graphs](#sceneGraphs)
  - [Controllers](#controllers)
  - [FXMLLoader](#fxmlLoader)
    - [Calling FXMLLoader.load()](#staticLoad)
    - [Using an Instance of FXMLLoader](#instanceLoad)
    - [Creating an FXML-defined Custom Control](#customControl)

- [The Actual Exercise](#theExercise)
  - [Creating the FXML Files](#createFXML)
    - [selector.fxml](#selectorInfo)
    - [frame.fxml](#frameInfo)
  - [Main.java](#mainJava)
  - [SelectorController.java](#selectorJava)
  - [FrameController,java](#frameJava)
  - [SelectorController as a Custom Control](#customSelector)
  - [Importing a Custom Control into SceneBuilder](#import)
  - [Using the CIRDLES ResourceExtractor](#extractor)
- [References](#references)

## <a name="requirements"></a>Requirements
- [Java 8 SDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [JavaFX SceneBuilder](http://www.oracle.com/technetwork/java/javafxscenebuilder-1x-archive-2199384.html) (Alternative version available from [Gluon](http://gluonhq.com/products/scene-builder/))
- [Project Files](https://github.com/marottajb/FXMLExercise)

An IDE is heavily recommended, as it can simplify building and running the project with Gradle. I'm using [IntelliJ](https://www.jetbrains.com/idea/), and have not tested this exercise in other Java IDEs.

## <a name="setup"></a>Setup
Import the project FXMLExercise into an IDE. If your IDE supports importing as a Gradle project, do so. Most likely, you won't have deal with Gradle past this point. However, if you've decided to brave the following without using an IDE, or you need a refresher on basic Gradle commands, there's this:

#### <a name="gradle"></a>Using Gradle
You don't have to have Gradle on your computer in order to follow the exercise. The project has an included Gradle wrapper, which can be executed from the command line with one of two files in the root directory of the project: gradlew or gradlew.bat (depending on your OS). Some commands to know:
- `./gradlew build` - This will download any missing dependencies and build the project files, without running them.
  - The `--refresh-dependencies` flag will ask Gradle to re-download the project dependencies, which is a good go-to solution if any are giving you trouble. 
- `./gradlew run` - This will run the main class of the project, which is `fxmlexercise.Main`.
- `./gradlew runCompleted` - This is not a built-in Gradle task, I specified it in the `build.gradle` file. It runs the finished version of the project found in `fxmlexercise.completed`.

I haven't tried going through the exercise without IntelliJ (IDE of choice) because I'm not a masochist, so if you run into any problems, let me know so I can fix/mention them.

## <a name="structure"></a>Project Structure
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

In this project, there is a single directory inside both `java` and `resources`, `fxmlexercise`, which contains incomplete starter files for this exercise and a subdirectory, `fxmlexercise/completed/`, which contains finished solutions.

## <a name="background"></a>Background
As mentioned, some familiarity with JavaFX controls is assumed. However, there are some classes and concepts that are important enough to mention in detail here. For a much more in-depth, official coverage of topics related to FXML, see Oracle's [*Introduction to FXML*](https://docs.oracle.com/javase/8/javafx/api/javafx/fxml/doc-files/introduction_to_fxml.html).

Code given in this section may not be related to the exercise itself, so don't worry about keeping track of it past understanding the demonstrated concept. The vast majority is made-up with generic names.

### <a name="properties"></a>Declaring JavaFX Properties
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
The property itself is declared and instantiated as a new SimpleStringProperty, a concrete class for StringProperty. 

Next, there's a getter method for the property, which is, by convention, the name of the property and the word "Property". This returns the property itself, not the value it contains.

The other two methods are a getter and setter for the value of the property. These are declared final by convention.

### <a name="bindings"></a>Bindings
There are two basic ways to bind a property to another property:
```java
private ObjectProperty<Color> propertyA = new SimpleObjectProperty();
private ObjectProperty<Color> propertyB = new SimpleObjectProperty();

public static void bindProperties() {
  p2.bind(propertyA);  // propertyB reflects changes made to propertyA
  p2.bindBidirectional(p1); // both propertyA and propertyB reflect changes made to each other
}
```
`propertyA` and `propertyB` above are both ObjectProperties with values of type Color. If we want `propertyB` to always have the same value as `propertyA`, we can simply say `propertyB.bind(propertyA)`. This has three notable effects:
1. The value of `propertyB` is set to the current value of `propertyA`.
2. Any further changes made to the value of `propertyB` will also be made to that of `propertyA`.
3. Changes to the value of `propertyA` are no longer allowed. It doesn't make sense to have a value bound to another if the two values can be made different.

Alternatively, we can say `propertyB.bindBidirectional(propertyA)`, after which:
1. Like above, the value of `propertyB` is set to the current value of `propertyA`.
2. Any further changes made to the value of `propertyB` will also be made to that of `propertyA`.
3. Any further changes made to the value of `propertyA` will also be made to that of `propertyB`.

If two properties are bound bidirectionally, both properties' values can still be changed without incident.

#### <a name="bindDiff"></a>Binding Different Values
What if we want to declare a property that depends on multiple values, or we want it to have a value that is slightly different from another property? There's a utility class called `Bindings` that has a lot of static methods for these cases:
```java
BooleanProperty thisValue = new SimpleBooleanProperty();
BooleanProperty otherValue = new SimpleBooleanProperty(false);

thisValue.bind( Bindings.not(otherValue) );  // thisValue will always have the opposite value of otherValue

DoubleProperty partOne = new SimpleDoubleProperty(12.7);
DoubleProperty partTwo = new SimpleDoubleProperty(68.1);
DoubleProperty sum = new SimpleDoubleProperty();

sum.bind( Bindings.add(partOne, partTwo) ); // sum will always have the value of partOne.get() + partTwo.get()
```

#### <a name="bindControls"></a>Binding Controls
Let's pretend that the address property that we declared earlier is inside of a controller class for an FXML-defined view containing a TextField. What if we want to bind the address property to that TextField so that it always reflects what the user has typed in? We would bind the address property to the TextField's textProperty. However, trying to bind the address property, as it's defined above, would cause a few problems in this case:
- The address property is instantiated when the object is constructed, but the controller won't have access to the FXML-defined TextField until the FXML document is fully loaded (more on that under "Controllers"). Do we want a property that has to wait around to have a useful value?
- When propertyA is bound to propertyB via `propertyA.bind(propertyB)`, you can no longer set the value of propertyA. If the `setName()` method were called, it would throw an Exception, because a change to the address propery would cause it to deviate from the value that it's supposed to have.
- What if the logical address value is changed somewhere else in the program, and we need to apply it to this controller? We could set the value of the TextField's textProperty, which would in turn update the bound address property, but we'd have to keep the TextField public, which might not be safe.

<a name="propertyControl"></a>We can solve these problems by tweaking the standard definition a bit:
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

### <a name="sceneGraphs"></a>Scene Graphs
When we write FXML, what we are doing is defining a hierarchy of JavaFX Nodes. Some terminology:

- A [Stage](https://docs.oracle.com/javase/8/javafx/api/javafx/stage/Stage.html) is similar to a JFrame in Java Swing. It is the top-most container in JavaFX, and acts as a program window. The primary Stage of a JavaFX Application is constructed by the platform (see the parameter "primaryStage" of `Main.start()`).
- Each Stage displays a [Scene](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Scene.html), which itself contains all of the content for a scene graph.
- A JavaFX [scene graph](https://docs.oracle.com/javafx/2/scenegraph/jfxpub-scenegraph.htm) is a set of tree data structures in which every item is a Node. A Node can be a "root" node (the Node has no "parent"), a "branch" node (the Node has one or more children), or a "leaf" node (the Node has no children).
- [Node](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Node.html) is the base class for all scene graph objects, including containers, controls, shapes, etc.
- [Parent](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Parent.html) is a subclass of Node which can have child nodes.

When constructing a meaningful window, `stage.setScene()` is called to set the content of a Stage to a particular scene graph. A Scene instance is constructed with a root node, which must be a Parent. For example, you might have created an FXML file where the outermost container is a VBox. The VBox would be the root node of the scene graph, and the tree would branch through all of the children of the VBox, and their children, and so forth, until it reaches the basic shapes which have no child Nodes of their own.

*Side Note:* Parent is an abstract class, so it needs to be an appropriate concrete subclass, such as Group or Region. If a Group is used, then changes to the window's size won't affect the layout of the scene graph. If a Region is used, changes to the window's size will cause the nodes in the scene graph to be re-laid out as necessary.

Most of the standard JavaFX controls are composed of many child nodes that determine how they act, what they look like, etc. Any Node in a scene graph can be expressed as a scene graph in which that Node is the root. So, when you add a Node A as a child of another Node B, what you're doing is adding the scene graph with A as its root into the scene graph with B as its root.

### <a name="controllers"></a>Controllers
Defining a scene graph through FXML only gives us some nice-looking controls with their default behavior. If you want to be able to programmatically define the behavior for or change the appearance of any of the controls, you can write a controller class. A controller class has fields that are injected with Nodes once an FXML document is loaded, based on their corresponding `fx:id`. For example, we might have an FXML file like this:

*controller-class.fxml*
```xml
<VBox xmlns:fx="http://javafx.com/fxml" fx:controller="org.example.ControllerClass">
  <children>
    <Button fx:id="button" text="Hello World"/>
  </children>
</VBox>
```
On the first line, the `fx:controller` attribute of the root node (more on that [later](#fxmlLoader)) tells the FXMLLoader to use an instance of ControllerClass as a controller. The root node's child Button has its `fx:id` attribute set to the name of a corresponding attribute of the controller class:

*ControllerClass.java*
```java
package org.example;

public class ControllerClass {
    public Button button;

    public void initialize() {
        button.setOnAction(action -> {
                System.out.println("Hello World!");
        });
    }
}
```
Notice the `initialize()` method. This is called by an FXMLLoader once it is finished injecting Nodes into the controller. This is your opportunity to apply any settings or behavior not specified in the FXML file to the controls. In this example, the behavior of the button is defined so that it prints "Hello World!" when pressed.

Unless otherwise specified, the FXMLLoader will use the default constructor of a controller cass. It's important to note that the constructor is called *before* the initialize method, so any attributes that correspond to FXML-defined controls (such as "button") haven't been loaded by the time the constructor is called, and therefore can't be referenced from inside the constructor. Wait to interact with any FXML-defined attributes until the `initialize()` method.

We've declared both the Button attribute and the initialize method as public. If they weren't, the fields wouldn't be visible to the FXMLLoader. However, this can pose some concerns when it comes to information hiding. To make the things a little more secure, while keeping the fields visible to FXMLLoader, we can annotate them with the [`@FXML`](https://docs.oracle.com/javase/8/javafx/api/javafx/fxml/doc-files/introduction_to_fxml.html#fxml_annotation) tag.

*ControllerClass.java*
```java
package org.example;

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

The `@FXML` tag isn't only used on `initialize()` and for denoting attributes for Nodes that have `fx:id`s. Suppose we added a method which performed the same action defined in `button.setOnAction()` above:

*ControllerClass.java*
```java
package org.example;

public class ControllerClass {
    @FXML
    private Button button;

    @FXML
    protected void initialize() {
      // now empty
    }
    
    @FXML
    private void printAction() {
      System.out.println("Hello World!");
    }
}
```
*controller-class.fxml*
```xml
<VBox xmlns:fx="http://javafx.com/fxml" fx:controller="org.example.ControllerClass">
  <children>
    <Button fx:id="button" text="Hello World" onAction="#printAction"/>
  </children>
</VBox>
```
Now, we don't have to define the button's behavior after it's been loaded. We pre-defined the behavior as `printAction()`, then added the `onAction` attribute in the FXML. When the button's `onAction` event is triggered, it will call `printAction()`. We don't even have to define an `initialize()` method at this point. The more you can define in FXML, the more that an FXMLLoader can do for you, and the less code you have to write.

### <a name="fxmlLoader"></a>FXMLLoader
The FXMLLoader class is responsible for turning FXML specifications into Java objects. It reads the hierarchy of Nodes defined in the FXML to build a scene graph, and injects Nodes into their appropriate fields in a controller, if applicable. There are three "fx" constructs that will be discussed in this exercise. Don't worry too much about understanding their usages completely right now; they'll be shown in more detail later.

- `fx:controller` is used to tell FXMLLoader that it needs to construct an instance of a controller class, and is an attribute of the root node. A controller is not necessary to load the FXML (unless you want to do anything remotely interesting). It's also possible to [supply the FXMLLoader with a controller instance](#customControls) instead of relying on the FXMLLoader to construct one itself, in which case there isn't a need to define `fx:controller`.
- Already discussed some in [Controllers](#controllers), `fx:id` supplies the name of an injectable field in a corresponding controller class to a node defined in the FXML. 
- `fx:root` is a tag that is used to reference a previously defined root element, and is only used as the root node of an FXML file. If `fx:root` is used, then a root Node will have to be supplied to the FXMLLoader. This tag is typically used when defining [custom controls](#customControls) in FXML.

#### <a name="staticLoad"></a>Calling FXMLLoader.load()
The simplest way to load an FXML file is by using FXMLLoader's static `load()` method. An FXMLLoader must be given the location of the FXML resource file as a URL. This is most easily done with `getClass().getResource("my-file.fxml")`. When an FXML file is loaded, the following happens:

1. The FXML file is loaded from a location relative to the loading class.
2. The FXMLLoader follows the FXML document to build a scene graph out of JavaFX Nodes. If no root node has been supplied (via `setRoot()`, in FXMLLoader objects), the FXMLLoader treats the outermost-defined Node from the document as the root node.
3. If a controller class has been specified (via `fx:controller`), the FXMLLoader constructs a new instance of that class to act as the controller. If a controller object has been supplied (via `setController()`, in FXMLLoader objects), the FXMLLoader simply uses that object.
    - *Side Note:* There should not be a controller class defined via `fx:controller` and a controller object supplied via `setController()` at the same time. An exception will be thrown if a controller already exists.
4. If a controller exists and has injectable fields for certain Nodes, the appropriate Nodes are injected into those fields (based on their `fx:id`) to tie in behavior from the controller.
5. Once all relevant Nodes are injected into the controller, the FXMLLoader calls that controller's overridden `initialize()` method (discussed further in [Controllers](#controllers)). 
6. The `load()` method will return the root Node, with all children appropriately loaded and tied to their behavior specified in the controller class (if applicable).

```java
Parent root = FXMLLoader.load(getClass().getResource("my-file.fxml"));
```
*my-file.fxml*
```xml
<HBox xmlns:fx="http://javafx.com/fxml/1" fx:controller="org.example.ControllerClass">
  <children>
    <Button fx:id="testButton" text="Test" />
    <TextField fx:id="textField" promptText="Text" />
  </children>
</HBox>
```
The Parent variable `root` is set to the root Node of the scene graph generated by loading the FXML file, which, as we can see in the FXML, is an HBox. The `fx:controller` attribute defines the controller class as ControllerClass, so the FXMLLoader will construct an instance of ControllerClass to act as the controller for the loaded scene graph. Both the Button and TextField inside of the HBox have `fx:id` attributes, implying that ControllerClass has injectable fields with those names, and that the generated Button and TextField will be injected into those fields. 

#### <a name="instanceLoad"></a>Using an Instance of FXMLLoader
Using static `load()` is useful if you don't need to do anything fancy with the controller, but you can also construct an instance of FXMLLoader, which will allow you set or access the FXMLLoader's root and controller instances. It will have to be wrapped in a try-catch block, because the instance `load()` method may throw an IOException:
```java
try {
  FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("my-file.fxml"));
  Parent root = fxmlLoader.load();
  ControllerClass controller = fxmlLoader.getController();
} catch (IOException e) {
  throw new RuntimeException(e);
}
```
Here we have access to both the root node of the scene graph and the controller instance for that scene graph. Because we can access the controller object through `getController()`, we can call setters or other public methods in the controller class, or bind properties to public properties of in the controller. The controller isn't constructed until `load()` is called, so calling `getController()` before then will throw an Exception.

Unlike the static `load()`, FXMLLoader's instance `load()` will return the same instance of the root for each subsequent call to it. Likewise, after the FXMLLoader is loaded for the first time, each call to `getController()` will return the same instance of the controller. This is good, in many contexts. But what if you want to load multiple instances from the same FXML file? There are a few ways to manage that:
1. Declare separate instances of FXMLLoader. (bad)
2. When you want new instances of the root and controller, use `setRoot()` to set the root to `null`, and do the same for the controller with `setController()`, before calling `load()` again. (better) 
3.  If you're going to be creating that many instances, you may as well create an FXML-defined custom control class.

#### <a name="customControl"></a>Creating an FXML-defined Custom Control
These two code blocks represent a custom control, which can be normally instantiated by its constructor without having to use FXMLLoader. In FXML, we've defined this control as being a VBox with two children: a TextField, and a ColorPicker. In Java, our controller class extends VBox, because the instance of the controller will also act as the instance of the root node. But if you look at the FXML, the root node is defined with the `fx:root` tag, not one for VBox.

*CustomControl.java*
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
*custom-control.fxml*
```html
<fx:root type="javafx.scene.layout.VBox" xmlns="http://javafx.com/javafx/8.0.111" xmlns:fx="http://javafx.com/fxml/1">
  <HBox alignment="CENTER" spacing="15.0">
    <TextField fx:id="textField" minHeight="25.0" minWidth="150.0" promptText="Color Name" />
    <ColorPicker fx:id="colorPicker" minHeight="25.0" minWidth="125.0" />
  </HBox>  
</fx:root>
```
The `type` attribute of `fx:root` is set to the VBox class, so the tag is treated as if it were defining a VBox. The difference is that the `fx:root` tag tell FXMLLoader to use a pre-existing instance of VBox as the root node instead of creating a new one. By extending VBox, an instance of CustomControl is able to perform that role.

In the custom control's constructor, we create an instance of FXMLLoader. Then, instead of *getting* the root node and controller from the FXML file, we *set* the root node and controller to be the newly created object. Finally, a call to `load()` creates the scene graph and injects nodes with an `fx:id` into the fields we've declared for them.

## <a name="theExercise"></a>The Actual Exercise
Now for the part where we actually do things. We'll be working in three .java files (found in `src/.../fxmlexercise/`):
- Main.java
- SelectorController.java
- FrameController.java
... and two .fxml files (found in `resources/.../fxmlexercise/`):
- selector.fxml
- frame.fxml

Completed versions of these files can be found in `fxmlexercise.completed`, and can be used as a reference while you fill in the empty ones. Run CompletedMain.java to see the finished program and get a sense of what to build towards. Keep in mind, though, that all of them are written as if the entire exercise was completed. At times, the exercise will ask you to do something, then replace it with a different version later. Because of this, copy-and-pasting sections of code from the completed files as you go may not work in some places where the approach has been updated.

*Side Note:* I didn't write this exercise to be a homework assignment, it's meant to be more like a walkthrough, so looking at and adopting the answers doesn't matter. However, I mention that copy-and-pasting parts of the solutions may not work in case anyone is going through the exercise relying on it. Any code that isn't provided should be very easy to figure out. If it isn't, then my walkthrough isn't clear enough, so please reach out with questions and suggestions.

### <a name="createFXML"></a>Creating the FXML Files (Optional)
The provided FXML files in `src/main/resources/` can be used to complete the exercise, in which case you should skip to [Main.java](#mainJava). However, if you want an extra challenge, or some practice using SceneBuilder, use the following descriptions to create your own. Since this isn't an exercise in design, layout doesn't necessarily matter, as long as the files have the required controls. If you make your own, remember to put them in `resources/fxmlexercise`.

#### <a name="selectorInfo"></a>selector.fxml
This file is the simpler of the two. It will need two [TextFields](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TextField.html), two [ColorPickers](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/ColorPicker.html), and one [Button](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Button.html) (all found under "Controls" in the left sidebar of SceneBuilder). Each TextField corresponds to a ColorPicker, so a user can type in a custom name to describe the color they've chosen. The Button will later print the name and value of the selected colors, so the Button's text should say as much.

#### <a name="frameInfo"></a>frame.fxml
This one is a little trickier. It needs two [AnchorPanes](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/layout/AnchorPane.html) (found under "Containers"), four [Labels](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Label.html) (under "Controls"), four [Rectangles](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/shape/Rectangle.html) (under "Shapes"), and three [Buttons](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Button.html). 

Each AnchorPane will hold a "selector" in it. However, we'll be adding them in programmatically, so there won't actually be anything inside of the pane at this stage. To make the AnchorPanes easier to see, you can add a border. Under "Properties" in the right sidebar, scroll down until you see the header "JavaFX CSS". Under "Style", there are two TextFields followed by two buttons. In the first text field, type `-fx-border-color`, and in the second, type `black`. Then, click the plus-sign button. This will add a style definition to the AnchorPane which adds a black border. 

Each Label will be paired with a Rectangle. We'll be changing each Label's text to reflect a custom color name in a selector, and we'll be changing each corresponding Rectangle's fill color to match the selected color in a ColorPicker. Two Label/Rectangle pairs will be bound to each "selector".

### <a name="mainJava"></a>Main.java
You won't have to do much with this class. It's fairly standard for a JavaFX application.
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
        primaryStage.setTitle("FXMLExercise");

        Parent root = FXMLLoader.load(getClass().getResource("selector.fxml"));

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
```
`primaryStage.setTitle("FXMLExercise)` sets the title of the Stage. The value here is what willl show up in the title bar of the window that the application runs in.

After that, we assign the return value of FXMLLoader's static `load()` method to the Parent variable `root`. Remember to change the location of the resource file to match your FXML file, if you made your own. 

`primaryStage.setScene(new Scene(root))` sets the Scene of the primary Stage to a new instance of Scene, using `root` as the root node.

### <a name="selectorJava"></a>SelectorController.java
1. We'll start by creating attributes for the controls. It's not necessary to create one for every single Node in the FXML file, just for the controls that we want to access in the controller. Declare attributes for each TextField, each ColorPicker, and the Button. Remember to include the @FXML tags, like this:
```java
@FXML
private Button printButton;
```
2. Next, write the properties for this controller. These properties will be bound to our controls, so we'll want to use our [modified property style](#propertyControl). At this point, we'll want four properties: one for each custom color name, and one for each color selection. The color name properties will be bound to each TextField's `textProperty()` and the color properties will be bound to each ColorPicker's `valueProperty()`. Pay attention to the type of these controls' properties to know what type yours need to be.
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

3. Next, finish the `printButtonAction()` method. It only needs to print out each custom color name and each color in a way that makes sense.


4. Now switch to your selector FXML file in SceneBuilder. First, you'll need to set the controller class for the FXML file. Under the "Controller" menu in the bottom-right of SceneBuilder, type "fxmlexercise.SelectorController" in the text field labelled "Controller class".
    - *Side Note:* In my opnion, one of the shortcomings of SceneBuilder is that you can't specify a project directory. If your controller class and FXML file were in the same directory, you'd be able to see a drop-down list of available controller classes when you clicked on the text field. Once you'd specified the controller class, the same would happen when you define `fx:id`s. However, this is not conducive to Maven's standard directory layout, and your controller class should be in a different directory. It will still work, you'll just have to be careful to type the names correctly, and SceneBuilder will complain that it can't find fields of the same name. 


5. Repeat for each TextField, each ColorPicker, and the Button: click on the control, go to the "Code" menu that appears in the right side of SceneBuilder, and in the text field labelled "fx:id", type the name of the attribute for that control in the controller class. For example, if you declared the Button attribute as "printButton", you would put "printButton" in the text field. In addition, for the Button, you'll want to put "printButtonAction" in the text field labelled "On Action". This will make it so the `printButtonAction()` method is called each time the Button is pressed.


6. Save the file, and try to build and run the program. Mess aroung with the controls, make sure that the button prints the correct information when pressed, etc.


7. Now, we're going to make the selector a bit more responsive. Back in SelectorController.java, add a BooleanProperty called `textFieldsEmpty`, along with a property getter and a value getter. After you construct the SimpleBooleanProperty, use the `Bindings.and()` and `Bindings.equal()` methods to bind `textFieldsEmpty` in such a way that the value is true if either TextField is empty. We don't need a setter for this property, because it's completely dependent on the states of both TextFields. There isn't a logical case in which we would need to set this value programmatically.
```java
private BooleanProperty textFieldEmpty;
private BooleanProperty textFieldEmptyProperty() {
  if (textFieldEmpty == null) {
    textFieldEmpty = new SimpleBooleanProperty();
    textFieldEmpty.bind( Bindings.and( 
        Bindings.equal( textField1.textProperty(), "" ),
        Bindings.equal( textField2.textProperty(), "" ) 
      ));
  }
  return textFieldEmpty;
}
public Boolean textFieldEmpty() {
  return textFieldEmptyProperty().get();
}
```
8. To make use of this new property, bind the Button's `disableProperty()` to it in `initialize()`.
```java
@FXML
protected void initialize() {
  /*
    Configure disable state logic for "Print" button.
  */
  printButton.disableProperty().bind( textFieldsEmptyProperty() );
}
```
9. Try building and running the program again, and make sure the button is disabling and enabling itself properly.

### <a name="frameJava"></a>FrameController.java
0. Inside of Main.java, change the resource location from the name of your selector FXML file to the name of your frame FXML file, so that FXMLLoader loads a FrameController instead of a SelectorController.


1. Again, declare each of the necessary attributes from the FXML file. Each of the AnchorPanes, Labels. Rectangles, and buttons will need `@FXML` tags. 


2. Declare two more attributes, both of type SelectorController, without `@FXML` tags.


3. Switch to your frame FXML file in SceneBuilder. Similar to last time, go to the "Controller" menu at the lower-left of the window, and define the controller class as `fxmlexercise.FrameController` in the field labelled "Controller class".


4. For each of the `@FXML`-tagged attributes in FrameController.java, click on it, go to the "Code" menu in the lower-right of the window, and define each Node's `fx:id` in the text field labelled "fx:id". All three Buttons should also have "printButtonAction" in the text field labelled "On Action".


5. In `initialize()` of FrameController.java, construct a new FXMLLoader with the location as the selector FXML resource. We don't want to use the static `FXMLLoader.load()` method here, because we need the instance of the controller that goes with it. 


6. Load the first SelectorController.
    - Assign the root node returned by `fxmlLoader.load()` to a variable of type Parent. 
    - Assign the controller returned by `fxmlLoader.getController()` to the instance attribute for your first SelectorController. 
    - Add the root node to your first AnchorPane's children using `anchorPane.getChildren().add(root)`.


7. Set the root and controller of your FXMLLoader to `null`, then repeat step 4 for your second SelectorController.
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
    Load selectors from FXML
   */
  try {
    // 3.
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("selector.fxml"));
    // 4.
    Parent root = fxmlLoader.load();  // load() returns the scene graph as a Node, which must be a Parent
    this.selectorOne = fxmlLoader.getController(); // getController() returns the controller object
    anchorPaneOne.getChildren().add(root);  // add the Node to the AnchorPane
    //5.
    fxmlLoader.setRoot(null);
    fxmlLoader.setController(null);
    
    // @TODO Repeat for the second AnchorPane and SelectorController.
    
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

8. Try building and running the project. There should be two selectors inside of your frame. Mess around with the controls inside; you'll see that the print buttons in the selectors still behave the same way. However, we haven't done anything with the controls in FrameController yet, so they just sit there.


9. Now that we have access to the controllers for each selector, we can access public properties and methods for them. We're going to bind the Labels and Rectangles in FrameController to some of the properties we defined in SelectorController. Between one FrameController and two SelectorControllers, there are four pairs of TextField/ColorPicker and four pairs of Label/Rectangle. For every TextField/ColorPicker and Label/Rectangle, we're going to bind the Label's `textProperty()` to the TextField's `textProperty()`, and the Rectangle's `fillProperty()` to the ColorPicker's `valueProperty()`. Don't bind them bidirectionally. A user can't interact with the Labels and Rectangles with their default behavior, and we want the locus of control to be in the properties, so we don't need to have the properties listening for changes in the Labels and Rectangles.
```java
@FXML
private Label labelOne, labelTwo, labelThree, labelFour;
@FXML
private Rectangle rectOne, rectTwo, rectThree, rectFour;

private SelectorController selectorOne, selectorTwo;
```
```java
@FXML 
protected void initialize() {
  /*
    Load selectors from FXML
   */
   
   /*
     Bind controls in this controller to the appropriate properties in the selectors' controllers.
    */
   labelOne.textProperty().bind(selectorOne.colorNameOneProperty());
   // @TODO
   // Repeat for labelTwo and selectorOne.colorNameTwoProperty()
   // Repeat for labelThree and selectorTwo.colorNameOneProperty()
   // Repeat for labelFour and selectorTwo.colorNameTwoProperty()
   
   rectOne.fillProperty().bind(selectorOne.colorOneProperty());
   // @TODO
   // Repeat for rectTwo and selectorOne.colorTwoProperty()
   // Repeat for rectThree and selectorTwo.colorOneProperty()
   // Repeat for rectFour and selectorTwo.colorTwoProperty()
   
   /*
    @TODO Configure disable state logic for "Print" buttons.
    */
}
```

10. Next, we'll define what the frame's buttons do. We're going to use a variation of how we defined the behavior of the Button in SelectorController. Notice that in FrameController.java, there's a `printButtonAction()` method declared, like in SelectorController. There's only one method, but there are three buttons, and this method has a parameter that wasn't in the other one. We're going to use that parameter to make it so that all three buttons call this method when pressed, but what happens will be based on which button was pressed. When a Node's action happens, it generates an ActionEvent which, in the case of Nodes whose action is tied to an `@FXML`-tagged method, is passed to the method as an argument. By comparing against `e.getSource()`, we can determine which button generated the ActionEvent. Remember, we want one button to print the values from the first selector, one button to print the values from the second selector, and one button to print the values from both selectors.
```java
@FXML 
private Button printButtonOne, printButtonTwo, printButtonBoth;

private SelectorController selectorOne, selectorTwo;
```
```java
@FXML
private void printButtonAction(ActionEvent e) {
  /*
    Print colors' names and values, depending on which "Print" button was pressed.
  */
  if ( e.getSource() == printButtonOne ) {
    // Print each color name and value from the first SelectorController
    System.out.println( selectorOne.getColorNameOne() + ": " + selectorOne.getColorOne() );
    System.out.println( selectorOne.getColorNameTwo() + ": " + selectorOne.getColorTwo() );
  } else if ( e.getSource() == printButtonTwo ) {
   // @TODO Print each color name and value from the second SelectorController
   
  } else if ( e.getSource() == printButtonBoth ){
    // @TODO Print each color name and value from both SelectorControllers
    
  }
}
```

11. Now let's make the frame's buttons responsive like in Selector Controller. Luckily, we wrote a property which reflects whether or not either text field is empty for our selectors, so there is much less code to write. 
    - The button that prints from the first selector should disable when either of that selector's TextFields are empty. 
    - The button that prints from the second selector should disable when either of *that* selector's TextFields are empty. 
    - The button that prints from both selectors should disable when any TextField across both selectors is empty.
```java
@FXML 
private Button printButtonOne, printButtonTwo, printButtonBoth;
@FXML 
private SelectorController selectorOne, selectorTwo;
```
```java
@FXML
protected void initialize() {
  /*
    Bind controls in this controller to the appropriate properties in the selectors' controllers
  */

  /*
    Configure disable state logic for "Print" buttons.
  */
  
  // Bind `printButtonOne.disableProperty()` to `selectorOne.textFieldEmptyProperty()`
  printButtonOne.disableProperty().bind(selectorOne.textFieldEmptyProperty() );
  
  // @TODO Bind `printButtonTwo.disableProperty()` to `selectorTwo.textFieldEmptyProperty()`
  
  // @TODO Bind `printButtonBoth.disableProperty()` to both selectors' `textFieldEmptyProperty()`
  // Hint: Use `Bindings.or()`
  
}
```

12. Fire up the program and see how it works!

## <a name="customSelector"></a>SelectorController as a Custom Control
As I mentioned in the [section](#instanceLoad) on FXMLLoader, if you need multiple instances from the same FXML document, you might as well create a custom control. So now, we're going to adapt SelectorController so it can be instantiated like a regular Java class. 

1. Go to your selector FXML file in SceneBuilder. Under the "Controller" menu in the lower-right of the window, **delete** the value in the text field labelled "Controller class". Then, check the box right below it labelled "Use fx:root construct". This will remove the `fx:controller` attribute from the selector FXML, and replace the root node with an `fx:root` tag of the same type (in this case, VBox).
    - *Side Note:* Because the `fx:root` tag has the type "VBox", attributes that would apply to a VBox can also be applied to `fx:root`. See the `alignment` attribute below:
```xml
<fx:root alignment="TOP_CENTER" type="VBox" xmlns="http://javafx.com/javafx/8.0.111" xmlns:fx="http://javafx.com/fxml/1">
    <children>
        <HBox alignment="CENTER" spacing="15.0">
            <children>
                <TextField fx:id="topTextField" minHeight="25.0" minWidth="150.0" promptText="Color Name">
/* ~~et cetera~~ */
```


2. In SelectorController.java, add "extends VBox" to the class declaration.


3. Write a constructor for Selector controller with no parameters, in which an instance of FXMLLoader is created with the location of the selector FXML file, set both the root and the controller of the FXMLLoader to `this`, and load the FXML. 
```java
public class SelectorController extends VBox {

  /* ~~attribute and property declarations~~ */

  public SelectorController() {
    try {
      FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("selector.fxml"));
      fxmlLoader.setRoot(this);
      fxmlLoader.setController(this);
      fxmlLoader.load();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  /* ~~the rest of the class~~ */
  
}
```

4. In FrameController.java, **delete** the code that loads the SelectorControllers from FXML. Replace it with a few lines that assign a new SelectorController (using the constructor we just wrote) to each SelectorController attribute, then add each SelectorController as a child of the appropriate AnchorPane.
```java
@FXML
protected void initialize() {
  /* 
    Load selectors from FXML 
  */
  
//        try {
//            FXMLLoader loaderOne = new FXMLLoader(getClass().getResource("selector.fxml"));
//
//            Parent root = loaderOne.load();  // load() returns the scene graph as a Node, which must be a Parent
//            this.leftSelector = loaderOne.getController(); // getController() returns the controller object
//            leftAnchorPane.getChildren().add(root);  // add the Node to the AnchorPane
//
//            loaderOne.setRoot(null);
//            loaderOne.setController(null);
//
//            root = loaderOne.load();  // load() returns the scene graph as a Node, which must be a Parent
//            this.rightSelector = loaderOne.getController(); // getController() returns the controller object
//            rightAnchorPane.getChildren().add(root);  // add the Node to the AnchorPane
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

  this.selectorOne = new SelectorController();
  anchorPaneOne.getChildren().add(selectorOne);
  
  // @TODO Repeat for anchorPaneTwo and selectorTwo
  
  /* ~~Bind controls in this controller to the appropriate properties in the selectors' controllers.~~ */
  
  /* ~~Configure disable state logic for "Print" buttons.~~ */
        
}
```

5. Run the program, make sure it still works.

Not having to mess with FXMLLoader in more than one place for SelectorController is nice. But, now that we've defined SelectorController as a contained custom control, what's stopping us from just including it in the frame's FXML file, and letting the FXMLLoader for FrameController handle their construction? The answer is nothing at all.

## <a name="import"></a>Importing a Custom Control into SceneBuilder
*Under Construction*

## <a name="extractor"></a>Using the CIRDLES ResourceExtractor
*Under Construction*

## <a name="references"></a>References
*Under Construction*

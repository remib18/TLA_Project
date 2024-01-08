# AdventureLang

This project was developed for our third year of computer science at the University of Tours, France.

The goal was to create a language that describes a game's adventure and then to interpret it. The language itself only describe an adventure and does not implement the game. The language is interpreted by a Java program.

## Getting Started

### Prerequisites

- Java 21 (with support for Preview features like StringTemplates and Switch Expressions).
- We recommend using IntelliJ IDEA as an IDE but you can use any IDE you want.

### Installing

You can either download the project as a zip file or clone it using git.

## The language

### Syntax

The language is composed of 3 main parts : the title, the locations and the actions.

#### General syntax

Each instruction end with a semicolon `;`. The language is case sensitive. The language only accepts integers as numbers and strings must be surrounded by double quotes `"`. The language does not accept comments. You can insert new lines, tabs and spaces wherever you want. In strings, you can use the `\` character to escape the double quotes or any character. The standard escape characters are supported.

- The syntax is case sensitive and ends each instruction with a semicolon (`;`).
- Only integers are allowed for numbers; strings should be enclosed in double quotes (`"`).
- Standard escape characters are supported within strings, using the backslash (`\`) for escaping.
- The language does not support comments.
- Whitespace (new lines, tabs, spaces) can be used freely for readability.

#### The title

The title is the first line of the file. It is composed of the keyword `setTitle` followed by the title of the adventure.

Example :

```
setTitle "My adventure";
```

#### The locations

The locations are the places where the player can go. They are composed of the keyword `addLocation` followed by the identifier of the location and the description of the location.

Each location must have a unique identifier.

One location can have one or more actions.

Example :

```
addLocation 1 "You are in a dark room" [actions];
addLocation 2 "You are in a garden";
```

#### The actions

The actions are the actions that the player can do in a location. They are defined with `->` followed by the identifier of the place to go and the description of the action.

We do recommend to put the actions in a new line and add a tabulation before the action. It is not mandatory but it makes the code more readable.

Example :

```
addLocation 1 "You are in a dark room"
    -> 2 "Go to the garden"
    -> 3 "Go to the kitchen";
addLocation 2 "You are in a garden"
    -> 1 "Go to the dark room";
addLocation 3 "You are in a kitchen"
    -> 1 "Go to the dark room";
```

### The grammar

The grammar of the language is the following :

- S -> `setTitle Str;ABCD` Allows setting the title and defines the sequence of non terminal symbols
- A -> `setHealth Int;` Allows defining the initial health

- B -> `FB | EB | ε` Allows looping over the definition of characters or objects
- E -> `addItem Var;` Allows creating an item
- F -> `addCharacter Var Int Int;` Allows creating a character

- C -> `setInventory C’;` Allows defining the initial inventory of the player
- C’ -> `Var C’ | ε`

- D -> `GD | ε` Allows looping over the addition of locations
- G -> `addLocation Int Str IH;` Allows adding a location
- H -> `-> M Int Str IH | ε` Allows adding an option to a location

- I -> `(J) | ε`  Définit une action
- J -> `J’J | ε` Allows the possibility of multiple expressions in an action
- J’ -> `K:LVar` Defines the structure of a condition (e.g., `health:+5`, `item:+$sword` ...)
- K -> `health | item | character` Types of action
- L -> `+ | -` Action operation (add / remove)

- M -> `(M’) | ε` Defines a condition
- M’ -> `NM’ | ε` Allows the possibility of multiple expressions in a condition
- N -> `Neg N’`  Defines the structure of a condition
- N’ -> `O | P` Allows two types of conditions: on objects or characters
- O -> `item:Var:Int` Defines a condition on objects (e.g., `item:$sword:1`, `item:$coin:10` ...)
- P -> `character:Var` Defines a condition on characters (eg: `character:$philip` ...)

- Neg -> `! | ε` Defines a negation
- Int -> `intVal Int | ε` Defines an integer
- Str -> `"Str'"` Defines a string
- Str' -> `charVal Str' | ε`
- Var -> `$Var'` Defines a variable
- Var' -> `charVal Var' | ε`

#### The first and follow sets [PLUS A JOUR]

The first and follow sets of the grammar are the following :

| Non-terminal  | First set            | Follow set           |
|---------------|----------------------|----------------------|
| S             | {`setTitle`}         | {`$`}                |
| A             | {`addLocation`, `ε`} | {`addLocation`, `$`} |
| B             | {`addLocation`}      | {`addLocation`, `$`} |
| C             | {`->`, `ε`}          | {`;`, `->`}          |
| Int           | {`intVal`, `ε`}      | {`"`}                |
| Str           | {`"`}                | {`;`, `->`}          |
| Str'          | {`charVal`, `ε`}     | {`"`}                |

#### State machine diagram [PLUS A JOUR]

The syntax tree of the grammar is the following :

![Syntax tree](docs/state-machine-diagram.svg)

##### The accepting states [PLUS A JOUR]

| state number | accepting   |
|--------------|-------------|
| 101          | A string    |
| 102          | A keyword   |
| 103          | An integer  |
| 104          | An arrow    |
| 105          | A semicolon |

#### Translation table [PLUS A JOUR]

The translation table of the grammar is the following :

| state | space | `"`   | `\`   | `-`   | `>`   | `;`   | `intVal` | `charVal` |
|-------|-------|-------|-------|-------|-------|-------|----------|-----------|
| **0** | 0     | 1     | ❌     | 5     | ❌     | *105* | 4        | 3         |
| **1** | 1     | *101* | 2     | 1     | 1     | 1     | 1        | 1         |
| **2** | 1     | 1     | 1     | 1     | 1     | 1     | 1        | 1         |
| **3** | *102* | *102* | *102* | *102* | *102* | *102* | 3        | 3         |
| **4** | *103* | *103* | *103* | *103* | *103* | *103* | 4        | *103*     |
| **5** | ❌     | ❌     | ❌     | ❌     | *104* | ❌     | ❌        | ❌         |

The states in italic are the accepting states. Some states that are not valid are represented by a cross.

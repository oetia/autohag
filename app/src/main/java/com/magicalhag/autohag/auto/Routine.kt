package com.magicalhag.autohag.auto

import android.accessibilityservice.AccessibilityService

abstract class Routine(private val accessibilityService: AccessibilityService) { // open marks a class as inheritable

    protected open val qwerty = 0; // private incompatible with open - protected means subclass can access it
    open fun retard() {

    }

    open fun fuckYou() {

    }
}

interface FuckYou {
    val qwerty: Int;
    open fun fuckYou() {

    }
}

// if inherits from two classes with same member, there will be conflict. you have to resolve this conflict through overriding and providing your own implementation
// can inherit only one class, but multiple interfaces
// object : InnerClass notation is used to create inner classes inherits from some class
class ArknightsRoutine(private val accessibilityService: AccessibilityService) : Routine(accessibilityService),
    FuckYou {
    override var qwerty: Int = 2
        // get() { return field } // default getter - field is the value in question, but you use field as a temp holder
        // set(value) {field = value} // default setter



    final override fun retard() {
        super.retard()
    }

    override fun fuckYou() {
        TODO("Not yet implemented")
    }

    // override fun fuckYou() {}


    // can't override function without open modifier
    // override fun fuckYou() {
    //
    // }

    inner class Asdf: Any() {

        // super accesses the superclass. when it's ambiguous which superclass is being referred to (inner class), you can use the @ symbol to resolve it
        // the same applies to this. you can use @ to determine what exactly should be referred to
        fun asdf() {
            super@ArknightsRoutine.retard() // when you need to specify which function's super method you're looking @. you can use the @ to be more specific
            super@Asdf.toString()
        }
    }
}
open class Rectangle {
    open fun draw() { println("Drawing a rectangle") }
    val borderColor: String get() = "black"
}
class FilledRectangle: Rectangle() {
    override fun draw() {
        val filler = Filler()
        filler.drawAndFill()
    }

    inner class Filler {
        fun fill() { println("Filling") }
        fun drawAndFill() {
            super@FilledRectangle.draw() // Calls Rectangle's implementation of draw()
            fill()
            println("Drawn a filled rectangle with color ${super@FilledRectangle.borderColor}") // Uses Rectangle's implementation of borderColor's get()
        }
    }
}

// extending classes
/*
makes it so that i don't have to pass blockText to every single fucking function call to specify context.

 */
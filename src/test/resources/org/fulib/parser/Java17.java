package org.fulib.parser;

// --------------- Sealed Examples ---------------
// https://docs.oracle.com/en/java/javase/17/language/sealed-classes-and-interfaces.html

public sealed class Figure
{}

final class Circle extends Figure
{
   float radius;
}

non-sealed class Square extends Figure
{
   float side;
}

sealed class Rectangle extends Figure
{
   float length, width;
}

final class FilledRectangle extends Rectangle
{
   int red, green, blue;
}

public sealed class Shape
   permits com.example.polar.Circle,
   com.example.quad.Rectangle,
   com.example.quad.simple.Square
{}

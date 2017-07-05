# uncertainty-calculator-android

An Android app that performs calculations on numbers with uncertainty (numbers of the form `a ± b`). Available on the Google Play Store as [Uncertainty Calculator](https://play.google.com/store/apps/details?id=com.nooverlap314.uncertaintycalculator).

## Use

This app is intended to make calculations in science and engineering fields faster and more reliable.

Typical use consists of the following actions:

* Enter an equation in the text box (using saved variables\*, \pi, and *e*)
* Double check that the preview displays the equation correctly (use more brackets if not).
* Press the "=" button or the enter key to see the result.
* Press the "FORMAT" button to see the result in engineering format.
* Press the "SAVE" button to store the current result in a (chosen) save slot, for later use.

\* Previous results may be referred to in calculations by the letter of a save slot. These may be found and entered through the "SAVED" menu.

A full description of use and calculations may be accessed in-app by tapping the info button.

## Calculations

All uncertainty calculations are defined by the following three rules:

* Absolute uncertainties\* are summed in addition and subtraction.
* Percentage uncertainties\* are summed in division and multiplication.
* For other mathematical operations, the brute force\*\* method is used.

\* For a number `a ± b`, `b` is the absolute uncertainty and `b / a * 100` is the percentage uncerainty.

\*\* For some function `f(a ± b)`, the absolute uncertainty calculated by the brute force method is `f(a + b) - f (a)`.

### Supported Operations

The following standard operators are supported (listed in order of decreasing precedence).  

| Operator | Symbol |  
| --- | --- |  
| Brackets | `(`, `)` |  
| Uncertainty | `±` |  
| Exponentiation | `^` |  
| Division | `/` |  
| Multiplication | `*` |  
| Addition | `+` |  
| Subtraction | `-` |  

The following mathematical functions are supported.

| Logarithms | Trigonometry | Inverse Trig. | Hyperbolic Trig. | Other |  
| --- | --- | --- | --- | --- |  
| log | sin | asin | sinh | sqrt |  
| ln | cos | acos | cosh |  
| exp | tan | atan | tanh |  

All trigonometric calculations take place in radians. All mathematical functions must be written with brackets, as `f(a ± b)`.


## Example
In a physics lab experiment involving fluid dynamics, the following expression needed to be evaluated:  
`sqrt(2 * 9.81 * (0.11 ± 0.002))`  
By typing that equation (or one without the brackets around the uncertainty) in the provided text-box, the equation preview field displays the (same) correctly formatted equation.

A tap of the "=" button replaces the equation with the following result:  
`1.4690813456034353 ± 0.013355284960031228`  
along with the percentage uncertainty (which is useful for intermediate calculations).

As this was the final calculation, the only remaining is to convert this to the standard uncertainty format. A tap of the "FORMAT" button yields:  
`1.47 ± 0.01`  

The alternative (manual) method involves evaluating the expression using the nominal value (1.4690813456034353), then evaluating the absolute uncertainty by multiplying this result by half of the percentage uncertainty (due to the square root) of 0.11±0.002 (0.9090…%) yielding 0.013355284960031228 - the same result.

## Authors

* **Marc Katzef** - [mkatzef](https://github.com/mkatzef)

## Acknowledgements

* **Dave Barton** - author of the library [`jqMath`](http://mathscribe.com/author/jqmath.html) which powers the equation preview.

* **Roman Nurik** - [romannurik](https://github.com/romannurik), author of the online tool [`Android Asset Studio`](https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html), used to generate this application's icon.
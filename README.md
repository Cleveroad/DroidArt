# WordArt

## Setup and usage
### Installation
by gradle : 
```groovy
dependencies {
    implementation 'com.cleveroad.wordart:wordart:1.0.0.SNAPSHOT'
}
```
### Usage ###
#### WordArt ####
```XML
  <com.cleveroad.wordart.EditorView
          android:id="@+id/evSample"
          android:layout_width="match_parent"
          android:layout_height="match_parent"
          app:font_path="fonts/some_fonts.ttf"
          app:text="@string/some_text"
          app:text_color="@color/text_color"
          app:text_shadow_color="@color/text_shadow"
          app:text_size="25sp"/>
```
|  attribute name | description |
|---|---|
| text  | Text value for displaying. |
| text_color  | Value of color for displaying text. |
| text_size  | Value for size of displaying text. |
| text_elevation_percent  | Elevation in percent. Shadow will be shown only if [textShadowColor] will be set. |
| text_shadow_color  | Color of the shadow of the text. |
| text_shadow_blur_radius  | Radius for shadow blur. |
| selector_color | Value of color for the selector rectangle. |
| button_color | Value of color for the control buttons. |
| font_id | Id to the custom font for text. Note than if id will be incorrect, you will receive [RuntimeException]. |
| icon_reset_button | drag and drop mode. If enable, column or row will change its position with dragging after long press on row or column header. |
| icon_scale_button | drag and drop mode. If enable, column or row will change its position with dragging after long press on row or column header. |
| icon_change_view_button | drag and drop mode. If enable, column or row will change its position with dragging after long press on row or column header. |

```groovy
/**
* Text value for displaying
*/
var text: String

/**
 * Value of color for displaying text
 */
var textColor: Int

/**
 * Value for size of displaying text
 */
var textSize: Float

/**
 * Id to the custom font for text
 *
 * Note than if id will be incorrect, you will receive [RuntimeException]
 */
var fontId: Int

/**
 * Elevation in percent
 *
 * Shadow will be shown only if [textShadowColor] will be set
 */
var textElevationPercent: Int

/**
 * Color of the shadow of the text
 */
var textShadowColor: Int

/**
 * Radius for shadow blur
 */
var textShadowBlurRadius: Float

/**
 * Use the method to subscribe to receive a callback when a touch event occurs
 *
 * @param touchEventCallback  The interface for listener to the touch event
 */
fun subscribeTouchEventCallback(touchEventCallback: TouchEventCallback)

/**
 * Use the method to subscribe to receive a callback when a touch event occurs
 *
 * @param changeViewTextCallback  The interface for listener to the change view text mode
 */
fun subscribeChangeViewTextCallback(changeViewTextCallback: ChangeViewTextCallback)

/**
 * Use the method to cancel the subscription of receiving a callback
 * when a touch event occurs
 *
 * @param touchEventCallback  The interface for listener to the touch event
 */
fun unsubscribeTouchEventCallback(touchEventCallback: TouchEventCallback)

/**
 * Use the method to cancel the subscription of receiving a callback
 * when a change view text mode occurs
 *
 * @param changeViewTextCallback  The interface for listener to the change view text mode
 */
fun unsubscribeChangeViewTextCallback(changeViewTextCallback: ChangeViewTextCallback)

/**
 * Use the method to set [PathEffect] to the selector paint
 * @see PathEffect
 */
fun setPathEffectForSelector(effect: PathEffect)

/**
 * Use the method to set [Color] to the selector rectangle
 * @see Color
 */
fun setColorForSelector(@ColorInt intColor: Int)

/**
 * Use the method to set [Color] to the dash paint
 * @see Color
 */
fun setColorForDashLine(@ColorInt intColor: Int)

/**
 * Use the method to set [Color] to control buttons
 * @see Color
 */
fun setColorForSelectorButton(@ColorInt intColor: Int)

/**
 * Use the method to set [Color] to shadow
 * @see Color
 */
fun setColorForTextShadow(@ColorInt intColor: Int)

/**
 * Use the method to set the width for stroking
 *
 * @param width set the paint's stroke width, used whenever the paint's
 *              style is Stroke or StrokeAndFill.
 */
fun setStrokeWidthForSelector(width: Float)

/**
 * Use the method to set the width for stroking
 *
 * @param width set the paint's stroke width, used whenever the paint's
 *              style is Stroke or StrokeAndFill.
 */
fun setStrokeWidthForDashLine(width: Float)

/**
 * Use the method to set the icon for the change view button
 *
 * @param resId  The resource id for the button icon
 */
fun setBitmapChangeViewTextButton(@DrawableRes resId: Int)

/**
 * Use the method to set the icon for the scale button
 *
 * @param resId  The resource id for the button icon
 */
fun setBitmapScaleButton(@DrawableRes resId: Int)

/**
 * Use the method to set the icon for the reset button
 *
 * @param resId  The resource id for the button icon
 */
fun setBitmapResetChangeViewTextButton(@DrawableRes resId: Int)

/**
 * Use the method to obtain current [ChangeText] mod
 */
fun getChangeViewTextMode()

/**
 * Use the method for set the [ShowButtonOnSelector] mode for the button
 *
 * @param showButtonOnSelector  The enum for show a button on the selector
 */
fun showScaleRotateButton(showButtonOnSelector: ShowButtonOnSelector)

/**
 * Use the method for set the [ShowButtonOnSelector] mode for the button
 *
 * @param showButtonOnSelector  The enum for show a button on the selector
 */
fun showChangeViewTextButton(showButtonOnSelector: ShowButtonOnSelector)

/**
 * Use the method for set the [ShowButtonOnSelector] mode for the button
 *
 * @param showButtonOnSelector  The enum for show a button on the selector
 */
fun showResetViewTextButton(showButtonOnSelector: ShowButtonOnSelector)

/**
 * Use the method for set the [ChangeText] mode for text
 *
 * @param changeText  The enum with different types of actions for interacting with text
 */
fun changeViewTextMode(changeText: ChangeText)

/**
 * Use the method for set the initial value for the Bezier curve
 */
fun resetViewText()

/**
 * Use the method to draw the text in the bitmap
 *@see Bitmap
 *
 * @param bitmap  The bitmap that will be drawn below the text
 */
fun saveResult(bitmap: Bitmap): Bitmap

```
#### Fragment/Activity usage ####
```groovy
private const val DASH_PATH_ON_DISTANCE = 30F
private const val DASH_PATH_OFF_DISTANCE = 10F
private const val DASH_PATH_PHASE = 0F
private const val STROKE_WIDTH_FOR_DASH_LINE = 4F
...
evWordArt = (EditorView) view.findViewById(R.id.editorView)

evWordArt?.apply {
    setPathEffectForSelector(
                DashPathEffect(floatArrayOf(DASH_PATH_ON_DISTANCE, DASH_PATH_OFF_DISTANCE),
                DASH_PATH_PHASE))
    setStrokeWidthForDashLine(STROKE_WIDTH_FOR_DASH_LINE)
    setColorForTextShadow(Color.GRAY)
    setColorForSelectorButton(Color.WHITE)
    setColorForDashLine(Color.WHITE)
    showScaleRotateButton(ShowButtonOnSelector.HIDE_BUTTON)
    showResetViewTextButton(ShowButtonOnSelector.HIDE_BUTTON)
    subscribeTouchEventCallback(touchEventCallback)
}
```
#### XML usage ####
```groovy
   <com.cleveroad.wordart.EditorView
           android:id="@+id/evSample"
           android:layout_width="match_parent"
           android:layout_height="match_parent"
           app:font_path="fonts/some_fonts.ttf"
           app:text="@string/some_text"
           app:text_color="@color/text_color"
           app:text_shadow_color="@color/text_shadow"
           app:text_size="25sp"/>
```
### Support ###
If you have any questions, issues or propositions, please create a <a href="../../issues/new">new issue</a> in this repository.

If you want to hire us, send an email to sales@cleveroad.com or fill the form on <a href="https://www.cleveroad.com/contact">contact page</a>

Follow us:

[![Awesome](/images/social/facebook.png)](https://www.facebook.com/cleveroadinc/)   [![Awesome](/images/social/twitter.png)](https://twitter.com/cleveroadinc)   [![Awesome](/images/social/google.png)](https://plus.google.com/+CleveroadInc)   [![Awesome](/images/social/linkedin.png)](https://www.linkedin.com/company/cleveroad-inc-)   [![Awesome](/images/social/youtube.png)](https://www.youtube.com/channel/UCFNHnq1sEtLiy0YCRHG2Vaw)
<br/>

### License ###
* * *
    The MIT License (MIT)
    
    Copyright (c) 2016 Cleveroad Inc.
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.

[changelog history]: /CHANGELOG.md
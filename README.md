# text-chip
TextChip sample application

This sample application demonstrates the usage of the text-chip library which is a UI element based on the Chip from material design guidelines

Get the sample app:

<a href='https://play.google.com/store/apps/details?id=com.divshark.textchipsample&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' width="200" height="75"/></a>

Screenshots:

<img src="https://github.com/kylejablonski/text-chip/blob/master/app/art/text-chip-sample.png" style="display:inline-block" width="360" height="640"/>
<img src="https://github.com/kylejablonski/text-chip/blob/master/app/art/text-chip-2.png" style="display:inline-block" width="360" height="640"/>

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
Step 2. Add the dependency

	dependencies {
	        compile 'com.github.kylejablonski:text-chip:1.0.2'
	}
	
	
Library usage:

add a TextChip to your xml file:

    <com.divshark.text_chip.TextChip
        android:id="@+id/text_chip"
        android:layout_centerHorizontal="true"
        android:padding="@dimen/activity_horizontal_margin"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"/>
        
        
Get a reference to the chip and set its values:

    TextChip mChip = (TextChip) findViewById(R.id.text_chip);
  
    mChip.setTextColor(Color.BLACK);/* set the text color */
    mChip.setText("Hello, World!"); /* Set the text */
    mChip.setTextSize(18 * 2);/* set the Text size */
    mChip.setBackgroundColor(Color.parseColor("#2196F3")); /* add a background color*/
    mChip.setUpperCase(true) /* Set capital letters */


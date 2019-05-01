package stepDefinitions;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import cucumber.api.java.en.Then;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

//import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;


public class stepDefinition {
	WebElement nextEpisode;
	static WebDriver mDriver = null;

	@Given("^user is on EastEnders programme on BBC website$")
    public void user_is_on_eastenders_programme_on_bbc_website()
	{
		mDriver = new ChromeDriver();
		System.setProperty("webdriver.chrome.driver", "chromedriver\\chromedriver.exe");
		mDriver.get("https://www.bbc.co.uk/programmes/b006m86d");
		System.out.println("print from 1" + mDriver);
	}

	@When("^user finds Next episode card$")
    public void user_finds_next_episode_card() 
	{
		try
		{
			WebElement mainContent = mDriver.findElement(By.cssSelector("[role=main]"));
			nextEpisode = mainContent.findElement(By.cssSelector("[data-map-column=tx]"));
		}
		catch(NoSuchElementException e)
		{
			System.err.println("Next episode card hasn't found");
		}
		
	}

	@Then("^user should correct date of the next episode$")
    public void user_should_correct_date_of_the_next_episode()
	{
		String [] parts = nextEpisode.getText().split("\n");
		Calendar episodeShown = getNextEpisodeDate(parts);
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		Calendar episodeExpected = getExpectedNextEpisodeDate(cal);
		
		System.out.println(displayCalendar(episodeShown)+ "-->" + displayCalendar(episodeExpected));
		
		if (isEqual(episodeShown, episodeExpected) == true) {
			System.out.println("Dates are equal");
		}
		else {
			System.out.println("Dates are NOT equal");
		}
	}
	
	/**
	 * Compares the Calendar dates in terms of date, and time.
	 */
	public boolean isEqual(Calendar cal1, Calendar cal2) {
		if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
			cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
			cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE) &&
			cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY) &&
			cal1.get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE)) {
				return true;
			}
		return false;
	}
	
	public Calendar getCalendarFromString(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date date;
		try {
			date = sdf.parse(strDate);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
	
	/**
	 * Returns a Calendar object from the date shown in text in the web site.
	 */
	public Calendar getNextEpisodeDate(String [] parts)
	{
		String dayTime=parts[2];
		String [] parts2 = dayTime.split(" ");
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		 
		String dateInString = parts[1]+" "+parts2[1]; 
		return getCalendarFromString(dateInString);
	}
	
	/**
	 * Adds dateoffset date to the calendar object and
	 * set the hour and min as passed.
	 */
	public Calendar setTime(Calendar c, int dateoffset, int hour, int min) {
		Calendar cal = (Calendar) c.clone();
		cal.add(Calendar.DAY_OF_MONTH, dateoffset);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, min);
		return cal;
	}

	/**
	 * Computes the next episode date based on current date in the param,
	 * The program occurs on Mon, Tues, Thurs, and Fri.
	 */
	public Calendar getExpectedNextEpisodeDate(Calendar c ) 
	{
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		switch (dayOfWeek) {
			case Calendar.MONDAY:
			case Calendar.FRIDAY:
				if (c.get(Calendar.HOUR_OF_DAY) > 20 && c.get(Calendar.MINUTE) > 0) {
					if (dayOfWeek == Calendar.MONDAY) {
						return setTime(c, 1, 19, 30); // Next episode is on Tuesday.
					}
					else if (dayOfWeek == Calendar.FRIDAY) {
						return setTime(c, 3, 20, 00); // Next episode is on Monday.
					}
				}
				else {
					if (dayOfWeek == Calendar.MONDAY) {
						return setTime(c, 0, 20, 00); // Next episode is today.
					}
					else if (dayOfWeek == Calendar.FRIDAY) {
						return setTime(c, 0, 20, 00); // Next episode is today.
					}
				}
				break;
			
			case Calendar.TUESDAY:
			case Calendar.THURSDAY:
				if (c.get(Calendar.HOUR_OF_DAY) > 19 && c.get(Calendar.MINUTE) > 30) {
					if (dayOfWeek == Calendar.TUESDAY) {
						return setTime(c, 2, 19, 30);
					}
					else if (dayOfWeek == Calendar.THURSDAY) {
						return setTime(c, 1, 20, 00);
					}
				}
				else {
					if (dayOfWeek == Calendar.TUESDAY) {
						return setTime(c, 0, 19, 30);
					}
					else if (dayOfWeek == Calendar.THURSDAY) {
						return setTime(c, 0, 19, 30);
					}
				}
				break;
			case Calendar.WEDNESDAY:
				return setTime(c, 1, 19, 30);
			case Calendar.SATURDAY:
				return setTime(c, 2, 20, 00);
			case Calendar.SUNDAY:
				return setTime(c, 1, 20, 00);
		}
		return null; // We should never reach here.
	}
	/**
	 * Returns string for the calendar for debugging and display.
	 */
	public static String displayCalendar(Calendar cal) {
		if (cal == null) {
			return "null";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		return sdf.format(cal.getTime());
	}
}

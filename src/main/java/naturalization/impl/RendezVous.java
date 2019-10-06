package naturalization.impl;

import naturalization.exceptions.BookingPageLoadingException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static naturalization.helpers.Constants.*;



public class RendezVous{

    Logger logger = Logger.getLogger(RendezVous.class.getName());


    final String baseUrl = "http://www.val-de-marne.gouv.fr/booking/create/4963/1";

    WebDriver driver;

    public RendezVous() throws IOException, BookingPageLoadingException {
        driver = new HtmlUnitDriver();
        driver.get(baseUrl);
        handleBan(driver);
        testCalendarAvailability();
        //we're done, close the drive
        driver.close();

    }


    public void testCalendarAvailability() throws IOException, BookingPageLoadingException{

        selectCounter(By.id("planning5955"));

        By confirm = By.name("nextButton");
        driver.findElement(confirm).click();

        //page successfully changed
        if(!BOOKING_PAGE_URL.equals(driver.getCurrentUrl())){
            throw new BookingPageLoadingException(CANT_LOAD_THE_BOOKING_PAGE);
        }


        By noCalendar = By.id("FormBookingCreate");
        // naturalization calendar is 99% of the time off, the event is set to a failure by default
        try {
            String formText =  driver.findElement(noCalendar).getText();
            if(formText.contains(NO_TIME_RANGE_AVAILABLE)){
                logger.warning(NO_APPOINTMENT_AVAILABLE);
                //log to csv : attempt & timedate
                updateEventFile(ATTEMPT_EVENT_FAILURE + getFormattedCurrentDate());
            }else{
                logger.warning(APPOINTMENT_AVAILABLE);
                //booking create form does exist with a different content, it's probably an updated calendar
                //log the event and send e-mail.
                updateEventFile(ATTEMPT_EVENT_SUCCESS + getFormattedCurrentDate());
            }
        }catch (NoSuchElementException noSuchElementException){
            logger.warning(APPOINTMENT_AVAILABLE);
            updateEventFile(ATTEMPT_EVENT_SUCCESS + getFormattedCurrentDate());
        }

    }

    private void updateEventFile(String event) throws IOException {
        FileWriter eventWriter = new FileWriter("rendezVousLog.csv", true);

        eventWriter.append(event);
        eventWriter.append("\n");

        eventWriter.flush();
        eventWriter.close();
    }

    private void selectCounter(By id) {
        By counter1 = id;
        try {
            driver.findElement(counter1).click();
        }catch(Exception ex){
            logger.warning("Cant select the right counter");
        }
    }

    //make sure we are not banned or blacklisted by the webserver
    private void handleBan(WebDriver driver) {
        if(!driver.getTitle().equals(PAGE_TITLE)){
            if(driver.getTitle().equals("502 Bad Gateway")){
                //log temporary ban event and sleep for 3min
                driver.manage().timeouts().implicitlyWait(3, TimeUnit.MINUTES);
                try {
                    updateEventFile(TEMPORARY_BANNED + ", " + getFormattedCurrentDate());
                }catch (IOException IE){
                    logger.warning(CANT_OPEN_LOG_EVENT_FILE);
                }
            }
        }
    }

    private String getFormattedCurrentDate(){
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date currentDate = new Date(System.currentTimeMillis());
        return formatter.format(currentDate);
    }
}

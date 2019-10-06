import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;


public class RendezVousTest {

    Logger logger = Logger.getLogger(RendezVousTest.class.getName());

    public static final String ATTEMPT_EVENT_FAILURE = "Failure, ";
    public static final String ATTEMPT_EVENT_SUCCESS = "Failure, ";
    public static final String NO_TIME_RANGE_AVAILABLE = "Il n'existe plus de plage horaire libre";
    public static final String NO_APPOINTMENT_AVAILABLE = "No appointment available :(";
    public static final String APPOINTMENT_AVAILABLE = "Appointment available !";

    final String baseUrl = "http://www.val-de-marne.gouv.fr/booking/create/4963/1";
    WebDriver driver;

    @Before
    public void setUp() throws Exception{
        driver = new HtmlUnitDriver();
    }

    @Test
    public void testRdvPageIsUp() throws Exception{
        driver.get(baseUrl);
        assertEquals(driver.getTitle(),"Prise de rendez-vous au titre de la naturalisation - Les services de l'État dans le Val de Marne");
    }

    @Test
    public void testCalendarAvailability() throws Exception{

        driver.get(baseUrl);

        selectCounter(By.id("planning5955"));

        By confirm = By.name("nextButton");
        driver.findElement(confirm).click();

        //page successfully changed
        assertEquals(driver.getCurrentUrl(),"http://www.val-de-marne.gouv.fr/booking/create/4963/2");


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

        //driver.findElement(noCalendar).getText().contains("Il n'existe plus de plage horaire libre");

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

    private String getFormattedCurrentDate(){
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date currentDate = new Date(System.currentTimeMillis());
        return formatter.format(currentDate);
    }

    @After
    public void tearDown() throws Exception{
        driver.close();
    }

    //By title = By.xpath("/html/head/title");
    //driver.findElement(title).getText();
}

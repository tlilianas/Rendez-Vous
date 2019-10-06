package naturalization;

import naturalization.exceptions.BookingPageLoadingException;
import naturalization.impl.RendezVous;

import java.io.IOException;

public class Launcher {

    public static void main(String[] args){
        try {
            RendezVous rendezVous = new RendezVous();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (BookingPageLoadingException e) {
            e.printStackTrace();
        }

    }
}

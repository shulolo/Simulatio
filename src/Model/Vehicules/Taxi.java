package Model.Vehicules;

import Model.Itineraire;

public class Taxi extends Voiture {

    /**
     *
     */
    
    public Taxi(Itineraire intineraire, double longueur, double vitesse, double vitesseMax) {
        super(intineraire, longueur, vitesse, vitesseMax);
    }

}

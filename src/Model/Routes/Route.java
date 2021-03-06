/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model.Routes;

import Model.Intersections.Intersection;
import Model.Signalisations.CouleurFeu;
import Model.Signalisations.Feu;
import Model.Signalisations.PanneauCedezLePassage;
import Model.Signalisations.PanneauStop;
import Model.Signalisations.Signalisation;
import Model.Vehicules.Vehicule;
import Model.VoieDeCirculation;
import Model.ZoneSpecifiques.Station;
import Model.ZoneSpecifiques.ZoneARisque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Benjamin
 */
public class Route extends VoieDeCirculation {

    private Intersection depart;
    private Intersection arrivee;

    private String nomRoute;
    private double longueur;

    // HashMap de vehicules avec leur distance à parcourir
    private Map<Vehicule, Double> voie;

    private List<Signalisation> signalisations;
    private List<ZoneARisque> zonesARisque;
    private List<Station> stations;

    private boolean routePleine;

    private int nbFeu;
    private int nbCedezLePassage;
    private int nbStop;

    private static final double PAS = 2;
    private static final int NOMBRE_MAX_FEUX = 1;
    private static final int NOMBRE_MAX_CEDEZLEPASSAGE = 1;
    private static final int NOMBRE_MAX_STOP = 1;

    public Route(String nom, Intersection depart, Intersection arrivee, double longueur, Map<Vehicule, Double> voie, List<Signalisation> signalisations, List<ZoneARisque> zonesARisque, List<Station> stations) {
        this.nomRoute = nom;
        this.depart = depart;
        this.arrivee = arrivee;
        this.longueur = longueur;
        this.voie = voie;
        this.signalisations = signalisations;
        this.zonesARisque = zonesARisque;
        this.stations = stations;
        this.depart.addRoute(this);
    }

    public boolean ajouterSignalisation(Signalisation s) {
        if (s instanceof Feu && this.getNbFeu() <= NOMBRE_MAX_FEUX) {
            return signalisations.add(s);
        } else if (s instanceof PanneauCedezLePassage && this.getNbCedezLePassage() <= NOMBRE_MAX_CEDEZLEPASSAGE) {
            return signalisations.add(s);
        } else if (s instanceof PanneauStop && this.getNbFeu() <= NOMBRE_MAX_FEUX) {
            return signalisations.add(s);
        }
        return false;
    }

    public boolean ajouterStation(Station st) {
        return stations.add(st);
    }

    public boolean ajouterZoneARisque(ZoneARisque zar) {
        return zonesARisque.add(zar);
    }

    public boolean ajouterVehicule(Vehicule v) {
        if (!this.voie.values().contains(this.longueur)) {
            for (Map.Entry<Vehicule, Double> entry : this.voie.entrySet()) {
                Vehicule vehicule = entry.getKey();
                Double double1 = entry.getValue();
                if (double1 + vehicule.getLongueur() + Route.PAS >= this.longueur) {
                    return false;
                }
            }
            this.voie.put(v, this.longueur);
            return true;
        }
        return false;
    }

    public boolean supprimerVehicule(Vehicule v) {
        this.voie.remove(v);
        return true;
    }

    public void setLongueur(double longueur) {
        this.longueur = longueur;
    }

    public void setRoutePleine(boolean routePleine) {
        this.routePleine = routePleine;
    }

    public double getLongueur() {
        return longueur;
    }

    public boolean isRoutePleine() {
        return routePleine;
    }

    public String getNomRoute() {
        return nomRoute;
    }

    public int getNbFeu() {
        for (Signalisation s : signalisations) {
            if (s instanceof Feu) {
                nbFeu += 1;
            }
        }
        return nbFeu;
    }

    public int getNbCedezLePassage() {
        for (Signalisation s : signalisations) {
            if (s instanceof PanneauCedezLePassage) {
                nbFeu += 1;
            }
        }
        return nbCedezLePassage;
    }

    public int getNbStop() {
        return nbStop;
    }

    public Intersection getDepart() {
        return depart;
    }

    public Intersection getArrivee() {
        return arrivee;
    }

    @Override
    public String toString() {
        return "Route{" + "nomRoute=" + nomRoute + ", longueur=" + longueur + ", routePleine=" + routePleine + ", nbFeu=" + nbFeu + ", nbCedezLePassage=" + nbCedezLePassage + ", nbStop=" + nbStop + '}';
    }

    public void avancer(Vehicule v, double distanceParcourue) throws AvancerException {
        if (this.voie.containsKey(v)) {
            boolean peutAvancer = true;
            double distance = this.voie.get(v);
            double distanceRestante = distance - distanceParcourue;
            Feu f = null;
            if (distanceRestante <= 0) {
                for (Signalisation signalisation : this.signalisations) {
                    if (signalisation instanceof Feu) {
                        f = (Feu) signalisation;
                    }
                }
                if (f != null) {
                    if (f.getCouleurFeu() != CouleurFeu.ROUGE) {
                        //System.out.println("[!!] Avance ! Feu Vert");
                        this.avancerRoute(v);
                    } else {
                        //System.out.println("[!!] Ne peut pas avancer ! Feu Rouge");
                        throw new AvancerException("[!!] Ne peut pas avancer ! Feu Rouge");
                    }
                } else {
                    this.avancerRoute(v);
                }
            } else {
                for (Map.Entry<Vehicule, Double> entry : this.voie.entrySet()) {
                    if (!entry.getKey().equals(v) && entry.getValue() < this.voie.get(v)) {
                        if (entry.getValue() <= distanceRestante - entry.getKey().getLongueur()) {
                            throw new AvancerException("[!!] Ne peut pas avancer ! Voiture devant");
                        }
                    }
                    /*if (!entry.getKey().equals(v)
                            && entry.getValue() <= distance
                            && entry.getValue() >= distanceRestante
                            && entry.getValue() <= distanceRestante - entry.getKey().getLongueur()) {
                        //System.out.println("[!!] Ne peut pas avancer ! Voiture devant");
                        throw new AvancerException("[!!] Ne peut pas avancer ! Voiture devant");
                        //peutAvancer = false;
                        //break;
                    }*/
                }
            }
            if (peutAvancer) {
                this.voie.put(v, distanceRestante);
                //System.out.println(this.voie.get(v));
            }
        }
    }

    private void avancerRoute(Vehicule v) {
        this.supprimerVehicule(v);
        VoieDeCirculation vdc = v.prochainDeplacement();
        if (vdc != null) {
            vdc.ajouterVehicule(v);
        }
    }

}

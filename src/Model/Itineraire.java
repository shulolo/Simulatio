/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import Model.Intersections.Intersection;
import Model.Routes.Route;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Benjamin
 */
public class Itineraire {

    private List<VoieDeCirculation> parcours;
    private List<VoieDeCirculation> voiesPrecedentes;

    private Itineraire(Carte c, VoieDeCirculation depart, VoieDeCirculation arrivee) {
        this.parcours = new ArrayList<VoieDeCirculation>();
        this.voiesPrecedentes = new ArrayList<VoieDeCirculation>();
        //this.calculerItineraire(c, depart, arrivee);
    }

    /**
     * Fonction "prochainDeplacement". Fonction permettant d'obtenir la
     * prochaine route de l'itinéraire.
     *
     * @return VoieDeCirculation, prochaine voie de circulation à parcourir.
     */
    public VoieDeCirculation prochainDeplacement() {
        VoieDeCirculation voie = null;

        if (!this.parcours.isEmpty()) {
            voie = this.parcours.get(0);
            this.voiesPrecedentes.add(voie);
            this.parcours.remove(voie);
        }

        return voie;
    }

    /**
     * Fonction "getVoiePrecedente". Fonction permettant de récupère la dernière
     * parcourue ou qui est en train d'être parcourue.
     *
     * @return VoieDeCirculation, dernière voie parcourue ou voie en train
     * d'âtre parcourue.
     */
    public VoieDeCirculation getVoiePrecedente() {
        VoieDeCirculation voie = null;

        if (!this.parcours.isEmpty()) {
            voie = this.voiesPrecedentes.get(this.voiesPrecedentes.size() - 1);
        }

        return voie;
    }

    public void setParcours(List<VoieDeCirculation> parcours) {
        this.parcours = parcours;
    }

    public Itineraire getItineraire(Carte c, Route depart, Route arrivee) throws Exception {
        Itineraire i = new Itineraire(c, depart, arrivee);
        List<VoieDeCirculation> l;
        l = calculerItineraire(depart, arrivee);
        if (l != null) {
            i.setParcours(parcours);
        } else {
            throw new Exception("Probleme lors de la génération de l'itinéraire !");
        }
        return i;
    }

    private List<VoieDeCirculation> calculerItineraire(Route depart, Route arrivee) {
        List<VoieDeCirculation> chemin = new ArrayList<VoieDeCirculation>();
        chemin.addAll(parcoursGraphe(null, depart.getArrivee(), arrivee.getArrivee()));
        return chemin;
    }

    private List<VoieDeCirculation> parcoursGraphe(Intersection precendent, Intersection depart, Intersection arrivee) {
        List<VoieDeCirculation> parcours = new ArrayList<VoieDeCirculation>();
        List<VoieDeCirculation> tmp = null;
        parcours.add(depart);
        for (Route r : depart.getRoutes()) {
            if (r.getArrivee().equals(arrivee)) {
                parcours.add(r);
                return (parcours);
            } else if (precendent == null) {
                parcours.add(r);
                tmp = parcoursGraphe(depart, r.getArrivee(), arrivee);
            } else if (!precendent.equals(r.getArrivee())) {
                parcours.add(r);
                tmp = parcoursGraphe(depart, r.getArrivee(), arrivee);
            }
            if (tmp != null) {
                parcours.addAll(tmp);
                parcours.add(arrivee);
                return (parcours);
            }
        }

        return null;
    }
}
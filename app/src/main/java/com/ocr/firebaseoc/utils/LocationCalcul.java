package com.ocr.firebaseoc.utils;

//Classe utilitaire nous permettant de calculer la distance entre la position de l'ecole et la position de l'utilisateur grâce à la formule de Haversine .
public class LocationCalcul {
    private static final double R = 6371; // rayon de la Terre en kilomètres

    private final double referenceLatitude;
    private final double referenceLongitude;
    private final double userLatitude;
    private final double userLongitude;

    private final double rayonDeTolerance ;

    public LocationCalcul(double referenceLatitude, double referenceLongitude, double userLatitude, double userLongitude, double rayonDeTolerance) {
        this.referenceLatitude = referenceLatitude;
        this.referenceLongitude = referenceLongitude;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
        this.rayonDeTolerance = rayonDeTolerance ;
    }

    public double getDistance() {
        double differenceLatitude = Math.toRadians(this.userLatitude - this.referenceLatitude);
        double differenceLongitude = Math.toRadians(this.userLongitude - this.referenceLongitude);
        double a = Math.pow(Math.sin(differenceLatitude / 2), 2) + Math.cos(Math.toRadians(this.referenceLatitude))
                * Math.cos(Math.toRadians(this.userLatitude)) * Math.pow(Math.sin(differenceLongitude / 2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public boolean dansLeRayon(){
        return (getDistance() <= this.rayonDeTolerance) ;
    }
}

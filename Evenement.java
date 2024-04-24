package model.mapping;

import java.sql.*;
import org.postgresql.util.PGInterval;
@Table( name="Evenement" )
public class Evenement {
    @Column(name="id_e" , type="integer" , primary_key = true )
    private Integer id_evnement;
    @Column(name="nom" , type="varchar")
    private String nom;
    @Column(name="dateconcert" , type="timestamp")
    private Timestamp dateconcert;
    @Column(name="reservationlimite" , type="timestamp")
    private PGInterval reservationlimite;
    @Column(name="limiteavantheure" , type="interval")
    private PGInterval limiteavantheure;

/// constructors
    public Evenement(){}

    

    public Evenement(Integer id_evnement, String nom, String dateconcert, String reservationlimite,String limiteavantheure)throws Exception {
        this.id_evnement = id_evnement;
        this.nom = nom;
        setDateconcert( dateconcert );
        setReservationlimite(reservationlimite);
        setLimiteavantheure(limiteavantheure);
    }

    // Getters and Setters

    public Integer getId_evnement() {
        return id_evnement;
    }

    public void setId_evnement(String id_evnement) {
        if( id_evnement != null )
            setId_evnement(Integer.valueOf(id_evnement));
    }

    public void setId_evnement(int id_evnement) {
        this.id_evnement = id_evnement;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }


    @Override
    public String toString() {
        return "Evenement [id_evnement=" + id_evnement + ", nom=" + nom + ", dateconcert=" + dateconcert
                + ", reservationlimite=" + reservationlimite + ", limiteavantheure=" + limiteavantheure + "]";
    }

    



    public void setLimiteavantheure(String limiteavantheure)throws Exception {
        if( limiteavantheure != null ){
            PGInterval p = new PGInterval( limiteavantheure );
            setLimiteavantheure( p );
        }
    }

    public void setId_evnement(Integer id_evnement) {
        this.id_evnement = id_evnement;
    }

    public Timestamp getDateconcert() {
        return dateconcert;
    }

    public void setDateconcert(String dateconcert) {
        if( dateconcert != null ){
            setDateconcert(Timestamp.valueOf(dateconcert));
        }
    }

    public void setDateconcert(Timestamp dateconcert) {
        this.dateconcert = dateconcert;
    }

    public PGInterval getReservationlimite() {
        return reservationlimite;
    }

    public void setReservationlimite(String reservationlimite) throws Exception{
        if( reservationlimite != null ){
            PGInterval p = new PGInterval( reservationlimite );
            setReservationlimite( p );
        }
    }

    public void setReservationlimite(PGInterval reservationlimite) {
        this.reservationlimite = reservationlimite;
    }

    public PGInterval getLimiteavantheure() {
        return limiteavantheure;
    }

    public void setLimiteavantheure(PGInterval limiteavantheure) {
        this.limiteavantheure = limiteavantheure;
    }

}
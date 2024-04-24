package model.mapping;

public class Place{
    @Column(name = "id_p" , type = "integer")
    private Integer idPlace;
    
    @Column(name = "nom" , type = "varchar")
    private String nom;
    
    public Place(){

    }

    public Place(Integer idPlace, String nom) {
        this.idPlace = idPlace;
        this.nom = nom;
    }

    public Place(Integer idPlace) {
        this.idPlace = idPlace;
    }

    public Place(String nom) {
        this.nom = nom;
    }

    /// getters et setters 
    public Integer getIdPlace() {
        return idPlace;
    }

    public void setIdPlace(Integer idPlace) {
        this.idPlace = idPlace;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return "Place [idPlace=" + idPlace + ", nom=" + nom + "]";
    }
    
}

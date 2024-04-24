package model.mapping;

import java.sql.*;
import org.postgresql.util.PGInterval;
@Table( name="huhu" )
public class Huhu {
    @Column(name="haha" , type="date")
    Date haha;

    public void setHaha( String haha ){
        System.out.println("date : "+haha);
        if( haha != null )
            setHaha(Date.valueOf(haha));
    }
    public void setHaha(Date haha) {
        this.haha = haha;
    }
    public Date getHaha() {
        return haha;
    }
    @Override
    public String toString() {
        return "Huhu [haha=" + haha + "]";
    }
    
}

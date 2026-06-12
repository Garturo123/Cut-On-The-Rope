package Usuarios;

import java.io.Serializable;
import java.util.Date;

public class Actividad implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Date timestamp;
    private String mensaje;
    
    public Actividad(String mensaje) {
        this.timestamp = new Date();
        this.mensaje = mensaje;
    }
    
    public Actividad(Date timestamp, String mensaje) {
        this.timestamp = timestamp;
        this.mensaje = mensaje;
    }
    
    public Date getTimestamp() { return timestamp; }
    public String getMensaje() { return mensaje; }
    
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    
    public String getTimestampFormateado() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
    }
    
    @Override
    public String toString() {
        return String.format("[%s] %s", getTimestampFormateado(), mensaje);
    }
}

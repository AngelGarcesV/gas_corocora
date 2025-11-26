// bpmn-service/src/main/java/com/example/demo/util/IdGeneratorUtil.java
package com.example.demo.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Utilidad para generar IDs únicos
 */
public class IdGeneratorUtil {
    
    private static final String PREFIX = "OC";
    private static final Random random = new Random();
    
    /**
     * Genera un ID único para una orden de compra usando UUID
     * Formato: OC-YYYY-XXXXXXXX
     */
    public static String generarOrdenId() {
        String año = new SimpleDateFormat("yyyy").format(new Date());
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return String.format("%s-%s-%s", PREFIX, año, uuid);
    }
    
    /**
     * Genera un ticket único para discrepancias
     * Formato: DISC-YYYYMMDD-HHMMSS-XXXX
     */
    public static String generarTicketDiscrepancia() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
        Date ahora = new Date();
        
        String fecha = dateFormat.format(ahora);
        String hora = timeFormat.format(ahora);
        String aleatorio = String.format("%04d", random.nextInt(10000));
        
        return String.format("DISC-%s-%s-%s", fecha, hora, aleatorio);
    }
    
    /**
     * Genera un ID de recepción único
     * Formato: REC-YYYY-XXXXXXXX
     */
    public static String generarRecepcionId() {
        String año = new SimpleDateFormat("yyyy").format(new Date());
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return String.format("REC-%s-%s", año, uuid);
    }
}

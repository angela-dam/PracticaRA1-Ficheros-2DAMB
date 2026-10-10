package org.educa.service;

import jakarta.xml.bind.JAXBException;
import org.educa.dao.ActivityDAOImpl;
import org.educa.dao.ActivityDAOInt;
import org.educa.entity.ProductoEntity;
import org.educa.entity.SummaryEntity;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

public class ProductoService {

    private static final String PREFIJO_XML = "inventario_";

    private final ActivityDAOInt activityDAO;

    public ProductoService() {
        this.activityDAO = new ActivityDAOImpl();
    }

    public List<ProductoEntity> readFile(String fileXml) throws JAXBException {
        //TODO: Implementar
        return null;
    }

    /**
     * Genera el fichero de texto con el resumen del inventario.
     *
     * @param path    carpeta de destino
     * @param fileXml ruta del fichero XML
     * @throws JAXBException si el XML no se puede leer
     * @throws IOException   si no se puede escribir el resumen
     */
    public void exportSummary(String path, String fileXml) throws JAXBException, IOException {
        File fileXML = new File(fileXml);
        String fecha = crearFechaXML(fileXML);
        File fileTXT = new File(path, "result_" + fecha + ".txt");

        List<ProductoEntity> productos = readFile(fileXml);
        BigDecimal beneficioTotal = productos.stream()
                .map(ProductoEntity::getProfit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        SummaryEntity resumen = new SummaryEntity(
                fecha,
                productos.size(),
                beneficioTotal,
                fileXML.getAbsolutePath(),
                nombreSinExtension(fileXML),
                fileXML.length());

        activityDAO.escribirResumen(fileTXT, resumen);
    }

    private String nombreSinExtension(File archivo) {
        String nombre = archivo.getName();
        int punto = nombre.lastIndexOf('.');
        return punto > 0 ? nombre.substring(0, punto) : nombre;
    }

    public String crearFechaXML(File archivoXML) {
        return nombreSinExtension(archivoXML).replace(PREFIJO_XML, "");
    }

    public void exportExcel(String path, String fileXml) throws JAXBException, IOException, ParseException {
        //TODO: Implementar
    }
}

package org.educa.service;

import jakarta.xml.bind.JAXBException;
import org.educa.dao.ActivityDAOImpl;
import org.educa.dao.ActivityDAOInt;
import org.educa.entity.ProductoEntity;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class ProductoService {

    private static final String PREFIJO_XML = "inventario_";
    private final ActivityDAOInt activity1DAO;

    public ProductoService() {
        this.activity1DAO = new ActivityDAOImpl();
    }

    public List<ProductoEntity> readFile(String fileXml) throws JAXBException {
        //TODO: Implementar
        return null;
    }

    public void exportSummary(String path, String fileXml) throws JAXBException, IOException {
        //TODO: Implementar

    }

    public String crearFechaXML(File archivoXML) {
        return nombreSinExtension(archivoXML).replace(PREFIJO_XML, "");
    }

    /**
     * Genera el Excel con los productos del XML.
     *
     * @param path carpeta de destino
     * @param fileXml ruta del fichero XML
     * @throws JAXBException  si el XML no se puede leer
     * @throws IOException    si no se puede escribir el Excel
     * @throws ParseException se mantiene por exigencia de la firma del enunciado
     */
    public void exportExcel(String path, String fileXml) throws JAXBException, IOException, ParseException {
        File fileExcel = new File(path, "export_" + crearFechaXML(new File(fileXml)) + ".xlsx");
        activity1DAO.crearExcel(fileExcel, readFile(fileXml));
    }

    private String nombreSinExtension(File archivo) {
        String nombre = archivo.getName();
        int punto = nombre.lastIndexOf('.');
        return punto > 0 ? nombre.substring(0, punto) : nombre;
    }
}

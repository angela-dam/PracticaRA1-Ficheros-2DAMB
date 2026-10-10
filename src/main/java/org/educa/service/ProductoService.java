package org.educa.service;

import jakarta.xml.bind.JAXBException;
import org.educa.dao.ActivityDAOImpl;
import org.educa.dao.ActivityDAOInt;
import org.educa.entity.ProductoEntity;
import org.educa.entity.SummaryEntity;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.List;

public class ProductoService {

    private static final String PREFIJO_XML = "inventario_";
    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    private final ActivityDAOInt activityDAO;

    public ProductoService() {
        this.activityDAO = new ActivityDAOImpl();
    }

    /**
     * Lee el XML y calcula precio final, coste y beneficio de cada producto.
     *
     * @param fileXml ruta del fichero XML
     * @return lista de {@link ProductoEntity} con sus importes calculados
     * @throws JAXBException si el XML no se puede leer
     */
    public List<ProductoEntity> readFile(String fileXml) throws JAXBException {
        List<ProductoEntity> productos = activityDAO.parsearXML(fileXml);
        productos.forEach(this::calcularImportes);
        return productos;
    }

    private void calcularImportes(ProductoEntity entity) {
        generated.Producto p = entity.getProducto();

        BigDecimal descuento = p.getDescuento().divide(CIEN);
        BigDecimal precioFinal = p.getPrecio()
                .subtract(p.getPrecio().multiply(descuento))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal costes = p.getCostes().getCostesEnvio().add(p.getCostes().getCostesAlmacenaje());

        entity.setPrecioFinal(precioFinal);
        entity.setCost(costes);
        entity.setProfit(precioFinal.subtract(costes));
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
        activityDAO.crearExcel(fileExcel, readFile(fileXml));
    }

}

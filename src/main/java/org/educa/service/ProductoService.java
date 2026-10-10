package org.educa.service;

import jakarta.xml.bind.JAXBException;
import org.educa.dao.ActivityDAOImpl;
import org.educa.dao.ActivityDAOInt;
import org.educa.entity.ProductoEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.List;

public class ProductoService {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    private final ActivityDAOInt activity1DAO;

    public ProductoService() {
        this.activity1DAO = new ActivityDAOImpl();
    }

    /**
     * Lee el XML y calcula precio final, coste y beneficio de cada producto.
     *
     * @param fileXml ruta del fichero XML
     * @return lista de {@link ProductoEntity} con sus importes calculados
     * @throws JAXBException si el XML no se puede leer
     */
    public List<ProductoEntity> readFile(String fileXml) throws JAXBException {
        List<ProductoEntity> productos = activity1DAO.parsearXML(fileXml);
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

    public void exportSummary(String path, String fileXml) throws JAXBException, IOException {
        //TODO: Implementar

    }

    public void exportExcel(String path, String fileXml) throws JAXBException, IOException, ParseException {
        //TODO: Implementar
    }
}

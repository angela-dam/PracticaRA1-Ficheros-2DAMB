package org.educa.dao;

import org.educa.entity.ProductoEntity;

import java.util.List;

public interface ActivityDAOInt {

    /**
     * Lee el XML y devuelve un {@link ProductoEntity} por cada producto con los datos tal cual vienen
     * en el fichero (los importes calculados quedan sin informar).
     *
     * @param fileXml ruta del fichero XML
     * @return lista de {@link ProductoEntity} leidos
     */
    List<ProductoEntity> parsearXML(String fileXml);
}

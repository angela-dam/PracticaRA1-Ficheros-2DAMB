package org.educa.dao;

import org.educa.entity.ProductoEntity;

import java.util.List;

import org.educa.entity.SummaryEntity;

import java.io.File;
import java.io.IOException;

public interface ActivityDAOInt {

    /**
     * Lee el XML y devuelve un {@link ProductoEntity} por cada producto con los datos tal cual vienen
     * en el fichero (los importes calculados quedan sin informar).
     *
     * @param fileXml ruta del fichero XML
     * @return lista de {@link ProductoEntity} leidos
     */
    List<ProductoEntity> parsearXML(String fileXml);

    /**
     * Escribe el resumen en un fichero de texto, creando la carpeta destino si no existe.
     *
     * @param archivoTXT fichero de destino
     * @param resumen    datos ya calculados que hay que escribir
     * @throws IOException si no se puede escribir el fichero
     */
    void escribirResumen(File archivoTXT, SummaryEntity resumen) throws IOException;

    /**
     * Escribe los productos en un fichero Excel (xlsx), creando la carpeta destino si no existe.
     *
     * @param archivoExcel   fichero de destino
     * @param listaProductos productos con sus importes ya calculados
     * @throws IOException si no se puede escribir el fichero
     */
    void crearExcel(File archivoExcel, List<ProductoEntity> listaProductos) throws IOException;
}

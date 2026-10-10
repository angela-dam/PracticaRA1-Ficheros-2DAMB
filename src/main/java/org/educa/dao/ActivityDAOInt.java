package org.educa.dao;

import org.educa.entity.SummaryEntity;

import java.io.File;
import java.io.IOException;

public interface ActivityDAOInt {

    /**
     * Escribe el resumen en un fichero de texto, creando la carpeta destino si no existe.
     *
     * @param archivoTXT fichero de destino
     * @param resumen    datos ya calculados que hay que escribir
     * @throws IOException si no se puede escribir el fichero
     */
    void escribirResumen(File archivoTXT, SummaryEntity resumen) throws IOException;
}

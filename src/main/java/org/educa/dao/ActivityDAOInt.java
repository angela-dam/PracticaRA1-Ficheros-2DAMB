package org.educa.dao;

import org.educa.entity.ProductoEntity;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ActivityDAOInt {

    /**
     * Escribe los productos en un fichero Excel (xlsx), creando la carpeta destino si no existe.
     *
     * @param archivoExcel   fichero de destino
     * @param listaProductos productos con sus importes ya calculados
     * @throws IOException si no se puede escribir el fichero
     */
    void crearExcel(File archivoExcel, List<ProductoEntity> listaProductos) throws IOException;
}

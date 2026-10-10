package org.educa.dao;

import generated.Producto;
import generated.Productos;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.educa.entity.ProductoEntity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.educa.entity.SummaryEntity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ActivityDAOImpl implements ActivityDAOInt{

    @Override
    public List<ProductoEntity> parsearXML(String fileXml) {
        try {
            JAXBContext contexto = JAXBContext.newInstance(Productos.class);
            Unmarshaller unmarshaller = contexto.createUnmarshaller();
            Productos productosXML = (Productos) unmarshaller.unmarshal(new File(fileXml));

            List<ProductoEntity> listaParseada = new ArrayList<>();
            for (Producto p : productosXML.getProducto()) {
                ProductoEntity productoEntity = new ProductoEntity();
                productoEntity.setProducto(p);
                listaParseada.add(productoEntity);
            }
            return listaParseada;
        } catch (JAXBException e) {
            throw new RuntimeException("Error en el Unmarshaller al leer el XML: " + e.getMessage(), e);
        }
    }

    @Override
    public void escribirResumen(File archivoTXT, SummaryEntity resumen) throws IOException {
        crearCarpetaDestino(archivoTXT);
        try (BufferedWriter escritor = Files.newBufferedWriter(archivoTXT.toPath(), StandardCharsets.UTF_8)) {
            escritor.write(resumen.toPrint());
        }
    }

    private void crearCarpetaDestino(File archivo) throws IOException {
        File carpeta = archivo.getAbsoluteFile().getParentFile();
        if (carpeta != null && !carpeta.exists() && !carpeta.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta " + carpeta);
        }
    }

}

package org.educa.dao;

import org.educa.entity.SummaryEntity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ActivityDAOImpl implements ActivityDAOInt{

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

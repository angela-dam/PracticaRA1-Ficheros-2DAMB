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

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.educa.entity.SummaryEntity;

import java.io.BufferedWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class ActivityDAOImpl implements ActivityDAOInt {

    private static final String[] CABECERAS = {"Codigo", "Número de Serie", "Precio", "Descuento",
            "Precio Final", "Costes Envío", "Costes Almacenaje", "Beneficio"};
    private static final String FORMATO_MONEDA = "#,##0.00 €";
    private static final String FORMATO_PORCENTAJE = "0.00%";

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

    @Override
    public void crearExcel(File archivoExcel, List<ProductoEntity> listaProductos) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Productos");

            // Los estilos se crean una sola vez y se reutilizan en todas las celdas
            CellStyle estiloCabecera = crearEstiloCabecera(workbook);
            CellStyle[] texto = {crearEstilo(workbook, IndexedColors.WHITE, null),
                    crearEstilo(workbook, IndexedColors.LIGHT_GREEN, null)};
            CellStyle[] moneda = {crearEstilo(workbook, IndexedColors.WHITE, FORMATO_MONEDA),
                    crearEstilo(workbook, IndexedColors.LIGHT_GREEN, FORMATO_MONEDA)};
            CellStyle[] porcentaje = {crearEstilo(workbook, IndexedColors.WHITE, FORMATO_PORCENTAJE),
                    crearEstilo(workbook, IndexedColors.LIGHT_GREEN, FORMATO_PORCENTAJE)};

            Row filaCabecera = sheet.createRow(0);
            for (int i = 0; i < CABECERAS.length; i++) {
                Cell cell = filaCabecera.createCell(i);
                cell.setCellValue(CABECERAS[i]);
                cell.setCellStyle(estiloCabecera);
            }

            int indexFila = 1;
            for (ProductoEntity entity : listaProductos) {
                Producto p = entity.getProducto();
                Row row = sheet.createRow(indexFila);
                int parity = indexFila % 2; // color de fondo alterno por filas

                escribirTexto(row, 0, p.getCodigo(), texto[parity]);
                escribirTexto(row, 1, p.getNumeroSerie(), texto[parity]);
                escribirNumero(row, 2, p.getPrecio().doubleValue(), moneda[parity]);
                escribirNumero(row, 3, p.getDescuento().doubleValue() / 100.0, porcentaje[parity]);
                escribirNumero(row, 4, entity.getPrecioFinal().doubleValue(), moneda[parity]);
                escribirNumero(row, 5, p.getCostes().getCostesEnvio().doubleValue(), moneda[parity]);
                escribirNumero(row, 6, p.getCostes().getCostesAlmacenaje().doubleValue(), moneda[parity]);
                escribirNumero(row, 7, entity.getProfit().doubleValue(), moneda[parity]);
                indexFila++;
            }

            for (int i = 0; i < CABECERAS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            crearCarpetaDestino(archivoExcel);
            try (FileOutputStream out = new FileOutputStream(archivoExcel)) {
                workbook.write(out);
            }
        }
    }

    private CellStyle crearEstiloCabecera(Workbook workbook) {
        Font negrita = workbook.createFont();
        negrita.setBold(true);
        CellStyle estilo = workbook.createCellStyle();
        estilo.setFont(negrita);
        estilo.setAlignment(HorizontalAlignment.CENTER);
        return estilo;
    }

    private CellStyle crearEstilo(Workbook workbook, IndexedColors fondo, String formato) {
        CellStyle estilo = workbook.createCellStyle();
        estilo.setFillForegroundColor(fondo.getIndex());
        estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estilo.setAlignment(HorizontalAlignment.CENTER);
        if (formato != null) {
            estilo.setDataFormat(workbook.createDataFormat().getFormat(formato));
        }
        return estilo;
    }

    private void escribirTexto(Row row, int columna, String valor, CellStyle estilo) {
        Cell cell = row.createCell(columna);
        cell.setCellValue(valor);
        cell.setCellStyle(estilo);
    }

    private void escribirNumero(Row row, int columna, double valor, CellStyle estilo) {
        Cell cell = row.createCell(columna);
        cell.setCellValue(valor);
        cell.setCellStyle(estilo);
    }

    private void crearCarpetaDestino(File archivo) throws IOException {
        File carpeta = archivo.getAbsoluteFile().getParentFile();
        if (carpeta != null && !carpeta.exists() && !carpeta.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta " + carpeta);
        }
    }
}

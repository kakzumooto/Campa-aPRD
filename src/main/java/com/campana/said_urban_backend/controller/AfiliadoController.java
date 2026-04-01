package com.campana.said_urban_backend.controller;

import com.campana.said_urban_backend.model.Afiliado;
import com.campana.said_urban_backend.repository.AfiliadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/afiliados")
@CrossOrigin(origins = "*") // Permite que React se conecte
public class AfiliadoController {

    @Autowired
    private AfiliadoRepository repository;

    // Carpeta donde se guardarán las fotos físicamente
    private final String UPLOAD_DIR = "uploads/ines/";

    @PostMapping("/registrar")
    public Afiliado registrarAfiliado(
            @RequestParam("nombre") String nombre,
            @RequestParam("telefono") String telefono,
            @RequestParam("email") String email,
            @RequestParam("direccion") String direccion,
            @RequestParam("foto") MultipartFile foto) throws IOException {

        // 1. Crear la carpeta si no existe
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 2. Generar un nombre único para la foto para que no se sobrescriban
        String fileName = System.currentTimeMillis() + "_" + foto.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        // 3. Guardar el archivo en el disco duro del servidor
        Files.copy(foto.getInputStream(), filePath);

        // 4. Crear el objeto Afiliado y guardarlo en la DB
        Afiliado nuevo = new Afiliado();
        nuevo.setNombreCompleto(nombre);
        nuevo.setTelefono(telefono);
        nuevo.setEmail(email);
        nuevo.setDireccion(direccion);
        nuevo.setFotoIneUrl(filePath.toString()); // Guardamos la ruta del archivo

        return repository.save(nuevo);
    }

    @GetMapping("/lista")
    public List<Afiliado> listarTodos() {
        return repository.findAll();
    }

    @GetMapping("/exportar")
    public void exportarExcel(HttpServletResponse response) throws IOException {
        List<Afiliado> afiliados = repository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Afiliados Said Urban");

        // 1. Estilo para la cabecera
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        // 2. Crear Cabeceras
        Row header = sheet.createRow(0);
        String[] columnas = {"Nombre", "Teléfono", "Email", "Dirección", "Link a INE", "Fecha de Registro"};

        for (int i = 0; i < columnas.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columnas[i]);
            cell.setCellStyle(headerStyle);
        }

        // 3. Llenar los datos de la base de datos
        int rowNum = 1;
        for (Afiliado a : afiliados) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(a.getNombreCompleto());
            row.createCell(1).setCellValue(a.getTelefono());
            row.createCell(2).setCellValue(a.getEmail());
            row.createCell(3).setCellValue(a.getDireccion());

            // Creamos el link a la foto (esto es lo que más le servirá a Said)
            String nombreFoto = a.getFotoIneUrl().substring(a.getFotoIneUrl().lastIndexOf("/") + 1);
            String urlPublica = "https://localhost:8080/uploads/ines/" + nombreFoto;
            row.createCell(4).setCellValue(urlPublica);

            row.createCell(5).setCellValue(a.getFechaRegistro().toString());
        }

        // 4. Configurar la respuesta del navegador
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=base_datos_campana.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
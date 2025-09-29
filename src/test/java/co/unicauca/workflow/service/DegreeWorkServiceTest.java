package co.unicauca.workflow.service;

import co.unicauca.workflow.access.IDegreeWorkRepository;
import co.unicauca.workflow.access.DegreeWorkSQLiteRepository;
import co.unicauca.workflow.domain.entities.*;
import co.unicauca.workflow.service.DegreeWorkService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Pruebas unitarias para DegreeWorkService
 *
 * Casos validados:
 * - Registro de un trabajo de grado válido
 * - Actualización de estado
 * - Avanzar en las evaluaciones
 * - Rechazar trabajo de grado
 * - Guardar correcciones
 * - Listar por docente
 * - Listar por estudiante y modalidad
 */
public class DegreeWorkServiceTest {

    public DegreeWorkServiceTest() {}

    // Método auxiliar para crear un trabajo de prueba
    private DegreeWork crearTrabajoPrueba(String emailEst, String emailDoc) {
        Student estudiante = new Student("Juan", "Perez", "3216549870", "Ingeniería de Sistemas", emailEst, "12345") {
            @Override
            public void showDashboard() {}
        };
        Teacher director = new Teacher("Ana", "Lopez", "3216549870", "Ingeniería de Sistemas",  emailDoc, "67890") {
            @Override
            public void showDashboard() {}
        };
        return new DegreeWork(
                estudiante,
                director,
                "Sistema de Gestión",
                Modalidad.INVESTIGACION,
                LocalDate.now(),
                null,
                "Objetivo general",
                Arrays.asList("Obj1", "Obj2"),
                "archivo.pdf"
        );
    }

    @Test
    public void testRegistrarFormatoValido() {
        System.out.println("registrarFormatoValido");
        IDegreeWorkRepository repository = new DegreeWorkSQLiteRepository();
        DegreeWorkService service = new DegreeWorkService(repository);

        DegreeWork formato = crearTrabajoPrueba("juan@unicauca.edu.co", "ana@unicauca.edu.co");
        boolean expResult = true;
        boolean result = service.registrarFormato(formato);
        assertEquals(expResult, result);
    }

    @Test
    public void testActualizarEstado() {
        System.out.println("actualizarEstado");
        IDegreeWorkRepository repository = new DegreeWorkSQLiteRepository();
        DegreeWorkService service = new DegreeWorkService(repository);

        DegreeWork formato = crearTrabajoPrueba("carlos@unicauca.edu.co", "maria@unicauca.edu.co");
        service.registrarFormato(formato);
        formato.setEstado(EstadoFormatoA.SEGUNDA_EVALUACION);

        boolean result = service.actualizarFormato(formato);
        assertTrue(result);
        assertEquals(EstadoFormatoA.SEGUNDA_EVALUACION, formato.getEstado());
    }

    @Test
    public void testAvanzarEvaluacion() {
        System.out.println("avanzarEvaluacion");
        IDegreeWorkRepository repository = new DegreeWorkSQLiteRepository();
        DegreeWorkService service = new DegreeWorkService(repository);

        DegreeWork formato = crearTrabajoPrueba("sofia@unicauca.edu.co", "carlos@unicauca.edu.co");
        service.registrarFormato(formato);

        boolean result = service.avanzarEvaluacion(formato.getId());
        formato = service.obtenerFormato(formato.getId()); // refrescar
        assertEquals(EstadoFormatoA.SEGUNDA_EVALUACION, formato.getEstado());
    }

    @Test
    public void testRechazarFormato() {
        System.out.println("rechazarFormato");
        IDegreeWorkRepository repository = new DegreeWorkSQLiteRepository();
        DegreeWorkService service = new DegreeWorkService(repository);

        DegreeWork formato = crearTrabajoPrueba("andres@unicauca.edu.co", "marta@unicauca.edu.co");
        service.registrarFormato(formato);

        boolean result = service.rechazar(formato.getId());
        formato = service.obtenerFormato(formato.getId()); // refrescar
        assertEquals(EstadoFormatoA.RECHAZADO, formato.getEstado());
    }

    @Test
    public void testGuardarCorrecciones() {
        System.out.println("guardarCorrecciones");
        IDegreeWorkRepository repository = new DegreeWorkSQLiteRepository();
        DegreeWorkService service = new DegreeWorkService(repository);

        DegreeWork formato = crearTrabajoPrueba("laura@unicauca.edu.co", "jose@unicauca.edu.co");
        service.registrarFormato(formato);

        service.guardarCorrecciones(formato.getId(), "Corregir objetivos");
        formato = service.obtenerFormato(formato.getId()); // refrescar
        assertEquals("Corregir objetivos", formato.getCorrecciones());
    }

    @Test
    public void testListarPorDocente() {
        System.out.println("listarPorDocente");
        IDegreeWorkRepository repository = new DegreeWorkSQLiteRepository();
        DegreeWorkService service = new DegreeWorkService(repository);

        DegreeWork formato = crearTrabajoPrueba("miguel@unicauca.edu.co", "ana2@unicauca.edu.co");
        service.registrarFormato(formato);

        List<DegreeWork> result = service.listarDegreeWorksPorDocente("ana2@unicauca.edu.co");
        assertTrue(result.stream().anyMatch(f -> f.getDirectorProyecto().getEmail().equals("ana2@unicauca.edu.co")));
    }

    @Test
    public void testListarPorEstudianteYModalidad() {
        System.out.println("listarPorEstudianteYModalidad");
        IDegreeWorkRepository repository = new DegreeWorkSQLiteRepository();
        DegreeWorkService service = new DegreeWorkService(repository);

        DegreeWork formato = crearTrabajoPrueba("daniela@unicauca.edu.co", "rosa@unicauca.edu.co");
        service.registrarFormato(formato);

        List<DegreeWork> result = service.listarPorEstudianteYModalidad("daniela@unicauca.edu.co", Modalidad.INVESTIGACION);
        assertTrue(result.stream().anyMatch(f -> f.getEstudiante().getEmail().equals("daniela@unicauca.edu.co")));
    }
}

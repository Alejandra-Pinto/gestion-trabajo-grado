package co.unicauca.workflow.domain.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import co.unicauca.workflow.domain.entities.*;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class DegreeWorkTest {

    private Student student;
    private Teacher director;
    private Teacher codirector;
    private DegreeWork degreeWork;

    @BeforeEach
    void setUp() {
        student = new Student(
                "Juan", 
                "Pérez", 
                "3216549870", 
                "Ingeniería de Sistemas", 
                "juan@unicauca.edu.co", 
                "Pedro123"
        );

        director = new Teacher(
                "Ana", 
                "Martínez", 
                "3214567890", 
                "Ingeniería de Sistemas", 
                "ana@unicauca.edu.co", 
                "Clave123"
        );

        codirector = new Teacher(
                "Carlos", 
                "López", 
                "3127894561", 
                "Ingeniería de Software", 
                "carlos@unicauca.edu.co", 
                "Pass456"
        );

        degreeWork = new DegreeWork(
                student,
                director,
                "Sistema de gestión académica",
                Modalidad.INVESTIGACION,
                LocalDate.now(),
                codirector,
                "Mejorar procesos",
                Arrays.asList("Analizar requerimientos", "Diseñar arquitectura"),
                "C:/documentos/proyecto.pdf"
        );
    }

    @Test
    void testValidDegreeWorkCreation() {
        assertNotNull(degreeWork);
        assertEquals("Sistema de gestión académica", degreeWork.getTituloProyecto());
        assertEquals(EstadoFormatoA.PRIMERA_EVALUACION, degreeWork.getEstado());
        assertEquals(0, degreeWork.getNoAprobadoCount());
    }

    @Test
    void testSetTituloProyectoInvalid() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                degreeWork.setTituloProyecto("   "));
        assertEquals("El título del proyecto no puede estar vacío", ex.getMessage());
    }

    @Test
    void testSetObjetivoGeneralInvalid() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                degreeWork.setObjetivoGeneral(""));
        assertEquals("El objetivo general no puede estar vacío", ex.getMessage());
    }

    @Test
    void testSetArchivoPdfInvalid() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                degreeWork.setArchivoPdf("documento.txt"));
        assertEquals("El archivo debe ser un PDF válido", ex.getMessage());
    }

    @Test
    void testSetFechaFuturaInvalid() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                degreeWork.setFechaActual(LocalDate.now().plusDays(1)));
        assertEquals("La fecha no puede ser futura", ex.getMessage());
    }

    @Test
    void testSetCartaAceptacionEmpresaValidaEnPractica() {
        degreeWork.setModalidad(Modalidad.PRACTICA_PROFESIONAL);
        degreeWork.setCartaAceptacionEmpresa("C:/documentos/carta.pdf");
        assertEquals("C:/documentos/carta.pdf", degreeWork.getCartaAceptacionEmpresa());
    }

    @Test
    void testSetCartaAceptacionEmpresaIgnoradaEnInvestigacion() {
        degreeWork.setModalidad(Modalidad.INVESTIGACION);
        degreeWork.setCartaAceptacionEmpresa("C:/documentos/carta.pdf");
        assertNull(degreeWork.getCartaAceptacionEmpresa());
    }

    @Test
    void testIncrementNoAprobadoCount() {
        degreeWork.incrementNoAprobadoCount();
        degreeWork.incrementNoAprobadoCount();
        assertEquals(2, degreeWork.getNoAprobadoCount());
    }
}

package ru.sfedu.teamselection.service.report;

import java.io.ByteArrayInputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import ru.sfedu.teamselection.domain.Student;
import ru.sfedu.teamselection.domain.Team;
import ru.sfedu.teamselection.domain.Track;
import ru.sfedu.teamselection.domain.User;
import ru.sfedu.teamselection.service.StudentExportService;
import ru.sfedu.teamselection.service.StudentService;

class StudentExportServiceTest {

    @Mock
    private StudentService studentService;

    private StudentExportService studentExportService;

    private List<Student> testStudents;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        studentExportService = new StudentExportService(studentService);

        // Подготовка тестовых данных
        User user1 = new User();
        user1.setFio("Иванов Иван Иванович");
        user1.setEmail("ivanov@example.com");

        User user2 = new User();
        user2.setFio("Петров Петр Петрович");
        user2.setEmail("petrov@example.com");

        Track track = new Track();
        track.setName("Java Developer");

        Team team = new Team();
        team.setName("Dream Team");

        Student student1 = new Student();
        student1.setId(1L);
        student1.setUser(user1);
        student1.setCourse(3);
        student1.setGroupNumber(101);
        student1.setHasTeam(true);
        student1.setIsCaptain(true);
        student1.setCurrentTrack(track);
        student1.setCurrentTeam(team);
        student1.setContacts("telegram: @ivanov");

        Student student2 = new Student();
        student2.setId(2L);
        student2.setUser(user2);
        student2.setCourse(4);
        student2.setGroupNumber(1);
        student2.setHasTeam(false);
        student2.setIsCaptain(false);
        student2.setCurrentTrack(track);
        student2.setCurrentTeam(null);
        student2.setContacts("telegram");

        testStudents = List.of(student1, student2);
    }

    @Test
    void exportStudentsToCsvByTrack_ShouldReturnCorrectCsv() throws Exception {
        // Arrange
        Long trackId = 1L;
        when(studentService.findAllByTrack(eq(trackId), any(Sort.class)))
                .thenReturn(testStudents);

        // Act
        byte[] result = studentExportService.exportStudentsToCsvByTrack(trackId);
        String csvContent = new String(result);

        // Assert
        assertTrue(csvContent.startsWith("id,fio,email,course,groupNumber,hasTeam,isCaptain,track,teamName,contacts"));
        assertTrue(csvContent.contains("Dream Team"));
        assertTrue(csvContent.contains("telegram: @ivanov"));
    }

    @Test
    void exportStudentsToExcelByTrack_ShouldCreateValidWorkbook() throws Exception {
        // Arrange
        Long trackId = 1L;
        when(studentService.findAllByTrack(eq(trackId), any(Sort.class)))
                .thenReturn(testStudents);

        // Act
        byte[] excelData = studentExportService.exportStudentsToExcelByTrack(trackId);
        Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData));
        Sheet sheet = workbook.getSheetAt(0);

        // Assert - Basic structure
        assertEquals("Students_1", sheet.getSheetName());
        assertEquals(3, sheet.getPhysicalNumberOfRows()); // Header + 2 students

        // Check header
        Row headerRow = sheet.getRow(0);
        assertEquals("ID", headerRow.getCell(0).getStringCellValue());
        assertEquals("Команда", headerRow.getCell(8).getStringCellValue());

        // Check styles
        CellStyle headerStyle = headerRow.getCell(0).getCellStyle();
        assertEquals(FillPatternType.SOLID_FOREGROUND, headerStyle.getFillPattern());
        assertEquals(HorizontalAlignment.CENTER, headerStyle.getAlignment());

        // Check data rows
        Row firstDataRow = sheet.getRow(1);
        assertTrue(firstDataRow.getCell(5).getBooleanCellValue()); // hasTeam
        assertEquals("Dream Team", firstDataRow.getCell(8).getStringCellValue());
    }

    @Test
    void exportStudentsToExcelByTrack_ShouldHandleMissingTeamAndContacts() throws Exception {
        // Arrange
        Long trackId = 1L;
        testStudents.get(1).setCurrentTeam(null);
        testStudents.get(1).setContacts(null);
        when(studentService.findAllByTrack(eq(trackId), any(Sort.class)))
                .thenReturn(testStudents);

        // Act
        byte[] excelData = studentExportService.exportStudentsToExcelByTrack(trackId);
        Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelData));
        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(2);

        // Assert
        assertEquals("", row.getCell(8).getStringCellValue()); // team name
        assertEquals("", row.getCell(9).getStringCellValue()); // contacts
    }

    @Test
    void exportMethods_ShouldThrowExceptionOnError() {
        // Arrange
        Long trackId = 1L;
        when(studentService.findAllByTrack(eq(trackId), any(Sort.class)))
                .thenThrow(new RuntimeException("DB error"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> studentExportService.exportStudentsToCsvByTrack(trackId));
        assertThrows(RuntimeException.class,
                () -> studentExportService.exportStudentsToExcelByTrack(trackId));
    }
}